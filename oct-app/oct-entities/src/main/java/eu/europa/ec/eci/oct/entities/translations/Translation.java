package eu.europa.ec.eci.oct.entities.translations;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import eu.europa.ec.eci.oct.entities.system.Language;

/**
 * User: franzmh
 * Date: 13/03/17
 */

@Entity
@IdClass(TranslationId.class)
@Table(name = "OCT_TRANSLATION")
public class Translation {

    @Id
    @Column(name = "tr_key" , nullable = false)
    private String key;

    @Id
    @Column(name = "LANG_ID", nullable = false)
    private Long langId ;

    @Column(name = "tr_description", nullable = false)
    @Lob
    private String description;

    @ManyToOne (optional =  false)
    @JoinColumn(name = "LANG_ID", updatable = false, insertable = false, nullable = false)
    private Language language;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public long getLangId() {
        return langId;
    }

    public void setLangId(long langId) {
        this.langId = langId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Language getLanguage() {
        return language;
    }

    public void setLanguage(Language language) {
        this.language = language;
    }
}
