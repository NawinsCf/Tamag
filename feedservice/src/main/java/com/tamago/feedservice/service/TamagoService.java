package com.tamago.feedservice.service;

import com.tamago.feedservice.dto.TamagoCreateRequest;
import com.tamago.feedservice.model.Tamago;
import com.tamago.feedservice.model.Tamagotype;
import com.tamago.feedservice.repository.TamagoRepository;
import com.tamago.feedservice.repository.TamagotypeRepository;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.Duration;
import java.util.Optional;

import java.time.LocalDateTime;

@Service
public class TamagoService {

    private final TamagoRepository repo;
    private final TamagotypeRepository tamagotypeRepo;

    public TamagoService(TamagoRepository repo, TamagotypeRepository tamagotypeRepo) {
        this.repo = repo;
        this.tamagotypeRepo = tamagotypeRepo;
    }

    public Tamago create(TamagoCreateRequest r, com.tamago.feedservice.model.Tamagotype tamagotype, com.tamago.feedservice.model.User user) {
        Tamago t = new Tamago();
        t.setIdtype(tamagotype.getId());
        t.setIduser(user.getId());
        String nom = r.getNom();
        if (nom == null || nom.trim().isEmpty()) nom = tamagotype.getNom();
        t.setNom(nom);
        t.setPv(tamagotype.getPv());
        t.setPf(tamagotype.getPf());
        t.setEstVivant(true);
        t.setLastcon(LocalDateTime.now());
        return repo.save(t);
    }

    @Transactional
    public Optional<Tamago> calculeFaim(Long tamagoId, Instant atInstant) {
        // obtain pessimistic lock for update to avoid concurrent modifications
        Tamago t = repo.findByIdForUpdate(tamagoId).orElse(null);
        if (t == null) return Optional.empty();

        Long typeId = t.getIdtype();
        Tamagotype tt = tamagotypeRepo.findById(typeId).orElse(null);
        if (tt == null) return Optional.of(t); // or handle as not found

        // convert lastcon (LocalDateTime) to Instant
        LocalDateTime lastcon = t.getLastcon();
        Instant lastInstant = lastcon.atZone(ZoneId.systemDefault()).toInstant();
        Instant at = atInstant != null ? atInstant : Instant.now();

        if (at.isBefore(lastInstant)) {
            // do not allow past recalculation
            return Optional.of(t);
        }

        // rates
        double pfRate = tt.getValueFaim(); // points of pf lost per minute
        double pvRate = tt.getValueRegen(); // points of pv lost per minute (as per option B)

        Duration dur = Duration.between(lastInstant, at);
        double elapsedMinutes = dur.toMillis() / 60000.0; // fractional minutes allowed

        double currentPf = t.getPf() != null ? t.getPf() : 0;
        double currentPv = t.getPv() != null ? t.getPv() : 0;

        double totalPfToRemove = elapsedMinutes * pfRate;

        double newPf = currentPf;
        double newPv = currentPv;
        boolean estVivant = t.getEstVivant();

        if (totalPfToRemove <= currentPf) {
            newPf = currentPf - totalPfToRemove;
            // pv unchanged
        } else {
            double remainingPfPoints = totalPfToRemove - currentPf;
            // time to consume current pf
            double timeToConsumePf = currentPf / pfRate; // minutes
            double timeRemaining = elapsedMinutes - timeToConsumePf;

            newPf = 0;
            double pvLost = timeRemaining * pvRate;
            newPv = currentPv - pvLost;
            if (newPv <= 0) {
                newPv = 0;
                newPf = 0;
                estVivant = false;
            }
        }

        // store integer values (floor)
        t.setPf((int)Math.floor(Math.max(newPf, 0)));
        t.setPv((int)Math.floor(Math.max(newPv, 0)));
        t.setEstVivant(estVivant);
        t.setLastcon(LocalDateTime.ofInstant(at, ZoneId.systemDefault()));

        Tamago saved = repo.save(t);
        return Optional.of(saved);
    }

    @Transactional
    public Optional<Tamago> updateTamago(Long tamagoId, Boolean kill, String nom) {
        // obtain pessimistic lock for update to avoid concurrent modifications
        Tamago t = repo.findByIdForUpdate(tamagoId).orElse(null);
        if (t == null) return Optional.empty();

        boolean modified = false;
        if (kill != null && kill) {
            t.setPv(0);
            t.setPf(0);
            t.setEstVivant(false);
            modified = true;
        }
        if (nom != null) {
            // if provided but blank, replace with Tamagotype name
            String newName = nom.trim();
            if (newName.isEmpty()) {
                Long typeId = t.getIdtype();
                Tamagotype tt = tamagotypeRepo.findById(typeId).orElse(null);
                if (tt != null && tt.getNom() != null) {
                    newName = tt.getNom();
                }
            }
            if (!newName.isEmpty()) {
                t.setNom(newName);
                modified = true;
            }
        }

        if (modified) {
            Tamago saved = repo.save(t);
            return Optional.of(saved);
        }
        return Optional.of(t);
    }

    @Transactional
    public Optional<Tamago> nourrirTamago(Long tamagoId) {
        // lock row for update
        Tamago t = repo.findByIdForUpdate(tamagoId).orElse(null);
        if (t == null) return Optional.empty();

        // reload type
        Long typeId = t.getIdtype();
        Tamagotype tt = tamagotypeRepo.findById(typeId).orElse(null);
        if (tt == null) {
            throw new com.tamago.feedservice.exception.ResourceNotFoundException("Tamagotype not found: " + typeId);
        }

        if (Boolean.TRUE.equals(t.getEstVivant())) {
            // reset pv/pf to tamagotype defaults and update lastcon
            t.setPf(tt.getPf());
            t.setPv(tt.getPv());
            t.setLastcon(LocalDateTime.now());
            Tamago saved = repo.save(t);
            return Optional.of(saved);
        }

        // Tamago is dead; do nothing
        return Optional.of(t);
    }

    public org.springframework.data.domain.Page<Tamago> page(int page, int size) {
        org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest.of(page, size);
        return repo.findAll(pageable);
    }

    // Return all tamagos (used by controller's non-paginated endpoint)
    public java.util.List<Tamago> findAll() {
        return repo.findAll();
    }
}
