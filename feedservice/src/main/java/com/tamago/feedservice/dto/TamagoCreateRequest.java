package com.tamago.feedservice.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class TamagoCreateRequest {
    @NotNull(message = "idUser is required")
    private Long idUser;

    @NotNull(message = "idTamagotype is required")
    private Long idTamagotype;

    @Size(max = 100, message = "nom must be at most 100 characters")
    private String nom;

    public Long getIdUser() { return idUser; }
    public void setIdUser(Long idUser) { this.idUser = idUser; }

    public Long getIdTamagotype() { return idTamagotype; }
    public void setIdTamagotype(Long idTamagotype) { this.idTamagotype = idTamagotype; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }
}
