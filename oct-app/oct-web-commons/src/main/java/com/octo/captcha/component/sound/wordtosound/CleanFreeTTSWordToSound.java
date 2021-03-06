/*
 * JCaptcha, the open source java framework for captcha definition and integration
 * Copyright (c)  2007 jcaptcha.net. All Rights Reserved.
 * See the LICENSE.txt file distributed with this package.
 */

package com.octo.captcha.component.sound.wordtosound;

import javax.sound.sampled.AudioInputStream;

import com.octo.captcha.component.sound.soundconfigurator.SoundConfigurator;

/**
 * Clean use of FreeTTS, without effect.
 *
 * @author Benoit Doumas
 * @version 1.0
 */
public class CleanFreeTTSWordToSound extends AbstractFreeTTSWordToSound {
    public CleanFreeTTSWordToSound() {
        super();
    }

    /**
     * @see com.octo.captcha.component.sound.wordtosound.AbstractFreeTTSWordToSound#AbstractFreeTTSWordToSound(com.octo.captcha.component.sound.soundconfigurator.SoundConfigurator, int, int)
     */
    public CleanFreeTTSWordToSound(SoundConfigurator configurator, int minAcceptedWordLength,
                                   int maxAcceptedWordLength) {
        super(configurator, minAcceptedWordLength, maxAcceptedWordLength);
    }

    /**
     * @see AbstractFreeTTSWordToSound#addEffects(javax.sound.sampled.AudioInputStream)
     */
    protected AudioInputStream addEffects(AudioInputStream sound) {
        return sound;
    }

}