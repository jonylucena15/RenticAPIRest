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
    /**
     * Default value included to remove warning. Remove or modify at will. *
     */
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id;

    private String nom;

    private String descripcio;

    private float preu;

    @ElementCollection
    private List<String> tags;

    private Boolean dispCapDeSetmana;

    private Boolean dispEntreSetmana;

    @OneToMany(cascade = CascadeType.ALL,mappedBy = "objecte")
    private Collection<Disponibilitat> dispRang;

    @Column(name = "idCoordenades", insertable = false, updatable = false)
    private Long idCoordenades;

    @OneToOne(fetch = FetchType.LAZY,optional = false)
    @JoinColumn(name="idCoordenades", unique=true, nullable=false, updatable=false)
    private Coordenades coordenades;

    @Column(name = "idUser", insertable = false, updatable = false)
    private Long userId;

    @ManyToOne
    @JoinColumn(name = "idUser", nullable=false)
    @JsonIgnore
    private User user;

    public Long getId() {  return id;  }

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

    public void setDisponibilitats(List<Disponibilitat> ts) {  this.dispRang = ts;   }

    public void addDisponibilitat(Disponibilitat disponibilitat) {
        dispRang.add(disponibilitat);
    }

    public Coordenades getCoordenades() { return coordenades; }

   public void setCoordenades(Coordenades ts) {  this.coordenades = ts; }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Boolean getDispCapDeSetmana() { return dispCapDeSetmana;    }

    public void setDispCapDeSetmana(Boolean dispCapDeSetmana) {   this.dispCapDeSetmana = dispCapDeSetmana;    }

    public Boolean getDispEntreSetmana() {  return dispEntreSetmana;    }

    public void setDispEntreSetmana(Boolean dispEntreSetmana) {        this.dispEntreSetmana = dispEntreSetmana;    }
}