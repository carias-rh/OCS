import { Component, OnInit, ViewChild, ElementRef } from '@angular/core';
import { FormGroup, FormBuilder, Validators } from '@angular/forms';

import { UploadComponent } from '../../../../shared/upload/upload.component';
import { Upload } from '../../../../shared/upload/upload';
import 'rxjs/add/operator/merge';

import { InitiativeService } from '../../../../core/services/initiative.service';

import { Initiative } from '../../../../core/models/initiative';
import { Description } from '../../../../core/models/description';
import { Customisations } from '../../../../core/models/customisations';

@Component({
    selector: 'ocs-settings-customise',
    templateUrl: 'customise.component.html'
})

export class SettingsCustomiseComponent implements OnInit {
    @ViewChild('imgLogo') imgLogo: ElementRef;
    @ViewChild('upload') upload: UploadComponent;

    initiative: Initiative;
    description: Description;
    custom: Customisations;
    form: FormGroup;
    file: File = null;
    alert: {type: string; label: string};
    showImage = false;

    constructor(private fb: FormBuilder, private service: InitiativeService) {
        this.formBuild();
        this.alert = null;
    }

    ngOnInit() {
        this.getCustoms();
    }

    onSave() {
        const newCustom = {
            showProgressionBar: this.form.controls['showProgressionBar'].value,
            showDistributionMap: this.form.controls['showDistributionMap'].value,
            showRecentSupporters: this.form.controls['showRecentSupporters'].value,
            optionalValidation: this.form.controls['optionalValidation'].value,
            customLogo: true,
            showSocialMedia:  this.custom.showSocialMedia,
            showFacebook: this.custom.showFacebook,
            showTwitter: this.custom.showTwitter,
            signatureGoal: this.form.controls['signatureGoal'].value,
            callbackUrl: this.form.controls['callbackUrl'].value,
            background: this.form.controls['background'].value,
            facebookUrl: this.custom.facebookUrl,
            twitterUrl: this.custom.twitterUrl,
            colorPicker: this.custom.colorPicker,
            alternateLogoText: this.form.controls['description'].value
        };

        const logo$ = this.service.storeLogo(this.file);
        const customisation$ = this.service.saveCustomisations(newCustom);
        const save$ = logo$.merge(customisation$);

        this.alert = null;
        save$.subscribe(
            () => {
                this.setAlert('success', 'personalize.save_ok');
                this.reset(false);
            },
            error => {
                // Fix HttpErrorResponse with 200 OK
                if (error.status === 200 && error.statusText === 'OK') {
                    this.setAlert('success', 'personalize.save_ok');
                } else {
                    this.setAlert('danger', 'personalize.save_err');
                }
                this.reset(false);
            }
        );
    }

    onCancel() {
        this.reset();
    }

    // load a new logo
    onUpload(images: Upload[]) {
        this.file = images[0].file;
        if (this.file) {
            this.imgLogo.nativeElement.src = window.URL.createObjectURL(this.file);
        } else {
            this.imgLogo.nativeElement.src = null;
        }
    }

    private getCustoms() {
        this.service.getCustomisations().subscribe(data => {
            this.custom = data;
            this.setDefaultData();
            this.setDefaultLogo();
        });
    }

    private formBuild() {
        this.form = this.fb.group({
            'showProgressionBar': [false, Validators.required],
            'signatureGoal': [null],
            'showDistributionMap': [false, Validators.required],
            'showRecentSupporters': [false, Validators.required],
            'callbackUrl': [null],
            'optionalValidation': [false, Validators.required],
            'background': [null],
            'description': [null]
        });
    }

    private setDefaultData() {
        this.form.patchValue({
            showProgressionBar: this.custom.showProgressionBar,
            signatureGoal: this.custom.signatureGoal,
            showDistributionMap: this.custom.showDistributionMap,
            showRecentSupporters: this.custom.showRecentSupporters,
            callbackUrl: this.custom.callbackUrl,
            optionalValidation: this.custom.optionalValidation,
            background: this.custom.background,
            description: this.custom.alternateLogoText
        });
    }

    private setDefaultLogo () {
        if ( this.custom.customLogo === true) {
            this.imgLogo.nativeElement.src = this.service.getUrlLogo() + '?v=' + Date.now();
        } else {
            this.imgLogo.nativeElement.src = 'assets/img/logo.png';
        }
    }

    private reset(alertReset = true) {
        this.getCustoms();
        this.resetUpload();
        window.scrollTo(0, 0);
        if (alertReset) {
            this.alert = null;
        }
    }

    private resetUpload() {
        this.file = null;
        this.upload.reset();
    }

    private setAlert(type: string, label: string) {
        this.alert = {
            type: type,
            label: label
        };
    }

}
