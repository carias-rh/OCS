package eu.europa.ec.eci.oct.webcommons.services.captcha;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Locale;

import javax.imageio.ImageIO;
import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.octo.captcha.service.CaptchaServiceException;
import com.octo.captcha.service.image.ImageCaptchaService;
import com.octo.captcha.service.sound.SoundCaptchaService;

import eu.europa.ec.eci.oct.webcommons.services.BaseService;


@Service
public class CaptchaServiceImpl extends BaseService implements CaptchaService {


    @Autowired
    @Qualifier("imageCaptchaService")
    protected ImageCaptchaService imageCaptchaService;
    @Autowired
    @Qualifier("soundCaptchaService")
    protected SoundCaptchaService soundCaptchaService;

    private boolean captchaEnabled=true;

    synchronized public byte[] generateImageCaptcha(String captchaId) throws Exception {
        ByteArrayOutputStream result = new ByteArrayOutputStream();
        try {

            BufferedImage challenge = imageCaptchaService.getImageChallengeForID(captchaId, Locale.forLanguageTag("en_US"));

            ImageIO.write(challenge, "jpeg", result);
            result.flush();

            return result.toByteArray();
        } catch (IllegalArgumentException e) {
            logger.error("unable to generate captcha", e);
            return new ByteArrayOutputStream().toByteArray();
        } catch (CaptchaServiceException e) {
            logger.error("unable to generate captcha", e);
            return new ByteArrayOutputStream().toByteArray();
        } catch (IOException e) {
            logger.error("unable to generate captcha", e);
            return new ByteArrayOutputStream().toByteArray();
        } catch (Exception e) {
            logger.error("unable to generate captcha", e);
            return new ByteArrayOutputStream().toByteArray();
        } finally {
            try {
                result.close();
            } catch (Exception e) {
                throw e;
            }
        }
    }


    synchronized public byte[] generateAudioCaptcha(String captchaId) throws Exception {
        ByteArrayOutputStream soundOutputStream = new ByteArrayOutputStream();
        try {
            AudioInputStream captchaAudio = soundCaptchaService.getSoundChallengeForID(captchaId, Locale.forLanguageTag("en_US"));
            // wave format conversion
            AudioSystem.write(captchaAudio, AudioFileFormat.Type.WAVE, soundOutputStream);
//			soundOutputStream.flush();
//			soundOutputStream.close();
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
            return new ByteArrayOutputStream().toByteArray();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return new ByteArrayOutputStream().toByteArray();
        } finally {
            try {
                soundOutputStream.flush();
                soundOutputStream.close();
            } catch (Exception e) {
                throw e;
            }
        }

        return soundOutputStream.toByteArray();
    }

    public boolean validateCaptcha(String captchaId, String captchaValue, String captchaType)
            throws CaptchaServiceException {
        boolean result;

        if (isCaptchaEnabled() == false) {
            result = true;
        } else {
            if (CAPTCHA_AUDIO_TYPE.equalsIgnoreCase(captchaType)) {
                result = soundCaptchaService.validateResponseForID(captchaId, captchaValue).booleanValue();
            } else if (CAPTCHA_IMAGE_TYPE.equalsIgnoreCase(captchaType)) {
                result = imageCaptchaService.validateResponseForID(captchaId, captchaValue).booleanValue();
            } else {
                result = false;
            }
        }
        return result;

    }

    public void setCaptchaEnabled(boolean captchaEnabled) {
        this.captchaEnabled = captchaEnabled;
    }

    public boolean isCaptchaEnabled() {
        return captchaEnabled;
    }
}