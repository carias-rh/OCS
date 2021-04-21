package eu.europa.ec.eci.oct.webcommons.services.captcha;

import com.octo.captcha.service.CaptchaServiceException;

/**
 * User: falloda,franzmh
 * Date: 14/02/17
 */
public interface CaptchaService {
    public static String CAPTCHA_AUDIO_TYPE = "audio";
    public static String CAPTCHA_IMAGE_TYPE = "image";

    /**
     * Generates an image captcha.
     *
     * @return the captcha image for the request
     */
    public byte[] generateImageCaptcha(String captchaId) throws Exception;

    /**
     * Generates an audio captcha.
     *
     * @return the captcha audio for the request
     */
    public byte[] generateAudioCaptcha(String captchaId) throws Exception;

    /**
     *
     * @param captchaType
     *            : which type of captcha will be tested: CAPTCHA_AUDIO_TYPE or CAPTCHA_IMAGE_TYPE
     * @return true if the correct response has been set for the captcha image
     */
    public boolean validateCaptcha(String captchaId, String captchaValue, String captchaType)
            throws CaptchaServiceException;

    public void setCaptchaEnabled(boolean captchaEnabled);

    public boolean isCaptchaEnabled();
}
