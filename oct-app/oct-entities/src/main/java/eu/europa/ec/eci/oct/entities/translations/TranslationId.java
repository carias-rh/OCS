package eu.europa.ec.eci.oct.entities.translations;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * User: franzmh
 * Date: 13/03/17
 */
@Embeddable
public class TranslationId implements Serializable{
    private static final long serialVersionUID = 4556947723383210715L;

    @Column(name = "tr_key", nullable = false)
    private String key;

    @Column(name = "lang_id", nullable = false)
    private Long langId;

    public TranslationId(){}

    public TranslationId(String key, long langId){
        this.key = key;
        this.langId = langId;
    }

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TranslationId that = (TranslationId) o;

        if (langId != that.langId) return false;
        return key != null ? key.equals(that.key) : that.key == null;

    }

    @Override
    public int hashCode() {
        int result = key != null ? key.hashCode() : 0;
        result = 31 * result + (int) (langId ^ (langId >>> 32));
        return result;
    }
}
