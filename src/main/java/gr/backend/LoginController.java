package gr.backend;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class LoginController {

    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody LoginRequest request, HttpSession session) {
        Map<String, Object> response = new HashMap<>();

        if ("test@example.com".equals(request.getEmail()) && "1234".equals(request.getPassword())) {
            Collection<GrantedAuthority> authorities = new ArrayList<>();
            authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
            
            Authentication authentication = new UsernamePasswordAuthenticationToken(
                    request.getEmail(),
                    request.getPassword(),
                    authorities
            );
            
            SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
            securityContext.setAuthentication(authentication);
            SecurityContextHolder.setContext(securityContext);
            
            session.setAttribute("SPRING_SECURITY_CONTEXT", securityContext);

            response.put("message", "Login erfolgreich!");
            response.put("email", request.getEmail());
            return response;
        } else {
            response.put("error", "Ungültige Anmeldedaten");
            return response;
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
