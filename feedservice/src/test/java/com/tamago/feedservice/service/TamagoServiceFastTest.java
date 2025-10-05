package com.tamago.feedservice.service;

import com.tamago.feedservice.exception.ResourceNotFoundException;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TamagoServiceFastTest {

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
    void calculeFaim_deadWhenPvDropsToZero() {
        Tamago t = new Tamago();
        t.setId(10L);
        t.setPf(0);
        t.setPv(1);
        t.setEstVivant(true);
        t.setLastcon(LocalDateTime.now().minusMinutes(60));
        t.setIdtype(5L);

        Tamagotype tt = new Tamagotype();
        tt.setId(5L);
        tt.setValueFaim(1); // pf per minute
        tt.setValueRegen(1); // pv per minute

        when(repo.findByIdForUpdate(10L)).thenReturn(Optional.of(t));
        when(tamagotypeRepo.findById(5L)).thenReturn(Optional.of(tt));
        when(repo.save(any())).thenAnswer(i -> i.getArgument(0));

        Instant at = t.getLastcon().atZone(ZoneId.systemDefault()).toInstant().plusSeconds(60 * 60);
        Optional<Tamago> res = service.calculeFaim(10L, at);
        assertTrue(res.isPresent());
        Tamago out = res.get();
        assertEquals(0, out.getPv());
        assertEquals(0, out.getPf());
        assertFalse(out.getEstVivant());
    }

    @Test
    void calculeFaim_handlesNullPfPv() {
        Tamago t = new Tamago();
        t.setId(11L);
        t.setPf(null);
        t.setPv(null);
        t.setEstVivant(true);
        t.setLastcon(LocalDateTime.now().minusMinutes(30));
        t.setIdtype(6L);

        Tamagotype tt = new Tamagotype();
        tt.setId(6L);
        tt.setValueFaim(2);
        tt.setValueRegen(1);

        when(repo.findByIdForUpdate(11L)).thenReturn(Optional.of(t));
        when(tamagotypeRepo.findById(6L)).thenReturn(Optional.of(tt));
        when(repo.save(any())).thenAnswer(i -> i.getArgument(0));

        Instant at = t.getLastcon().atZone(ZoneId.systemDefault()).toInstant().plusSeconds(30 * 60);
        Optional<Tamago> res = service.calculeFaim(11L, at);
        assertTrue(res.isPresent());
        Tamago out = res.get();
        // starting from null -> treated as 0 -> pf should remain 0 and pv 0 (dead)
        assertEquals(0, out.getPf());
        assertEquals(0, out.getPv());
    }

    @Test
    void nourrirTamago_restoresToTamagotype_whenAlive() {
        Tamago t = new Tamago();
        t.setId(20L);
        t.setPf(1);
        t.setPv(1);
        t.setEstVivant(true);
        t.setLastcon(LocalDateTime.now().minusMinutes(5));
        t.setIdtype(7L);

        Tamagotype tt = new Tamagotype();
        tt.setId(7L);
        tt.setPf(50);
        tt.setPv(80);

        when(repo.findByIdForUpdate(20L)).thenReturn(Optional.of(t));
        when(tamagotypeRepo.findById(7L)).thenReturn(Optional.of(tt));
        when(repo.save(any())).thenAnswer(i -> i.getArgument(0));

        Optional<Tamago> res = service.nourrirTamago(20L);
        assertTrue(res.isPresent());
        Tamago out = res.get();
        assertEquals(50, out.getPf());
        assertEquals(80, out.getPv());
        assertTrue(out.getEstVivant());
    }

    @Test
    void nourrirTamago_throwsWhenTamagotypeMissing() {
        Tamago t = new Tamago();
        t.setId(21L);
        t.setPf(1);
        t.setPv(1);
        t.setEstVivant(true);
        t.setLastcon(LocalDateTime.now().minusMinutes(5));
        t.setIdtype(999L);

        when(repo.findByIdForUpdate(21L)).thenReturn(Optional.of(t));
        when(tamagotypeRepo.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.nourrirTamago(21L));
    }
}
