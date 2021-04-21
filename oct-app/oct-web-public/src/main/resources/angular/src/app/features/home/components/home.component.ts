import { Component, OnInit } from '@angular/core';
import { InitiativeService } from '../../../core/services/initiative.service';
import { LanguageService } from 'src/app/core/services/language.service';

@Component({
    templateUrl: './home.component.html'
})
export class HomeComponent implements OnInit {

    isProgress = false;
    isDistributionMap = false;
    isRecentSignatories = false;

    constructor(private iniciativeService: InitiativeService, private languageService: LanguageService) {
        this.languageService.titleText = 'common.page-title';
        this.languageService.setMetadata();
    }

    ngOnInit() {
        this.iniciativeService.getCustomisations().subscribe(data => {
            this.isRecentSignatories = data.showRecentSupporters;
            this.isDistributionMap = data.showDistributionMap;
            this.isProgress = data.showProgressionBar;
        });
    }

}
