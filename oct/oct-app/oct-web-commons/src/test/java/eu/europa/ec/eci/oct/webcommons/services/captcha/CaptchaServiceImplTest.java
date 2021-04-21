package eu.europa.ec.eci.oct.webcommons.services.captcha;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import eu.europa.ec.eci.oct.webcommons.services.commons.ServicesTest;

/**
 * User: franzmh
 * Date: 14/02/17
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/services-test.xml")
public class CaptchaServiceImplTest extends ServicesTest {
    private String captchaId = "ABC123";
    private String resolution = "wrong resolution";
    @Test
    public void generateImageCaptcha()throws Exception{
        byte[] v = captchaService.generateImageCaptcha(captchaId);
        assertNotNull(v);
        assertTrue(v.length>1000);
    }
    @Test
    public void generateAudioCaptcha()throws Exception{
        byte[] v = captchaService.generateAudioCaptcha(captchaId);
        assertNotNull(v);
        assertTrue(v.length>10000);
    }
    @Test
    public void validateImageCaptcha() throws Exception{
        captchaService.setCaptchaEnabled(true);
        byte[] v = captchaService.generateImageCaptcha(captchaId);
        assertNotNull(v);
        assertFalse(captchaService.validateCaptcha(captchaId, resolution, CaptchaService.CAPTCHA_IMAGE_TYPE));
        captchaService.setCaptchaEnabled(false);
        captchaService.generateImageCaptcha(captchaId);
        assertTrue(captchaService.validateCaptcha(captchaId, resolution, CaptchaService.CAPTCHA_IMAGE_TYPE));
    }
    @Test
    public void validateAudioCaptcha() throws Exception{
        captchaService.setCaptchaEnabled(true);
        byte[] v = captchaService.generateAudioCaptcha(captchaId);
        assertNotNull(v);
        assertFalse(captchaService.validateCaptcha(captchaId, resolution, CaptchaService.CAPTCHA_AUDIO_TYPE));
        captchaService.setCaptchaEnabled(false);
        captchaService.generateAudioCaptcha(captchaId);
        assertTrue(captchaService.validateCaptcha(captchaId, resolution, CaptchaService.CAPTCHA_AUDIO_TYPE));
    }

}
