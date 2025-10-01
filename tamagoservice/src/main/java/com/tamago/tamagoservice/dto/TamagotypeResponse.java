package com.tamago.tamagoservice.dto;

public class TamagotypeResponse {
    private Long id;
    private String nom;
    private String descr;
    private Integer pv;
    private Integer pf;
    private String nomImg;
    private String couleur;
    private Integer valueFaim;
    private Integer valueRegen;
    private Boolean estActif;

    // getters/setters
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

    public static TamagotypeResponse fromEntity(com.tamago.tamagoservice.model.Tamagotype t) {
        TamagotypeResponse r = new TamagotypeResponse();
        r.setId(t.getId());
        r.setNom(t.getNom());
        r.setDescr(t.getDescr());
        r.setPv(t.getPv());
        r.setPf(t.getPf());
        r.setNomImg(t.getNomImg());
        r.setCouleur(t.getCouleur());
        r.setValueFaim(t.getValueFaim());
        r.setValueRegen(t.getValueRegen());
        r.setEstActif(t.getEstActif());
        return r;
    }
}
