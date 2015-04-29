package org.rentic.rentic_javaee.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Collection;
import java.util.List;


@Entity
public class Objecte implements Serializable{

    /**
     * Default value included to remove warning. Remove or modify at will. *
     */
    private static final long serialVersionUID = 1L;


    @Id
    @Column(name = "ID_OBJECTE")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id;

    @Column(name = "NOM")
    private String nom;

    @Column(name = "DESCRIPCIO")
    private String descripcio;

    @Column(name = "PREU")
    private Number preu;

    @ElementCollection
    @CollectionTable(
            name="IMATGES",
            joinColumns=@JoinColumn(name="OBJECTE_ID")
    )

    @Column(name = "URL_IMATGE")
    private List<String> imatges;

    @ElementCollection
    @CollectionTable(
            name="TAGS",
            joinColumns=@JoinColumn(name="OBJECTE_ID")
    )

    @Column (name = "NOM_TAG")
    private List<String> tags;

    @Column(name = "DISP_CAP_DE_SETMANA")
    private Boolean dispCapDeSetmana;

    @Column(name = "DISP_ENTRE_SETMANA")
    private Boolean dispEntreSetmana;

    @ElementCollection
    @CollectionTable(
            name="DISPONIBILITAT_RANGS",
            joinColumns=@JoinColumn(name="OBJECTE_ID")
    )

    @Column(name = "DISP_RANGS")
    private List<Disponibilitat> dispRangs;

    @Column(name = "LONGITUD")
    private Number longitud;

    @Column(name = "LATITUD")
    private Number latitud;

    @Column(name = "USUARI_ID", insertable = false, updatable = false)
    private Long userId;

    @ManyToOne
    @JoinColumn(name = "USUARI_ID", nullable=false)
    @JsonIgnore
    private User user;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "objecte")
    @JsonIgnore
    private Collection<Lloguer> lloguers;

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

    public Number getPreu() {
        return preu;
    }

    public void setPreu(Number preu) {
        this.preu = preu;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public List<Disponibilitat> getDispRangs() {  return dispRangs;
    }

    public void setDispRangs(List<Disponibilitat> ts) {  this.dispRangs = ts;   }

    public void addDispRang(Disponibilitat disponibilitat) { dispRangs.add(disponibilitat);   }

    public User getUser() { return user;  }

    public void setUser(User user) {
        this.user = user;
    }

    public Boolean getDispCapDeSetmana() { return dispCapDeSetmana;    }

    public void setDispCapDeSetmana(Boolean dispCapDeSetmana) {   this.dispCapDeSetmana = dispCapDeSetmana;    }

    public Boolean getDispEntreSetmana() {  return dispEntreSetmana;    }

    public void setDispEntreSetmana(Boolean dispEntreSetmana) {        this.dispEntreSetmana = dispEntreSetmana;    }

    public List<String> getImatges() { return imatges;   }

    public void setImatges(List<String> imatges) {  this.imatges = imatges; }

        public Number getLongitud() { return longitud; }

    public void setLongitud(Number longitud) { this.longitud = longitud; }

    public Number getLatitud() {  return latitud;   }

    public void setLatitud(Number latitud) { this.latitud = latitud; }
    public Collection<Lloguer> getLloguers() {
        lloguers.size();
        return lloguers;
    }

    public void setLloguers(List<Lloguer> ts) {
        this.lloguers = ts;
    }

    public void addLloguer(Lloguer lloguer) {  lloguers.add(lloguer); }


}