import { Component, OnInit } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';

import { ProgressionStatus } from '../../core/models/progressionStatus';
import { InitiativeService } from '../../core/services/initiative.service';

import { CountryMap } from '../../core/models/countryMap';

@Component({
    templateUrl: './dashboard.component.html'
})
export class DashboardComponent implements OnInit {
    top: ProgressionStatus;
    top7: CountryMap[] = [];
    evolutionTotal: any;
    evolutionCountries: any[] = [];
    countries: any[] = [];

    constructor(private initiativeService: InitiativeService, private translateService: TranslateService) {}

    ngOnInit() {
        this.initiativeService.getProgressionStatus().subscribe(data => this.top = data);
        this.initiativeService.getTop7DistributionMap().subscribe(data => this.top7 = data);

        this.initiativeService.getEvolutionMap().subscribe(res => {
            this.evolutionTotal = res.find(item => item.countryCode === 'all');
            this.evolutionCountries = res.filter(item => item.countryCode !== 'all');
        });

        this.translateService.onLangChange.subscribe(() => this.loadCountries());

        this.loadCountries();
    }

    private loadCountries() {
        this.initiativeService.getCountries().subscribe(data => this.countries = data);
    }

}
