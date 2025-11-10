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

        // http ist ein Builder-Objekt - wir konfigurieren es Schritt für Schritt
        http
                // CSRF (Cross-Site Request Forgery) Protection
                // csrf -> csrf.disable(): Lambda-Ausdruck (Java 8+)
                // Wir deaktivieren CSRF weil:
                // 1. Wir verwenden stateless REST API (keine Sessions für normale Requests)
                // 2. OAuth2 hat eigenen CSRF-Schutz (State Parameter)
                // ACHTUNG: In Produktion nur deaktivieren wenn du weißt was du tust!
                // Docs: https://docs.spring.io/spring-security/reference/servlet/exploits/csrf.html
                .csrf(csrf -> csrf.disable())

                // CORS (Cross-Origin Resource Sharing) aktivieren
                // cors -> cors.configurationSource(...): Lambda-Ausdruck
                // Wir sagen: "Verwende die CORS-Config von corsConfigurationSource() Methode"
                // Ohne CORS: Browser blockiert Requests von localhost:5173 zu localhost:8080
                // Docs: https://docs.spring.io/spring-security/reference/servlet/integrations/cors.html
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                // authorizeHttpRequests: Definiert welche URLs geschützt sind
                // auth -> auth...: Lambda-Ausdruck mit den Regeln
                .authorizeHttpRequests(auth -> auth
                        // requestMatchers: Welche URL-Patterns sollen diese Regel bekommen?
                        // permitAll(): Diese URLs sind OHNE Login erreichbar
                        // "/" = Homepage
                        // "/api/login" = Email/Password Login Endpoint
                        // "/error" = Spring's Error Page
                        // "/webjars/**" = JavaScript/CSS Libraries (falls verwendet)
                        // Docs: https://docs.spring.io/spring-security/reference/servlet/authorization/authorize-http-requests.html
                        .requestMatchers("/", "/api/login", "/error", "/webjars/**").permitAll()

                        // anyRequest().authenticated(): ALLE anderen URLs brauchen Login!
                        // Beispiel: /api/user ist geschützt, nur eingeloggte User können darauf zugreifen
                        .anyRequest().authenticated()
                )

                // oauth2Login: Aktiviert OAuth2 Login (GitHub, Google, Facebook, etc.)
                // Spring erstellt automatisch:
                // - /oauth2/authorization/{registrationId} (startet OAuth Flow)
                // - /login/oauth2/code/{registrationId} (Callback von OAuth Provider)
                // oauth2 -> oauth2...: Lambda-Ausdruck für OAuth2-Konfiguration
                // Docs: https://docs.spring.io/spring-security/reference/servlet/oauth2/login/core.html
                .oauth2Login(oauth2 -> oauth2
                        // successHandler: Was passiert nach erfolgreichem OAuth2 Login?
                        // Wir verwenden unseren customSuccessHandler() (siehe unten)
                        // Default wäre: Weiterleitung zu "/"
                        // Wir wollen aber: Weiterleitung zu Frontend Dashboard
                        .successHandler(customSuccessHandler())
                );

        // http.build(): Baut die finale SecurityFilterChain aus unserer Konfiguration
        // Diese Chain wird dann auf ALLE HTTP-Requests angewendet
        return http.build();
    }

    // @Bean: CORS-Konfiguration als Bean registrieren
    // CorsConfigurationSource: Interface das Spring für CORS-Regeln verwendet
    // Docs: https://docs.spring.io/spring-framework/reference/web/webmvc-cors.html
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {

        // CorsConfiguration: Objekt das die CORS-Regeln enthält
        CorsConfiguration configuration = new CorsConfiguration();

        // setAllowedOrigins: Von welchen Domains dürfen Requests kommen?
        // Arrays.asList(...): Erstellt eine List mit einem Element
        // "http://localhost:5173": Unser React Frontend
        // WICHTIG: In Produktion hier die echte Domain eintragen!
        // Docs: https://developer.mozilla.org/en-US/docs/Web/HTTP/CORS
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:5173"));

        // setAllowedMethods: Welche HTTP-Methoden sind erlaubt?
        // GET: Daten abrufen
        // POST: Daten senden (z.B. Login)
        // PUT: Daten aktualisieren
        // DELETE: Daten löschen
        // OPTIONS: Preflight-Request (Browser fragt vor dem echten Request)
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));

        // setAllowedHeaders: Welche HTTP-Headers darf das Frontend senden?
        // "*": Alle Headers erlaubt
        // Beispiele: Content-Type, Authorization, X-Custom-Header, etc.
        configuration.setAllowedHeaders(Arrays.asList("*"));

        // setAllowCredentials: Dürfen Cookies/Sessions mitgesendet werden?
        // true: Ja, Cookies werden zwischen Frontend und Backend geteilt
        // WICHTIG für: Sessions, OAuth2 Cookies, CSRF Tokens
        // Docs: https://developer.mozilla.org/en-US/docs/Web/HTTP/Headers/Access-Control-Allow-Credentials
        configuration.setAllowCredentials(true);

        // UrlBasedCorsConfigurationSource: Wendet CORS-Config auf URL-Patterns an
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();

        // registerCorsConfiguration: Registriert unsere CORS-Config
        // "/**": Für ALLE URLs (/** = alle Pfade und Unterpfade)
        // configuration: Die CORS-Regeln von oben
        source.registerCorsConfiguration("/**", configuration);

        // Gibt die CORS-Quelle zurück, die Spring dann verwendet
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
