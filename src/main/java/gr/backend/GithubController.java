package gr.backend;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;

import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpSession;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class GithubController {

    private final SupabaseService supabaseService;

    public GithubController(SupabaseService supabaseService) {
        this.supabaseService = supabaseService;
    }

    @GetMapping("/user")
    public Map<String, Object> getUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Map<String, Object> response = new HashMap<>();

        if (auth == null || !auth.isAuthenticated() || auth instanceof AnonymousAuthenticationToken) {
            response.put("error", "Not authenticated");
            return response;
        }

        Object principal = auth.getPrincipal();

        if (principal instanceof OAuth2User oAuth2User) {
            Long githubId = ((Number) oAuth2User.getAttribute("id")).longValue();
            String githubEmail = (String) oAuth2User.getAttribute("email");
            String githubLogin = (String) oAuth2User.getAttribute("login");
            
            if (githubEmail == null || githubEmail.isBlank()) {
                githubEmail = githubLogin + "@github.local";
            }
            
            Optional<Map<String, Object>> existingUser = supabaseService.getUserByGithubId(githubId);
            
            if (existingUser.isEmpty()) {
                Map<String, Object> newUser = new HashMap<>();
                newUser.put("email", githubEmail);
                newUser.put("github_name", githubLogin);
                newUser.put("name", oAuth2User.getAttribute("name"));
                newUser.put("avatar_url", oAuth2User.getAttribute("avatar_url"));
                newUser.put("github_id", githubId);
                newUser.put("type", "github");
                
                supabaseService.createUser(newUser);
            }
            
            response.put("name", oAuth2User.getAttribute("name"));
            response.put("login", githubLogin);
            response.put("email", githubEmail);
            response.put("avatar_url", oAuth2User.getAttribute("avatar_url"));
            response.put("id", githubId);
            response.put("type", "github");
        } else {
            String email = auth.getName();
            Optional<Map<String, Object>> user = supabaseService.getUserByEmail(email);
            
            if (user.isPresent()) {
                Map<String, Object> userData = user.get();
                response.put("name", userData.getOrDefault("name", email));
                response.put("email", email);
                response.put("type", userData.getOrDefault("type", "email"));
            } else {
                response.put("name", email);
                response.put("email", email);
                response.put("type", "email");
            }
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
