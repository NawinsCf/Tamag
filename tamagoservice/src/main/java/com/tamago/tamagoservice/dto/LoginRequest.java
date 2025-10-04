package com.tamago.tamagoservice.dto;

import jakarta.validation.constraints.NotBlank;

public class LoginRequest {

    @NotBlank
    private String pseudo;

    @NotBlank
    private String mdp;

    /** Optional raw password provided during a transition period. Not required in new clients. */
    private String mdpRaw;

    public String getPseudo() {
        return pseudo;
    }

    public void setPseudo(String pseudo) {
        this.pseudo = pseudo;
    }

    public String getMdp() {
        return mdp;
    }

    public void setMdp(String mdp) {
        this.mdp = mdp;
    }

    public String getMdpRaw() {
        return mdpRaw;
    }

    public void setMdpRaw(String mdpRaw) {
        this.mdpRaw = mdpRaw;
    }
}
