package gr.backend;

// Spring Web Annotations für REST Controllers
// Siehe GithubController.java für detaillierte Erklärungen
import org.springframework.web.bind.annotation.*;

// @RestController: Markiert diese Klasse als REST API Controller
// Gibt JSON/XML zurück (nicht HTML)
@RestController

// @RequestMapping: Basis-URL für alle Endpoints
// "/api": Alle Methoden haben URLs die mit /api/ beginnen
@RequestMapping("/api")

// @CrossOrigin: CORS-Konfiguration
// Erlaubt Requests vom Frontend (localhost:5173)
// allowCredentials = "true": Cookies/Sessions werden geteilt
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
public class LoginController {

    // @PostMapping: Diese Methode behandelt HTTP POST Requests
    // "/login": Die URL ist /api/login
    // Dieser Endpoint behandelt Email/Password Login
    @PostMapping("/login")

    // Rückgabetyp: String wird als Text zurückgegeben
    // (Könnte man zu JSON ändern für bessere API)
    //
    // @RequestBody LoginRequest request:
    // - @RequestBody: Spring konvertiert JSON aus dem Request-Body zu Java-Objekt
    // - LoginRequest: Unsere eigene Klasse (siehe unten)
    // - request: Variable-Name
    //
    // WIE funktioniert das?
    // 1. Frontend sendet POST Request mit JSON: {"email": "...", "password": "..."}
    // 2. Spring konvertiert JSON automatisch zu LoginRequest-Objekt
    // 3. Spring ruft diese Methode mit dem Objekt auf
    // 4. Wir können request.getEmail() und request.getPassword() verwenden
    //
    // Docs: https://docs.spring.io/spring-framework/reference/web/webmvc/mvc-controller/ann-methods/requestbody.html
    public String login(@RequestBody LoginRequest request) {

        // ACHTUNG: DIES IST NICHT SICHER!
        // Hardcoded Credentials sind nur für Entwicklung/Testing!
        //
        // .equals(): Vergleicht zwei Strings
        // "test@example.com".equals(request.getEmail()): Ist die Email korrekt?
        // "1234".equals(request.getPassword()): Ist das Passwort korrekt?
        // &&: Logisches UND - beide Bedingungen müssen wahr sein
        //
        // WARUM .equals() und nicht ==?
        // == vergleicht Objekt-Referenzen (Speicheradressen)
        // .equals() vergleicht den Inhalt der Strings
        // Docs: https://docs.oracle.com/javase/tutorial/java/data/comparestrings.html
        if ("test@example.com".equals(request.getEmail()) && "1234".equals(request.getPassword())) {

            // Login erfolgreich!
            // Gibt einfachen String zurück
            // PROBLEM: Keine Session wird erstellt, keine Authentifizierung!
            // User ist NICHT wirklich eingeloggt im Spring Security Sinne
            return "Login erfolgreich!";
        } else {

            // Login fehlgeschlagen!
            // throw new RuntimeException: Wirft eine Exception
            // Spring fängt die Exception und gibt HTTP 500 Error zurück
            //
            // BESSER wäre:
            // - HTTP 401 Unauthorized zurückgeben
            // - JSON Error-Response
            // - Logging des fehlgeschlagenen Login-Versuchs
            //
            // Docs: https://docs.spring.io/spring-framework/reference/web/webmvc/mvc-controller/ann-exceptionhandler.html
            throw new RuntimeException("Ungültige Anmeldedaten");
        }
    }

    // static class: Innere Klasse (nested class)
    // static: Kann ohne Instanz von LoginController verwendet werden
    // LoginRequest: Name der Klasse
    //
    // WARUM eine innere Klasse?
    // - Nur in diesem Controller verwendet
    // - Hält den Code zusammen
    // - Könnte auch in separate Datei ausgelagert werden
    //
    // Diese Klasse ist ein DTO (Data Transfer Object)
    // Wird verwendet um Daten zwischen Frontend und Backend zu übertragen
    // Docs: https://docs.oracle.com/javase/tutorial/java/javaOO/nested.html
    public static class LoginRequest {

        // private: Nur innerhalb dieser Klasse zugreifbar
        // String: Datentyp für Text
        // email: Variable-Name
        //
        // WARUM private?
        // Encapsulation (Kapselung) - Best Practice in OOP
        // Zugriff nur über Getter/Setter Methoden
        private String email;
        private String password;

        // Getter-Methode für email
        // public: Von überall aufrufbar
        // String: Rückgabetyp
        // getEmail(): Methoden-Name (Naming Convention: get + Feldname)
        // return email: Gibt den Wert der email-Variable zurück
        //
        // WARUM Getter?
        // - Kontrollierter Zugriff auf private Felder
        // - Könnte Validierung/Logging enthalten
        // - Java Beans Convention (wichtig für Spring)
        //
        // Docs: https://docs.oracle.com/javase/tutorial/javabeans/writing/properties.html
        public String getEmail() { return email; }

        // Setter-Methode für email
        // void: Gibt nichts zurück
        // setEmail(String email): Methoden-Name + Parameter
        // this.email = email: Setzt das Feld email auf den Parameter-Wert
        //
        // this.email: Das Feld der Klasse
        // email: Der Parameter
        // this. unterscheidet zwischen Feld und Parameter
        //
        // WARUM Setter?
        // - Spring braucht Setter um JSON zu Java-Objekt zu konvertieren
        // - Könnte Validierung enthalten (z.B. Email-Format prüfen)
        public void setEmail(String email) { this.email = email; }

        // Getter für password
        public String getPassword() { return password; }

        // Setter für password
        public void setPassword(String password) { this.password = password; }

        // WIE Spring JSON zu LoginRequest konvertiert:
        // 1. Frontend sendet: {"email": "test@example.com", "password": "1234"}
        // 2. Spring erstellt: new LoginRequest()
        // 3. Spring ruft auf: setEmail("test@example.com")
        // 4. Spring ruft auf: setPassword("1234")
        // 5. Spring übergibt das Objekt an die login() Methode
        //
        // Das nennt man "Deserialization" (JSON → Java-Objekt)
        // Docs: https://www.baeldung.com/jackson-object-mapper-tutorial
    }
}

/*
 * ZUSAMMENFASSUNG - Was macht dieser Controller?
 *
 * POST /api/login
 * - Empfängt Email und Passwort als JSON
 * - Prüft gegen hardcoded Credentials
 * - Gibt Success/Error Message zurück
 *
 * WICHTIGE PROBLEME:
 * ❌ Hardcoded Credentials (nicht sicher!)
 * ❌ Keine echte Authentifizierung
 * ❌ Keine Session wird erstellt
 * ❌ Passwort nicht gehasht
 * ❌ Keine Datenbank-Anbindung
 *
 * FÜR PRODUKTION BRÄUCHTE MAN:
 * ✅ Datenbank mit User-Tabelle
 * ✅ Passwort-Hashing (BCrypt)
 * ✅ Spring Security's AuthenticationManager
 * ✅ Session/JWT Token erstellen
 * ✅ Proper Error Handling
 *
 * WICHTIGE DOCS:
 * - Spring Security Authentication: https://docs.spring.io/spring-security/reference/servlet/authentication/index.html
 * - Password Encoding: https://docs.spring.io/spring-security/reference/features/authentication/password-storage.html
 * - Request Body: https://docs.spring.io/spring-framework/reference/web/webmvc/mvc-controller/ann-methods/requestbody.html
 */
