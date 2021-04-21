import { Component } from '@angular/core';
import { LanguageService } from 'src/app/core/services/language.service';

@Component({
    templateUrl: './submitted.component.html'
})
export class SubmittedComponent {

    constructor(private languageService: LanguageService) {
        this.languageService.titleText = 'common.page-title';
        this.languageService.setMetadata();
    }

}
