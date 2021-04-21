import { Component, Input, OnChanges } from '@angular/core';
import { CountryMap } from '../../../../core/models/countryMap';

@Component({
    selector: 'ocs-top7',
    templateUrl: 'top7.component.html'
})
export class DashboardTop7Component implements OnChanges {
    @Input() data: CountryMap[] = [];

    first: CountryMap;

    constructor() {}

    ngOnChanges() {
        if (this.data.length > 0) {
            this.first = this.data.shift();
        }
    }

}
