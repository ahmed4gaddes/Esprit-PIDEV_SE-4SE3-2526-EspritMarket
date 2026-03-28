package tn.esprit.esprit_market.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import tn.esprit.esprit_market.modules.user.service.CustomUserDetailsService;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

  private final JwtAuthenticationFilter jwtAuthFilter;
  private final CustomUserDetailsService userDetailsService;
  private final RestAuthenticationEntryPoint restAuthenticationEntryPoint;
  private final RestAccessDeniedHandler restAccessDeniedHandler;

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http
      .cors(cors -> cors.configurationSource(corsConfigurationSource()))
      .csrf(csrf -> csrf.disable())
      .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
      .exceptionHandling(ex -> ex
        .authenticationEntryPoint(restAuthenticationEntryPoint)
        .accessDeniedHandler(restAccessDeniedHandler))
      .httpBasic(httpBasic -> httpBasic.disable())
      .formLogin(formLogin -> formLogin.disable())
      .authorizeHttpRequests(auth -> auth
        .requestMatchers("/api/auth/**").permitAll()
        .requestMatchers("/Store/**").permitAll()
        .requestMatchers("/category/**").permitAll()
        .requestMatchers("/Product/**").permitAll()
        .requestMatchers("/Stock/**").permitAll()
        .requestMatchers("/ProductImage/**").permitAll()
        .requestMatchers("/uploads/**").permitAll()
        .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/sponsorship-requests/public/**").permitAll()
        .requestMatchers(org.springframework.http.HttpMethod.POST, "/api/upload").authenticated()
        .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/events/**", "/api/live-sessions/**").permitAll()
        // Customers buy tickets: POST .../events/{id}/tickets — must be before the broad POST /api/events/** rule for event creation
        .requestMatchers(org.springframework.http.HttpMethod.POST, "/api/events/*/tickets").authenticated()
        .requestMatchers(org.springframework.http.HttpMethod.POST, "/api/events/**").hasAnyAuthority("ROLE_COMPANY", "ROLE_EXPERT", "ROLE_ADMIN", "ROLE_SELLER")
        .requestMatchers(org.springframework.http.HttpMethod.POST, "/api/live-sessions").hasAnyAuthority("ROLE_SELLER", "ROLE_COMPANY", "ROLE_EXPERT", "ROLE_ADMIN")
        .requestMatchers(org.springframework.http.HttpMethod.POST, "/api/live-sessions/*/chat").authenticated()
        .requestMatchers(org.springframework.http.HttpMethod.PUT, "/api/events/**").hasAnyAuthority("ROLE_COMPANY", "ROLE_EXPERT", "ROLE_ADMIN", "ROLE_SELLER")
        .requestMatchers(org.springframework.http.HttpMethod.PUT, "/api/live-sessions/**").hasAnyAuthority("ROLE_SELLER", "ROLE_COMPANY", "ROLE_EXPERT", "ROLE_ADMIN")
        .requestMatchers(org.springframework.http.HttpMethod.DELETE, "/api/events/**").hasAnyAuthority("ROLE_COMPANY", "ROLE_EXPERT", "ROLE_ADMIN", "ROLE_SELLER")
        .requestMatchers(org.springframework.http.HttpMethod.DELETE, "/api/live-sessions/**").hasAnyAuthority("ROLE_SELLER", "ROLE_COMPANY", "ROLE_EXPERT", "ROLE_ADMIN")
        .requestMatchers("/api/cart/**").authenticated()
        .requestMatchers("/api/orders/**").authenticated()
        .requestMatchers("/api/payments/**").authenticated()
        .requestMatchers("/api/deliveries/**").authenticated()
        .requestMatchers("/api/admin/**").hasAuthority("ROLE_ADMIN")
        .anyRequest().authenticated())
      .authenticationProvider(authenticationProvider())
      .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
    return http.build();
  }

  @Bean
  public AuthenticationProvider authenticationProvider() {
    DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
    authProvider.setUserDetailsService(userDetailsService);
    authProvider.setPasswordEncoder(passwordEncoder());
    return authProvider;
  }

  @Bean
  public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
    return config.getAuthenticationManager();
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration configuration = new CorsConfiguration();
    configuration.setAllowedOrigins(List.of(
      "http://localhost:4200",
      "http://127.0.0.1:4200"
    ));
    configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
    configuration.setAllowedHeaders(List.of("*"));
    configuration.setAllowCredentials(true);
    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", configuration);
    return source;
  }
}
