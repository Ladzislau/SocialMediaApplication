package by.ladz.gusakov.SocialMedialApp.config;

import by.ladz.gusakov.SocialMedialApp.security.CustomAuthenticationEntryPoint;
import by.ladz.gusakov.SocialMedialApp.services.PersonDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final PersonDetailsService personDetailsService;

    private final JWTFilter jwtFilter;

    private final CustomAuthenticationEntryPoint authenticationEntryPoint;

    @Autowired
    public SecurityConfig(PersonDetailsService personDetailsService, JWTFilter jwtFilter, CustomAuthenticationEntryPoint authenticationEntryPoint) {
        this.personDetailsService = personDetailsService;
        this.jwtFilter = jwtFilter;
        this.authenticationEntryPoint = authenticationEntryPoint;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/v1/auth/login", "/api/v1/error", "/api/v1/auth/registration", "/api/v1/publications",
                                "/api/v1/publications/{id}", "/api/v1/api/v3/api-docs/", "/swagger-ui").permitAll()
                        .anyRequest().authenticated())
                .formLogin(form -> form
                        .loginPage("/auth/login"))
                .logout(logout ->
                        logout.logoutUrl("/logout")
                                .logoutSuccessUrl("/auth/login"))
                .exceptionHandling(exceptionHandling ->
                        exceptionHandling.authenticationEntryPoint(authenticationEntryPoint))
                .sessionManagement(sessionManagement ->
                        sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(personDetailsService);
        authenticationProvider.setPasswordEncoder(passwordEncoder());
        return authenticationProvider;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
