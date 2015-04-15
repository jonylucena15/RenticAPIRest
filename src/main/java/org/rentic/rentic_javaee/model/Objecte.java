package org.rentic.rentic_javaee.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Set;

@Entity
public class Objecte implements Serializable{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id;

    private String nom;

    private String descripcio;

    private float preu;

    @ElementCollection
    private List<String> tags;

    @NotNull
    private Boolean dispCapDeSetmana;

    @NotNull
    private Boolean dispEntreSetmana;

    @OneToMany(cascade = CascadeType.ALL,mappedBy = "objecte")
    private Collection<Disponibilitat> dispRang;

    @OneToOne(mappedBy="objecte")
    public Coordenades coordenades;

   @Column(name = "idUsuari", insertable = false, updatable = false)
    private Long userId;

    @ManyToOne
    @JoinColumn(name = "idUsuari", nullable=false)
    @JsonIgnore
    public User user;


    public Objecte(){
        this.nom = "";
        this.descripcio = "";
        this.preu = 0;
        this.tags = null;
        this.dispCapDeSetmana=false;
        this.dispEntreSetmana=false;
    }

    public Objecte(String nom, String descripcio, float preu, List<String> tags,Boolean dispCapDeSetmana, Boolean dispEntreDeSetmana) {
        this.nom = nom;
        this.descripcio = descripcio;
        this.preu = preu;
        this.tags = tags;
        this.dispEntreSetmana=dispEntreDeSetmana;
        this.dispCapDeSetmana=dispCapDeSetmana;
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

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public Collection<Disponibilitat> getDisponibilitats() {
        dispRang.size();
        return dispRang;
    }

    public void setDisponibilitats(List<Disponibilitat> ts) {
        this.dispRang = ts;
    }

    public void addDisponibilitat(Disponibilitat disponibilitat) {
        dispRang.add(disponibilitat);
    }

    public Coordenades getCoordenades() { return coordenades; }

    public void setCoordenades(Coordenades ts) {
        this.coordenades = ts;
    }

    public User getUser() { return user;     }
}