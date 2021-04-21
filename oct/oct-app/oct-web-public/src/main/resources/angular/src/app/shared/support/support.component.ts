import { Component, OnInit } from '@angular/core';
import { LanguageService } from '../../core/services/language.service';

@Component({
    selector: 'app-support',
    templateUrl: 'support.component.html'
})

export class SupportComponent implements OnInit {

    selectedLanguage: string;

    constructor( private languageService: LanguageService) { }

    ngOnInit() {}

    urlLanguage() {
        return this.languageService.selectedLanguage;
    }

}
