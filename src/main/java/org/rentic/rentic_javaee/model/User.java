package org.rentic.rentic_javaee.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Collection;
import java.util.List;

@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"email"}))
public class User implements Serializable {
    /**
     * Default value included to remove warning. Remove or modify at will. *
     */
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "USUARI_ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id;

    @Column(name = "NOM_COMPLET")
    private String nomComplet;

    @NotNull
    @Column(name = "EMAIL")
    private String email;

    @Column(name = "TELEFON")
    private String telefon;

    @Column(name = "FACEBOOK_ID")
    private String facebookId;

    @Column(name = "FOTO_PERFIL")
    private String fotoPerfil;

    @NotNull
    @JsonIgnore
    @Column(name = "PASSWORD")
    private String password;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "user")
    @JsonIgnore
    private Collection<Objecte> objectes;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "user")
    @JsonIgnore
    private Collection<Lloguer> lloguers;

    @ManyToMany(cascade = CascadeType.ALL, mappedBy = "users")
    @JsonIgnore
    private Collection<Conversa> converses;


    @OneToMany(cascade = CascadeType.ALL, mappedBy = "user")
    @JsonIgnore
    private Collection<Missatge> missatges;

    public Long getId() { return id; }

    public void setId(Long id) {  this.id = id; }

    public String getPassword() { return password;  }

    public void setPassword(String password) {  this.password = password;  }

    public String getEmail() { return email;  }

    public void setEmail(String email) {  this.email = email;  }

    public String getNomComplet() { return nomComplet;  }

    public void setNomComplet(String nomComplet) { this.nomComplet = nomComplet; }

    public String getTelefon() {  return telefon;    }

    public void setTelefon(String telefon) { this.telefon = telefon; }

    public String getFacebookId() { return facebookId; }

    public void setFacebookId(String facebookId) { this.facebookId = facebookId; }

    public String getFotoPerfil() { return fotoPerfil; }

    public void setFotoPerfil(String fotoPerfil) { this.fotoPerfil = fotoPerfil; }

    public Collection<Objecte> getObjectes() {
        objectes.size();
        return objectes;
    }

    public void setObjectes(List<Objecte> ts) {
        this.objectes = ts;
    }

    public void addObjecte(Objecte objecte) {  objectes.add(objecte); }

    public Collection<Lloguer> getLloguers() {
        lloguers.size();
        return lloguers;
    }

    public void setLloguers(List<Lloguer> ts) {
        this.lloguers = ts;
    }

    public void addLloguer(Lloguer lloguer) {  lloguers.add(lloguer); }

    public Collection<Conversa> getConverses() {
        converses.size();
        return converses;
    }

    public void setConverses(Collection<Conversa> ts) {
        this.converses = ts;
    }

    public void addConversa(Conversa conversa) {  converses.add(conversa); }

    public Collection<Missatge> getMissatges() {  return missatges; }

    public void setMissatges(Collection<Missatge> missatges) { this.missatges = missatges;  }

    public void addMissatge(Missatge missatge) {  missatges.add(missatge); }



}
