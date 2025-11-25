package com.padel.app.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.padel.app.dto.auth.LoginRequestDTO;
import com.padel.app.dto.auth.RegisterRequestDTO;
import com.padel.app.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AuthControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper mapper;

    @Autowired
    UserRepository userRepository;

    @Test
    void testRegisterSuccess() throws Exception {

        RegisterRequestDTO request = new RegisterRequestDTO(
                "Juan",
                "test_" + System.currentTimeMillis() + "@mail.com",
                "Password123",
                ""
        );

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value(request.email()));
    }


    @Test
    void testLoginSuccess() throws Exception {

        String email = "login_" + System.currentTimeMillis() + "@mail.com";

        RegisterRequestDTO register = new RegisterRequestDTO(
                "Marcos",
                email,
                "Password123",
                ""
        );

        // 1) registrar usuario
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(register)))
                .andExpect(status().isCreated());

        // 2) login
        LoginRequestDTO login = new LoginRequestDTO(email, "Password123");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(login)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token", notNullValue()));
    }


    @Test
    void testLoginWrongPassword() throws Exception {

        LoginRequestDTO login = new LoginRequestDTO(
                "noexist_" + System.currentTimeMillis() + "@mail.com",
                "wrongpass"
        );

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(login)))
                .andExpect(status().isUnauthorized());
    }
}
