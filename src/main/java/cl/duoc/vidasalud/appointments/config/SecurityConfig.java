package cl.duoc.vidasalud.appointments.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    private final RolesConverter rolesConverter;

    public SecurityConfig(RolesConverter rolesConverter) {
        this.rolesConverter = rolesConverter;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/actuator/health", "/h2-console/**").permitAll()
                .requestMatchers(org.springframework.http.HttpMethod.PUT, "/api/appointments/*/status")
                    .hasAnyRole("ADMIN", "RECEPCIONISTA")
                .anyRequest().hasAnyRole("ADMIN", "RECEPCIONISTA", "PACIENTE")
            )
            .oauth2ResourceServer(oauth2 -> oauth2
                .jwt(jwt -> jwt.jwtAuthenticationConverter(rolesConverter))
            )
            // Necesario solo para poder ver la consola H2 en dev (usa frames)
            .headers(headers -> headers.frameOptions(frame -> frame.sameOrigin()));

        return http.build();
    }
}
