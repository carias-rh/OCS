import { Component, OnInit, Input, OnDestroy } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';
import { InitiativeService } from '../../../../core/services/initiative.service';
import { CountryMap } from '../../../../core/models/countryMap.model';
import { Subscription } from 'rxjs/Subscription';
import { LanguageService } from '../../../../core/services/language.service';

@Component({
    selector: 'app-carrousel',
    templateUrl: 'carrousel.component.html'
})

export class CarrouselComponent implements OnInit, OnDestroy {
    @Input() showProgress = false;

    distributionMap: CountryMap[];
    subscriptionLanguage: Subscription;
    currentLanguage: string;

    constructor(
        private iniciativeService: InitiativeService,
        private translateService: TranslateService,
        private languageService: LanguageService
    ) { }

    ngOnInit() {
        this.currentLanguage = this.languageService.selectedLanguage;
        this.loadDistributionMap();

        this.subscriptionLanguage = this.translateService.onLangChange.subscribe(() => {
            if (this.currentLanguage !== this.translateService.currentLang) {
                this.loadDistributionMap();
            }
        });
    }

    ngOnDestroy() {
        this.subscriptionLanguage.unsubscribe();
    }

    private loadDistributionMap() {
        this.distributionMap = null;
        this.iniciativeService.getDistributionMap().subscribe(data => this.distributionMap = data);
    }

}
