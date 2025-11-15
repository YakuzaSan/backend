package gr.backend;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
import java.util.Optional;

@Service
public class SupabaseService {
    
    @Value("${supabase.url}")
    private String supabaseUrl;
    
    @Value("${supabase.api-key}")
    private String supabaseApiKey;
    
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    
    public SupabaseService(RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }
    
    public Optional<Map<String, Object>> getUserByEmail(String email) {
        String url = supabaseUrl + "/rest/v1/users?email=eq." + email;
        return fetchUsersList(url);
    }
    
    public Optional<Map<String, Object>> getUserByGithubId(Long githubId) {
        String url = supabaseUrl + "/rest/v1/users?github_id=eq." + githubId;
        return fetchUsersList(url);
    }
    
    public Map<String, Object> createUser(Map<String, Object> userData) {
        try {
            String url = supabaseUrl + "/rest/v1/users";
            HttpHeaders headers = getHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            Map<String, Object> filteredData = new java.util.HashMap<>();
            for (Map.Entry<String, Object> entry : userData.entrySet()) {
                if (entry.getValue() != null) {
                    filteredData.put(entry.getKey(), entry.getValue());
                }
            }
            
            String json = objectMapper.writeValueAsString(filteredData);
            HttpEntity<String> entity = new HttpEntity<>(json, headers);
            
            org.springframework.http.ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);
            
            java.util.List<Map<String, Object>> users = objectMapper.readValue(
                response.getBody(),
                objectMapper.getTypeFactory().constructCollectionType(java.util.List.class, Map.class)
            );

            return users.stream().findFirst().orElse(new java.util.HashMap<>());
        } catch (HttpClientErrorException e) {
            throw new RuntimeException("Failed to create user in Supabase: " + e.getResponseBodyAsString(), e);
        } catch (Exception e) {
            throw new RuntimeException("Failed to create user in Supabase: " + e.getMessage(), e);
        }
    }

    private Optional<Map<String, Object>> fetchUsersList(String url) {
        try {
            HttpHeaders headers = getHeaders();
            HttpEntity<String> entity = new HttpEntity<>(headers);
            
            org.springframework.http.ResponseEntity<String> response = restTemplate.exchange(
                url, 
                org.springframework.http.HttpMethod.GET, 
                entity, 
                String.class
            );
            
            java.util.List<Map<String, Object>> users = objectMapper.readValue(
                response.getBody(), 
                objectMapper.getTypeFactory().constructCollectionType(java.util.List.class, Map.class)
            );
            
            return users.stream().findFirst();
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    private HttpHeaders getHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + supabaseApiKey);
        headers.set("apikey", supabaseApiKey);
        headers.set("Prefer", "return=representation");
        return headers;
    }
}
