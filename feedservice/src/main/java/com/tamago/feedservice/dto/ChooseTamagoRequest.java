package com.tamago.feedservice.dto;

public class ChooseTamagoRequest {
    private Long tamagoId;
    private Long idtype;
    private String nom;

    public Long getTamagoId() { return tamagoId; }
    public void setTamagoId(Long tamagoId) { this.tamagoId = tamagoId; }

    public Long getIdtype() { return idtype; }
    public void setIdtype(Long idtype) { this.idtype = idtype; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }
}
