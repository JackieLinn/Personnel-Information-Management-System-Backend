package zxylearn.bcnlserver.OAuth2;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class OAuth2Service {

    private final RestClient restClient;

    public OAuth2Service(RestClient.Builder restClientBuilder) {
        this.restClient = restClientBuilder.baseUrl("https://data.zxylearn.top").build();
    }

    public Map<String, String> loginYNU(String username, String password) {
        if (username == null || password == null || username.isBlank() || password.isBlank()) {
            return null;
        }

        Map<String, String> body = new HashMap<>();
        body.put("username", username);
        body.put("password", password);

        Map<String, Object> response = restClient.post()
                .uri("/login-ynu")
                .contentType(MediaType.APPLICATION_JSON)
                .body(body)
                .retrieve()
                .body(new org.springframework.core.ParameterizedTypeReference<Map<String, Object>>() {
                });

        if (response != null && "1".equals(String.valueOf(response.get("code")))) {
            Object dataObj = response.get("data");
            if (dataObj instanceof Map<?, ?> data) {
                Map<String, String> result = new HashMap<>();
                result.put("number", String.valueOf(data.get("number")));
                result.put("name", String.valueOf(data.get("name")));
                return result;
            }
        }

        return null;
    }

}
