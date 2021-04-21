import { Component, Input, OnInit, Output, EventEmitter, OnChanges, OnDestroy, ViewChild } from '@angular/core';

import { TranslateService } from '@ngx-translate/core';
import { Subscription } from 'rxjs/Subscription';
import * as moment from 'moment';

import { Property } from '../../../../../core/models/property.model';
import { BasePropertyComponent } from './base-property.component';
import { InitiativeService } from '../../../../../core/services/initiative.service';
import { UxFormControlComponent } from '@eui/core';

@Component({
    selector: 'app-property',
    templateUrl: './property.component.html'
})
export class PropertyComponent extends BasePropertyComponent implements OnInit, OnChanges, OnDestroy {

    @Input() property: Property;
    @Input() submitted: boolean;
    @Output() propertyChange = new EventEmitter();
    @ViewChild('dateInput') dateInput: UxFormControlComponent;

    other: Property;
    hasOther = false;
    showOther = false;
    translateSubscription: Subscription;
    inputDateValue = null;
    datePickerLocale: any = {};

    constructor(
        private translateService: TranslateService,
        private initiativeService: InitiativeService
    ) {
        super();
        this.other = new Property();
    }

    get isCountry() {
        return this.property.inputType === 'country';
    }

    get isNationality() {
        return this.property.type === 'NATIONALITY';
    }

    get isSelect() {
        return this.property.inputType === 'select';
    }

    get isInputText() {
        return this.property.inputType === 'text';
    }

    get isDate() {
        return this.property.inputType === 'date';
    }

    get isErrorOther() {
        return this.showOther && this.submitted && this.other.error;
    }

    get feedbackClassOther() {
        return this.showOther && this.isErrorOther ? 'danger' : null;
    }

    ngOnInit() {
        if (this.isCountry) {
            this.hasOther = true;
            this.showOther = false;
            this.checkErrorInOther();
        }

        this.datePickerLocale = this.translateService.instant('datePicker');
        this.translateOptions();

        this.translateSubscription = this.translateService.onLangChange.subscribe(lang => {
            if (this.isDate) {
                this.datePickerLocale = this.translateService.instant('datePicker');
                this.dateInput.calendarControl.locale = this.datePickerLocale;
            }
            if (this.isCountry || this.isNationality) {
                this.translateOptions();
            }
        });
    }

    ngOnChanges() {
        if (this.hasOther && this.submitted) {
            this.checkErrorInOther();
        }
    }

    ngOnDestroy() {
        if (this.translateSubscription) {
            this.translateSubscription.unsubscribe();
        }
    }

    onChange(event) {
        if (this.hasOther && this.property.value === 'other') {
            this.showOther = true;
            this.checkErrorInOther();
        } else {
            this.showOther = false;
            this.other.value = null;
            this.property.other = null;
        }

        if (this.isDate) {
            this.property.clearError();
            if (event) {
                const date = moment(event);
                this.property.value = date.format('DD/MM/YYYY');
            } else {
                this.property.setError('oct.error.invaliddateformat');
            }
        }

        this.propertyChange.emit(this.property);
    }

    onChangeOther(value) {
        this.property.other = this.other.value;
        this.propertyChange.emit(this.property);
    }

    checkErrorInOther() {
        if (!this.submitted || !this.hasOther) {
            this.other.error = false;
            return false;
        }

        if (this.property.value === 'other' && this.getOtherValue().length === 0) {
            this.other.setError('oct.empty.property');
        }
    }

    private translateOptions() {
        if (this.isCountry) {
            this.translateCountries();
        }

        if (this.isNationality) {
            this.translateNationalities();
        }
    }

    private translateCountries() {
        let options = [];
        const first = [{
            id: '#',
            value: this.translateService.instant('common.select-country')
        }];
        const last = this.property.options.pop();

        this.initiativeService.getSystemCountries().subscribe(data => {
            const countries = data.map(country => ({ id: country.id, value: country.name }));
            options = first.concat(countries);
        });

        if (last.id === 'other') {
            options.push({
                id: 'other',
                value: this.translateService.instant('common.other-country')
            });
        }

        this.property.options = options;
    }

    private translateNationalities() {
        let options = [];
        const first = [{
            id: '#',
            value: this.translateService.instant('common.select-nationality')
        }];

        const nationalities = this.initiativeService.getNationalities();
        options = first.concat(nationalities);
        this.property.options = options;
    }

    private getOtherValue(): string {
        if (this.other.value) {
            return this.other.value.trim();
        }

        return '';
    }

    private isValidDate(value: string): boolean {
        const pattern = /^\d{2}[\/\-\.]\d{2}[\/\-\.]\d{4}$/;
        const dateObject = moment(value, 'DD/MM/YYYY');

        if (false === pattern.test(value)) {
            return false;
        }

        return dateObject.isValid();
    }

    private formatDateProperty(value: string): void {
        const dateObject = moment(value, 'DD/MM/YYYY');
        const formatted = dateObject.format('DD/MM/YYYY');

        if (formatted !== this.property.value) {
            this.property.value = formatted;
        }
    }

}
