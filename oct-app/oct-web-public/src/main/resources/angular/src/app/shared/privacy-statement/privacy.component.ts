import { Component } from '@angular/core';
import { LanguageService } from 'src/app/core/services/language.service';

@Component({
    selector: 'app-privacy',
    templateUrl: 'privacy.component.html'
})

export class PrivacyComponent {

    constructor(private languageService: LanguageService) {
        this.languageService.titleText = 'common.page-title';
        this.languageService.setMetadata();
    }

}
