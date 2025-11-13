package gr.backend;

// Bean: Spring's Dependency Injection - Objekte die Spring verwaltet
// Docs: https://docs.spring.io/spring-framework/reference/core/beans.html
import org.springframework.context.annotation.Bean;

// Configuration: Markiert diese Klasse als Spring Konfigurationsklasse
// Spring scannt diese Klasse beim Start und führt alle @Bean Methoden aus
// Docs: https://docs.spring.io/spring-framework/reference/core/beans/java/configuration-annotation.html
import org.springframework.context.annotation.Configuration;

// HttpSecurity: Builder-Klasse um Security-Regeln zu definieren
// Hier konfigurieren wir: CSRF, CORS, OAuth2, welche URLs geschützt sind, etc.
// Docs: https://docs.spring.io/spring-security/reference/servlet/configuration/java.html
import org.springframework.security.config.annotation.web.builders.HttpSecurity;

// SecurityFilterChain: Die Kette von Security-Filtern die jede HTTP-Request durchläuft
// Jeder Filter prüft etwas: CSRF? Authentifiziert? CORS erlaubt? etc.
// Docs: https://docs.spring.io/spring-security/reference/servlet/architecture.html#servlet-securityfilterchain
import org.springframework.security.web.SecurityFilterChain;

// AuthenticationSuccessHandler: Interface für "Was passiert nach erfolgreichem Login?"
// Wir implementieren das, um nach GitHub Login zum Frontend weiterzuleiten
// Docs: https://docs.spring.io/spring-security/reference/servlet/authentication/session-management.html#properly-clearing-authentication
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

// CORS (Cross-Origin Resource Sharing) Klassen
// Erlaubt unserem Frontend (localhost:5173) mit Backend (localhost:8080) zu kommunizieren
// Ohne CORS würde der Browser die Requests blockieren (Same-Origin Policy)
// Docs: https://docs.spring.io/spring-framework/reference/web/webmvc-cors.html
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

// Arrays.asList() - Konvertiert Array zu List
import java.util.Arrays;

// @Configuration sagt Spring: "Diese Klasse enthält Bean-Definitionen"
// Spring wird beim Start alle @Bean Methoden aufrufen und die Objekte verwalten
@Configuration
public class SecurityConfig {

    // @Bean: Diese Methode gibt ein Objekt zurück, das Spring verwalten soll
    // SecurityFilterChain: Die Haupt-Security-Konfiguration für unsere App
    // Spring Security ruft diese Methode automatisch beim Start auf
    // Docs: https://docs.spring.io/spring-security/reference/servlet/configuration/java.html#jc-httpsecurity
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/api/login", "/api/logout", "/api/user", "/error", "/webjars/**").permitAll()
                        .anyRequest().authenticated()
                )
                .oauth2Login(oauth2 -> oauth2
                        .successHandler(customSuccessHandler())
                );

        return http.build();
    }

    // @Bean: CORS-Konfiguration als Bean registrieren
    // CorsConfigurationSource: Interface das Spring für CORS-Regeln verwendet
    // Docs: https://docs.spring.io/spring-framework/reference/web/webmvc-cors.html
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {

        CorsConfiguration configuration = new CorsConfiguration();

        configuration.setAllowedOrigins(Arrays.asList("http://localhost:5173"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);
        configuration.setExposedHeaders(Arrays.asList("Set-Cookie"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }

    // @Bean: Success Handler als Bean registrieren
    // AuthenticationSuccessHandler: Interface mit einer Methode:
    // onAuthenticationSuccess(request, response, authentication)
    // Docs: https://docs.spring.io/spring-security/reference/servlet/authentication/architecture.html#servlet-authentication-authenticationsuccesshandler
    @Bean
    public AuthenticationSuccessHandler customSuccessHandler() {

        // Lambda-Ausdruck (Java 8+): Kurze Schreibweise für Interface-Implementierung
        // (request, response, authentication) -> { ... }
        // request: HttpServletRequest - Die HTTP-Anfrage
        // response: HttpServletResponse - Die HTTP-Antwort
        // authentication: Authentication - Infos über den eingeloggten User
        return (request, response, authentication) -> {

            // sendRedirect: Leitet den Browser zu einer anderen URL weiter
            // HTTP 302 Redirect: Browser macht automatisch neuen Request zur URL
            // "http://localhost:5173/dashboard": Unser React Frontend Dashboard
            //
            // Ablauf:
            // 1. User loggt sich via GitHub ein
            // 2. Spring ruft diesen Handler auf
            // 3. Browser wird zu Frontend Dashboard weitergeleitet
            // 4. User sieht Dashboard (ist jetzt eingeloggt)
            //
            // Docs: https://docs.oracle.com/javaee/7/api/javax/servlet/http/HttpServletResponse.html#sendRedirect-java.lang.String-
            response.sendRedirect("http://localhost:5173/dashboard");
        };
    }
}

/*
 * ZUSAMMENFASSUNG - Was macht diese Klasse?
 *
 * 1. CSRF deaktiviert (für REST API)
 * 2. CORS aktiviert (Frontend darf mit Backend kommunizieren)
 * 3. Bestimmte URLs sind öffentlich (/api/login, etc.)
 * 4. Alle anderen URLs brauchen Login
 * 5. OAuth2 Login aktiviert (GitHub)
 * 6. Nach Login: Weiterleitung zu Frontend Dashboard
 *
 * WICHTIGE DOCS:
 * - Spring Security: https://docs.spring.io/spring-security/reference/index.html
 * - OAuth2 Login: https://docs.spring.io/spring-security/reference/servlet/oauth2/login/core.html
 * - CORS: https://docs.spring.io/spring-framework/reference/web/webmvc-cors.html
 */
