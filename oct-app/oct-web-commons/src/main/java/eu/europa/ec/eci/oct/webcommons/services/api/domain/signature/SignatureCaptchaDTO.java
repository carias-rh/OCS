package eu.europa.ec.eci.oct.webcommons.services.api.domain.signature;

import java.io.Serializable;

import eu.europa.ec.eci.oct.webcommons.services.api.domain.captcha.CaptchaValidationDTO;

/**
 * User: franzmh
 * Date: 16/02/17
 */
public class SignatureCaptchaDTO implements Serializable {
    private static final long serialVersionUID = 3867517308949012782L;

    public SignatureCaptchaDTO(){}

    private SignatureDTO signatureDTO;
    private CaptchaValidationDTO captchaValidationDTO;

    public SignatureDTO getSignatureDTO() {
        return signatureDTO;
    }

    public void setSignatureDTO(SignatureDTO signatureDTO) {
        this.signatureDTO = signatureDTO;
    }

    public CaptchaValidationDTO getCaptchaValidationDTO() {
        return captchaValidationDTO;
    }

    public void setCaptchaValidationDTO(CaptchaValidationDTO captchaValidationDTO) {
        this.captchaValidationDTO = captchaValidationDTO;
    }
}
