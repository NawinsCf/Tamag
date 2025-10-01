package com.tamago.feedservice.model;

import jakarta.persistence.*;

@Entity
@Table(name = "tamagotype")
public class Tamagotype {
    @Id
    private Long id;

    @Column(nullable = false, length = 50)
    private String nom;

    private Integer pv;

    private Integer pf;
    
    @Column(name = "value_faim", nullable = false)
    private Integer valueFaim;

    @Column(name = "value_regen", nullable = false)
    private Integer valueRegen;

    // getters/setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public Integer getPv() { return pv; }
    public void setPv(Integer pv) { this.pv = pv; }

    public Integer getPf() { return pf; }
    public void setPf(Integer pf) { this.pf = pf; }

    public Integer getValueFaim() { return valueFaim; }
    public void setValueFaim(Integer valueFaim) { this.valueFaim = valueFaim; }

    public Integer getValueRegen() { return valueRegen; }
    public void setValueRegen(Integer valueRegen) { this.valueRegen = valueRegen; }
}
