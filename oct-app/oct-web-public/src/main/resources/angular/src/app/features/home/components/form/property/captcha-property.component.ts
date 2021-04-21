import { Component, OnChanges, SimpleChanges, ViewChild, ElementRef, AfterViewChecked } from '@angular/core';
import { BasePropertyComponent } from './base-property.component';

import { CaptchaService } from '../../../../../core/services/captcha.service';

@Component({
    selector: 'app-captcha-property',
    templateUrl: './captcha-property.component.html'
})
export class CaptchaPropertyComponent extends BasePropertyComponent implements OnChanges, AfterViewChecked {

    @ViewChild('audiofocus') audioFocus: ElementRef;
    @ViewChild('imagefocus') imageFocus: ElementRef;

    audioData: string;
    imageData: string;
    setFocusCaptcha = false;

    constructor(private service: CaptchaService) {
        super();
    }

    get isImage() {
        return this.property.type === 'image';
    }

    get isAudio() {
        return this.property.type === 'audio';
    }

    get label() {
        if (this.isImage) {
            return 'form.captcha-image-title';
        }
        return 'form.captcha-audio-download';
    }

    ngOnChanges(changes: SimpleChanges) {
        if (changes.hasOwnProperty('property')) {
            this.refresh();
        }
    }

    ngAfterViewChecked() {
        if (this.isImage && this.imageFocus && this.imageFocus.nativeElement && this.setFocusCaptcha) {
            this.imageFocus.nativeElement.focus();
            this.setFocusCaptcha = false;
        }

        if (this.isAudio && this.audioFocus && this.audioFocus.nativeElement && this.setFocusCaptcha) {
            this.audioFocus.nativeElement.focus();
            this.setFocusCaptcha = false;
        }
    }

    public selectImage() {
        this.property.type = 'image';
        this.property.value = '';
        this.service.getImageCaptcha().subscribe((res: any) => {
            this.property.id = res.id;
            this.imageData = 'data:image/png;base64,' + res.data;
            this.audioData = null;
            this.setFocusCaptcha = true;
        });
        if (this.isImage && this.imageFocus && this.imageFocus.nativeElement && this.setFocusCaptcha) {
            this.imageFocus.nativeElement.focus();
            this.setFocusCaptcha = false;
        }
    }

    public selectAudio() {
        this.property.type = 'audio';
        this.property.value = '';
        this.service.getAudioCaptcha().subscribe((res: any) => {
            this.property.id = res.id;
            this.audioData = 'data:audio/mp3;base64,' + res.data;
            this.imageData = null;
            this.setFocusCaptcha = true;
        });
    }

    public refresh() {
        if (this.property.type === 'audio') {
            this.selectAudio();
        } else {
            this.selectImage();
        }
    }

}
