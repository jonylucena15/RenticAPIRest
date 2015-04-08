package org.rentic.rentic_javaee.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;

@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"email", "username"}))
public class User implements Serializable {
    /**
     * Default value included to remove warning. Remove or modify at will. *
     */
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id;

    @NotNull
    private String username;

    private String nomComplet;

    @NotNull
    private String email;

    private String telefon;

    private String facebookId;

    private String fotoPerfil;

    @NotNull
    @JsonIgnore
    private String password;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "user")
    @JsonIgnore
    private Collection<Task> tasks;

    public User(){
        this.username="";
        this.nomComplet="";
        this.email="";
        this.telefon="";
        this.facebookId="";
        this.fotoPerfil="";
        this.password="";
    }

    public User(String username,String nomComplet, String email, String telefon, String facebookId, String fotoPerfil, String password ){
        this.username=username;
        this.nomComplet=nomComplet;
        this.email=email;
        this.telefon=telefon;
        this.facebookId=facebookId;
        this.fotoPerfil=fotoPerfil;
        this.password=password;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNomComplet() { return nomComplet;  }

    public void setNomComplet(String nomComplet) { this.nomComplet = nomComplet; }

    public String getTelefon() {  return telefon;    }

    public void setTelefon(String telefon) { this.telefon = telefon; }

    public String getFacebookId() { return facebookId; }

    public void setFacebookId(String facebookId) { this.facebookId = facebookId; }

    public String getFotoPerfil() { return fotoPerfil; }

    public void setFotoPerfil(String fotoPerfil) { this.fotoPerfil = fotoPerfil; }

    public Collection<Task> getTasks() {
        tasks.size();
        return tasks;
    }

    public void setTasks(List<Task> ts) {
        this.tasks = ts;
    }

    public void addTask(Task task) {
        tasks.add(task);
    }

}