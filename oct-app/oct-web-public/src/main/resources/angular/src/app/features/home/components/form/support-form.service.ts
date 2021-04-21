import { Injectable, Inject } from '@angular/core';
import { CONFIG_TOKEN } from '@eui/core';
import { Property } from '../../../../core/models/property.model';
import { SupportForm } from '../../../../core/models/supportForm.model';
import { InitiativeService } from '../../../../core/services/initiative.service';
import { TranslateService } from '@ngx-translate/core';

import * as moment from 'moment';

import { Router } from '@angular/router';
import { ApplicationService } from '../../../../core/services/application.service';

@Injectable()
export class SupportFormService {

    selectedCountry: string;
    supportForm: SupportForm;
    systemCountries: any[] = [];
    nationalities: any[] = [];
    hasErrorsIgnore: boolean = false;

    validProperties = [
        'street', 'postal.code', 'city', 'country', 'date.of.birth',
        'passport', 'id.card', 'residence.permit', 'personal.number', 'personal.id',
        'permanent.residence', 'national.id.number', 'registration.certificate',
        'citizens.card', 'full.first.names', 'family.names', 'street.number'
    ];

    constructor(
        @Inject(CONFIG_TOKEN) protected config: any,
        private applicationService: ApplicationService,
        private initiativeService: InitiativeService,
        private translateService: TranslateService,
        private router: Router
    ) {
        this.initiativeService.getSystemCountries().subscribe(data => {
            this.systemCountries = data;
            this.nationalities = this.initiativeService.getNationalities();
        });
    }

    public loadProperties(countryCode: string, data: Property[]) {
        this.selectedCountry = countryCode;
        this.supportForm = new SupportForm(countryCode);

        data.sort(this.prioritySorting).forEach(property => {
            if (this.isValidProperty(property)) {
                this.addPropertyField(property);
            }
        });

        if (this.supportForm.documentType) {
            this.supportForm.documentType.options = this.getDocumentOptions();
        }

        return this.supportForm;
    }

    public clearErrors() {
        this.supportForm.clearErrors();
    }

    public validate() {
        let valid = true;

        this.clearErrors();

        if (!this.supportForm.certify.value) {
            this.supportForm.certify.setError('oct.empty.property');
            valid = false;
        }

        if (!this.supportForm.privacy.value) {
            this.supportForm.privacy.setError('oct.empty.property');
            valid = false;
        }

        if (!this.supportForm.captcha.value) {
            this.supportForm.captcha.setError('oct.empty.property');
            valid = false;
        }

        if (this.supportForm.hasDocumentsFields() && !this.supportForm.documentType.value) {
            this.supportForm.documentType.setError('oct.empty.property');
            valid = false;
        }

        this.supportForm.personalFields.forEach((field, index) => {
            const value = this.getValue(field);

            if (field.required) {
                if (value === null || value.trim() === '') {
                    this.supportForm.personalFields[index].setError('oct.empty.property');
                    valid = false;
                } else if (value === '#' && field.options) {
                    this.supportForm.personalFields[index].setError('oct.empty.property');
                    valid = false;
                }
            }

            if (value === 'invalid-date') {
                this.supportForm.personalFields[index].setError('oct.error.invaliddateformat');
            }
        });

        this.supportForm.addressFields.forEach((field, index) => {
            const value = this.getValue(field);

            if (field.required) {
                if (value.trim() === '') {
                    this.supportForm.addressFields[index].setError('oct.empty.property');
                    valid = false;
                } else if (value === '#' && (field.type === 'COUNTRY' || field.type === 'NATIONALITY')) {
                    this.supportForm.addressFields[index].setError('oct.empty.property');
                    valid = false;
                } else if (field.label === 'postal.code' && !this.validPostalCode(this.supportForm.addressFields, value)) {
                    this.supportForm.addressFields[index].setError('oct.error.fr.postalcode');
                    valid = false;
                }
            }

            if (value === 'invalid-date') {
                this.supportForm.addressFields[index].setError('oct.error.invaliddateformat');
            }
        });

        return valid;
    }

    public setErrorFromLabel(fieldkey: string, errorMessage: string) {
        let field;

        if (fieldkey === 'postal.code') {
            this.hasErrorsIgnore = true;
        }

        field = this.supportForm.personalFields.filter(item => item.label === fieldkey);
        if (field.length) {
            if (fieldkey === 'nationality') {
                errorMessage = errorMessage.concat('.').concat(fieldkey);
            }
            this.supportForm.personalFields[field[0].index].setError(errorMessage);
        }

        field = this.supportForm.addressFields.filter(item => item.label === fieldkey);
        if (field.length) {
            this.supportForm.addressFields[field[0].index].setError(errorMessage);
        }

        if (this.supportForm.documentType && this.supportForm.documentType.label === fieldkey) {
            this.supportForm.documentType.setError(errorMessage);
            this.hasErrorsIgnore = true;
        }
    }

    public getPropertiesForApi() {
        let data: any[];
        let fields: any[];

        fields = this.supportForm.personalFields.concat(this.supportForm.addressFields);
        if (this.supportForm.documentType) {
            fields.push(this.supportForm.documentType);
        }

        data = fields.map(item => {
            return {
                group: item.group,
                id: item.id,
                label: item.label,
                priority: item.priority,
                required: item.required,
                type: item.type,
                value: this.getValue(item)
            };
        });

        return data;
    }

    public refreshCaptcha() {
        const captcha = new Property();
        captcha.type = this.supportForm.captcha.type;
        captcha.value = this.supportForm.captcha.value;
        captcha.error = this.supportForm.captcha.error;
        captcha.errorMessage = this.supportForm.captcha.errorMessage;

        this.supportForm.captcha = captcha;
    }

    public processErrorResponse(res: any) {
        // Recheck if they are error for ignore and continue
        // But don't reset the current value for optionalValidation

        this.hasErrorsIgnore = false;

        if (res.code === 409) {
            this.applicationService.alreadySupported = true;
            this.router.navigateByUrl('/screen/home/already');

        } else {
            try {
                if (res.errorFields.length > 0) {
                    res.errorFields.forEach(errField => {
                        this.setErrorFromLabel(errField.fieldKey, errField.errorMessage);
                    });
                }

                if (res.captchaValidation === false) {
                    this.supportForm.captcha.error = true;
                    this.supportForm.captcha.errorMessage = 'oct.captcha.invalid';
                }
            } catch (e) { }
        }
    }

    private getValue(property: Property): string {
        let value = property.value;

        if (property.inputType === 'date' && value) {
            value = this.getDateValue(value);
        }

        return value;
    }

    private getDateValue(value): string {
        const dateObject = moment(value, 'DD/MM/YYYY');
        const invalidValue = 'invalid-date';

        if (!dateObject.isValid()) {
            return invalidValue;
        }

        if (typeof dateObject['_i'] === 'string') {
            const pattern = /^\d{2}\/\d{2}\/\d{4}$/;
            const valid = pattern.test(dateObject['_i']);
            if (!valid) {
                return invalidValue;
            }
        }

        return dateObject.format('DD/MM/YYYY');
    }

    private isValidProperty(property: Property) {
        return this.validProperties.indexOf(property.label) !== -1;
    }

    private addPropertyField(item) {
        let property = new Property(item.id, item.group, item.label, item.priority, item.required, item.type);

        property.value = '';
        property.error = false;

        if (property.type === 'NATIONALITY') {
            property.value = '#';
            property.options = this.getNationalityOptions();
        }
        if (property.type === 'COUNTRY') {
            property.value = '#';
            property.options = this.getCountriesOptionsForCountry();
        }

        if (property.group === 1) {
            this.supportForm.addPersonalField(property);
        } else if (property.group === 2) {
            this.supportForm.addAddressField(property);
        } else if (property.group === 3) {
            this.supportForm.addDocumentField(property);
        }
    }

    private getCountriesOptionsForCountry() {
        let options = this.getCountryOptions();
        const country = this.supportForm.countryCode;

        if (options.length && country && country !== '#') {
            options.push({
                id: 'other',
                value: this.translateService.instant('common.other-country')
            });
        }

        return options;
    }

    private getCountryOptions() {
        const first = [{
            id: '#',
            value: this.translateService.instant('common.select-country')
        }];

        const options = this.systemCountries.map(country => ({ id: country.id, value: country.name }));
        return first.concat(options);
    }

    private getNationalityOptions() {
        const first = [{
            id: '#',
            value: this.translateService.instant('common.select-nationality')
        }];

        return first.concat(this.nationalities);
    }

    private getDocumentOptions() {
        const options = this.supportForm.documentTypes.map(label => {
            const key = this.supportForm.countryCode + '.' + label;
            return { id: label, value: this.config.global.documents[key] };
        });

        return options;
    }

    private prioritySorting(a, b) {
        if (a.priority > b.priority) {
            return -1;
        }

        if (a.priority < b.priority) {
            return 1;
        }

        return 0;
    }

    private validPostalCode(addressFields, value) {
        let country: string;
        addressFields.forEach(item => {
            if (item.label === 'country') {
                country = item.value;
            }
        });
        if (country === 'fr') {
            if (value.length !== 5 || isNaN(value)) {
                return false;
            } else {
                return true;
            }
        }
        return true;
    }

}
