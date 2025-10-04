package com.tamago.tamagoservice.controller;

import com.tamago.tamagoservice.dto.TamagotypeCreateRequest;
import com.tamago.tamagoservice.dto.TamagotypeResponse;
import com.tamago.tamagoservice.model.Tamagotype;
import com.tamago.tamagoservice.service.TamagotypeService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

@RestController
@RequestMapping("/api/tamagotype")
public class TamagotypeController {

    private final TamagotypeService service;

    public TamagotypeController(TamagotypeService service) {
        this.service = service;
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public TamagotypeResponse create(@Valid @RequestBody TamagotypeCreateRequest data) {
        Tamagotype t = service.create(data);
        TamagotypeResponse resp = new TamagotypeResponse();
        resp.setId(t.getId());
        resp.setNom(t.getNom());
        resp.setDescr(t.getDescr());
        resp.setPf(t.getPf());
        resp.setPv(t.getPv());
        resp.setNomImg(t.getNomImg());
        resp.setCouleur(t.getCouleur());
        resp.setValueFaim(t.getValueFaim());
        resp.setValueRegen(t.getValueRegen());
        resp.setEstActif(t.getEstActif());
        return resp;
    }
    
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<java.util.List<TamagotypeResponse>> findAll() {
        java.util.List<Tamagotype> list = service.findAll();
        java.util.List<TamagotypeResponse> resp = list.stream().map(TamagotypeResponse::fromEntity).toList();
        return ResponseEntity.ok(resp);
    }

    // Paginated endpoint for admin UI: supports page, size, optional q (search by name/descr), and sort (e.g. nom, -pv)
    @GetMapping(value = "/page", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<org.springframework.data.domain.Page<TamagotypeResponse>> page(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "20") int size,
            @RequestParam(value = "q", required = false) String q,
            @RequestParam(value = "sort", required = false) String sort
    ) {
        org.springframework.data.domain.Page<com.tamago.tamagoservice.model.Tamagotype> p = service.page(page, size, q, sort);
        org.springframework.data.domain.Page<TamagotypeResponse> resp = p.map(TamagotypeResponse::fromEntity);
        return ResponseEntity.ok(resp);
    }

    @GetMapping(value = "/active", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<java.util.List<TamagotypeResponse>> findAllActive() {
        java.util.List<Tamagotype> list = service.findAllActive();
        java.util.List<TamagotypeResponse> resp = list.stream().map(TamagotypeResponse::fromEntity).toList();
        return ResponseEntity.ok(resp);
    }

    @GetMapping(value = "/{id:\\d+}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<TamagotypeResponse> findById(@PathVariable("id") Long id) {
        Tamagotype t = service.findById(id);
        return ResponseEntity.ok(TamagotypeResponse.fromEntity(t));
    }

    // PUT = replace (full representation required)
    @PutMapping(value = "/{id:\\d+}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<TamagotypeResponse> replace(@PathVariable("id") Long id, @Valid @RequestBody TamagotypeCreateRequest request) {
        Tamagotype replaced = service.replace(id, request);
        return ResponseEntity.ok(TamagotypeResponse.fromEntity(replaced));
    }

    // PATCH = partial update
    @PatchMapping(value = "/{id:\\d+}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<TamagotypeResponse> patch(@PathVariable("id") Long id, @RequestBody com.tamago.tamagoservice.dto.TamagotypeUpdateRequest request) {
        Tamagotype updated = service.update(id, request);
        return ResponseEntity.ok(TamagotypeResponse.fromEntity(updated));
    }

    // PATCH to deactivate a Tamagotype
    @PatchMapping(value = "/{id:\\d+}/deactivate", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<TamagotypeResponse> deactivate(@PathVariable("id") Long id) {
        Tamagotype t = service.deactivate(id);
        return ResponseEntity.ok(TamagotypeResponse.fromEntity(t));
    }
}
