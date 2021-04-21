import { Component, OnInit, Input, OnChanges, SimpleChanges } from '@angular/core';
import { FormGroup, FormBuilder } from '@angular/forms';

@Component({
    selector: 'ocs-evolution-country',
    templateUrl: 'evolution-country.component.html'
})

export class DashboardEvolutionCountryComponent implements OnInit, OnChanges {
    @Input() countries: any[] = [];
    @Input() data: any[] = [];

    countryData;
    countryInfo;
    showChart = true;
    form: FormGroup;

    totalSupporters: number;
    mostActiveMonth: '';

    constructor(private fb: FormBuilder) {
        this.form = this.fb.group({
            country: [null]
        });

    }

    ngOnInit() {
    }

    ngOnChanges(changes: SimpleChanges) {
        if (this.countries.length && this.data.length) {
            const code = this.countries[0].id;
            this.selectCountry(code);
            this.form.get('country').setValue(code);
        }
    }

    onSelectCountry(countryCode) {
        this.selectCountry(countryCode);
    }

    onButtonClicked(event) {
        this.showChart = event.id === 'chart';
    }

    private selectCountry(countryCode) {
        try {
            this.countryData = this.data.find(item => item.countryCode === countryCode).evolutionMapEntryDetailDTOs.slice();
            this.countryInfo = this.data.find(item => item.countryCode === countryCode);
        } catch (e) {
            this.countryData = null;
            this.countryInfo = null;
        }
    }

}
