package com.tamago.tamagoservice.dto;

public class UserResponse {

    private Long id;
    private String pseudo;
    private String mail;
    private Boolean estAdmin;

    public UserResponse() {
    }

    public UserResponse(Long id, String pseudo, String mail, Boolean estAdmin) {
        this.id = id;
        this.pseudo = pseudo;
        this.mail = mail;
        this.estAdmin = estAdmin;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPseudo() {
        return pseudo;
    }

    public void setPseudo(String pseudo) {
        this.pseudo = pseudo;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public Boolean getEstAdmin() {
        return estAdmin;
    }

    public void setEstAdmin(Boolean estAdmin) {
        this.estAdmin = estAdmin;
    }
}
