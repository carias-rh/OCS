import { Injectable } from '@angular/core';
import { ApiOcsService } from './api-ocs.service';

@Injectable()
export class CaptchaService {

    constructor(private api: ApiOcsService) {}

    getAudioCaptcha() {
        return this.api.getAudioCaptcha();
    }

    getImageCaptcha() {
        return this.api.getImageCaptcha();
    }
}
