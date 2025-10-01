package com.tamago.tamagoservice;

import com.tamago.tamagoservice.dto.LoginRequest;
import com.tamago.tamagoservice.dto.UserResponse;
import com.tamago.tamagoservice.model.User;
import com.tamago.tamagoservice.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
public class AuthIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private UserService userService;

    private String baseUrl() {
        return "http://localhost:" + port + "/api/auth";
    }

    @BeforeEach
    public void setup() {
        // create a user directly via service (uses BCrypt)
        try {
            userService.createUser("intuser", "intpassword", "int@test.local");
        } catch (Exception ignored) {
            // if already exists from previous run, ignore
        }
    }

    @Test
    public void loginSuccessReturns200AndUser() {
        LoginRequest req = new LoginRequest();
        req.setPseudo("intuser");
        req.setMdp("intpassword");

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_TYPE, "application/json");

        ResponseEntity<UserResponse> resp = restTemplate.postForEntity(baseUrl() + "/login", req, UserResponse.class);

        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(resp.getBody()).isNotNull();
        assertThat(resp.getBody().getPseudo()).isEqualTo("intuser");
        assertThat(resp.getBody().getMail()).isEqualTo("int@test.local");
    }

    @Test
    public void loginFailureReturns401() {
        LoginRequest req = new LoginRequest();
        req.setPseudo("intuser");
        req.setMdp("wrongpassword");

        ResponseEntity<String> resp = restTemplate.postForEntity(baseUrl() + "/login", req, String.class);

        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(resp.getBody()).contains("AUTH_FAILED");
    }
}
