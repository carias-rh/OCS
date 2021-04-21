package eu.europa.ec.eci.oct.webcommons.services.translations;

import org.junit.Test;

import eu.europa.ec.eci.oct.entities.system.Language;
import eu.europa.ec.eci.oct.webcommons.services.commons.ServicesTest;
import eu.europa.ec.eci.oct.webcommons.services.exceptions.OCTException;
import eu.europa.ec.eci.oct.webcommons.services.exceptions.TranslationException;

public class TranslationServiceImplTest extends ServicesTest {


    @Test
    public void testRetrieveTranslation_OK() throws OCTException, TranslationException {

        Language en = systemManager.getLanguageByCode("en");
        String translation = translationService.getTranslation("public.en.title", en);
        assertEquals(translation, "Title of something");

        Language fr = systemManager.getLanguageByCode("fr");
        translation = translationService.getTranslation("public.fr.title", fr);
        assertEquals(translation, "le title de la chanson");

    }

    @Test
    public void testRetrieveTranslation_noMatchingLang() throws OCTException, TranslationException {

        Language it = systemManager.getLanguageByCode("it");
        String translation = translationService.getTranslation("public.en.title", it);
        assertEquals(translation, "[public.en.title:it]");
    }

    @Test
    public void testRetrieveTranslation_noMatchingKey() throws OCTException, TranslationException {

        Language en = systemManager.getLanguageByCode("en");
        String translation = translationService.getTranslation("public.blabla", en);
        assertEquals(translation, "[public.blabla:en]");
    }

}
