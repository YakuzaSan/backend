package gr.backend;

// @AuthenticationPrincipal: Spring Annotation um den aktuell eingeloggten User zu bekommen
// Spring injiziert automatisch das User-Objekt in die Controller-Methode
// Docs: https://docs.spring.io/spring-security/reference/servlet/integrations/mvc.html#mvc-authentication-principal
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;

import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpSession;

import java.util.HashMap;
import java.util.Map;

// @RestController: Markiert diese Klasse als REST API Controller
// Unterschied zu @Controller:
// - @Controller: Gibt HTML-Views zurück (Thymeleaf, JSP, etc.)
// - @RestController: Gibt JSON/XML zurück (REST API)
// Spring konvertiert automatisch Java-Objekte zu JSON
// Docs: https://docs.spring.io/spring-framework/reference/web/webmvc/mvc-controller/ann-restcontroller.html
@RestController
@RequestMapping("/api")
public class GithubController {

    // @GetMapping: Diese Methode behandelt HTTP GET Requests
    // "/user": Die URL ist /api/user (wegen @RequestMapping("/api") oben)
    // Dieser Endpoint gibt die Daten des eingeloggten GitHub-Users zurück
    // Docs: https://docs.spring.io/spring-framework/reference/web/webmvc/mvc-controller/ann-methods/requestmapping.html
    @GetMapping("/user")
    public Map<String, Object> getUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Map<String, Object> response = new HashMap<>();

        if (auth == null || !auth.isAuthenticated() || auth instanceof AnonymousAuthenticationToken) {
            response.put("error", "Not authenticated");
            return response;
        }

        Object principal = auth.getPrincipal();

        if (principal instanceof OAuth2User) {
            OAuth2User oAuth2User = (OAuth2User) principal;
            response.put("name", oAuth2User.getAttribute("name"));
            response.put("login", oAuth2User.getAttribute("login"));
            response.put("email", oAuth2User.getAttribute("email"));
            response.put("avatar_url", oAuth2User.getAttribute("avatar_url"));
            response.put("id", oAuth2User.getAttribute("id"));
            response.put("type", "github");
        } else {
            response.put("name", auth.getName());
            response.put("email", auth.getName());
            response.put("type", "email");
        }

        return response;
    }

    @PostMapping("/logout")
    public Map<String, String> logout(HttpSession session) {
        SecurityContextHolder.clearContext();
        session.invalidate();

        Map<String, String> response = new HashMap<>();
        response.put("message", "Logged out successfully");
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
