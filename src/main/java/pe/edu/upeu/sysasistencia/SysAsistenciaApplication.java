package pe.edu.upeu.sysasistencia;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import jakarta.annotation.PostConstruct; // <--- 1. IMPORTANTE: Agrega este import
import java.util.TimeZone;               // <--- 2. IMPORTANTE: Agrega este import

@SpringBootApplication
public class SysAsistenciaApplication {

    public static void main(String[] args) {
        SpringApplication.run(SysAsistenciaApplication.class, args);
    }

    @PostConstruct
    public void init() {
        // Configura la hora por defecto a Perú/Bogotá (GMT-5)
        TimeZone.setDefault(TimeZone.getTimeZone("America/Lima"));
        System.out.println("Hora configurada a: " + new java.util.Date());
    }

}
