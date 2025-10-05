package com.tamago.feedservice.service;

import com.tamago.feedservice.model.Tamago;
import com.tamago.feedservice.model.Tamagotype;
import com.tamago.feedservice.repository.TamagoRepository;
import com.tamago.feedservice.repository.TamagotypeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class TamagoServiceTest {

    @Mock
    private TamagoRepository repo;

    @Mock
    private TamagotypeRepository tamagotypeRepo;

    @InjectMocks
    private TamagoService service;

    @BeforeEach
    void setUp() {
    }

    @Test
    void calculeFaim_partialPfDecrease_pvUnchanged() {
        // Tamago with pf=100, pv=50, lastcon 10 minutes ago
        Tamago t = new Tamago();
        t.setId(1L);
        t.setPf(100);
        t.setPv(50);
        t.setEstVivant(true);
        t.setLastcon(LocalDateTime.now().minusMinutes(10));
        t.setIdtype(1L);

        Tamagotype tt = new Tamagotype();
        tt.setId(1L);
        tt.setValueFaim(1); // pf lost per minute
        tt.setValueRegen(1); // pv lost per minute

        when(repo.findByIdForUpdate(1L)).thenReturn(Optional.of(t));
        when(tamagotypeRepo.findById(1L)).thenReturn(Optional.of(tt));
        when(repo.save(any())).thenAnswer(i -> i.getArgument(0));

    Instant at = t.getLastcon().atZone(ZoneId.systemDefault()).toInstant().plusSeconds(10 * 60);
    Optional<Tamago> res = service.calculeFaim(1L, at);
        assertTrue(res.isPresent());
        Tamago out = res.get();
        // pf should decrease by ~10 -> 90
        assertEquals(90, out.getPf());
        // pv unchanged (still 50)
        assertEquals(50, out.getPv());
        assertTrue(out.getEstVivant());
    }

    @Test
    void calculeFaim_pfConsumed_thenPvDecreases_andStillAlive() {
        // Tamago with pf=5, pv=20, lastcon 10 minutes ago -> pf consumed in 5m, remaining 5m reduce pv
        Tamago t = new Tamago();
        t.setId(2L);
        t.setPf(5);
        t.setPv(20);
        t.setEstVivant(true);
        t.setLastcon(LocalDateTime.now().minusMinutes(10));
        t.setIdtype(2L);

        Tamagotype tt = new Tamagotype();
        tt.setId(2L);
        tt.setValueFaim(1); // pf per minute
        tt.setValueRegen(2); // pv per minute when pf exhausted

        when(repo.findByIdForUpdate(2L)).thenReturn(Optional.of(t));
        when(tamagotypeRepo.findById(2L)).thenReturn(Optional.of(tt));
        when(repo.save(any())).thenAnswer(i -> i.getArgument(0));

    Instant at2 = t.getLastcon().atZone(ZoneId.systemDefault()).toInstant().plusSeconds(10 * 60);
    Optional<Tamago> res = service.calculeFaim(2L, at2);
        assertTrue(res.isPresent());
        Tamago out = res.get();
        // pf should be zero
        assertEquals(0, out.getPf());
        // pv lost = (elapsedMinutes - timeToConsumePf) * pvRate
        // elapsed 10, timeToConsumePf = 5, remaining 5 * 2 = 10 -> new pv = 20 - 10 = 10
        assertEquals(10, out.getPv());
        assertTrue(out.getEstVivant());
    }
}
