package com.tamago.feedservice.controller;

import com.tamago.feedservice.repository.TamagoRepository;
import com.tamago.feedservice.repository.TamagotypeRepository;
import java.time.LocalDateTime;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import jakarta.servlet.http.HttpServletRequest;
import com.tamago.feedservice.dto.ChooseTamagoRequest;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    private final TamagoRepository tamagoRepo;
    private final TamagotypeRepository tamagotypeRepo;

    public UserController(TamagoRepository tamagoRepo, TamagotypeRepository tamagotypeRepo) {
        this.tamagoRepo = tamagoRepo;
        this.tamagotypeRepo = tamagotypeRepo;
    }

    @GetMapping("/{id}/has-living-tamago")
    public ResponseEntity<?> hasLivingTamago(@PathVariable("id") Long userId) {
        boolean exists = tamagoRepo.existsByIduserAndEstVivantTrue(userId);
        return ResponseEntity.ok(exists);
    }

    @GetMapping("/{id}/living-tamagos")
    public ResponseEntity<?> getLivingTamagos(@PathVariable("id") Long userId) {
        java.util.List<com.tamago.feedservice.model.Tamago> list = tamagoRepo.findByIduserAndEstVivantTrue(userId);
        java.util.List<com.tamago.feedservice.dto.TamagoResponse> resp = new java.util.ArrayList<>();
        for (com.tamago.feedservice.model.Tamago t : list) {
            resp.add(com.tamago.feedservice.dto.TamagoResponse.fromEntity(t));
        }
        return ResponseEntity.ok(resp);
    }

    @PostMapping("/{id}/choose-tamago")
    public ResponseEntity<?> chooseTamago(@PathVariable("id") Long userId, @RequestBody ChooseTamagoRequest req, HttpServletRequest request) {
        Object o = request.getAttribute("authUserId");
        if (o == null) {
            return ResponseEntity.status(401).body("Unauthorized");
        }
        Long authUserId = (Long) o;
        if (!authUserId.equals(userId)) {
            return ResponseEntity.status(403).body("Forbidden");
        }

        try {
            // If tamagoId is provided, claim existing tamago (ensure it exists and is alive)
            if (req.getTamagoId() != null) {
                Optional<com.tamago.feedservice.model.Tamago> opt = tamagoRepo.findByIdForUpdate(req.getTamagoId());
                if (opt.isEmpty()) {
                    return ResponseEntity.badRequest().body("Tamago not found");
                }
                com.tamago.feedservice.model.Tamago t = opt.get();
                t.setIduser(authUserId);
                tamagoRepo.save(t);
                log.info("User {} claimed tamago {}", authUserId, t.getId());
                return ResponseEntity.ok(com.tamago.feedservice.dto.TamagoResponse.fromEntity(t));
            }

            // Otherwise create a new Tamago from idtype + nom
            if (req.getIdtype() == null || req.getNom() == null) {
                return ResponseEntity.badRequest().body("idtype and nom required when creating a tamago");
            }
            com.tamago.feedservice.model.Tamagotype tt = tamagotypeRepo.findById(req.getIdtype()).orElse(null);
            if (tt == null) return ResponseEntity.badRequest().body("Tamagotype not found");
            com.tamago.feedservice.model.Tamago t = new com.tamago.feedservice.model.Tamago();
            t.setIdtype(req.getIdtype());
            t.setIduser(authUserId);
            t.setNom(req.getNom());
            // initialize to tamagotype defaults
            t.setPv(tt.getPv());
            t.setPf(tt.getPf());
            t.setEstVivant(true);
            t.setLastcon(LocalDateTime.now());
            tamagoRepo.save(t);
            log.info("User {} created tamago {} (type={})", authUserId, t.getId(), t.getIdtype());
            return ResponseEntity.ok(com.tamago.feedservice.dto.TamagoResponse.fromEntity(t));
        } catch (Exception ex) {
            log.error("Error choosing tamago", ex);
            return ResponseEntity.status(500).body("Internal error");
        }
    }

    // New simplified endpoint: infer userId from JWT, no need to pass it in URL.
    @PostMapping("/choose-tamago")
    public ResponseEntity<?> chooseTamagoSelf(@RequestBody ChooseTamagoRequest req, HttpServletRequest request) {
        Object o = request.getAttribute("authUserId");
        if (o == null) {
            return ResponseEntity.status(401).body("Unauthorized");
        }
        Long authUserId = (Long) o;
        // delegate to existing logic by reusing same method body (small duplication for clarity)
        try {
            if (req.getTamagoId() != null) {
                Optional<com.tamago.feedservice.model.Tamago> opt = tamagoRepo.findByIdForUpdate(req.getTamagoId());
                if (opt.isEmpty()) {
                    return ResponseEntity.badRequest().body("Tamago not found");
                }
                com.tamago.feedservice.model.Tamago t = opt.get();
                t.setIduser(authUserId);
                tamagoRepo.save(t);
                log.info("User {} claimed tamago {}", authUserId, t.getId());
                return ResponseEntity.ok(com.tamago.feedservice.dto.TamagoResponse.fromEntity(t));
            }

            if (req.getIdtype() == null || req.getNom() == null) {
                return ResponseEntity.badRequest().body("idtype and nom required when creating a tamago");
            }
            com.tamago.feedservice.model.Tamagotype tt = tamagotypeRepo.findById(req.getIdtype()).orElse(null);
            if (tt == null) return ResponseEntity.badRequest().body("Tamagotype not found");
            com.tamago.feedservice.model.Tamago t = new com.tamago.feedservice.model.Tamago();
            t.setIdtype(req.getIdtype());
            t.setIduser(authUserId);
            t.setNom(req.getNom());
            // initialize to tamagotype defaults
            t.setPv(tt.getPv());
            t.setPf(tt.getPf());
            t.setEstVivant(true);
            t.setLastcon(LocalDateTime.now());
            tamagoRepo.save(t);
            log.info("User {} created tamago {} (type={})", authUserId, t.getId(), t.getIdtype());
            return ResponseEntity.ok(com.tamago.feedservice.dto.TamagoResponse.fromEntity(t));
        } catch (Exception ex) {
            log.error("Error choosing tamago", ex);
            return ResponseEntity.status(500).body("Internal error");
        }
    }
}
