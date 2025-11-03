/*package pe.edu.upeu.sysasistencia.control;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import pe.edu.upeu.sysasistencia.dtos.PersonaDTO;
import pe.edu.upeu.sysasistencia.excepciones.CustomResponse;
import pe.edu.upeu.sysasistencia.mappers.PersonaMapper;
import pe.edu.upeu.sysasistencia.modelo.Persona;
import pe.edu.upeu.sysasistencia.modelo.TipoPersona;
import pe.edu.upeu.sysasistencia.modelo.Usuario;
import pe.edu.upeu.sysasistencia.servicio.IPersonaService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.logging.Logger;

@ExtendWith(MockitoExtension.class)
public class PersonaControllerTest {

    @Mock
    private IPersonaService personaService;

    @Mock
    private PersonaMapper personaMapper;

    @InjectMocks
    private PersonaController personaController;

    private Persona persona;
    private PersonaDTO personaDTO;
    private Usuario usuario;
    private static final Logger logger = Logger.getLogger(PersonaControllerTest.class.getName());

    List<Persona> personas;

    @BeforeEach
    void setUp() {
        // Crear usuario
        usuario = Usuario.builder()
                .idUsuario(1L)
                .user("juan.perez@upeu.edu.pe")
                .clave("$2a$10$encrypted")
                .estado("ACTIVO")
                .build();

        // Crear persona
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

        // Crear personaDTO
        personaDTO = PersonaDTO.builder()
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
                .usuarioId(1L)
                .build();

        personas = List.of(persona);
    }

    @Test
    public void testFindAll_ReturnsListOfPersonaDTO_WithHttpStatusOK() {
        // given
        BDDMockito.given(personaService.findAll()).willReturn(personas);
        BDDMockito.given(personaMapper.toDTOs(personas)).willReturn(List.of(personaDTO));

        // when
        ResponseEntity<List<PersonaDTO>> response = personaController.findAll();

        // then
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertNotNull(response.getBody());
        Assertions.assertEquals(1, response.getBody().size());
        Assertions.assertEquals(List.of(personaDTO), response.getBody());

        for (PersonaDTO p : response.getBody()) {
            logger.info(String.format("PersonaDTO{id=%d, nombre='%s', documento='%s', tipo=%s}",
                    p.getIdPersona(), p.getNombreCompleto(), p.getDocumento(), p.getTipoPersona()));
        }

        BDDMockito.then(personaService).should().findAll();
        BDDMockito.then(personaMapper).should().toDTOs(personas);
    }

    @Test
    void testFindById_ReturnsPersonaDTO_WithHttpStatusOK() {
        // given
        Long id = 1L;
        BDDMockito.given(personaService.findById(id)).willReturn(persona);
        BDDMockito.given(personaMapper.toDTO(persona)).willReturn(personaDTO);

        // when
        ResponseEntity<PersonaDTO> response = personaController.findById(id);

        // then
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertEquals(personaDTO, response.getBody());
        Assertions.assertEquals("Juan Pérez García", response.getBody().getNombreCompleto());
        Assertions.assertEquals("75123456", response.getBody().getDocumento());

        BDDMockito.then(personaService).should().findById(id);
        BDDMockito.then(personaMapper).should().toDTO(persona);
    }

    @Test
    void testFindByCodigo_ReturnsPersonaDTO_WithHttpStatusOK() {
        // given
        String codigo = "2024001";
        BDDMockito.given(personaService.findByCodigoEstudiante(codigo))
                .willReturn(java.util.Optional.of(persona));
        BDDMockito.given(personaMapper.toDTO(persona)).willReturn(personaDTO);

        // when
        ResponseEntity<PersonaDTO> response = personaController.findByCodigo(codigo);

        // then
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertEquals(personaDTO, response.getBody());
        Assertions.assertEquals(codigo, response.getBody().getCodigoEstudiante());

        BDDMockito.then(personaService).should().findByCodigoEstudiante(codigo);
        BDDMockito.then(personaMapper).should().toDTO(persona);
    }

    @Test
    void testSave_ReturnsCreatedStatusAndPersonaDTO() {
        // given
        BDDMockito.given(personaMapper.toEntity(personaDTO)).willReturn(persona);
        BDDMockito.given(personaService.save(persona)).willReturn(persona);
        BDDMockito.given(personaMapper.toDTO(persona)).willReturn(personaDTO);

        // when
        ResponseEntity<PersonaDTO> response = personaController.save(personaDTO);

        // then
        Assertions.assertEquals(HttpStatus.CREATED, response.getStatusCode());
        Assertions.assertNotNull(response.getBody());
        Assertions.assertEquals("Juan Pérez García", response.getBody().getNombreCompleto());
        Assertions.assertEquals("75123456", response.getBody().getDocumento());

        BDDMockito.then(personaMapper).should().toEntity(personaDTO);
        BDDMockito.then(personaService).should().save(persona);
        BDDMockito.then(personaMapper).should().toDTO(persona);
    }

    @Test
    void testUpdate_ReturnsUpdatedPersonaDTO_WithHttpStatusOK() {
        // given
        Long id = 1L;
        personaDTO.setIdPersona(id);
        personaDTO.setNombreCompleto("Juan Carlos Pérez García");
        personaDTO.setCelular("999888777");

        persona.setNombreCompleto("Juan Carlos Pérez García");
        persona.setCelular("999888777");

        BDDMockito.given(personaMapper.toEntity(personaDTO)).willReturn(persona);
        BDDMockito.given(personaService.update(id, persona)).willReturn(persona);
        BDDMockito.given(personaMapper.toDTO(persona)).willReturn(personaDTO);

        // when
        ResponseEntity<PersonaDTO> response = personaController.update(id, personaDTO);

        // then
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertEquals(personaDTO, response.getBody());
        Assertions.assertEquals("Juan Carlos Pérez García", response.getBody().getNombreCompleto());
        Assertions.assertEquals("999888777", response.getBody().getCelular());

        BDDMockito.then(personaMapper).should().toEntity(personaDTO);
        BDDMockito.then(personaService).should().update(id, persona);
        BDDMockito.then(personaMapper).should().toDTO(persona);
    }

    @Test
    void testDelete_ReturnsCustomResponse_WithHttpStatusOK() {
        // given
        Long id = 1L;
        CustomResponse customResponse = new CustomResponse(
                200,
                LocalDateTime.now(),
                "true",
                "Eliminado correctamente"
        );

        BDDMockito.given(personaService.delete(id)).willReturn(customResponse);

        // when
        ResponseEntity<CustomResponse> response = personaController.delete(id);

        // then
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertEquals(customResponse, response.getBody());
        Assertions.assertEquals("true", response.getBody().getMessage());
        Assertions.assertEquals(200, response.getBody().getStatusCode());

        BDDMockito.then(personaService).should().delete(id);
    }

    @Test
    void testSave_PersonaInvitado_ReturnsCreatedStatus() {
        // given - Crear persona invitado
        PersonaDTO invitadoDTO = PersonaDTO.builder()
                .nombreCompleto("Carlos Mendoza Quispe")
                .documento("70555666")
                .correo("carlos.mendoza@gmail.com")
                .celular("955666777")
                .pais("Perú")
                .fechaNacimiento(LocalDate.of(1985, 3, 10))
                .tipoPersona(TipoPersona.INVITADO)
                .build();

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

        invitadoDTO.setIdPersona(2L);

        BDDMockito.given(personaMapper.toEntity(invitadoDTO)).willReturn(invitado);
        BDDMockito.given(personaService.save(invitado)).willReturn(invitado);
        BDDMockito.given(personaMapper.toDTO(invitado)).willReturn(invitadoDTO);

        // when
        ResponseEntity<PersonaDTO> response = personaController.save(invitadoDTO);

        // then
        Assertions.assertEquals(HttpStatus.CREATED, response.getStatusCode());
        Assertions.assertEquals(TipoPersona.INVITADO, response.getBody().getTipoPersona());
        Assertions.assertNull(response.getBody().getCodigoEstudiante());
    }

    @Test
    void testFindAll_EmptyList_ReturnsEmptyListWithHttpStatusOK() {
        // given
        List<Persona> personasVacias = List.of();
        List<PersonaDTO> personasDTOVacias = List.of();

        BDDMockito.given(personaService.findAll()).willReturn(personasVacias);
        BDDMockito.given(personaMapper.toDTOs(personasVacias)).willReturn(personasDTOVacias);

        // when
        ResponseEntity<List<PersonaDTO>> response = personaController.findAll();

        // then
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertNotNull(response.getBody());
        Assertions.assertTrue(response.getBody().isEmpty());
    }
}*/