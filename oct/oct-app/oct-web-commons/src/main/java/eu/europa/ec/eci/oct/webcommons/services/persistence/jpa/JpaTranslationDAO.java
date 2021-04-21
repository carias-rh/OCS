package eu.europa.ec.eci.oct.webcommons.services.persistence.jpa;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import eu.europa.ec.eci.oct.entities.system.Language;
import eu.europa.ec.eci.oct.entities.translations.Translation;
import eu.europa.ec.eci.oct.webcommons.services.persistence.TranslationDAO;

/**
 * User: franzmh
 * Date: 13/03/17
 */
@Repository
@Transactional
public class JpaTranslationDAO extends AbstractJpaDAO implements TranslationDAO{
    
    @Override
    @Transactional(readOnly = true)
    public String getDescriptionByLanguage(String key, Language language) {
    	
    	Translation translation = (Translation) this.sessionFactory.getCurrentSession().createQuery("FROM Translation t WHERE t.key = :key AND t.langId = :langId")
    	.setParameter("key", key)
    	.setParameter("langId", language.getId()).uniqueResult();
        
    	String result = "";
        if(translation != null){
            result = translation.getDescription();
        }else{
            // TODO: falloda should we try to return the value in EN? or the key/language as display warning?
            result = "[" + key + ":" + language.getCode() + "]";
            logger.error("No key/language found [" + key + "," + language.getCode() + "]");
        }
        return result;
    }
}
