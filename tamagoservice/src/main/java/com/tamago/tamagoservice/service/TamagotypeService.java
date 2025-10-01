package com.tamago.tamagoservice.service;

import com.tamago.tamagoservice.dto.TamagotypeCreateRequest;
import com.tamago.tamagoservice.model.Tamagotype;
import com.tamago.tamagoservice.repository.TamagotypeRepository;
import org.springframework.stereotype.Service;

@Service
public class TamagotypeService {

    private final TamagotypeRepository repo;

    public TamagotypeService(TamagotypeRepository repo) {
        this.repo = repo;
    }

    public Tamagotype create(TamagotypeCreateRequest r) {
        if (repo.existsByNom(r.getNom())) {
            throw new IllegalArgumentException("Tamagotype with this name already exists");
        }

        Tamagotype t = new Tamagotype();
        t.setNom(r.getNom());
        t.setDescr(r.getDescr());
        t.setPv(r.getPv());
        t.setPf(r.getPf());
        t.setCouleur(r.getCouleur());
        t.setValueFaim(r.getValueFaim());
        t.setValueRegen(r.getValueRegen());
        t.setEstActif(r.getEstActif());
        t.setNomImg(r.getNomImg());

        return repo.save(t);
    }

    public java.util.List<Tamagotype> findAll() {
        return repo.findAll();
    }

    public java.util.List<Tamagotype> findAllActive() {
        return repo.findAllByEstActifTrue();
    }

    public Tamagotype findById(Long id) {
        return repo.findById(id).orElseThrow(() -> new com.tamago.tamagoservice.exception.ResourceNotFoundException("Tamagotype not found: " + id));
    }

    public Tamagotype update(Long id, com.tamago.tamagoservice.dto.TamagotypeUpdateRequest u) {
        Tamagotype t = findById(id);
        if (u.getNom() != null) t.setNom(u.getNom());
        if (u.getDescr() != null) t.setDescr(u.getDescr());
        if (u.getPv() != null) t.setPv(u.getPv());
        if (u.getPf() != null) t.setPf(u.getPf());
        if (u.getCouleur() != null) t.setCouleur(u.getCouleur());
        if (u.getValueFaim() != null) t.setValueFaim(u.getValueFaim());
        if (u.getValueRegen() != null) t.setValueRegen(u.getValueRegen());
        if (u.getEstActif() != null) t.setEstActif(u.getEstActif());
        if (u.getNomImg() != null) t.setNomImg(u.getNomImg());
        return repo.save(t);
    }

    /**
     * Replace the Tamagotype with the provided full representation (PUT semantics).
     */
    public Tamagotype replace(Long id, com.tamago.tamagoservice.dto.TamagotypeCreateRequest r) {
        Tamagotype t = findById(id);
        t.setNom(r.getNom());
        t.setDescr(r.getDescr());
        t.setPv(r.getPv());
        t.setPf(r.getPf());
        t.setCouleur(r.getCouleur());
        t.setValueFaim(r.getValueFaim());
        t.setValueRegen(r.getValueRegen());
        t.setEstActif(r.getEstActif());
        t.setNomImg(r.getNomImg());
        return repo.save(t);
    }

    public Tamagotype deactivate(Long id) {
        Tamagotype t = findById(id);
        t.setEstActif(false);
        return repo.save(t);
    }
}
