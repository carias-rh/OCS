package eu.europa.ec.eci.oct.entities.admin;

import java.io.Serializable;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="OCT_SOCIAL_MEDIA")
@Cacheable(false)
public class SocialMedia implements Serializable {

    private static final long serialVersionUID = 6200585122984736913L;
    
    static final String FACEBOOK = "facebook";
    static final String TWITTER = "twitter";
    static final String GOOGLE = "google";
    static final String CALL_FOR_ACTION = "callForAction";
    
	@Id
	private Long id;

    @Column(nullable=false, length=100)
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
