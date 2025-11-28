package pe.edu.upeu.sysasistencia.security;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class WebSecurityConfig {
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;
    private final UserDetailsService jwtUserDetailsService;
    private final JwtRequestFilter jwtRequestFilter;

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration)
            throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public static PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(jwtUserDetailsService).passwordEncoder(passwordEncoder());
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(req -> req
                        // RUTAS PÚBLICAS
                        .requestMatchers(
                                "/users/login", "/users/register",
                                "/accesos/menu", "/accesos/menu-movil",
                                "/mail/**", "/doc/**", "/v3/**","/swagger-ui/**", "/swagger-ui.html"
                        ).permitAll()

                        // RUTAS DE GESTIÓN (ADMIN / SUPERADMIN)
                        .requestMatchers(
                                "/sedes/**", "/facultades/**", "/programas/**", "/periodos/**",
                                "/matriculas/**", "/users/**", "/roles/**",
                                "/eventos-generales/**", "/grupos-generales/**", "/grupos-pequenos/**"
                        ).hasAnyRole("ADMIN", "SUPERADMIN","LIDER","INTEGRANTE")

                        // RUTAS DE LÍDER
                        .requestMatchers("/asistencias/qr/**", "/asistencias/lista-llamado/**",
                                "/grupos-pequenos/lider","/participantes","/grupos-pequenos/lider")
                        .hasAnyRole("LIDER", "ADMIN", "SUPERADMIN","INTEGRANTE")

                        // RUTAS DE INTEGRANTE
                        .requestMatchers(HttpMethod.POST, "/asistencias/registrar","/eventos-generales")
                        .hasAnyRole("INTEGRANTE", "LIDER", "ADMIN", "SUPERADMIN")

                        // RUTAS COMUNES AUTENTICADAS
                        .requestMatchers(
                                "/personas/my-profile",
                                "/asistencias/persona/**"
                        ).authenticated()

                        // CUALQUIER OTRA RUTA
                        .anyRequest().authenticated()
                )
                .formLogin(AbstractHttpConfigurer::disable)
                .exceptionHandling(e -> e
                        .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                        .accessDeniedHandler(jwtAccessDeniedHandler)
                );

        http.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}
