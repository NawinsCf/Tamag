package com.tamago.feedservice.dto;

import jakarta.validation.constraints.Size;

public class TamagoUpdateRequest {
    private Boolean kill;

    @Size(max = 100)
    private String nom;

    public Boolean getKill() { return kill; }
    public void setKill(Boolean kill) { this.kill = kill; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }
}
