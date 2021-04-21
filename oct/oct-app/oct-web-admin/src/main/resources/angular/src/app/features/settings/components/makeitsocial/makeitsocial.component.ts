import { Component, OnInit, ViewEncapsulation } from '@angular/core';
import { FormGroup, FormBuilder, FormControl, Validators } from '@angular/forms';
import { InitiativeService } from '../../../../core/services/initiative.service';
import { Customisations } from '../../../../core/models/customisations';
import { ApplicationService } from '../../../../core/services/application.service';
import { LanguageService } from '../../../../core/services/language.service';

interface Alert {
    type: string;
    label: string;
}

@Component({
    selector: 'ocs-settings-makeitsocial',
    templateUrl: 'makeitsocial.component.html',
    styleUrls: ['makeitsocial.component.scss'],
    encapsulation: ViewEncapsulation.None
})
export class SettingsMakeItSocialComponent implements OnInit {

    formAction: FormGroup;
    formSocial: FormGroup;
    formTwitter: FormGroup;

    custom: Customisations;
    languages = [];

    alertAction: Alert;
    alertSocial: Alert;
    alertTwitter: Alert;
    inputs: any[] = [];

    constructor(private fb: FormBuilder,
        private service: InitiativeService,
        private applicationService: ApplicationService,
        private languageService: LanguageService
    ) {
        this.languages = this.applicationService.systemLanguages;
        this.formsBuild();
    }

    get navigationLanguage() {
        return this.languageService.selectedLanguage;
        // this.alertAction = null;
    }

    get facebookUrl(): FormControl {
        return <FormControl>this.formSocial.controls['facebookUrl'];
    }

    get twitterUrl(): FormControl {
        return <FormControl>this.formSocial.controls['twitterUrl'];
    }

    ngOnInit() {
        this.service.getCustomisations().subscribe(data => {
            this.custom = data;
            this.init();
        });
    }

    init() {
        this.setDataSocial();
        const actionLanguage = this.formAction.get('languageCode').value || this.navigationLanguage;
        this.onSelectActionLanguage(actionLanguage);

        const twitterLanguage = this.formTwitter.get('languageCode').value || this.navigationLanguage;
        this.onSelectTwitterLanguage(twitterLanguage);
    }

    formsBuild () {
        this.formAction = this.fb.group ({
            'languageCode': [this.navigationLanguage],
            'message': [null],
            'socialMedia': [null]

        });
        this.formSocial = this.fb.group ({
            'facebookUrl': [null,
                [
                    // tslint:disable-next-line:max-line-length
                    Validators.pattern(/^(?:http(s)?:\/\/)?[\w.-]+(?:\.[\w\.-]+)+[\w\-\._~:/?#[\]@!\$&'\(\)\*\+,;=.]+$/i)
                ]
            ],
            'twitterUrl': [null,
                [
                    // tslint:disable-next-line:max-line-length
                    Validators.pattern(/^(?:http(s)?:\/\/)?[\w.-]+(?:\.[\w\.-]+)+[\w\-\._~:/?#[\]@!\$&'\(\)\*\+,;=.]+$/i)
                ]
            ]
        });
        this.formTwitter = this.fb.group ({
            'languageCode': [this.navigationLanguage],
            'message': [null],
            'socialMedia': [null]
        });
    }

    onSelectActionLanguage(lang: string) {
        this.service.getCallForActionMessage(lang).subscribe(data => {
            this.formAction.patchValue({
                message: data.message,
            });
            if (data.languageCode !== this.service.getCallForActionMessage(lang)) {
                this.alertAction = null;
            }
        });
    }

    // Revisar con Emilio esta funcionalidad
    onSaveAction() {
        this.service.saveCallForActionMessage(this.formAction.value.languageCode, this.formAction.value.message).subscribe(
            data => {
                this.alertAction = this.getAlert('success', 'social.save_ok');
            },
            () => {
                this.alertAction = this.getAlert('danger', 'social.save-message-err');
            });
    }

    // Form social

    setDataSocial() {
        this.formSocial.patchValue({
            facebookUrl: this.custom.facebookUrl,
            twitterUrl: this.custom.twitterUrl,
        });
    }

    onSaveSocial() {
        this.service.saveCustomisations(this.formSocial.value).subscribe(data => {
            this.custom = data;
            this.alertSocial = this.getAlert('success', 'social.save_ok');
        });
    }

    // Form Twitter
    setDataTwitter(message: string) {
        this.formTwitter.patchValue({
            message: message,
        });
    }

    onSelectTwitterLanguage(lang: string) {
        this.service.getTwitterMessage(lang).subscribe(data => {
            this.setDataTwitter(data.message);
            if (data.languageCode !== this.service.getTwitterMessage(lang)) {
                this.alertTwitter = null;
            }
        });
    }

    onSaveTwitter() {
        this.service.saveTwitterMessage(this.formTwitter.value.languageCode, this.formTwitter.value.message).subscribe(data => {
            this.alertTwitter = this.getAlert('success', 'social.twitter-save-ok');
        },
        (error) => {
            if (this.formTwitter.controls['message'].value === null) {
                this.alertTwitter = this.getAlert('danger', 'social.save-message-err');
            }
        });
    }

    onCancel() {
        this.init();
        this.alertAction = null;
        this.alertSocial = null;
        this.alertTwitter = null;
        window.scrollTo(0, 0);
    }

    private getAlert(type: string, label: string): { type: string, label: string } {
        const alert = {
            type: type,
            label: label
        };

        return alert;
    }
}
