/*package pe.edu.upeu.sysasistencia.control;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.upeu.sysasistencia.dtos.UsuarioDTO;
import pe.edu.upeu.sysasistencia.dtos.PersonaDTO;
import pe.edu.upeu.sysasistencia.modelo.TipoPersona;

import java.time.LocalDate;
import java.util.logging.Level;
import java.util.logging.Logger;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
public class PersonaControllerWebTestClientTest {

    @LocalServerPort
    private int port;

    // <- NO autowire aquí. Lo creamos manualmente:
    private WebTestClient webTestClient;

    private String token;
    private final Logger logger = Logger.getLogger(PersonaControllerWebTestClientTest.class.getName());
    private PersonaDTO persona;
    private Long idx;

    @BeforeEach
    public void setUp() {
        // Inicializa WebTestClient apuntando al servidor de prueba
        this.webTestClient = WebTestClient.bindToServer()
                .baseUrl("http://localhost:" + this.port)
                .build();

        System.out.println("Puerto: " + this.port);

        UsuarioDTO.UsuarioCrearDto udto = new UsuarioDTO.UsuarioCrearDto(
                "admin@upeu.edu.pe",
                "Admin123*".toCharArray(),
                "ADMIN",
                "Activo"
        );

        try {
            var dd = webTestClient.post()
                    .uri("/users/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(new UsuarioDTO.CredencialesDto(
                            "admin@upeu.edu.pe",
                            "Admin123*".toCharArray()))
                    .exchange()
                    .expectBody(String.class)
                    .returnResult()
                    .getResponseBody();

            JSONObject jsonObj = new JSONObject(dd);
            if (jsonObj.has("token")) {
                token = jsonObj.getString("token");
            }
        } catch (JSONException e) {
            System.out.println("Error al obtener token: " + e.getMessage());
            if (token == null) {
                webTestClient.post()
                        .uri("/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(udto)
                        .exchange()
                        .expectStatus().isCreated()
                        .expectBody(String.class)
                        .value(tokenx -> {
                            try {
                                JSONObject jsonObjx = new JSONObject(tokenx);
                                if (jsonObjx.has("token")) {
                                    token = jsonObjx.getString("token");
                                }
                            } catch (JSONException ex) {
                                logger.log(Level.SEVERE, null, ex);
                            }
                        });
            }
        }
    }

    private PersonaDTO crearPersonaDTO(String nombre) {
        PersonaDTO dto = new PersonaDTO();
        dto.setCodigoEstudiante("COD" + System.currentTimeMillis());
        dto.setNombreCompleto(nombre);
        dto.setDocumento("DOC" + System.currentTimeMillis());
        dto.setCorreo(nombre.toLowerCase().replace(" ", ".") + "@test.com");
        dto.setCorreoInstitucional(nombre.toLowerCase().replace(" ", ".") + "@upeu.edu.pe");
        dto.setCelular("9" + (System.currentTimeMillis() % 100000000));
        dto.setPais("Perú");
        dto.setReligion("Adventista");
        dto.setFechaNacimiento(LocalDate.of(2000, 1, 1));
        dto.setTipoPersona(TipoPersona.ESTUDIANTE);
        return dto;
    }

    @Test
    @Order(1)
    public void testListarPersonas() {
        webTestClient.get().uri("/personas")
                .header("Authorization", "Bearer " + token)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$").isArray();
    }

    @Transactional
    @Test
    @Order(2)
    public void testGuardarPersona() {
        PersonaDTO dto = crearPersonaDTO("Persona WebFlux Test");
        try {
            var result = webTestClient.post()
                    .uri("/personas")
                    .header("Authorization", "Bearer " + token)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(dto)
                    .exchange()
                    .expectStatus().isCreated()
                    .expectBody(String.class)
                    .returnResult()
                    .getResponseBody();

            JSONObject json = new JSONObject(result);
            idx = json.getLong("idPersona");
            Assertions.assertNotNull(idx);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error al guardar persona", e);
            Assertions.fail(e);
        }
    }

    @Transactional
    @Test
    @Order(3)
    public void testActualizarPersona() {
        PersonaDTO dto = crearPersonaDTO("Persona Original");
        try {
            var result = webTestClient.post()
                    .uri("/personas")
                    .header("Authorization", "Bearer " + token)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(dto)
                    .exchange()
                    .expectStatus().isCreated()
                    .expectBody(String.class)
                    .returnResult()
                    .getResponseBody();

            JSONObject json = new JSONObject(result);
            Long idPersona = json.getLong("idPersona");

            PersonaDTO actualizado = new PersonaDTO();
            actualizado.setNombreCompleto("Persona Actualizada");
            actualizado.setCelular("988777666");

            webTestClient.put()
                    .uri("/personas/{id}", idPersona)
                    .header("Authorization", "Bearer " + token)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(actualizado)
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody()
                    .jsonPath("$.nombreCompleto").isEqualTo("Persona Actualizada")
                    .jsonPath("$.celular").isEqualTo("988777666");
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error en actualización", e);
            Assertions.fail(e);
        }
    }

    @Test
    @Order(4)
    public void testBuscarPersonaPorCodigo() {
        String codigo = "COD" + System.currentTimeMillis();
        PersonaDTO dto = crearPersonaDTO("BuscarCodigo");
        dto.setCodigoEstudiante(codigo);

        webTestClient.post()
                .uri("/personas")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(dto)
                .exchange()
                .expectStatus().isCreated();

        webTestClient.get()
                .uri("/personas/codigo/{codigo}", codigo)
                .header("Authorization", "Bearer " + token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.codigoEstudiante").isEqualTo(codigo)
                .jsonPath("$.nombreCompleto").isEqualTo("BuscarCodigo");
    }

    @Test
    @Order(5)
    public void testEliminarPersona() {
        PersonaDTO dto = crearPersonaDTO("EliminarPersona");
        try {
            var result = webTestClient.post()
                    .uri("/personas")
                    .header("Authorization", "Bearer " + token)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(dto)
                    .exchange()
                    .expectStatus().isCreated()
                    .expectBody(String.class)
                    .returnResult()
                    .getResponseBody();

            JSONObject json = new JSONObject(result);
            Long id = json.getLong("idPersona");

            webTestClient.delete()
                    .uri("/personas/{id}", id)
                    .header("Authorization", "Bearer " + token)
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody()
                    .jsonPath("$.message").isEqualTo("true");

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error al eliminar persona", e);
            Assertions.fail(e);
        }
    }
}
*/