package org.rentic.rentic_javaee.model;


import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
@Embeddable
public class Disponibilitat implements Serializable {
    /**
     * Default value included to remove warning. Remove or modify at will. *
     */
    private static final long serialVersionUID = 1L;

    @Column(name = "DATA_INICI")
    private String dataInici;

    @Column(name = "DATA_FI")
    private String dataFi;

    public String getDataInici() {
        return dataInici;
    }

    public void setDataInici(String inici) {
        this.dataInici = inici;
    }

    public String getDataFi() {
        return dataFi;
    }

    public void setDataFi(String fi) {
        this.dataFi = fi;
    }
}
