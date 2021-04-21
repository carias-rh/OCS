import { Component, OnInit } from '@angular/core';
import { FormGroup, FormBuilder, Validators, FormsModule } from '@angular/forms';

import { ApiOcsService } from '../../../../core/services/api-ocs.service';
import { LanguageService } from '../../../../core/services/language.service';
import { ApplicationService } from '../../../../core/services/application.service';

@Component({
    selector: 'app-stay-informed',
    templateUrl: 'stay-informed.component.html'
})

export class StayInformedComponent implements OnInit {

    form: FormGroup;
    informedOk = false;
    invalidEmail = false;
    invalidPrivacy = false;
    errorSaveEmail = false;
    invalidCitizensInitiative = false;
    isBlocked = false;

    constructor(
        private fb: FormBuilder,
        private ocsService: ApiOcsService,
        private languageService: LanguageService,
        private applicationService: ApplicationService
    ) {}

    ngOnInit() {
        this.createForm();
    }

    createForm() {
        this.form = this.fb.group({
            citizensInitiative: [false, [Validators.required, Validators.requiredTrue]],
            privacy: [false, [Validators.required, Validators.requiredTrue]],
            email: [null,
                [
                    Validators.required,
                    // tslint:disable-next-line:max-line-length
                    Validators.pattern(/^(("[\w-\s]+")|([\w-]+(?:\.[\w-]+)*)|("[\w-\s]+")([\w-]+(?:\.[\w-]+)*))(@((?:[\w-]+\.)*\w[\w-]{0,66})\.([a-z]{2,6}(?:\.[a-z]{2})?)$)|(@\[?((25[0-5]\.|2[0-4][0-9]\.|1[0-9]{2}\.|[0-9]{1,2}\.))((25[0-5]|2[0-4][0-9]|1[0-9]{2}|[0-9]{1,2})\.){2}(25[0-5]|2[0-4][0-9]|1[0-9]{2}|[0-9]{1,2})\]?$)/)
                ]
            ]
        });
    }

    onSubmit() {
        this.errorSaveEmail = false;
        this.invalidEmail = false;
        this.invalidPrivacy = false;
        this.invalidCitizensInitiative = false;
        const formValidated = this.validateForm();

        if (Object.values(formValidated).find(i => i === true)) {
            if (formValidated.email) {
                this.invalidEmail = true;
            }
            if (formValidated.privacy) {
                this.invalidPrivacy = true;
            }
            if (formValidated.citizensInitiative) {
                this.invalidCitizensInitiative = true;
            }
        } else {
            this.invalidEmail = false;
            this.isBlocked = true;
            this.ocsService.saveEmail(
                this.form.controls['email'].value,
                this.languageService.selectedLanguage,
                this.applicationService.signatureIdentifier,
                this.form.controls['citizensInitiative'].value
            ).subscribe(
                () => {
                    this.informedOk = true;
                    this.isBlocked = false;
                },
                () => {
                    this.errorSaveEmail = true;
                    this.isBlocked = false;
                }
            );
        }
    }

    private validateForm() {
        const formErrors = {
            email: false,
            privacy: false,
            citizensInitiative: false
        };

        if (!this.form.controls['email'].value || this.form.controls['email'].invalid) {
            formErrors.email = true;
        }

        if (this.form.controls['privacy'].invalid) {
            formErrors.privacy = true;
        }

        if (this.form.controls['citizensInitiative'].invalid) {
            formErrors.citizensInitiative = true;
        }

        return formErrors;
    }

}
