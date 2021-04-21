package eu.europa.ec.eci.oct.entities.admin;

import java.io.Serializable;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

import eu.europa.ec.eci.oct.entities.system.Language;

@Entity
@Table(name="OCT_SOCIAL_MEDIA_MSG")
@Cacheable(false)
public class SocialMediaMessage implements Serializable {

    private static final long serialVersionUID = 3973312733840284545L;
    
    @GeneratedValue(generator = "IdGenerator", strategy = GenerationType.TABLE)
	@Id
	@TableGenerator(name = "IdGenerator", pkColumnValue = "social_media_message_id", table = "HIBERNATE_SEQUENCES", allocationSize = 1, pkColumnName = "sequence_name", valueColumnName = "sequence_next_hi_value")
	@Column(name = "id")
	private Long id;

	@ManyToOne
	@JoinColumn(name="media_id")
    private SocialMedia socialMedia;

	@ManyToOne
	@JoinColumn(name="language_id")
    private Language language;

    @Column
    private String message;
    
    public SocialMedia getSocialMedia() {
        return socialMedia;
    }

    public void setSocialMedia(SocialMedia socialMedia) {
        this.socialMedia = socialMedia;
    }

    public Language getLanguage() {
        return language;
    }

    public void setLanguage(Language language) {
        this.language = language;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
    
	@Override
	public String toString() {
		return "SocialMediaMessage [id=" + id + ", socialMedia=" + socialMedia.getName() + ", language=" + language.getCode() + ", message=" + message + "]";
	}
    
}