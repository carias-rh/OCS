import { Injectable, Inject } from '@angular/core';
import { TranslateService, LangChangeEvent } from '@ngx-translate/core';
import { CONFIG_TOKEN } from '@eui/core';
import { Language } from '../models/language';
import { isNullOrUndefined } from 'util';

@Injectable()
export class LanguageService {
    defaultLanguage = 'en';
    browserLanguage: string;
    selectedLanguage: string;
    navigationLanguages: any[];

    constructor(
        @Inject(CONFIG_TOKEN) private config: any,
        private translate: TranslateService
    ) {
        this.browserLanguage = translate.getBrowserLang();

        this.translate.onLangChange.subscribe((params: LangChangeEvent) => {
            this.setLaguageMetadata(params.translations);
        });
    }

    public loadNavigationLanguages(systemLanguages: any[], queryLanguage = null) {
        let selected = this.defaultLanguage;
        let save = false;
        this.navigationLanguages = [];

        systemLanguages.forEach(item => {
            if (this.isAvailableLanguage(item.id)) {
                this.navigationLanguages.push(item);
            }
        });

        if (this.isAvailableLanguage(this.browserLanguage)) {
            selected = this.browserLanguage;
        }

        const sessionLanguage = this.getSessionLanguage();
        if (this.isAvailableLanguage(sessionLanguage)) {
            selected = sessionLanguage;
        }

        if (this.isAvailableLanguage(queryLanguage)) {
            selected = queryLanguage;
            save = true;
        }

        this.setNavigationLanguage(selected);
    }

    public setNavigationLanguage(lang: string, save = false) {
        if (this.selectedLanguage === lang) {
            return false;
        }

        this.selectedLanguage = lang;

        if (save) {
            this.saveSessionLanguage();
        }
        this.translate.use(this.selectedLanguage);
    }

    public createLanguage(code): Language {
        return new Language(code, this.config.global.languageList[code]);
    }

    public createLanguageList(languageCodelist: string[]): Language[] {
        return languageCodelist
            .map(code => new Language(code, this.config.global.languageList[code]))
            .sort(this.compareByName);
    }

    public orderLanguages(languages: Language[]) {
        return languages.sort(this.compareByName);
    }

    private isAvailableLanguage(language) {
        if (isNullOrUndefined(language)) {
            return false;
        }

        if (this.config.global.availableLanguages.length === 0) {
            return true;
        }

        return this.config.global.availableLanguages.indexOf(language) !== -1;
    }

    private saveSessionLanguage() {
        localStorage.setItem('nav-lang', this.selectedLanguage);
    }

    private getSessionLanguage() {
        const language = localStorage.getItem('nav-lang');

        return language;
    }

    private setLaguageMetadata(translations: any) {
        const title = translations['common']['page-title'];
        document.title = title;

        // document.querySelector('html').setAttribute('lang', this.selectedLanguage);
    }

    /**
     * Sort function for compare language objects
     */
    private compareByName(a: Language, b: Language) {
        if (a.name < b.name) {
            return -1;
        }

        if (a.name > b.name) {
            return 1;
        }

        return 0;
    }

}
