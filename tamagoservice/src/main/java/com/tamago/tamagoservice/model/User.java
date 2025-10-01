package com.tamago.tamagoservice.model;

import java.io.Serializable;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * JPA entity mapped to the `users` table.
 */
@Entity
@Table(name = "users")
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "pseudo", length = 50, nullable = false, unique = true)
    private String pseudo;

    @Column(name = "mdp", length = 100, nullable = false)
    private String mdp;

    @Column(name = "mail", length = 100)
    private String mail;

    // Column name in DB is `est_admin`; map exactly to avoid mismatch
    @Column(name = "est_admin", nullable = false)
    private Boolean estAdmin = Boolean.FALSE;

    public User() {
    }

    public User(Long id, String pseudo, String mdp, String mail, Boolean estAdmin) {
        this.id = id;
        this.pseudo = pseudo;
        this.mdp = mdp;
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

    public String getMdp() {
        return mdp;
    }

    public void setMdp(String mdp) {
        this.mdp = mdp;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(id, user.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", pseudo='" + pseudo + '\'' +
                ", mail='" + mail + '\'' +
                ", estAdmin=" + estAdmin +
                '}';
    }
}
