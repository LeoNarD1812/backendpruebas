/*package pe.edu.upeu.sysasistencia.servicio;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pe.edu.upeu.sysasistencia.excepciones.CustomResponse;
import pe.edu.upeu.sysasistencia.excepciones.ModelNotFoundException;
import pe.edu.upeu.sysasistencia.modelo.Persona;
import pe.edu.upeu.sysasistencia.modelo.TipoPersona;
import pe.edu.upeu.sysasistencia.modelo.Usuario;
import pe.edu.upeu.sysasistencia.repositorio.IPersonaRepository;
import pe.edu.upeu.sysasistencia.servicio.impl.PersonaServiceImp;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class PersonaServiceTest {

    @Mock
    private IPersonaRepository repo;

    @InjectMocks
    private PersonaServiceImp personaService;

    private Persona persona;
    private Usuario usuario;

    @BeforeEach
    public void setUp() {
        // Crear usuario mock
        usuario = Usuario.builder()
                .idUsuario(1L)
                .user("juan.perez@upeu.edu.pe")
                .clave("$2a$10$encrypted")
                .estado("ACTIVO")
                .build();

        // Crear persona de prueba
        persona = Persona.builder()
                .idPersona(1L)
                .codigoEstudiante("2024001")
                .nombreCompleto("Juan Pérez García")
                .documento("75123456")
                .correo("juan.perez@test.com")
                .correoInstitucional("juan.perez@upeu.edu.pe")
                .celular("987654321")
                .pais("Perú")
                .foto("https://example.com/foto.jpg")
                .religion("Adventista")
                .fechaNacimiento(LocalDate.of(2000, 5, 15))
                .tipoPersona(TipoPersona.ESTUDIANTE)
                .usuario(usuario)
                .build();
    }

    @Order(1)
    @DisplayName("Test: Guardar Persona")
    @Test
    public void testSavePersona() {
        // given
        given(repo.save(persona)).willReturn(persona);

        // when
        Persona personaGuardada = personaService.save(persona);

        // then
        Assertions.assertThat(personaGuardada.getNombreCompleto()).isNotNull();
        Assertions.assertThat(personaGuardada.getNombreCompleto()).isEqualTo(persona.getNombreCompleto());
        Assertions.assertThat(personaGuardada.getDocumento()).isEqualTo("75123456");
        Assertions.assertThat(personaGuardada.getTipoPersona()).isEqualTo(TipoPersona.ESTUDIANTE);
    }

    @Order(2)
    @DisplayName("Test: Listar Personas")
    @Test
    public void testListPersona() {
        // given
        Persona persona2 = Persona.builder()
                .idPersona(2L)
                .codigoEstudiante("2024002")
                .nombreCompleto("María López Torres")
                .documento("75987654")
                .correo("maria.lopez@test.com")
                .correoInstitucional("maria.lopez@upeu.edu.pe")
                .celular("912345678")
                .pais("Perú")
                .religion("Adventista")
                .fechaNacimiento(LocalDate.of(1999, 8, 20))
                .tipoPersona(TipoPersona.ESTUDIANTE)
                .build();

        given(repo.findAll()).willReturn(List.of(persona, persona2));

        // when
        List<Persona> listaPersonas = personaService.findAll();

        for (Persona p : listaPersonas) {
            System.out.println(p.getNombreCompleto() + " - " + p.getDocumento());
        }

        // then
        Assertions.assertThat(listaPersonas).hasSize(2);
        Assertions.assertThat(listaPersonas.get(0)).isEqualTo(persona);
        Assertions.assertThat(listaPersonas.size()).isEqualTo(2);
    }

    @Order(3)
    @DisplayName("Test: Buscar Persona por ID")
    @Test
    public void testFindByIdPersona() {
        // given
        given(repo.findById(1L)).willReturn(Optional.of(persona));

        // when
        Persona encontrada = personaService.findById(1L);

        // then
        Assertions.assertThat(encontrada).isNotNull();
        Assertions.assertThat(encontrada.getIdPersona()).isEqualTo(1L);
        Assertions.assertThat(encontrada.getNombreCompleto()).isEqualTo("Juan Pérez García");
    }

    @Order(4)
    @DisplayName("Test: Buscar Persona por Código Estudiante")
    @Test
    public void testFindByCodigoEstudiante() {
        // given
        given(repo.findByCodigoEstudiante("2024001")).willReturn(Optional.of(persona));

        // when
        Optional<Persona> encontrada = personaService.findByCodigoEstudiante("2024001");

        // then
        Assertions.assertThat(encontrada).isPresent();
        Assertions.assertThat(encontrada.get().getNombreCompleto()).isEqualTo("Juan Pérez García");
        Assertions.assertThat(encontrada.get().getDocumento()).isEqualTo("75123456");
    }

    @Order(5)
    @DisplayName("Test: Buscar Persona por Documento")
    @Test
    public void testFindByDocumento() {
        // given
        given(repo.findByDocumento("75123456")).willReturn(Optional.of(persona));

        // when
        Optional<Persona> encontrada = personaService.findByDocumento("75123456");

        // then
        Assertions.assertThat(encontrada).isPresent();
        Assertions.assertThat(encontrada.get().getNombreCompleto()).isEqualTo("Juan Pérez García");
        Assertions.assertThat(encontrada.get().getCodigoEstudiante()).isEqualTo("2024001");
    }

    @Order(6)
    @DisplayName("Test: Actualizar Persona")
    @Test
    public void testUpdatePersona() {
        // given
        given(repo.save(persona)).willReturn(persona);
        given(repo.findById(1L)).willReturn(Optional.of(persona));

        // when
        persona.setNombreCompleto("Juan Carlos Pérez García");
        persona.setCelular("999888777");
        Persona actualizada = personaService.update(persona.getIdPersona(), persona);

        // then
        System.out.println("Nombre actualizado: " + actualizada.getNombreCompleto());
        System.out.println("Celular actualizado: " + actualizada.getCelular());

        Assertions.assertThat(actualizada.getNombreCompleto()).isEqualTo("Juan Carlos Pérez García");
        Assertions.assertThat(actualizada.getCelular()).isEqualTo("999888777");
    }

    @Order(7)
    @DisplayName("Test: Eliminar Persona")
    @Test
    public void testDeletePersona() {
        // given
        given(repo.findById(1L)).willReturn(Optional.of(persona));
        willDoNothing().given(repo).deleteById(1L);

        // when
        CustomResponse response = personaService.delete(1L);

        // then
        System.out.println("Respuesta: " + response.getMessage());
        Assertions.assertThat(response.getMessage()).isEqualTo("true");
        Assertions.assertThat(response.getStatusCode()).isEqualTo(200);
    }

    @Order(8)
    @DisplayName("Test: Eliminar Persona - ID no Existe")
    @Test
    void testDeleteByIdNonExistent() {
        // given
        Long idInexistente = 99L;
        given(repo.findById(idInexistente)).willReturn(Optional.empty());

        // when and then
        Assertions.assertThatThrownBy(() ->
                        personaService.delete(idInexistente))
                .isInstanceOf(ModelNotFoundException.class)
                .hasMessageContaining("ID NOT FOUND: " + idInexistente);
    }

    @Order(9)
    @DisplayName("Test: Buscar Persona por ID Inexistente")
    @Test
    void testFindByIdNonExistent() {
        // given
        Long idInexistente = 99L;
        given(repo.findById(idInexistente)).willReturn(Optional.empty());

        // when and then
        Assertions.assertThatThrownBy(() ->
                        personaService.findById(idInexistente))
                .isInstanceOf(ModelNotFoundException.class)
                .hasMessageContaining("ID NOT FOUND: " + idInexistente);
    }

    @Order(10)
    @DisplayName("Test: Crear Persona Tipo INVITADO")
    @Test
    public void testSavePersonaInvitado() {
        // given
        Persona invitado = Persona.builder()
                .idPersona(2L)
                .nombreCompleto("Carlos Mendoza Quispe")
                .documento("70555666")
                .correo("carlos.mendoza@gmail.com")
                .celular("955666777")
                .pais("Perú")
                .fechaNacimiento(LocalDate.of(1985, 3, 10))
                .tipoPersona(TipoPersona.INVITADO)
                .build();

        given(repo.save(invitado)).willReturn(invitado);

        // when
        Persona guardado = personaService.save(invitado);

        // then
        Assertions.assertThat(guardado.getTipoPersona()).isEqualTo(TipoPersona.INVITADO);
        Assertions.assertThat(guardado.getCodigoEstudiante()).isNull();
        Assertions.assertThat(guardado.getNombreCompleto()).isEqualTo("Carlos Mendoza Quispe");
    }

    @Order(11)
    @DisplayName("Test: Buscar Persona por Código Inexistente")
    @Test
    public void testFindByCodigoEstudianteNonExistent() {
        // given
        String codigoInexistente = "9999999";
        given(repo.findByCodigoEstudiante(codigoInexistente)).willReturn(Optional.empty());

        // when
        Optional<Persona> resultado = personaService.findByCodigoEstudiante(codigoInexistente);

        // then
        Assertions.assertThat(resultado).isEmpty();
    }

    @Order(12)
    @DisplayName("Test: Buscar Persona por Documento Inexistente")
    @Test
    public void testFindByDocumentoNonExistent() {
        // given
        String documentoInexistente = "99999999";
        given(repo.findByDocumento(documentoInexistente)).willReturn(Optional.empty());

        // when
        Optional<Persona> resultado = personaService.findByDocumento(documentoInexistente);

        // then
        Assertions.assertThat(resultado).isEmpty();
    }
}*/