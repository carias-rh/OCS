import { Component, OnInit, Input } from '@angular/core';
import { LanguageService } from '../../../../../core/services/language.service';

@Component({
    selector: 'app-support-country',
    templateUrl: 'support-country.component.html'
})

export class SupportCountryComponent implements OnInit {

    @Input() showFields = false;

    optionalValidation = false;
    optionalValidationSelected = false;
    showButtons = false;
    activeButton = true;
    supportCountry = false;
    urlRegister: string;

    constructor(private languageService: LanguageService) {}

    ngOnInit() { }

    onChangePrivacy(event) {
        this.supportCountry = event.target.checked;
        this.activeButton = false;
    }

    urlPrivacy() {
        return '#/screen/privacy?lang=' + this.languageService.selectedLanguage;
    }

    getUrlRegister() {
        return this.urlRegister;
    }

    onSaveUsernameChanged(value) {
        this.supportCountry = value;
    }

}
