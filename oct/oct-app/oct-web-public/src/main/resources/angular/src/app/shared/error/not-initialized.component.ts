import { Component } from '@angular/core';
import { LanguageService } from 'src/app/core/services/language.service';

@Component({
    templateUrl: 'not-initialized.component.html'
})

export class NotInitializedComponent {

    constructor(private languageService: LanguageService) {
        this.languageService.titleText = 'common.page-title';
        this.languageService.setMetadata();
    }

}
