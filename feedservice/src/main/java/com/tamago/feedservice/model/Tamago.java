package com.tamago.feedservice.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "tamago")
public class Tamago {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "idtype", nullable = false)
    private Long idtype;

    @Column(name = "iduser", nullable = false)
    private Long iduser;

    @Column(nullable = false, length = 50)
    private String nom;

    private Integer pv;

    private Integer pf;

    @Column(name = "est_vivant")
    private Boolean estVivant = true;

    @Column(name = "lastcon")
    private LocalDateTime lastcon = LocalDateTime.now();

    // getters / setters
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
}
