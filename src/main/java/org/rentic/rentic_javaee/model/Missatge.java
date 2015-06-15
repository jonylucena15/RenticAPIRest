package org.rentic.rentic_javaee.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.io.Serializable;


/**
 * Created by Jony Lucena.
 */
@Entity
public class Missatge implements Serializable {

    /**
     * Default value included to remove warning. Remove or modify at will. *
     */
    private static final long serialVersionUID = 1L;


    @Id
    @Column(name = "ID_MISSATGE")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id;

    @Column(name = "CONVERSA_ID", insertable = false, updatable = false)
    private Long conversaId;

    @ManyToOne
    @JoinColumn(name = "CONVERSA_ID", nullable=false)
    @JsonIgnore
    private Conversa conversa;


    @Column(name = "MISSATGE", length=1000)
    private String missatge;


    @Column(name = "DATA_HORA")
    private String dataHora;


    @Column(name = "Enviat")
    private Boolean enviat;

    @Column(name = "USUARI_ID", insertable = false, updatable = false)
    private Long userId;

    @ManyToOne(cascade = {CascadeType.PERSIST,CascadeType.MERGE, CascadeType.DETACH})
    @JoinColumn(name = "USUARI_ID",nullable = false)
    @JsonIgnore
    private User user;

    public String getMissatge() { return missatge; }

    public void setMissatge(String missatge) { this.missatge = missatge; }

    public Boolean getEnviat() { return enviat; }

    public void setEnviat(Boolean enviat) { this.enviat = enviat;  }

    public Long getId() {  return id;  }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDataHora() {   return dataHora;   }

    public void setDataHota(String dataHora) { this.dataHora = dataHora; }

    public Long getUserId(){return userId;}

    public void setUserId(Long userId){this.userId=userId;}

    public User getUser() { return user;  }

    public void setUser(User user) {
        this.user = user;
    }

    public Conversa getConversa() { return conversa; }

    public void setConversa(Conversa conversa) { this.conversa = conversa;  }

    public Long getConversaId(){return conversaId;}

    public void setConversaId(Long conversaId){this.conversaId=conversaId;}



}
