import { Component, OnInit } from '@angular/core';
import { ApplicationService } from '../../../core/services/application.service';
import { LanguageService } from 'src/app/core/services/language.service';

@Component({
    templateUrl: './already.component.html'
})
export class AlreadyComponent implements OnInit {
    constructor(private applicationService: ApplicationService, private languageService: LanguageService) {
        this.languageService.titleText = 'common.page-title';
        this.languageService.setMetadata();
    }

    ngOnInit() {
        // To allow to navigate to the Home
        this.applicationService.alreadySupported = false;
    }

}
