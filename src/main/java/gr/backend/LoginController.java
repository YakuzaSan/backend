package gr.backend;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class LoginController {

    private final SupabaseService supabaseService;
    private final PasswordEncoder passwordEncoder;

    public LoginController(SupabaseService supabaseService, PasswordEncoder passwordEncoder) {
        this.supabaseService = supabaseService;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/register")
    public Map<String, Object> register(@Valid @RequestBody LoginRequest request, HttpSession session) {
        Map<String, Object> response = new HashMap<>();

        Optional<Map<String, Object>> existingUser = supabaseService.getUserByEmail(request.getEmail());
        if (existingUser.isPresent()) {
            response.put("error", "Email existiert bereits");
            return response;
        }

        try {
            Map<String, Object> newUser = new HashMap<>();
            newUser.put("email", request.getEmail());
            newUser.put("password_hash", passwordEncoder.encode(request.getPassword()));
            newUser.put("type", "email");

            supabaseService.createUser(newUser);

            setupSecurityContext(request.getEmail(), session);

            response.put("message", "Registrierung erfolgreich!");
            response.put("email", request.getEmail());
            response.put("redirect", "/dashboard");
            return response;
        } catch (Exception e) {
            response.put("error", "Registrierung fehlgeschlagen: " + e.getMessage());
            return response;
        }
    }

    @PostMapping("/login")
    public Map<String, Object> login(@Valid @RequestBody LoginRequest request, HttpSession session) {
        Map<String, Object> response = new HashMap<>();

        Optional<Map<String, Object>> userOptional = supabaseService.getUserByEmail(request.getEmail());
        
        if (userOptional.isEmpty()) {
            response.put("error", "Benutzer nicht gefunden");
            return response;
        }
        
        Map<String, Object> user = userOptional.get();
        String passwordHash = (String) user.get("password_hash");
        
        if (passwordHash == null || !passwordEncoder.matches(request.getPassword(), passwordHash)) {
            response.put("error", "Ungültige Anmeldedaten");
            return response;
        }
        
        setupSecurityContext(request.getEmail(), session);

        response.put("message", "Login erfolgreich!");
        response.put("email", request.getEmail());
        return response;
    }

    private void setupSecurityContext(String email, HttpSession session) {
        Collection<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
        
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                email,
                null,
                authorities
        );
        
        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        securityContext.setAuthentication(authentication);
        SecurityContextHolder.setContext(securityContext);
        
        session.setAttribute("SPRING_SECURITY_CONTEXT", securityContext);
    }

    public static class LoginRequest {
        @NotBlank(message = "Email ist erforderlich")
        @Email(message = "Email-Format ist ungültig")
        private String email;
        
        @NotBlank(message = "Passwort ist erforderlich")
        private String password;

        public String getEmail() { return email; }
        @SuppressWarnings("unused")
        public void setEmail(String email) { this.email = email; }
        public String getPassword() { return password; }
        @SuppressWarnings("unused")
        public void setPassword(String password) { this.password = password; }
    }
}
