package com.tamago.tamagoservice.model;

import jakarta.persistence.*;

@Entity
@Table(name = "tamagotype")
public class Tamagotype {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String nom;

    @Column(nullable = false, length = 250)
    private String descr;

    private Integer pv;

    private Integer pf;

    @Column(name = "nom_img", length = 100)
    private String nomImg;

    @Column(length = 7)
    private String couleur; // hex color like #RRGGBB

    @Column(name = "value_faim")
    private Integer valueFaim = 3;

    @Column(name = "value_regen")
    private Integer valueRegen = 1;

    @Column(name = "est_actif")
    private Boolean estActif = true;

    // getters & setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getDescr() { return descr; }
    public void setDescr(String descr) { this.descr = descr; }

    public Integer getPv() { return pv; }
    public void setPv(Integer pv) { this.pv = pv; }

    public Integer getPf() { return pf; }
    public void setPf(Integer pf) { this.pf = pf; }

    public String getNomImg() { return nomImg; }
    public void setNomImg(String nomImg) { this.nomImg = nomImg; }

    public String getCouleur() { return couleur; }
    public void setCouleur(String couleur) { this.couleur = couleur; }

    public Integer getValueFaim() { return valueFaim; }
    public void setValueFaim(Integer valueFaim) { this.valueFaim = valueFaim; }

    public Integer getValueRegen() { return valueRegen; }
    public void setValueRegen(Integer valueRegen) { this.valueRegen = valueRegen; }

    public Boolean getEstActif() { return estActif; }
    public void setEstActif(Boolean estActif) { this.estActif = estActif; }
}
