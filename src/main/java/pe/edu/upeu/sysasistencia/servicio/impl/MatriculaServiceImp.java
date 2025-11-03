package pe.edu.upeu.sysasistencia.servicio.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import pe.edu.upeu.sysasistencia.dtos.ImportFilterDTO;
import pe.edu.upeu.sysasistencia.dtos.ImportResultDTO;
import pe.edu.upeu.sysasistencia.modelo.*;
import pe.edu.upeu.sysasistencia.repositorio.ICrudGenericoRepository;
import pe.edu.upeu.sysasistencia.repositorio.IMatriculaRepository;
import pe.edu.upeu.sysasistencia.repositorio.IUsuarioRepository;
import pe.edu.upeu.sysasistencia.servicio.*;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class MatriculaServiceImp extends CrudGenericoServiceImp<Matricula, Long>
        implements IMatriculaService {

    private final IMatriculaRepository repo;
    private final IPersonaService personaService;
    private final ISedeService sedeService;
    private final IFacultadService facultadService;
    private final IProgramaEstudioService programaService;
    private final IPeriodoService periodoService;
    private final IUsuarioRepository usuarioRepository;
    private final IRolService rolService;
    private final IUsuarioRolService usuarioRolService;
    private final PasswordEncoder passwordEncoder;

    private static final DateTimeFormatter[] DATE_TIME_FORMATTERS = {
            DateTimeFormatter.ofPattern("dd/MM/yyyy h:mm a", Locale.forLanguageTag("es")),
            DateTimeFormatter.ofPattern("dd/MM/yyyy hh:mm a", Locale.forLanguageTag("es")),
            DateTimeFormatter.ofPattern("dd/MM/yyyy H:mm", Locale.forLanguageTag("es")),
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm", Locale.forLanguageTag("es")),
            DateTimeFormatter.ofPattern("dd-MM-yyyy h:mm a", Locale.forLanguageTag("es")),
            DateTimeFormatter.ofPattern("dd-MM-yyyy H:mm", Locale.forLanguageTag("es"))
    };

    private static final DateTimeFormatter[] DATE_FORMATTERS = {
            DateTimeFormatter.ofPattern("dd/MM/yyyy"),
            DateTimeFormatter.ofPattern("dd-MM-yyyy"),
            DateTimeFormatter.ofPattern("yyyy-MM-dd")
    };

    @Override
    protected ICrudGenericoRepository<Matricula, Long> getRepo() {
        return repo;
    }

    @Override
    public List<Matricula> findByCodigoEstudiante(String codigo) {
        return repo.findByCodigoEstudiante(codigo);
    }

    @Override
    public List<Matricula> findByFiltros(
            Long sedeId,
            Long facultadId,
            Long programaId,
            Long periodoId,
            TipoPersona tipoPersona
    ) {
        return repo.findByFiltros(sedeId, facultadId, programaId, periodoId, tipoPersona);
    }

    @Override
    public ImportResultDTO importarDesdeExcel(MultipartFile file, ImportFilterDTO filtros)
            throws Exception {
        ImportResultDTO result = new ImportResultDTO();
        result.setTotalRegistros(0);
        result.setExitosos(0);
        result.setFallidos(0);

        // ✅ VALIDAR QUE EL FILTRO DE PERIODO SEA OBLIGATORIO
        if (filtros.getPeriodoId() == null) {
            throw new Exception("Debe seleccionar un periodo para la importación");
        }

        Periodo periodoSeleccionado = periodoService.findById(filtros.getPeriodoId());

        TipoPersona tipoPersona = filtros.esEstudiante() ? TipoPersona.ESTUDIANTE : TipoPersona.INVITADO;
        if (filtros.getTipoPersona() != null) {
            tipoPersona = filtros.getTipoPersona();
        }

        try (InputStream is = file.getInputStream();
             Workbook workbook = new XSSFWorkbook(is)) {

            Sheet sheet = workbook.getSheetAt(0);
            if (sheet == null || sheet.getPhysicalNumberOfRows() <= 1) {
                result.getErrores().add("El archivo Excel está vacío o sin datos");
                return result;
            }

            int totalRows = sheet.getPhysicalNumberOfRows();
            result.setTotalRegistros(totalRows - 1);

            log.info("Iniciando importación con periodo seleccionado: {}", periodoSeleccionado.getNombre());
            log.info("Filtros: Sede={}, Facultad={}, Programa={}, TipoPersona={}",
                    filtros.getSedeId(), filtros.getFacultadId(), filtros.getProgramaId(), tipoPersona);

            for (int i = 1; i < totalRows; i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;

                try {
                    procesarFila(row, i + 1, filtros, tipoPersona, periodoSeleccionado, result);
                } catch (Exception e) {
                    result.setFallidos(result.getFallidos() + 1);
                    result.getErrores().add("Fila " + (i + 1) + ": " + e.getMessage());
                    log.error("Error procesando fila {}: {}", i + 1, e.getMessage());
                }
            }

            log.info("Importación completada. Total: {}, Exitosos: {}, Fallidos: {}",
                    result.getTotalRegistros(), result.getExitosos(), result.getFallidos());

        } catch (Exception e) {
            throw new Exception("Error al procesar el archivo Excel: " + e.getMessage());
        }

        return result;
    }

    private void procesarFila(Row row, int rowNum, ImportFilterDTO filtros,
                              TipoPersona tipoPersona, Periodo periodoSeleccionado,
                              ImportResultDTO result) throws Exception {
        try {
            // ✅ Leer datos del Excel (SIN leer periodo de la columna)
            String modoContrato = getCellValueAsString(row.getCell(0));
            String modalidadEstudio = getCellValueAsString(row.getCell(1));
            String sedeNombre = getCellValueAsString(row.getCell(2));
            String facultadNombre = getCellValueAsString(row.getCell(3));
            String programaNombre = getCellValueAsString(row.getCell(4));
            // ❌ ELIMINADO: periodo de columna 5
            String ciclo = getCellValueAsString(row.getCell(5)); // Ahora ciclo está en columna 5
            String grupo = getCellValueAsString(row.getCell(6));
            String idPersonaStr = getCellValueAsString(row.getCell(7));
            String codigoEstudiante = getCellValueAsString(row.getCell(8));
            String nombreCompleto = getCellValueAsString(row.getCell(9));
            String documento = getCellValueAsString(row.getCell(10));

            String correo = getCellValueAsString(row.getCell(11));
            if (correo != null) correo = correo.trim();

            String usuario = getCellValueAsString(row.getCell(12));
            if (usuario != null) usuario = usuario.trim();

            String correoInstitucional = getCellValueAsString(row.getCell(13));
            if (correoInstitucional != null) correoInstitucional = correoInstitucional.trim();

            String celular = getCellValueAsString(row.getCell(14));
            String pais = getCellValueAsString(row.getCell(15));
            String foto = getCellValueAsString(row.getCell(16));
            String religion = getCellValueAsString(row.getCell(17));
            LocalDate fechaNacimiento = parseFechaNacimiento(row.getCell(18));
            LocalDateTime fechaMatriculaConHora = parseFechaMatriculaConHora(row.getCell(19));

            // ✅ DETECCIÓN MEJORADA (sin periodo)
            boolean tieneDatosAcademicos = (modoContrato != null && !modoContrato.trim().isEmpty()) &&
                    (modalidadEstudio != null && !modalidadEstudio.trim().isEmpty()) &&
                    (sedeNombre != null && !sedeNombre.trim().isEmpty()) &&
                    (programaNombre != null && !programaNombre.trim().isEmpty());

            if (tieneDatosAcademicos) {
                if (filtros.getTipoPersona() != null && filtros.getTipoPersona() != TipoPersona.ESTUDIANTE) {
                    throw new Exception("Registro tiene datos académicos pero filtro especifica " +
                            filtros.getTipoPersona());
                }
                tipoPersona = TipoPersona.ESTUDIANTE;
                log.info("Fila {} detectada como ESTUDIANTE", rowNum);
            } else {
                if (filtros.getTipoPersona() == TipoPersona.ESTUDIANTE) {
                    throw new Exception("Filtro es ESTUDIANTE pero registro no tiene datos académicos completos");
                }
                if (filtros.getTipoPersona() == null) {
                    tipoPersona = TipoPersona.INVITADO;
                }
            }

            // Validaciones básicas
            if (documento == null || documento.trim().isEmpty()) {
                throw new Exception("Documento vacío - registro omitido");
            }

            if (nombreCompleto == null || nombreCompleto.trim().isEmpty()) {
                throw new Exception("Nombre completo vacío - registro omitido");
            }

            // Verificar si la persona ya existe
            Optional<Persona> personaExistente = personaService.findByDocumento(documento);

            if (personaExistente.isPresent()) {
                Persona persona = personaExistente.get();

                if (tipoPersona == TipoPersona.ESTUDIANTE) {
                    // ✅ BUSCAR SI YA TIENE MATRÍCULA EN ESTE PERIODO
                    Optional<Matricula> matriculaExistente = repo.findByPersonaIdPersonaAndPeriodoIdPeriodo(
                            persona.getIdPersona(),
                            periodoSeleccionado.getIdPeriodo()
                    );

                    if (matriculaExistente.isPresent()) {
                        // ✅ ACTUALIZAR MATRÍCULA EXISTENTE
                        Matricula matricula = matriculaExistente.get();
                        Sede sede = obtenerOValidarSede(sedeNombre, filtros);
                        Facultad facultad = obtenerOValidarFacultad(facultadNombre, filtros);
                        ProgramaEstudio programa = obtenerOValidarPrograma(programaNombre, facultad, filtros);

                        if (sede == null) throw new Exception("Sede no coincide con filtros");
                        if (facultad == null) throw new Exception("Facultad no coincide con filtros");
                        if (programa == null) throw new Exception("Programa no coincide con filtros");

                        matricula.setSede(sede);
                        matricula.setFacultad(facultad);
                        matricula.setProgramaEstudio(programa);
                        matricula.setModoContrato(modoContrato);
                        matricula.setModalidadEstudio(modalidadEstudio);
                        matricula.setCiclo(ciclo);
                        matricula.setGrupo(grupo);
                        matricula.setFechaMatricula(fechaMatriculaConHora != null ?
                                fechaMatriculaConHora : LocalDateTime.now());

                        repo.save(matricula);
                        result.getWarnings().add("Fila " + rowNum +
                                ": Matrícula actualizada para documento " + documento);
                        log.info("✅ Matrícula actualizada: {} - Periodo: {}",
                                documento, periodoSeleccionado.getNombre());
                    } else {
                        // ✅ CREAR NUEVA MATRÍCULA EN EL PERIODO SELECCIONADO
                        crearNuevaMatricula(persona, filtros, periodoSeleccionado, modoContrato,
                                modalidadEstudio, ciclo, grupo, fechaMatriculaConHora,
                                sedeNombre, facultadNombre, programaNombre);

                        log.info("✅ Nueva matrícula creada: {} - Periodo: {}",
                                documento, periodoSeleccionado.getNombre());
                    }
                }

                result.setExitosos(result.getExitosos() + 1);
                return; // No crear nueva persona
            }

            // Verificar entidades relacionadas (solo para estudiantes)
            Sede sede = null;
            Facultad facultad = null;
            ProgramaEstudio programa = null;

            if (tipoPersona == TipoPersona.ESTUDIANTE) {
                sede = obtenerOValidarSede(sedeNombre, filtros);
                if (sede == null) throw new Exception("Sede no coincide con filtros");

                facultad = obtenerOValidarFacultad(facultadNombre, filtros);
                if (facultad == null) throw new Exception("Facultad no coincide con filtros");

                programa = obtenerOValidarPrograma(programaNombre, facultad, filtros);
                if (programa == null) throw new Exception("Programa no coincide con filtros");
            }

            // Crear usuario automáticamente
            Usuario usuarioCreado = crearUsuario(correo, documento, usuario);

            // Crear Persona y asignar usuario
            Persona persona = new Persona();
            persona.setCodigoEstudiante(codigoEstudiante);
            persona.setNombreCompleto(nombreCompleto);
            persona.setDocumento(documento);
            persona.setCorreo(correo);
            persona.setCorreoInstitucional(correoInstitucional);
            persona.setCelular(celular);
            persona.setPais(pais);
            persona.setFoto(foto);
            persona.setReligion(religion);
            persona.setFechaNacimiento(fechaNacimiento);
            persona.setTipoPersona(tipoPersona);
            persona.setUsuario(usuarioCreado);
            persona = personaService.save(persona);

            // ✅ CREAR MATRÍCULA SOLO PARA ESTUDIANTES (con periodo del filtro)
            if (tipoPersona == TipoPersona.ESTUDIANTE) {
                Matricula matricula = new Matricula();
                matricula.setPersona(persona);
                matricula.setSede(sede);
                matricula.setFacultad(facultad);
                matricula.setProgramaEstudio(programa);
                matricula.setPeriodo(periodoSeleccionado); // ✅ Periodo del filtro
                matricula.setModoContrato(modoContrato);
                matricula.setModalidadEstudio(modalidadEstudio);
                matricula.setCiclo(ciclo);
                matricula.setGrupo(grupo);
                matricula.setFechaMatricula(fechaMatriculaConHora != null ?
                        fechaMatriculaConHora : LocalDateTime.now());
                matricula.setEstado("ACTIVO");
                repo.save(matricula);

                log.info("✅ Fila {} procesada como ESTUDIANTE: {} - Usuario: {} - Periodo: {}",
                        rowNum, documento, usuarioCreado.getUser(), periodoSeleccionado.getNombre());
            } else {
                log.info("✅ Fila {} procesada como INVITADO: {} - Usuario: {}",
                        rowNum, documento, usuarioCreado.getUser());
            }

            result.setExitosos(result.getExitosos() + 1);

        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    // ✅ NUEVO MÉTODO AUXILIAR
    private void crearNuevaMatricula(Persona persona, ImportFilterDTO filtros,
                                     Periodo periodo, String modoContrato,
                                     String modalidadEstudio, String ciclo,
                                     String grupo, LocalDateTime fechaMatricula,
                                     String sedeNombre, String facultadNombre,
                                     String programaNombre) {
        Sede sede = obtenerOValidarSede(sedeNombre, filtros);
        Facultad facultad = obtenerOValidarFacultad(facultadNombre, filtros);
        ProgramaEstudio programa = obtenerOValidarPrograma(programaNombre, facultad, filtros);

        Matricula matricula = new Matricula();
        matricula.setPersona(persona);
        matricula.setSede(sede);
        matricula.setFacultad(facultad);
        matricula.setProgramaEstudio(programa);
        matricula.setPeriodo(periodo);
        matricula.setModoContrato(modoContrato);
        matricula.setModalidadEstudio(modalidadEstudio);
        matricula.setCiclo(ciclo);
        matricula.setGrupo(grupo);
        matricula.setFechaMatricula(fechaMatricula != null ? fechaMatricula : LocalDateTime.now());
        matricula.setEstado("ACTIVO");

        repo.save(matricula);
    }

    private Sede obtenerOValidarSede(String sedeNombre, ImportFilterDTO filtros) {
        if (filtros.getSedeId() != null) {
            Sede sedeFiltro = sedeService.findById(filtros.getSedeId());
            if (!sedeFiltro.getNombre().equalsIgnoreCase(sedeNombre)) {
                log.warn("Sede '{}' no coincide con filtro '{}'", sedeNombre, sedeFiltro.getNombre());
                return null;
            }
            return sedeFiltro;
        }

        return sedeService.findByNombre(sedeNombre)
                .orElseGet(() -> {
                    Sede nuevaSede = new Sede();
                    nuevaSede.setNombre(sedeNombre);
                    nuevaSede.setDescripcion("Creada automáticamente desde importación");
                    return sedeService.save(nuevaSede);
                });
    }

    private Facultad obtenerOValidarFacultad(String facultadNombre, ImportFilterDTO filtros) {
        if (filtros.getFacultadId() != null) {
            Facultad facultadFiltro = facultadService.findById(filtros.getFacultadId());
            if (!facultadFiltro.getNombre().equalsIgnoreCase(facultadNombre)) {
                log.warn("Facultad '{}' no coincide con filtro '{}'", facultadNombre, facultadFiltro.getNombre());
                return null;
            }
            return facultadFiltro;
        }

        return facultadService.findByNombre(facultadNombre)
                .orElseGet(() -> {
                    Facultad nuevaFacultad = new Facultad();
                    nuevaFacultad.setNombre(facultadNombre);
                    nuevaFacultad.setDescripcion("Creada automáticamente desde importación");
                    return facultadService.save(nuevaFacultad);
                });
    }

    private ProgramaEstudio obtenerOValidarPrograma(String programaNombre, Facultad facultad,
                                                    ImportFilterDTO filtros) {
        if (filtros.getProgramaId() != null) {
            ProgramaEstudio programaFiltro = programaService.findById(filtros.getProgramaId());
            if (!programaFiltro.getNombre().equalsIgnoreCase(programaNombre)) {
                log.warn("Programa '{}' no coincide con filtro '{}'", programaNombre, programaFiltro.getNombre());
                return null;
            }
            return programaFiltro;
        }

        return programaService.findByNombre(programaNombre)
                .orElseGet(() -> {
                    ProgramaEstudio nuevoPrograma = new ProgramaEstudio();
                    nuevoPrograma.setNombre(programaNombre);
                    nuevoPrograma.setFacultad(facultad);
                    nuevoPrograma.setDescripcion("Creado automáticamente desde importación");
                    return programaService.save(nuevoPrograma);
                });
    }

    private Usuario crearUsuario(String correo, String documento, String usuarioExcel) throws Exception {
        if (documento == null || documento.trim().isEmpty()) {
            throw new Exception("No se puede crear usuario sin documento");
        }

        String username = null;

        if (usuarioExcel != null && !usuarioExcel.trim().isEmpty()) {
            username = usuarioExcel.trim();
        }
        else if (correo != null && !correo.trim().isEmpty()) {
            username = correo.trim();
        }
        else {
            username = documento.trim();
        }

        log.info("Creando usuario con username: {}", username);

        try {
            Optional<Usuario> usuarioExistente = usuarioRepository.findOneByUser(username);
            if (usuarioExistente.isPresent()) {
                log.warn("Usuario {} ya existe, reutilizando", username);
                return usuarioExistente.get();
            }

            Usuario nuevoUsuario = new Usuario();
            nuevoUsuario.setUser(username);
            nuevoUsuario.setClave(passwordEncoder.encode(documento));
            nuevoUsuario.setEstado("ACTIVO");
            nuevoUsuario = usuarioRepository.save(nuevoUsuario);

            Rol rolIntegrante = rolService.getByNombre(Rol.RolNombre.INTEGRANTE)
                    .orElseThrow(() -> new Exception("Rol INTEGRANTE no encontrado"));

            UsuarioRol usuarioRol = UsuarioRol.builder()
                    .usuario(nuevoUsuario)
                    .rol(rolIntegrante)
                    .build();
            usuarioRolService.save(usuarioRol);

            log.info("✅ Usuario creado: {} con contraseña: {}", username, documento);
            return nuevoUsuario;

        } catch (Exception e) {
            log.error("❌ Error creando usuario {}: {}", username, e.getMessage());
            throw new Exception("Error al crear usuario: " + e.getMessage());
        }
    }

    private LocalDate parseFechaNacimiento(Cell cell) {
        if (cell == null) return null;

        try {
            if (cell.getCellType() == CellType.NUMERIC && DateUtil.isCellDateFormatted(cell)) {
                Date date = cell.getDateCellValue();
                return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            }

            if (cell.getCellType() == CellType.STRING) {
                String dateStr = cell.getStringCellValue().trim();

                for (DateTimeFormatter formatter : DATE_FORMATTERS) {
                    try {
                        return LocalDate.parse(dateStr, formatter);
                    } catch (DateTimeParseException e) {
                    }
                }

                log.warn("No se pudo parsear la fecha de nacimiento: {}", dateStr);
            }
        } catch (Exception e) {
            log.error("Error parseando fecha de nacimiento: {}", e.getMessage());
        }

        return null;
    }

    private LocalDateTime parseFechaMatriculaConHora(Cell cell) {
        if (cell == null) return LocalDateTime.now();

        try {
            if (cell.getCellType() == CellType.NUMERIC && DateUtil.isCellDateFormatted(cell)) {
                Date date = cell.getDateCellValue();
                return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
            }

            if (cell.getCellType() == CellType.STRING) {
                String dateTimeStr = cell.getStringCellValue().trim();

                dateTimeStr = dateTimeStr.replaceAll("\\s+", " ");
                dateTimeStr = dateTimeStr.replace("p. m.", "PM")
                        .replace("a. m.", "AM")
                        .replace("p.m.", "PM")
                        .replace("a.m.", "AM");

                for (DateTimeFormatter formatter : DATE_TIME_FORMATTERS) {
                    try {
                        return LocalDateTime.parse(dateTimeStr, formatter);
                    } catch (DateTimeParseException e) {
                    }
                }

                for (DateTimeFormatter formatter : DATE_FORMATTERS) {
                    try {
                        LocalDate date = LocalDate.parse(dateTimeStr, formatter);
                        return date.atStartOfDay();
                    } catch (DateTimeParseException e) {
                    }
                }

                log.warn("No se pudo parsear la fecha de matrícula: {}", dateTimeStr);
            }
        } catch (Exception e) {
            log.error("Error parseando fecha de matrícula: {}", e.getMessage());
        }

        return LocalDateTime.now();
    }

    private String getCellValueAsString(Cell cell) {
        if (cell == null) return null;

        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue().trim();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getLocalDateTimeCellValue().toLocalDate().toString();
                }
                return String.valueOf((long) cell.getNumericCellValue());
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                return cell.getCellFormula();
            default:
                return null;
        }
    }

    @Autowired
    private ExcelExportService excelExportService;

    @Override
    public byte[] exportarMatriculasAExcel(Long sedeId, Long facultadId, Long programaId,
                                           Long periodoId, TipoPersona tipoPersona) throws Exception {
        List<Matricula> matriculas = findByFiltros(sedeId, facultadId, programaId, periodoId, tipoPersona);

        if (matriculas.isEmpty()) {
            throw new Exception("No hay registros para exportar con los filtros aplicados");
        }

        log.info("Exportando {} matrículas a Excel (Periodo: {})", matriculas.size(), periodoId);
        return excelExportService.exportarMatriculasAExcel(matriculas);
    }

    @Override
    public byte[] descargarPlantilla() throws Exception {
        try {
            log.info("Generando plantilla de importación SIN PERIODO...");

            Workbook workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet("Plantilla_Importacion");

            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerFont.setColor(IndexedColors.WHITE.getIndex());
            headerStyle.setFont(headerFont);
            headerStyle.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            headerStyle.setAlignment(HorizontalAlignment.CENTER);

            CellStyle exampleStyle = workbook.createCellStyle();
            exampleStyle.setFillForegroundColor(IndexedColors.LIGHT_YELLOW.getIndex());
            exampleStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            // ✅ ENCABEZADOS SIN PERIODO
            String[] headers = {
                    "MODO_CONTRATO", "MODALIDAD_ESTUDIO", "SEDE", "FACULTAD", "PROGRAMA_ESTUDIO",
                    "CICLO", "GRUPO", "ID_PERSONA", "CODIGO_ESTUDIANTE", "NOMBRE_COMPLETO",
                    "DOCUMENTO", "CORREO", "USUARIO", "CORREO_INSTITUCIONAL", "CELULAR",
                    "PAIS", "FOTO", "RELIGION", "FECHA_NACIMIENTO", "FECHA_MATRICULA"
            };

            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            // ✅ DATOS DE EJEMPLO SIN PERIODO
            String[][] examples = {
                    {
                            "Nuevo Ingreso", "Presencial", "Filial Juliaca",
                            "Facultad de Ingeniería y Arquitectura", "EP Ingeniería de Sistemas",
                            "2024-I", "A", "1001", "20240001", "EJEMPLO NOMBRE APELLIDO",
                            "87654321", "ejemplo@email.com", "usuario123", "20240001@upeu.edu.pe",
                            "987654321", "Perú", "foto.jpg", "Católico", "15/05/2000", "07/08/2024 10:00 AM"
                    },
                    {
                            "Continua", "Virtual", "Filial Juliaca",
                            "Facultad de Ingeniería y Arquitectura", "EP Ingeniería de Sistemas",
                            "2024-I", "B", "1002", "20240002", "OTRO EJEMPLO NOMBRE",
                            "87654322", "otro@email.com", "usuario456", "20240002@upeu.edu.pe",
                            "987654322", "Perú", "foto2.jpg", "Cristiano", "20/08/1999", "07/08/2024 11:30 AM"
                    }
            };

            for (int i = 0; i < examples.length; i++) {
                Row exampleRow = sheet.createRow(i + 1);
                for (int j = 0; j < examples[i].length; j++) {
                    Cell cell = exampleRow.createCell(j);
                    cell.setCellValue(examples[i][j]);
                    if (i == 0) {
                        cell.setCellStyle(exampleStyle);
                    }
                }
            }

            // Autoajustar columnas
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            // Agregar notas informativas
            Row noteRow1 = sheet.createRow(examples.length + 2);
            Cell noteCell1 = noteRow1.createCell(0);
            noteCell1.setCellValue("INSTRUCCIONES:");

            Row noteRow2 = sheet.createRow(examples.length + 3);
            Cell noteCell2 = noteRow2.createCell(0);
            noteCell2.setCellValue("1. Complete los datos respetando el formato de las columnas");

            Row noteRow3 = sheet.createRow(examples.length + 4);
            Cell noteCell3 = noteRow3.createCell(0);
            noteCell3.setCellValue("2. El PERIODO se seleccionará desde el filtro de importación");

            Row noteRow4 = sheet.createRow(examples.length + 5);
            Cell noteCell4 = noteRow4.createCell(0);
            noteCell4.setCellValue("3. El documento debe ser único para cada persona");

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            workbook.close();

            log.info("Plantilla SIN PERIODO generada exitosamente");
            return outputStream.toByteArray();

        } catch (Exception e) {
            log.error("Error generando plantilla: {}", e.getMessage());
            throw new Exception("Error al generar la plantilla: " + e.getMessage());
        }
    }
}