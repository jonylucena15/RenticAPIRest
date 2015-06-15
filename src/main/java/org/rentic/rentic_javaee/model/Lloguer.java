package org.rentic.rentic_javaee.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.io.Serializable;


/**
 * Created by Jony Lucena.
 */
@Entity
public class Lloguer implements Serializable {

    /**
     * Default value included to remove warning. Remove or modify at will. *
     */
    private static final long serialVersionUID = 1L;


    @Id
    @Column(name = "ID_LLOGUER")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id;

    @Column(name = "DATA_INICI")
    private String dataInici;

    @Column(name = "DATA_FI")
    private String dataFi;

    @Column(name = "USUARI_ID", insertable = false, updatable = false)
    private Long userId;

    @ManyToOne
    @JoinColumn(name = "USUARI_ID", nullable=false)
    @JsonIgnore
    private User user;

    @Column(name = "OBJECTE_ID", insertable = false, updatable = false)
    private Long objectId;

    @ManyToOne
    @JoinColumn(name = "OBJECTE_ID", nullable=false)
    @JsonIgnore
    private Objecte objecte;

    public Long getId() {  return id;  }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDataInici() {   return dataInici;   }

    public void setDataInici(String dataInici) { this.dataInici = dataInici; }

    public String getDataFi() { return dataFi; }

    public void setDataFi(String dataFi) { this.dataFi = dataFi; }

    public User getUser() { return user;  }

    public void setUser(User user) {
        this.user = user;
    }

    public Objecte getObjecte() { return objecte; }

    public void setObjecte(Objecte objecte) { this.objecte = objecte; }

}
