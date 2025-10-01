package com.tamago.feedservice.dto;

import java.time.LocalDateTime;

public class TamagoResponse {
    private Long id;
    private Long idtype;
    private Long iduser;
    private String nom;
    private Integer pv;
    private Integer pf;
    private Boolean estVivant;
    private LocalDateTime lastcon;

    // getters/setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getIdtype() { return idtype; }
    public void setIdtype(Long idtype) { this.idtype = idtype; }

    public Long getIduser() { return iduser; }
    public void setIduser(Long iduser) { this.iduser = iduser; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public Integer getPv() { return pv; }
    public void setPv(Integer pv) { this.pv = pv; }

    public Integer getPf() { return pf; }
    public void setPf(Integer pf) { this.pf = pf; }

    public Boolean getEstVivant() { return estVivant; }
    public void setEstVivant(Boolean estVivant) { this.estVivant = estVivant; }

    public LocalDateTime getLastcon() { return lastcon; }
    public void setLastcon(LocalDateTime lastcon) { this.lastcon = lastcon; }

    public static TamagoResponse fromEntity(com.tamago.feedservice.model.Tamago t) {
        TamagoResponse r = new TamagoResponse();
        r.setId(t.getId());
        r.setIdtype(t.getIdtype());
        r.setIduser(t.getIduser());
        r.setNom(t.getNom());
        r.setPv(t.getPv());
        r.setPf(t.getPf());
        r.setEstVivant(t.getEstVivant());
        r.setLastcon(t.getLastcon());
        return r;
    }
}
