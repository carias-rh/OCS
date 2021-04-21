package eu.europa.ec.eci.oct.entities.system;

import java.io.Serializable;
import java.sql.Clob;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "OCT_COUNTRY_RULE")
public class CountryRule implements Serializable {

	private static final long serialVersionUID = -7254843951989162018L;

	@Id
    @Column(name = "ID")
    private Long id;

    @Column(name = "CODE")
    private String code;

    @Column(name = "RULE")
    private Clob rule;

    @Column(name = "LAST_UPDATE_DATE")
    private Date lastUpdateDate;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Clob getRule() {
        return rule;
    }

    public void setRule(Clob rule) {
        this.rule = rule;
    }

    public Date getLastUpdateDate() {
        return lastUpdateDate;
    }

    public void setLastUpdateDate(Date lastUpdateDate) {
        this.lastUpdateDate = lastUpdateDate;
    }

    @Override
    public String toString() {
        return "CountryRule{" +
                "id=" + id +
                ", code='" + code + '\'' +
                ", lastUpdateDate=" + lastUpdateDate +
                '}';
    }
}
