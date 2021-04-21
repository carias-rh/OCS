import { Component, OnInit, OnDestroy } from '@angular/core';
import { HttpErrorResponse } from '@angular/common/http';
import { Router } from '@angular/router';

import { Subscription } from 'rxjs/Subscription';
import { TranslateService } from '@ngx-translate/core';

import { InitiativeService } from '../../../../core/services/initiative.service';
import { LanguageService } from '../../../../core/services/language.service';
import { SupportFormService } from './support-form.service';
import { ApplicationService } from '../../../../core/services/application.service';

import { Country } from '../../../../core/models/country.model';
import { SupportForm } from '../../../../core/models/supportForm.model';
import { Property } from '../../../../core/models/property.model';

@Component({
    selector: 'app-form',
    templateUrl: 'form.component.html'
})

export class FormComponent implements OnInit, OnDestroy {
    systemCountries: Country[] = [];
    countries: any[] = []; // residence country selector
    supportForm: SupportForm;
    backSelectCountry = false;
    countrySelected = false;
    showSelectCountry = true;
    selectedCountry = '#';
    residentValue = '#';
    alertResident = false;
    optionalValidation = false;
    optionalValidationSelected = false;
    translateSubscription: Subscription;
    supportCountry = false;
    defaultCountryFormUrl: string;

    // To check
    showFields: boolean = false;
    submitted: boolean = false;
    valid: boolean = false;
    error500: boolean = false;
    urlRegister: string;

    constructor(
        private applicationService: ApplicationService,
        private initiativeService: InitiativeService,
        private translateService: TranslateService,
        private languageService: LanguageService,
        private supportFormService: SupportFormService,
        private router: Router
    ) {}

    get warningResident() {
        return 'form.in-' + this.supportForm.countryCode + '-allowed';
    }

    get hasDocuments() {
        return this.supportForm && this.supportForm.documentType;
    }

    get requirementsCountry(): string {
        const text: string = this.translateService.instant('common.requirements.text');
        const url = 'https://eur-lex.europa.eu/legal-content/' + this.languageService.selectedLanguage + '/TXT/PDF/?uri=CELEX:32019R0788';

        return text.replace('{{url}}', url);
    }

    ngOnInit() {
        this.initiativeService.getSystemCountries().subscribe(data => {
            this.systemCountries = data;
            this.defaultCountryFormUrl = this.applicationService.defaultCountryForm;
            const okCountry = data.find( country => country.id === this.defaultCountryFormUrl);
            if (okCountry) {
                this.onSelectCountry(this.defaultCountryFormUrl);
            } else {
                this.onSelectCountry(this.selectedCountry);
            }
        });
        this.initiativeService.getCustomisations().subscribe(data => this.optionalValidation = data.optionalValidation);
        this.initiativeService.getInitiative().subscribe(data => this.urlRegister = data.webpage);

        this.translateSubscription = this.translateService.onLangChange
            .switchMap(() => this.initiativeService.getSystemCountries())
            .subscribe(data => this.systemCountries = data);
    }

    ngOnDestroy() {
        if (this.translateSubscription) {
            this.translateSubscription.unsubscribe();
        }
    }

    onSelectCountry(countryCode) {
        this.selectedCountry = countryCode;

        if (countryCode === null || countryCode === '#' ) {
            this.supportForm = null;
            this.showFields = null;
            this.countrySelected = null;
            this.backSelectCountry = null;
            this.showSelectCountry = true;
            this.languageService.titleText = 'common.page-title';
        } else {
            this.languageService.titleText = 'common.page-title';
            this.onFillForm();
        }

        this.languageService.setMetadata();
    }

    onFillForm() {
        this.loadCountryProperties(this.selectedCountry);
        this.supportCountry = true;
        this.backSelectCountry = true;
        this.countrySelected = true;
        this.showSelectCountry = false;
    }

    SelectCountryAgain() {
        this.supportForm = null;
        this.showFields = null;
        this.countrySelected = null;
        this.selectedCountry = '#';
        this.backSelectCountry = null;
        this.showSelectCountry = true;
        this.clearErrors();
    }

    onSelectResident(value) {
        this.residentValue = value;

        if (this.residentValue === '#') {
            this.alertResident = false;
            this.showFields = false;
        } else if (value === '0') {
            this.alertResident = true;
            this.showFields = false;
        } else {
            this.alertResident = false;
            this.showFields = true;
        }
    }

    public onDocumentChange(property: Property) {
        this.supportForm.documentType = property;
    }

    public onPropertyChange(property: Property) {
        this.supportForm.updateProperty(property);
    }

    public onCaptchaChange(property: Property) {
        this.supportForm.captcha = property;
    }

    public onChangePrivacy(event) {
        this.supportForm.privacy.value = event.target.checked;
    }

    public OnChangeCertify(event) {
        this.supportForm.certify.value = event.target.checked;
    }

    loadCountryProperties(countryCode: string) {
        this.initiativeService.getCountryProperties(countryCode).subscribe(data => this.setCountryForm(countryCode, data));
    }

    getResidentOptions() {
        const options = [
            { id: '#', value: 'common.select' },
            { id: '1', value: 'common.yes' },
            { id: '0', value: 'common.no' }
        ];

        return options;
    }

    public setCountryForm(countryCode: string, properties) {
        this.supportForm = this.supportFormService.loadProperties(countryCode, properties);
        this.showFields = true;
    }

    urlPrivacy() {
        return '#/screen/privacy?lang=' + this.languageService.selectedLanguage;
    }

    getUrlRegister() {
        return this.urlRegister;
    }

    // Show checkbox for ignore some errors and continue
    // If user has ckecked the input, continue showing the checkbox and checked
    public showOptionalValidation() {
        return this.showFields && this.hasErrorsIgnore() && this.optionalValidation;
    }

    public showCaptcha() {
        return this.showFields;
    }

    public isValidated() {
        return this.submitted ? this.valid : true;
    }

    public is500error() {
        return this.error500;
    }

    public onSubmit(event) {
        this.error500 = false;
        this.submitted = true;
        if (this.validate() === false) {
            this.valid = false;
            this.supportFormService.refreshCaptcha();
            this.scrollToErrors();
            return false;
        }

        this.valid = true;
        this.insertSignature();
    }

    public insertSignature() {
        const fields = this.getPropertiesForApi();

        this.initiativeService.insertSignature(
            this.supportForm.countryCode,
            fields,
            this.supportForm.captcha,
            this.optionalValidationSelected
        )
        .subscribe(
            (res: any) => {
                // status = 200
                this.initiativeService.initiative.signatureIdentifier = res.signatureIdentifier;
                this.applicationService.signatureIdentifier = res.signatureIdentifier;
                this.router.navigateByUrl('/screen/submitted');
            },
            (errorRes: HttpErrorResponse) => {
                // status != 200
                if (errorRes.error.code === 500) {
                    this.error500 = true;
                    this.scrollToErrors();
                } else {
                    this.error500 = false;
                    try {
                        this.supportFormService.processErrorResponse(errorRes.error);
                    } catch (e) {
                        // err json parse
                    }
                    this.supportFormService.refreshCaptcha();
                    this.valid = false;
                    this.scrollToErrors();
                }
            }
        );
    }

    public hasErrorsIgnore() {
        return this.supportFormService.hasErrorsIgnore;
    }

    private clearErrors() {
        this.valid = true;
        this.submitted = false;
        if (this.supportForm) {
            this.supportForm.clearErrors();
        }
    }

    private validate() {
        return this.supportFormService.validate();
    }

    private scrollToErrors() {
        let el = document.querySelector('form h2');

        try {
            el.scrollIntoView(true);
        } catch (e) {
            window.scrollTo(0, 0);
        }
    }

    private getPropertiesForApi() {
        return this.supportFormService.getPropertiesForApi();
    }

}
