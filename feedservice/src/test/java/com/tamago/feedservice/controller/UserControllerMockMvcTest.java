package com.tamago.feedservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tamago.feedservice.dto.ChooseTamagoRequest;
import com.tamago.feedservice.model.Tamago;
import com.tamago.feedservice.model.Tamagotype;
import com.tamago.feedservice.repository.TamagoRepository;
import com.tamago.feedservice.repository.TamagotypeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@ExtendWith(MockitoExtension.class)
public class UserControllerMockMvcTest {

    @Mock
    private TamagoRepository tamagoRepo;

    @Mock
    private TamagotypeRepository tamagotypeRepo;

    private UserController controller;
    private MockMvc mockMvc;
    private ObjectMapper mapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        controller = new UserController(tamagoRepo, tamagotypeRepo);
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .defaultRequest(post("/api/user/choose-tamago").requestAttr("authUserId", 42L))
                .build();
    }

    @Test
    void chooseTamagoSelf_createsTamagoWhenValid() throws Exception {
        ChooseTamagoRequest req = new ChooseTamagoRequest();
        req.setIdtype(2L);
        req.setNom("MonTamago");

        Tamagotype tt = new Tamagotype();
        tt.setId(2L);
        tt.setPv(30);
        tt.setPf(40);

        when(tamagotypeRepo.findById(2L)).thenReturn(Optional.of(tt));
        when(tamagoRepo.save(any())).thenAnswer(i -> {
            Tamago t = (Tamago) i.getArgument(0);
            t.setId(100L);
            return t;
        });

        mockMvc.perform(post("/api/user/choose-tamago")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(req))
                .requestAttr("authUserId", 42L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(100))
                .andExpect(jsonPath("$.nom").value("MonTamago"));
    }

    @Test
    void chooseTamagoSelf_claimsExistingTamago() throws Exception {
        ChooseTamagoRequest req = new ChooseTamagoRequest();
        req.setTamagoId(55L);

        Tamago t = new Tamago();
        t.setId(55L);
        t.setIduser(1L);
        t.setEstVivant(true);
        t.setLastcon(LocalDateTime.now());

        when(tamagoRepo.findByIdForUpdate(55L)).thenReturn(Optional.of(t));
        when(tamagoRepo.save(any())).thenAnswer(i -> i.getArgument(0));

        mockMvc.perform(post("/api/user/choose-tamago")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(req))
                .requestAttr("authUserId", 42L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(55))
                .andExpect(jsonPath("$.iduser").value(42));
    }
}
