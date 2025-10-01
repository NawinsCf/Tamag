package com.tamago.feedservice.controller;

import com.tamago.feedservice.dto.TamagoCreateRequest;
import com.tamago.feedservice.dto.TamagoResponse;
import com.tamago.feedservice.service.TamagoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.time.Instant;
import java.time.format.DateTimeParseException;
import java.util.Map;
import java.util.Optional;
import com.tamago.feedservice.model.Tamago;

@RestController
@RequestMapping("/api/tamago")
public class TamagoController {

    private final TamagoService service;
    private final com.tamago.feedservice.repository.TamagotypeRepository tamagotypeRepo;
    private final com.tamago.feedservice.repository.UserRepository userRepo;

    public TamagoController(TamagoService service, com.tamago.feedservice.repository.TamagotypeRepository tamagotypeRepo, com.tamago.feedservice.repository.UserRepository userRepo) {
        this.service = service;
        this.tamagotypeRepo = tamagotypeRepo;
        this.userRepo = userRepo;
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public TamagoResponse create(@Valid @RequestBody TamagoCreateRequest req) {
        // load user
        com.tamago.feedservice.model.User user = userRepo.findById(req.getIdUser()).orElseThrow(() -> new com.tamago.feedservice.exception.ResourceNotFoundException("User not found: " + req.getIdUser()));
        // load tamagotype
        com.tamago.feedservice.model.Tamagotype tt = tamagotypeRepo.findById(req.getIdTamagotype()).orElseThrow(() -> new com.tamago.feedservice.exception.ResourceNotFoundException("Tamagotype not found: " + req.getIdTamagotype()));
        com.tamago.feedservice.model.Tamago t = service.create(req, tt, user);
        return TamagoResponse.fromEntity(t);
    }

    /**
     * Compute hunger/health decay from tamago.lastcon to 'at' (optional, defaults to now).
     * Example: POST /api/tamago/1/calculefaim?at=2025-09-29T10:00:00Z
     */
    @PostMapping("/{id}/calculefaim")
    public ResponseEntity<?> calculeFaim(@PathVariable("id") Long id,
                                         @RequestParam(value = "at", required = false) String atIso) {
        Instant at = null;
        if (atIso != null && !atIso.isBlank()) {
            try {
                at = Instant.parse(atIso);
            } catch (DateTimeParseException e) {
                throw new com.tamago.feedservice.exception.BadRequestException("Invalid datetime format for 'at', use ISO-8601 UTC instant");
            }
        }

        Optional<Tamago> updated = service.calculeFaim(id, at);
        if (updated.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "NOT_FOUND", "message", "Tamago not found"));
        }
        return ResponseEntity.ok(TamagoResponse.fromEntity(updated.get()));
    }

    @PatchMapping(path = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> update(@PathVariable("id") Long id,
                                    @Valid @RequestBody com.tamago.feedservice.dto.TamagoUpdateRequest req,
                                    @RequestParam(value = "returnEntity", required = false) Boolean returnEntity) {
        Optional<Tamago> updated = service.updateTamago(id, req.getKill(), req.getNom());
        if (updated.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "NOT_FOUND", "message", "Tamago not found"));
        }

        // By default return 204 No Content for PATCH success. Clients may request the updated entity
        // by setting ?returnEntity=true.
        if (Boolean.TRUE.equals(returnEntity)) {
            return ResponseEntity.ok(TamagoResponse.fromEntity(updated.get()));
        }
        return ResponseEntity.noContent().build();
    }

    /**
     * Feed (nourrir) the tamago: recalc hunger, then if still alive reset pv/pf to type defaults and update lastcon.
     */
    @PostMapping(path = "/{id}/nourrir")
    public ResponseEntity<?> nourrir(@PathVariable("id") Long id,
                                     @RequestParam(value = "returnEntity", required = false) Boolean returnEntity) {
        // First, recalculate hunger/health to bring Tamago up to date
        Optional<com.tamago.feedservice.model.Tamago> afterCalc = service.calculeFaim(id, null);
        if (afterCalc.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "NOT_FOUND", "message", "Tamago not found"));
        }

        // Attempt to feed
        Optional<com.tamago.feedservice.model.Tamago> fed = service.nourrirTamago(id);
        if (fed.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "NOT_FOUND", "message", "Tamago not found"));
        }

        com.tamago.feedservice.model.Tamago t = fed.get();
        if (Boolean.TRUE.equals(returnEntity)) {
            return ResponseEntity.ok(TamagoResponse.fromEntity(t));
        }
        return ResponseEntity.noContent().build();
    }
}
