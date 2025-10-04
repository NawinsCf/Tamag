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
        /**
         * Crée un Tamagotype à partir de la requête fournie. Vérifie l'unicité du nom.
         * @param r données de création
         * @return Tamagotype créé
         */
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
        /**
         * Retourne tous les Tamagotypes.
         * @return liste de Tamagotype
         */
        return repo.findAll();
    }

    public org.springframework.data.domain.Page<Tamagotype> page(int page, int size, String q, String sort) {
        org.springframework.data.domain.Sort sortObj = org.springframework.data.domain.Sort.by("id");
        if (sort != null && !sort.isBlank()) {
            if (sort.startsWith("-")) {
                sortObj = org.springframework.data.domain.Sort.by(sort.substring(1)).descending();
            } else {
                sortObj = org.springframework.data.domain.Sort.by(sort).ascending();
            }
        }
        org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest.of(page, size, sortObj);
        if (q == null || q.isBlank()) {
            return repo.findAll(pageable);
        }
        String pattern = "%" + q.toLowerCase() + "%";
        return repo.findByNomIgnoreCaseContainingOrDescrIgnoreCaseContaining(q, q, pageable);
    }

    public java.util.List<Tamagotype> findAllActive() {
        return repo.findAllByEstActifTrue();
    }

    public Tamagotype findById(Long id) {
        /**
         * Retourne un Tamagotype par identifiant ou lève une exception si non trouvé.
         * @param id identifiant
         * @return Tamagotype trouvé
         */
        return repo.findById(id).orElseThrow(() -> new com.tamago.tamagoservice.exception.ResourceNotFoundException("Tamagotype not found: " + id));
    }

    public Tamagotype update(Long id, com.tamago.tamagoservice.dto.TamagotypeUpdateRequest u) {
        /**
         * Mise à jour partielle (PATCH) d'un Tamagotype : n'applique que les champs non nuls.
         * @param id identifiant
         * @param u requête de mise à jour
         * @return Tamagotype mis à jour
         */
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
        /**
         * Remplace complètement le Tamagotype (PUT semantics) par la représentation fournie.
         */
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
