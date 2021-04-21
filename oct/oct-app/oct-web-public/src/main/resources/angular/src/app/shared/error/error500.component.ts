import { Component, OnInit } from '@angular/core';
import { ApplicationService } from '../../core/services/application.service';
import { LanguageService } from 'src/app/core/services/language.service';

@Component({
    templateUrl: 'error500.component.html'
})

export class Error500Component implements OnInit {

    errorRef: string;

    constructor(private applicationService: ApplicationService, private languageService: LanguageService) {
        this.languageService.titleText = 'common.page-title';
        this.languageService.setMetadata();
    }

    ngOnInit() {
        if (this.applicationService.errorRef) {
            this.errorRef = this.applicationService.errorRef;
        } else {
            this.errorRef = null;
        }
    }

}
