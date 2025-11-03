/*package pe.edu.upeu.sysasistencia.control;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import pe.edu.upeu.sysasistencia.dtos.UsuarioDTO;
import pe.edu.upeu.sysasistencia.modelo.Persona;
import pe.edu.upeu.sysasistencia.modelo.TipoPersona;

import java.time.LocalDate;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Slf4j
public class PersonaControllerIntegrateTest {

    @Autowired
    private ObjectMapper objectMapper;

    @LocalServerPort
    private int port;

    private String token;
    private String idCreado;

    @BeforeEach
    public void setUp() {
        RestAssured.port = this.port;

        UsuarioDTO.UsuarioCrearDto udto = new UsuarioDTO.UsuarioCrearDto(
                "admin@upeu.edu.pe",
                "Admin123*".toCharArray(),
                "ADMIN",
                "Activo"
        );

        try {
            token = given()
                    .contentType(ContentType.JSON)
                    .body(new UsuarioDTO.CredencialesDto(
                            "admin@upeu.edu.pe",
                            "Admin123*".toCharArray()
                    ))
                    .when().post("/users/login")
                    .andReturn().jsonPath().getString("token");
        } catch (Exception e) {
            if (token == null) {
                token = given()
                        .contentType(ContentType.JSON)
                        .body(udto)
                        .when().post("/users/register")
                        .andReturn().jsonPath().getString("token");
            }
            System.out.println("Token obtenido: " + token);
        }
    }

    @Order(2)
    @Test
    public void testListPersona() throws Exception {
        given()
                .accept(ContentType.JSON)
                .header("Authorization", "Bearer " + token)
                .when()
                .get("/personas")
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_OK)
                .contentType(ContentType.JSON);
    }

    @Order(1)
    @Test
    public void testCrearPersona() throws Exception {
        // Crear persona de prueba
        Persona dto = Persona.builder()
                .codigoEstudiante("TEST" + System.currentTimeMillis())
                .nombreCompleto("Test Persona Integration")
                .documento("DNI" + System.currentTimeMillis())
                .correo("test.integration@test.com")
                .correoInstitucional("test.integration@upeu.edu.pe")
                .celular("987654321")
                .pais("Perú")
                .religion("Adventista")
                .fechaNacimiento(LocalDate.of(2000, 1, 1))
                .tipoPersona(TipoPersona.ESTUDIANTE)
                .build();

        given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + token)
                .body(objectMapper.writeValueAsString(dto))
                .when()
                .post("/personas")
                .then()
                .statusCode(HttpStatus.SC_CREATED)
                .body("nombreCompleto", equalTo("Test Persona Integration"));
    }

    @Order(3)
    @Test
    void testFindById() {
        // Primero, crear una persona y obtener su ID
        Persona persona = Persona.builder()
                .codigoEstudiante("FIND" + System.currentTimeMillis())
                .nombreCompleto("Persona Find Test")
                .documento("DOC" + System.currentTimeMillis())
                .correo("find@test.com")
                .celular("999888777")
                .pais("Perú")
                .fechaNacimiento(LocalDate.of(1998, 5, 10))
                .tipoPersona(TipoPersona.ESTUDIANTE)
                .build();

        try {
            String personaJson = objectMapper.writeValueAsString(persona);

            String id = given()
                    .contentType(ContentType.JSON)
                    .header("Authorization", "Bearer " + token)
                    .body(personaJson)
                    .when()
                    .post("/personas")
                    .then()
                    .statusCode(HttpStatus.SC_CREATED)
                    .extract().body().jsonPath().getString("idPersona");

            // Buscar por ID
            given()
                    .contentType(ContentType.JSON)
                    .header("Authorization", "Bearer " + token)
                    .when()
                    .get("/personas/{id}", id)
                    .then()
                    .statusCode(HttpStatus.SC_OK)
                    .body("nombreCompleto", equalTo("Persona Find Test"));

        } catch (Exception e) {
            log.error("Error en testFindById: {}", e.getMessage());
        }
    }

    @Order(4)
    @Test
    void testUpdate() {
        // Crear persona
        Persona persona = Persona.builder()
                .codigoEstudiante("UPD" + System.currentTimeMillis())
                .nombreCompleto("Persona Original")
                .documento("DOCUPD" + System.currentTimeMillis())
                .correo("update@test.com")
                .celular("911222333")
                .pais("Perú")
                .fechaNacimiento(LocalDate.of(1997, 8, 20))
                .tipoPersona(TipoPersona.ESTUDIANTE)
                .build();

        try {
            String id = given()
                    .contentType(ContentType.JSON)
                    .header("Authorization", "Bearer " + token)
                    .body(objectMapper.writeValueAsString(persona))
                    .when()
                    .post("/personas")
                    .then()
                    .statusCode(HttpStatus.SC_CREATED)
                    .extract().body().jsonPath().getString("idPersona");

            // Actualizar
            persona.setNombreCompleto("Persona Actualizada");
            persona.setCelular("955666777");

            given()
                    .contentType(ContentType.JSON)
                    .header("Authorization", "Bearer " + token)
                    .body(objectMapper.writeValueAsString(persona))
                    .when()
                    .put("/personas/{id}", id)
                    .then()
                    .statusCode(HttpStatus.SC_OK)
                    .body("nombreCompleto", equalTo("Persona Actualizada"))
                    .body("celular", equalTo("955666777"));

        } catch (Exception e) {
            log.error("Error en testUpdate: {}", e.getMessage());
        }
    }

    @Order(6)
    @Test
    void testDelete() {
        // Crear persona para eliminar
        Persona persona = Persona.builder()
                .codigoEstudiante("DEL" + System.currentTimeMillis())
                .nombreCompleto("Persona Delete Test")
                .documento("DOCDEL" + System.currentTimeMillis())
                .correo("delete@test.com")
                .celular("944555666")
                .pais("Perú")
                .fechaNacimiento(LocalDate.of(1996, 12, 25))
                .tipoPersona(TipoPersona.ESTUDIANTE)
                .build();

        try {
            String id = given()
                    .contentType(ContentType.JSON)
                    .header("Authorization", "Bearer " + token)
                    .body(objectMapper.writeValueAsString(persona))
                    .when()
                    .post("/personas")
                    .then()
                    .statusCode(HttpStatus.SC_CREATED)
                    .extract().body().jsonPath().getString("idPersona");

            // Eliminar
            given()
                    .contentType(ContentType.JSON)
                    .header("Authorization", "Bearer " + token)
                    .when()
                    .delete("/personas/{id}", id)
                    .then()
                    .statusCode(HttpStatus.SC_OK)
                    .body("message", equalTo("true"));

        } catch (Exception e) {
            log.error("Error en testDelete: {}", e.getMessage());
        }
    }

    @Order(5)
    @Test
    void testFindByCodigo() {
        String codigoUnico = "CODIGO" + System.currentTimeMillis();

        // Crear persona con código específico
        Persona persona = Persona.builder()
                .codigoEstudiante(codigoUnico)
                .nombreCompleto("Persona Buscar Codigo")
                .documento("CODOC" + System.currentTimeMillis())
                .correo("codigo@test.com")
                .celular("933444555")
                .pais("Perú")
                .fechaNacimiento(LocalDate.of(1999, 3, 15))
                .tipoPersona(TipoPersona.ESTUDIANTE)
                .build();

        try {
            given()
                    .contentType(ContentType.JSON)
                    .header("Authorization", "Bearer " + token)
                    .body(objectMapper.writeValueAsString(persona))
                    .when()
                    .post("/personas")
                    .then()
                    .statusCode(HttpStatus.SC_CREATED);

            // Buscar por código
            given()
                    .contentType(ContentType.JSON)
                    .header("Authorization", "Bearer " + token)
                    .when()
                    .get("/personas/codigo/{codigo}", codigoUnico)
                    .then()
                    .statusCode(HttpStatus.SC_OK)
                    .body("codigoEstudiante", equalTo(codigoUnico))
                    .body("nombreCompleto", equalTo("Persona Buscar Codigo"));

        } catch (Exception e) {
            log.error("Error en testFindByCodigo: {}", e.getMessage());
        }
    }

    @Order(7)
    @Test
    void testCrearPersonaInvitado() throws Exception {
        // Crear persona tipo INVITADO
        Persona invitado = Persona.builder()
                .nombreCompleto("Invitado Test")
                .documento("INVI" + System.currentTimeMillis())
                .correo("invitado@test.com")
                .celular("922333444")
                .pais("Chile")
                .fechaNacimiento(LocalDate.of(1985, 7, 10))
                .tipoPersona(TipoPersona.INVITADO)
                .build();

        given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + token)
                .body(objectMapper.writeValueAsString(invitado))
                .when()
                .post("/personas")
                .then()
                .statusCode(HttpStatus.SC_CREATED)
                .body("nombreCompleto", equalTo("Invitado Test"))
                .body("tipoPersona", equalTo("INVITADO"));
    }
}*/