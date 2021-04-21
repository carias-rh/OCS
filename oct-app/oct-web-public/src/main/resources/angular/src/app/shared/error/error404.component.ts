import { Component } from '@angular/core';
import { LanguageService } from 'src/app/core/services/language.service';

@Component({
    templateUrl: 'error404.component.html'
})

export class Error404Component {

    constructor(private languageService: LanguageService) {
        this.languageService.titleText = 'common.page-title';
        this.languageService.setMetadata();
    }

}
