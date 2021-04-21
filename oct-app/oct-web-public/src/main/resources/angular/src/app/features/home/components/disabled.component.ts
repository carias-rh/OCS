import { Component } from '@angular/core';
import { LanguageService } from 'src/app/core/services/language.service';

@Component({
    templateUrl: './disabled.component.html'
})
export class DisabledComponent {

    constructor(private languageService: LanguageService) {
        this.languageService.titleText = 'common.page-title';
        this.languageService.setMetadata();
    }

}
