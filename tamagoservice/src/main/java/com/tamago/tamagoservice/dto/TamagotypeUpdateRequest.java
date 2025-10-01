package com.tamago.tamagoservice.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class TamagotypeUpdateRequest {

    private String nom;

    private String descr;

    @Min(0)
    private Integer pv;

    @Min(0)
    private Integer pf;

    @Pattern(regexp = "^#([A-Fa-f0-9]{6})$", message = "couleur must be a hex color like #RRGGBB")
    private String couleur;

    private Integer valueFaim;

    private Integer valueRegen;

    private Boolean estActif;

    @Size(max = 100)
    private String nomImg;

    // getters/setters
    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getDescr() { return descr; }
    public void setDescr(String descr) { this.descr = descr; }

    public Integer getPv() { return pv; }
    public void setPv(Integer pv) { this.pv = pv; }

    public Integer getPf() { return pf; }
    public void setPf(Integer pf) { this.pf = pf; }

    public String getCouleur() { return couleur; }
    public void setCouleur(String couleur) { this.couleur = couleur; }

    public Integer getValueFaim() { return valueFaim; }
    public void setValueFaim(Integer valueFaim) { this.valueFaim = valueFaim; }

    public Integer getValueRegen() { return valueRegen; }
    public void setValueRegen(Integer valueRegen) { this.valueRegen = valueRegen; }

    public Boolean getEstActif() { return estActif; }
    public void setEstActif(Boolean estActif) { this.estActif = estActif; }

    public String getNomImg() { return nomImg; }
    public void setNomImg(String nomImg) { this.nomImg = nomImg; }
}
