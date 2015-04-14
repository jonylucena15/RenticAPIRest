package org.rentic.rentic_javaee.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.rentic.rentic_javaee.rest.Coordenades;
import org.rentic.rentic_javaee.rest.Disponibilitat;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Entity
public class Objecte implements Serializable{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id;

    private String nom;

    private String descripcio;

    private float preu;

    private Coordenades coordenada;

    @ElementCollection
    private List<String> tags;

    @ElementCollection
    private List<Disponibilitat> disponible;

    @Column(name = "idUsuari", insertable = false, updatable = false)
    private Long userId;

    @ManyToOne
    @JoinColumn(name = "idUsuari", nullable=false)
    @JsonIgnore
    private User user;

    public Objecte(){
        this.nom = "";
        this.descripcio = "";
        this.preu = 0;
        this.coordenada = null;
        this.tags = null;
        this.disponible = null;
    }

    public Objecte(String nom, String descripcio, float preu, Coordenades coordenada, List<String> tags, List<Disponibilitat> disponible) {
        this.nom = nom;
        this.descripcio = descripcio;
        this.preu = preu;
        this.coordenada = coordenada;
        this.tags = tags;
        this.disponible = disponible;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getDescripcio() {
        return descripcio;
    }

    public void setDescripcio(String descripcio) {
        this.descripcio = descripcio;
    }

    public float getPreu() {
        return preu;
    }

    public void setPreu(float preu) {
        this.preu = preu;
    }

    public Coordenades getCoordenada() {
        return coordenada;
    }

    public void setCoordenada(Coordenades coordenada) {
        this.coordenada = coordenada;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public List<Disponibilitat> getDisponible() {
        return disponible;
    }

    public void setDisponible(List<Disponibilitat> disponible) {
        this.disponible = disponible;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
