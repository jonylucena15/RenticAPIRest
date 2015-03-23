package org.udg.pds.simpleapp_javaee.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.udg.pds.simpleapp_javaee.util.DateAdapter;
import org.udg.pds.simpleapp_javaee.util.LongAdapter;

@Entity
// This tells JAXB that it has to ignore getters and setters and only use fields for XML marshaling/unmarshaling
public class Task implements Serializable {
    /** Default value included to remove warning. Remove or modify at will. **/
    private static final long serialVersionUID = 1L;

    // This tells JAXB that this field can be used as ID
    // Since XmlID can only be used on Strings, we need to use LongAdapter to transform Long <-> String
    @Id
    // Don't forget to use the extra argument "strategy = GenerationType.IDENTITY" to get AUTO_INCREMENT
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // We are using a DateAdapter to control how dates are translated to and from XML
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date dateCreated;

    @Temporal(javax.persistence.TemporalType.DATE)
    private Date dateLimit;

    private Boolean completed;

    private String text;


    @Column(name = "idUser", insertable = false, updatable = false)
    private Long userId;

    @ManyToOne
    @JoinColumn(name = "idUser", nullable=false)
    @JsonIgnore
    private User user;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getText() { return text; }
    public void setText(String s) { text = s; }

    public Date getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

    public Date getDateLimit() {
        return dateLimit;
    }

    public void setDateLimit(Date dateLimit) {
        this.dateLimit = dateLimit;
    }

    public Boolean getCompleted() {
        return completed;
    }

    public void setCompleted(Boolean completed) {
        this.completed = completed;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    /*
    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
    */
}
