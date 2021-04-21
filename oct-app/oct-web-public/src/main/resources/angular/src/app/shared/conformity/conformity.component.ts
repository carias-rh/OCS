import { Component } from '@angular/core';
import { InitiativeService } from '../../core/services/initiative.service';
import { LanguageService } from '../../core/services/language.service';

@Component({
    selector: 'app-conformity',
    templateUrl: 'conformity.component.html'
})

export class ConformityComponent {
    conformityCertificatePdf: string;
    page: number = 1;

    constructor(private service: InitiativeService, private languageService: LanguageService) {
        this.conformityCertificatePdf = service.getUrlCertificate();
        this.languageService.titleText = 'common.head-title.conformity';
        this.languageService.setMetadata();
    }

    onError(error) {
        console.log('Error in certificate');
    }

}
