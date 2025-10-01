package com.tamago.feedservice.controller;

import com.tamago.feedservice.repository.TamagoRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private final TamagoRepository tamagoRepo;

    public UserController(TamagoRepository tamagoRepo) {
        this.tamagoRepo = tamagoRepo;
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
}
