package eu.europa.ec.eci.oct.webcommons.services.captcha;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import com.octo.captcha.CaptchaFactory;
import com.octo.captcha.component.sound.soundconfigurator.SoundConfigurator;
import com.octo.captcha.component.sound.wordtosound.FreeTTSWordToSound;
import com.octo.captcha.component.word.worddecorator.SpellerWordDecorator;
import com.octo.captcha.component.word.wordgenerator.WordGenerator;
import com.octo.captcha.engine.sound.SoundCaptchaEngine;
import com.octo.captcha.sound.SoundCaptcha;
import com.octo.captcha.sound.SoundCaptchaFactory;
import com.octo.captcha.sound.speller.SpellerSoundFactory;

/**
 * User: franzmh
 * Date: 14/02/17
 */
public class SpellerSoundCaptchaEngine extends SoundCaptchaEngine {

    List<CaptchaFactory> factories = new ArrayList<CaptchaFactory>();
    private Random myRandom = new SecureRandom();

    public SpellerSoundCaptchaEngine(WordGenerator wordgen, SoundConfigurator configurator,
                                     SpellerWordDecorator decorator) {
        if (configurator != null) {
            System.setProperty("freetts.voices", configurator.getLocation());
        }
        FreeTTSWordToSound wordToSound = new FreeTTSWordToSound(configurator, 4, 10);
        this.addFactory(new SpellerSoundFactory(wordgen, wordToSound, decorator));
    }

    /**
     * Add a factory to the gimpy list
     *
     * @return true if added false otherwise
     */
    public boolean addFactory(SoundCaptchaFactory factory) {
        return this.factories.add(factory);
    }

    /**
     * Add an array of factories to the gimpy list
     */
    public void addFactories(SoundCaptchaFactory[] factories) {
        for (int i = 0; i < factories.length; i++) {
            this.factories.add(factories[i]);
        }
    }

    /**
     * @return captcha factories used by this engine
     */
    public CaptchaFactory[] getFactories() {
        return (CaptchaFactory[]) this.factories.toArray(new CaptchaFactory[factories.size()]);
    }

    /**
     * @param factories
     *            new captcha factories for this engine
     */
//    public void setFactories(CaptchaFactory[] factories) throws CaptchaEngineException {
    public void setFactories(CaptchaFactory[] factories) throws com.octo.captcha.engine.CaptchaEngineException{
        if (factories == null || factories.length == 0) {
            throw new com.octo.captcha.engine.CaptchaEngineException("impossible to set null or empty factories");
        }
        ArrayList<CaptchaFactory> tempFactories = new ArrayList<CaptchaFactory>();

        for (int i = 0; i < factories.length; i++) {
            if (SoundCaptchaFactory.class.isAssignableFrom(factories[i].getClass())) {
                throw new com.octo.captcha.engine.CaptchaEngineException("This factory is not an sound captcha factory "
                        + factories[i].getClass());
            }
            tempFactories.add(factories[i]);
        }

        this.factories = tempFactories;
    }

    /**
     * This method build a SoundCaptchaFactory.
     *
     * @return a CaptchaFactory
     */
    public SoundCaptchaFactory getSoundCaptchaFactory() {
        return (SoundCaptchaFactory) factories.get(myRandom.nextInt(factories.size()));
    }

    /**
     * This method build a SoundCaptchaFactory.
     *
     * @return a SoundCaptcha
     */
    public SoundCaptcha getNextSoundCaptcha() {
        return getSoundCaptchaFactory().getSoundCaptcha();
    }

    /**
     * This method build a SoundCaptchaFactory.
     *
     * @return a SoundCaptcha
     */
    public SoundCaptcha getNextSoundCaptcha(Locale locale) {
        return getSoundCaptchaFactory().getSoundCaptcha(locale);
    }
}