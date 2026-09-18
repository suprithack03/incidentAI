package incident_core.config;

import incident_core.security.JwtAuthenticationFilter;
import incident_core.security.JwtService;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtService jwtService;

    public SecurityConfig(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter(jwtService);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http) throws Exception {

        http
            .csrf(csrf -> csrf.disable())

            .sessionManagement(session ->
                session.sessionCreationPolicy(
                    SessionCreationPolicy.STATELESS
                )
            )

            .authorizeHttpRequests(auth -> auth

                // Allow browser CORS preflight requests
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                // Public health check
                .requestMatchers("/api/health").permitAll()

                // Temporary embedding test endpoint
                .requestMatchers("/api/test/embedding").permitAll()

                // Temporary runbook loading endpoint
                .requestMatchers("/api/test/load-runbooks").permitAll()

                // Temporary historical incident loading endpoint
                .requestMatchers("/api/test/load-historical-incidents").permitAll()

                // Temporary RAG retrieval test endpoint
                .requestMatchers("/api/test/rag-retrieval").permitAll()

                // Temporary RAG context test endpoint
                .requestMatchers("/api/test/rag-context").permitAll()

                // Internal log ingestion uses its own token filter
                .requestMatchers("/api/logs/ingest").permitAll()

                // Temporary detection/testing endpoints
                .requestMatchers("/api/anomalies/detect/**").permitAll()
                .requestMatchers("/api/incidents/group/**").permitAll()

                // Dashboard APIs require JWT
                .requestMatchers("/api/incidents/**").authenticated()
                .requestMatchers("/api/services/**").authenticated()

                // Error endpoint
                .requestMatchers("/error").permitAll()

                .anyRequest().authenticated()
            )

            .addFilterBefore(
                jwtAuthenticationFilter(),
                UsernamePasswordAuthenticationFilter.class
            );

        return http.build();
    }
}