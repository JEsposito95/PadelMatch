package com.padel.app.court;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.padel.app.model.User;
import com.padel.app.repository.UserRepository;
import com.padel.app.security.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class CourtControllerTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper mapper;
    @Autowired UserRepository userRepository;
    @Autowired JwtService jwtService;

    String ownerToken;
    String adminToken;

    @BeforeEach
    void setup() {
        userRepository.deleteAll();

        // OWNER
        User owner = new User();
        owner.setEmail("owner@test.com");
        owner.setPassword("123456");
        owner.setNameUser("Owner");
        owner.setRole(User.Role.OWNER);
        owner.setPoints(0);
        owner.setCreatedAt(LocalDateTime.now());
        owner.setUpdatedAt(LocalDateTime.now());
        userRepository.save(owner);
        ownerToken = jwtService.generateToken(owner);

        // ADMIN
        User admin = new User();
        admin.setEmail("admin@test.com");
        admin.setPassword("123456");
        admin.setNameUser("Admin");
        admin.setRole(User.Role.ADMIN);
        admin.setPoints(0);
        admin.setCreatedAt(LocalDateTime.now());
        admin.setUpdatedAt(LocalDateTime.now());
        userRepository.save(admin);
        adminToken = jwtService.generateToken(admin);
    }

    // =========================================================
    // 1) OWNER puede crear, actualizar y eliminar una cancha
    // =========================================================
    @Test
    void ownerCanCreateUpdateDeleteCourt_flow() throws Exception {

        String payload = """
        {
          "nameCourt": "Cancha 1",
          "direction": "Calle Falsa 123",
          "lat": -34.60,
          "lng": -58.38,
          "price": 3000
        }
        """;

        // CREAR
        var result = mockMvc.perform(post("/api/courts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + ownerToken)
                        .content(payload))
                .andExpect(status().isCreated())
                .andReturn();

        Long courtId = mapper.readTree(result.getResponse().getContentAsString())
                .get("idCourt").asLong();

        // ACTUALIZAR
        String updatePayload = """
        {
          "nameCourt": "Cancha Actualizada",
          "direction": "Av Libertador 999",
          "lat": -34.61,
          "lng": -58.40,
          "price": 3500
        }
        """;

        mockMvc.perform(put("/api/courts/" + courtId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + ownerToken)
                        .content(updatePayload))
                .andExpect(status().isOk());

        // ELIMINAR
        mockMvc.perform(delete("/api/courts/" + courtId)
                        .header("Authorization", "Bearer " + ownerToken))
                .andExpect(status().isOk());
    }

    // =========================================================
    // 2) Un USER no autorizado NO puede crear canchas
    // =========================================================
    @Test
    void onlyAdminOrOwnerCanCreateOrModifyCourts() throws Exception {

        User normal = new User();
        normal.setEmail("user@test.com");
        normal.setPassword("123456");
        normal.setNameUser("Normal User");
        normal.setRole(User.Role.USER);
        normal.setCreatedAt(LocalDateTime.now());
        normal.setUpdatedAt(LocalDateTime.now());
        userRepository.save(normal);

        String userToken = jwtService.generateToken(normal);

        String payload = """
        {
          "nameCourt": "Test",
          "direction": "Lugar",
          "lat": -34.6,
          "lng": -58.4,
          "price": 2000
        }
        """;

        mockMvc.perform(post("/api/courts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization","Bearer " + userToken)
                        .content(payload))
                .andExpect(status().isForbidden());
    }

    // =========================================================
    // 3) Endpoint de availability funciona
    // =========================================================
    @Test
    void availabilityEndpoint_filtersBookedCourts() throws Exception {

        String payload = """
        {
          "nameCourt": "Cancha A",
          "direction": "Calle 100",
          "lat": -34.6,
          "lng": -58.4,
          "price": 2500
        }
        """;

        mockMvc.perform(post("/api/courts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization","Bearer " + ownerToken)
                        .content(payload))
                .andExpect(status().isCreated());

        // DISPONIBILIDAD (público)
        mockMvc.perform(get("/api/courts/availability")
                        .param("startTime","2025-01-01T10:00:00")
                        .param("endTime","2025-01-01T11:00:00"))
                .andExpect(status().isOk());
    }
}
