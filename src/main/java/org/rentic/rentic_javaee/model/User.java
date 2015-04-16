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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id;

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

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "user")
    @JsonIgnore
    private Collection<Objecte> objectes;

    public User getUser(){  return this; }

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

    public Collection<Task> getTasks() {
        tasks.size();
        return tasks;
    }

    public void setTasks(List<Task> ts) {
        this.tasks = ts;
    }

    public void addTask(Task task) { tasks.add(task);  }

    public Collection<Objecte> getObjecte() {
        objectes.size();
        return objectes;
    }

    public void setObjecte(List<Objecte> ts) {
        this.objectes = ts;
    }

    public void addObjecte(Objecte objecte) {  objectes.add(objecte); }
}
