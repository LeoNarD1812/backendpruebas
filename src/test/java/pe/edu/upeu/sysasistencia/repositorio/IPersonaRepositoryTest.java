/*package pe.edu.upeu.sysasistencia.repositorio;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import pe.edu.upeu.sysasistencia.modelo.Persona;
import pe.edu.upeu.sysasistencia.modelo.TipoPersona;
import pe.edu.upeu.sysasistencia.modelo.Usuario;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class IPersonaRepositoryTest {

    @Autowired
    private IPersonaRepository personaRepository;

    @Autowired
    private IUsuarioRepository usuarioRepository;

    private static Long personaId;
    private static Usuario usuario;

    @BeforeEach
    public void setUp() {
        // Crear usuario para asociar con persona
        Usuario nuevoUsuario = new Usuario();
        nuevoUsuario.setUser("testuser" + System.currentTimeMillis());
        nuevoUsuario.setClave("$2a$10$test");
        nuevoUsuario.setEstado("ACTIVO");
        usuario = usuarioRepository.save(nuevoUsuario);

        // Crear persona de prueba
        Persona persona = new Persona();
        persona.setCodigoEstudiante("2024001");
        persona.setNombreCompleto("Juan Pérez García");
        persona.setDocumento("75123456");
        persona.setCorreo("juan.perez@test.com");
        persona.setCorreoInstitucional("juan.perez@upeu.edu.pe");
        persona.setCelular("987654321");
        persona.setPais("Perú");
        persona.setReligion("Adventista");
        persona.setFechaNacimiento(LocalDate.of(2000, 5, 15));
        persona.setTipoPersona(TipoPersona.ESTUDIANTE);
        persona.setUsuario(usuario);

        Persona guardada = personaRepository.save(persona);
        personaId = guardada.getIdPersona();
    }

    @Test
    @Order(1)
    @DisplayName("Test: Guardar Persona")
    public void testGuardarPersona() {
        // Crear nuevo usuario
        Usuario nuevoUsuario = new Usuario();
        nuevoUsuario.setUser("maria.lopez" + System.currentTimeMillis());
        nuevoUsuario.setClave("$2a$10$test2");
        nuevoUsuario.setEstado("ACTIVO");
        Usuario usuarioGuardado = usuarioRepository.save(nuevoUsuario);

        // Crear nueva persona
        Persona nuevaPersona = new Persona();
        nuevaPersona.setCodigoEstudiante("2024002");
        nuevaPersona.setNombreCompleto("María López Torres");
        nuevaPersona.setDocumento("75987654");
        nuevaPersona.setCorreo("maria.lopez@test.com");
        nuevaPersona.setCorreoInstitucional("maria.lopez@upeu.edu.pe");
        nuevaPersona.setCelular("912345678");
        nuevaPersona.setPais("Perú");
        nuevaPersona.setReligion("Adventista");
        nuevaPersona.setFechaNacimiento(LocalDate.of(1999, 8, 20));
        nuevaPersona.setTipoPersona(TipoPersona.ESTUDIANTE);
        nuevaPersona.setUsuario(usuarioGuardado);

        Persona guardada = personaRepository.save(nuevaPersona);

        assertNotNull(guardada.getIdPersona());
        assertEquals("María López Torres", guardada.getNombreCompleto());
        assertEquals("75987654", guardada.getDocumento());
        assertEquals(TipoPersona.ESTUDIANTE, guardada.getTipoPersona());
    }

    @Test
    @Order(2)
    @DisplayName("Test: Buscar Persona por ID")
    public void testBuscarPorId() {
        Optional<Persona> persona = personaRepository.findById(personaId);

        assertTrue(persona.isPresent());
        assertEquals("Juan Pérez García", persona.get().getNombreCompleto());
        assertEquals("75123456", persona.get().getDocumento());
        assertEquals("2024001", persona.get().getCodigoEstudiante());
    }

    @Test
    @Order(3)
    @DisplayName("Test: Buscar Persona por Código Estudiante")
    public void testBuscarPorCodigoEstudiante() {
        Optional<Persona> persona = personaRepository.findByCodigoEstudiante("2024001");

        assertTrue(persona.isPresent());
        assertEquals("Juan Pérez García", persona.get().getNombreCompleto());
        assertEquals("75123456", persona.get().getDocumento());
    }

    @Test
    @Order(4)
    @DisplayName("Test: Buscar Persona por Documento")
    public void testBuscarPorDocumento() {
        Optional<Persona> persona = personaRepository.findByDocumento("75123456");

        assertTrue(persona.isPresent());
        assertEquals("Juan Pérez García", persona.get().getNombreCompleto());
        assertEquals("2024001", persona.get().getCodigoEstudiante());
    }

    @Test
    @Order(5)
    @DisplayName("Test: Actualizar Persona")
    public void testActualizarPersona() {
        Persona persona = personaRepository.findById(personaId).orElseThrow();

        persona.setNombreCompleto("Juan Carlos Pérez García");
        persona.setCelular("999888777");
        persona.setCorreo("juan.carlos@test.com");

        Persona actualizada = personaRepository.save(persona);

        assertEquals("Juan Carlos Pérez García", actualizada.getNombreCompleto());
        assertEquals("999888777", actualizada.getCelular());
        assertEquals("juan.carlos@test.com", actualizada.getCorreo());
    }

    @Test
    @Order(6)
    @DisplayName("Test: Listar Personas")
    public void testListarPersonas() {
        List<Persona> personas = personaRepository.findAll();

        assertFalse(personas.isEmpty());
        System.out.println("Total personas registradas: " + personas.size());

        for (Persona p : personas) {
            System.out.println(p.getNombreCompleto() + "\t" +
                    p.getDocumento() + "\t" +
                    p.getTipoPersona());
        }
    }

    @Test
    @Order(7)
    @DisplayName("Test: Crear Persona Tipo INVITADO")
    public void testCrearPersonaInvitado() {
        Usuario nuevoUsuario = new Usuario();
        nuevoUsuario.setUser("invitado.test" + System.currentTimeMillis());
        nuevoUsuario.setClave("$2a$10$test3");
        nuevoUsuario.setEstado("ACTIVO");
        Usuario usuarioGuardado = usuarioRepository.save(nuevoUsuario);

        Persona invitado = new Persona();
        invitado.setNombreCompleto("Carlos Mendoza Quispe");
        invitado.setDocumento("70555666");
        invitado.setCorreo("carlos.mendoza@gmail.com");
        invitado.setCelular("955666777");
        invitado.setPais("Perú");
        invitado.setFechaNacimiento(LocalDate.of(1985, 3, 10));
        invitado.setTipoPersona(TipoPersona.INVITADO);
        invitado.setUsuario(usuarioGuardado);

        Persona guardado = personaRepository.save(invitado);

        assertNotNull(guardado.getIdPersona());
        assertEquals(TipoPersona.INVITADO, guardado.getTipoPersona());
        assertNull(guardado.getCodigoEstudiante()); // Los invitados no tienen código
    }

    @Test
    @Order(8)
    @DisplayName("Test: Buscar Persona Inexistente por Código")
    public void testBuscarPersonaInexistentePorCodigo() {
        Optional<Persona> persona = personaRepository.findByCodigoEstudiante("9999999");

        assertFalse(persona.isPresent());
    }

    @Test
    @Order(9)
    @DisplayName("Test: Buscar Persona Inexistente por Documento")
    public void     testBuscarPersonaInexistentePorDocumento() {
        Optional<Persona> persona = personaRepository.findByDocumento("99999999");

        assertFalse(persona.isPresent());
    }

    @Test
    @Order(10)
    @DisplayName("Test: Eliminar Persona")
    public void testEliminarPersona() {
        personaRepository.deleteById(personaId);
        Optional<Persona> eliminada = personaRepository.findById(personaId);

        assertFalse(eliminada.isPresent(), "La persona debería haber sido eliminada");
    }
}*/