package gr.backend;

// @AuthenticationPrincipal: Spring Annotation um den aktuell eingeloggten User zu bekommen
// Spring injiziert automatisch das User-Objekt in die Controller-Methode
// Docs: https://docs.spring.io/spring-security/reference/servlet/integrations/mvc.html#mvc-authentication-principal
import org.springframework.security.core.annotation.AuthenticationPrincipal;

// OAuth2User: Interface das die Daten eines OAuth2-eingeloggten Users repräsentiert
// Enthält alle Attribute die der OAuth2 Provider (GitHub) zurückgibt
// Beispiel: name, email, avatar_url, login, id, etc.
// Docs: https://docs.spring.io/spring-security/reference/servlet/oauth2/login/advanced.html#oauth2login-advanced-map-authorities
import org.springframework.security.oauth2.core.user.OAuth2User;

// Spring Web Annotations für REST Controllers
// @RestController: Kombiniert @Controller + @ResponseBody
// @RequestMapping: Basis-URL für alle Endpoints in diesem Controller
// @GetMapping: HTTP GET Request Handler
// @PostMapping: HTTP POST Request Handler
// @CrossOrigin: CORS-Konfiguration für diesen Controller
// Docs: https://docs.spring.io/spring-framework/reference/web/webmvc/mvc-controller.html
import org.springframework.web.bind.annotation.*;

// HashMap: Java's Standard-Implementierung einer Map (Key-Value Paare)
// Wir verwenden es um JSON-Responses zu bauen
import java.util.HashMap;

// Map: Interface für Key-Value Datenstrukturen
// Map<String, Object> = Keys sind Strings, Values können beliebige Objekte sein
import java.util.Map;

// @RestController: Markiert diese Klasse als REST API Controller
// Unterschied zu @Controller:
// - @Controller: Gibt HTML-Views zurück (Thymeleaf, JSP, etc.)
// - @RestController: Gibt JSON/XML zurück (REST API)
// Spring konvertiert automatisch Java-Objekte zu JSON
// Docs: https://docs.spring.io/spring-framework/reference/web/webmvc/mvc-controller/ann-restcontroller.html
@RestController

// @RequestMapping: Basis-URL für ALLE Endpoints in diesem Controller
// "/api": Alle Methoden in dieser Klasse haben URLs die mit /api/ beginnen
// Beispiel: @GetMapping("/user") wird zu /api/user
// Best Practice: API-Endpoints unter /api/ gruppieren
// Docs: https://docs.spring.io/spring-framework/reference/web/webmvc/mvc-controller/ann-requestmapping.html
@RequestMapping("/api")

// @CrossOrigin: CORS-Konfiguration auf Controller-Ebene
// origins = "http://localhost:5173": Nur Requests von diesem Frontend erlauben
// allowCredentials = "true": Cookies/Sessions zwischen Frontend und Backend teilen
// WICHTIG: Ohne das würde der Browser die Requests blockieren!
// Docs: https://docs.spring.io/spring-framework/reference/web/webmvc-cors.html#mvc-cors-controller
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
public class GithubController {

    // @GetMapping: Diese Methode behandelt HTTP GET Requests
    // "/user": Die URL ist /api/user (wegen @RequestMapping("/api") oben)
    // Dieser Endpoint gibt die Daten des eingeloggten GitHub-Users zurück
    // Docs: https://docs.spring.io/spring-framework/reference/web/webmvc/mvc-controller/ann-methods/requestmapping.html
    @GetMapping("/user")

    // Rückgabetyp: Map<String, Object> wird automatisch zu JSON konvertiert
    // Beispiel: {"name": "Max", "email": "max@example.com"}
    //
    // @AuthenticationPrincipal OAuth2User principal:
    // - @AuthenticationPrincipal: Spring injiziert den eingeloggten User
    // - OAuth2User: Interface mit allen GitHub-User-Daten
    // - principal: Variable-Name (kannst du frei wählen)
    //
    // WIE funktioniert das?
    // 1. User loggt sich via GitHub ein
    // 2. Spring speichert OAuth2User in der Session
    // 3. Bei jedem Request holt Spring den User aus der Session
    // 4. Spring injiziert den User in diese Methode
    // 5. Wenn nicht eingeloggt: principal = null
    //
    // Docs: https://docs.spring.io/spring-security/reference/servlet/integrations/mvc.html#mvc-authentication-principal
    public Map<String, Object> getUser(@AuthenticationPrincipal OAuth2User principal) {

        // Prüfen ob User eingeloggt ist
        // principal == null: Kein User eingeloggt (keine Session vorhanden)
        if (principal == null) {

            // HashMap erstellen für Error-Response
            Map<String, Object> error = new HashMap<>();

            // Key-Value Paar hinzufügen: "error" -> "Not authenticated"
            // Wird zu JSON: {"error": "Not authenticated"}
            error.put("error", "Not authenticated");

            // Error-Map zurückgeben
            // Spring konvertiert automatisch zu JSON
            // HTTP Status: 200 OK (könnte man zu 401 Unauthorized ändern)
            return error;
        }

        // User ist eingeloggt! Jetzt User-Daten aus principal holen

        // HashMap für User-Daten erstellen
        // Map<String, Object>: Keys = Strings, Values = beliebige Objekte
        Map<String, Object> userData = new HashMap<>();

        // principal.getAttribute("name"): Holt Attribut "name" vom OAuth2User
        // GitHub gibt folgende Attribute zurück:
        // - name: Voller Name des Users
        // - login: GitHub Username
        // - email: Email-Adresse
        // - avatar_url: Profilbild URL
        // - id: GitHub User ID
        // - bio, location, company, etc.
        //
        // getAttribute() gibt Object zurück (kann String, Integer, etc. sein)
        // Docs: https://docs.github.com/en/rest/users/users#get-the-authenticated-user
        userData.put("name", principal.getAttribute("name"));
        userData.put("login", principal.getAttribute("login"));
        userData.put("email", principal.getAttribute("email"));
        userData.put("avatar_url", principal.getAttribute("avatar_url"));
        userData.put("id", principal.getAttribute("id"));

        // Debug-Ausgabe in der Konsole
        // System.out.println: Gibt Text in der Server-Konsole aus
        // Nützlich zum Debuggen, aber in Produktion sollte man Logging verwenden
        System.out.println("User eingeloggt: " + principal.getAttribute("login"));

        // userData zurückgeben
        // Spring konvertiert automatisch zu JSON:
        // {
        //   "name": "Max Mustermann",
        //   "login": "maxmustermann",
        //   "email": "max@example.com",
        //   "avatar_url": "https://avatars.githubusercontent.com/u/12345",
        //   "id": 12345
        // }
        return userData;
    }

    // @PostMapping: Diese Methode behandelt HTTP POST Requests
    // "/logout": Die URL ist /api/logout
    // HINWEIS: Dieser Endpoint macht aktuell nichts Echtes!
    // Für echten Logout müsste man die Session invalidieren
    // Docs: https://docs.spring.io/spring-security/reference/servlet/authentication/logout.html
    @PostMapping("/logout")
    public Map<String, String> logout() {

        // HashMap für Response erstellen
        // Map<String, String>: Beide Keys und Values sind Strings
        Map<String, String> response = new HashMap<>();

        // Success-Message hinzufügen
        // Wird zu JSON: {"message": "Logged out successfully"}
        response.put("message", "Logged out successfully");

        // WICHTIG: Dieser Code macht KEINEN echten Logout!
        // Für echten Logout bräuchte man:
        // 1. Session invalidieren
        // 2. Cookies löschen
        // 3. OAuth2 Token widerrufen (optional)
        //
        // Beispiel für echten Logout:
        // request.getSession().invalidate();
        //
        // Oder Spring Security's Logout verwenden:
        // POST /logout (automatisch von Spring erstellt)

        return response;
    }
}

/*
 * ZUSAMMENFASSUNG - Was macht dieser Controller?
 *
 * 1. GET /api/user
 *    - Gibt Daten des eingeloggten GitHub-Users zurück
 *    - Spring injiziert automatisch OAuth2User
 *    - Wenn nicht eingeloggt: Error-Message
 *
 * 2. POST /api/logout
 *    - Gibt Success-Message zurück
 *    - ACHTUNG: Macht keinen echten Logout!
 *
 * WICHTIGE KONZEPTE:
 * - @AuthenticationPrincipal: Spring injiziert eingeloggten User
 * - OAuth2User: Interface mit allen OAuth2-User-Daten
 * - principal.getAttribute(): Holt Attribute vom OAuth2 Provider
 * - Spring konvertiert Map automatisch zu JSON
 *
 * WICHTIGE DOCS:
 * - OAuth2 User: https://docs.spring.io/spring-security/reference/servlet/oauth2/login/advanced.html
 * - REST Controller: https://docs.spring.io/spring-framework/reference/web/webmvc/mvc-controller.html
 * - Authentication Principal: https://docs.spring.io/spring-security/reference/servlet/integrations/mvc.html
 */
