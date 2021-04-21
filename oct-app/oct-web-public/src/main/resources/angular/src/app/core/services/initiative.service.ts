import { Injectable, Inject } from '@angular/core';
// import { Router } from '@angular/router';
import { Observable } from 'rxjs/Observable';
import { CONFIG_TOKEN } from '@eui/core';

import { ApiOcsService } from './api-ocs.service';
import { LanguageService } from './language.service';
import { Initiative } from '../models/initiative.model';
import { Contact, Contacts } from '../models/contact.model';
import { Customisations } from '../models/customisations.model';
import { Description } from '../models/description.model';
import { CountryMap } from '../models/countryMap.model';
import { Progression } from '../models/progression.model';
import { Country } from '../models/country.model';
import { TranslateService } from '@ngx-translate/core';

@Injectable()
export class InitiativeService {

    initiative: Initiative;
    initiativeLanguage;
    urlRegister;
    goal;
    progressBarCompleted;
    colorPicker;
    customization$;

    cachedDescription;
    cachedInitiativeLanguages;
    cachedInitiativeDescriptions;
    cachedSystemLanguages;
    cachedCountries;
    cachedCustomisations: Customisations;

    constructor(
        @Inject(CONFIG_TOKEN) protected config: any,
        private api: ApiOcsService,
        // private router: Router,
        private translateService: TranslateService,
        private languageService: LanguageService,
    ) {}

    public getSystemLanguages(): Observable<any[]> {
        if (this.cachedSystemLanguages) {
            return Observable.of(this.cachedSystemLanguages);
        }

        return this.api.getAllLanguages()
            .map(data => this.parseSystemLanguages(data))
            .do(data => this.cachedSystemLanguages = data);
    }

    public getSystemState() {
        return this.api.getSystemState();
    }

    public getSystemCountries() {
        if (this.cachedCountries) {
            return Observable.of(this.cachedCountries)
                .map(data => this.parseSystemCountries(data));
        }

        return this.api.getAllCountries()
            .do(countries => this.cachedCountries = countries)
            .map(data => this.parseSystemCountries(data));
    }

    public getNationalities() {
        return this.parseNationalities(this.cachedCountries);
    }

    public getInitiative(): Observable<Initiative> {
        if (this.initiative) {
            return Observable.of(this.initiative);
        }

        return this.api.getDefaultInititative()
            .map(data => this.parseDefaultInitiative(data))
            .do(data => this.initiative = data);
    }

    public getCachedInitiativeLanguages(): Observable<any> {
        if (this.cachedInitiativeLanguages) {
            return Observable.of(this.cachedInitiativeLanguages);
        }

        return this.api.getInitiativeLanguages()
            .map(data => this.parseSystemLanguages(data))
            .do(data => this.cachedInitiativeLanguages = data);
    }

    public getInitiativeDescriptions(): Observable<Description[]> {
        if (this.cachedInitiativeDescriptions) {
            return Observable.of(this.cachedInitiativeDescriptions);
        }

        return this.api.getLanguageDescriptions()
            .map(data => data.descriptions)
            .do(data => this.cachedInitiativeDescriptions = data);
    }

    public getInitiativeDescription(languageCode: string) {
        return this.api.getDescriptionByLanguage(languageCode);
    }

    public getCustomisations(): Observable<Customisations> {
        if (this.cachedCustomisations) {
            return Observable.of(this.cachedCustomisations);
        }

        return this.loadCustomisations()
            .do(data => this.cachedCustomisations = data);
    }

    public clearCustomisations() {
        this.cachedCustomisations = null;
    }

    public getContacts(): Observable<Contacts> {
        return this.api.getInitiativeContacts()
            .map(data => this.parseContacts(data));
    }

    public insertSignature(country: string, properties: any[], captcha: any, optionalValidation = false) {
        return this.api.insertSignature(country, properties, captcha, optionalValidation);
    }

    public loadCustomisations(): Observable<Customisations> {
        return this.api.getCustomizations().map(data => new Customisations(data));
    }

    public getRegisterUrl() {
        return this.urlRegister;
    }

    public getProgessStatus() {
        return this.api.getProgessStatus()
            .map(data => new Progression(data.goal, data.signatureCount));
    }

    public getRecentSignatures() {
        return this.api.getLatestSignatures();
    }

    public getDistributionMap() {
        return this.api.getDistributionMap()
            .map(list => list.map(item => new CountryMap(item.countryCode, item.count, item.treshold, item.percentage)));
    }

    public getUrlCertificate() {
        return this.api.getDownloadCustomFile('certificate');
    }

    public getUrlLogo() {
        return this.api.getDownloadCustomFile('logo') + '?v=' + Date.now();
    }

    public getUrlDownloadSignature(uuid: string) {
        const selectedLanguage = this.languageService.selectedLanguage;
        return this.api.getDownloadReceipt(uuid, selectedLanguage);
    }

    public getTwitterMessage() {
        const selectedLanguage = this.languageService.selectedLanguage;
        return this.api.getSocialMediaMessage('twitter', selectedLanguage);
    }

    public getCallForActionMessage() {
        const selectedLanguage = this.languageService.selectedLanguage;
        return this.api.getSocialMediaMessage('callForAction', selectedLanguage);
    }

    public getTwitterMessage2(languageCode: string) {
        return this.api.getSocialMediaMessage('twitter', languageCode);
    }

    public getCountryProperties(countryCode: string) {
        return this.api.getCountryProperties(countryCode);
    }

    public error500(text) {
        console.log('Error: %s', text);
        // this.router.navigate(['/error']);
    }

    private parseDefaultInitiative(data) {
        const initiative = new Initiative();

        if (data) {
            initiative.registrationNumber = data.initiativeInfo.registrationNumber;
            initiative.registrationDate = data.initiativeInfo.registrationDate;
            initiative.closingDate = data.initiativeInfo.closingDate;
            initiative.webpage = data.initiativeInfo.url;
            initiative.initiativeInfo = data.initiativeInfo;

            if (data.descriptions.length) {
                initiative.description.languageCode = data.descriptions[0].languageCode;
                initiative.description.title = data.descriptions[0].title;
                initiative.description.subject = data.descriptions[0].subject;
                initiative.description.objectives = data.descriptions[0].objectives;
                initiative.description.url = data.descriptions[0].url;
                initiative.description.partialRegistration = data.descriptions[0].partialRegistration;
            }
        }

        return initiative;
    }

    private parseSystemLanguages(data) {
        return data.map(item => {
            return {
                id: item.languageCode,
                text: this.config.global.languageList[item.languageCode],
                displayOrder: item.displayOrder
            };
        }).sort(this.compareDisplayOrderOptions);
    }

    private parseSystemCountries(list: any[]) {
        const countries = list.map(item => {
            const countryName = this.translateService.instant('common.country.' + item.countryCode);
            return new Country(item.countryCode, countryName);
        });

        return countries.sort(this.orderByProperty('name'));
    }

    private parseNationalities(list: any[]): any[] {
        const countries = list.map(item => {
            const name = this.translateService.instant('common.nationality.' + item.countryCode);
            return { id: item.countryCode, value: name };
        });

        return countries.sort(this.orderByProperty('value'));
    }

    private parseContacts(data): Contacts {
        const contacts = new Contacts();

        data.forEach(item => {
            const contact = new Contact(item.firstName + ' ' + item.familyName, item.email, item.residenceCountry);
            if (item.role === 'representative') {
                contacts.representative = contact;
            } else if (item.role === 'substitute') {
                contacts.substitute = contact;
            } else {
                contacts.entity = contact;
            }
        });

        return contacts;
    }

    /**
     * Sort function for compare optionSelect object
     */
    private compareDisplayOrderOptions(a, b) {
        let aText = a.displayOrder;
        let bText = b.displayOrder;

        if (aText < bText) {
            return -1;
        }
        if (aText > bText) {
            return 1;
        }

        return 0;
    }

    private orderByProperty(name: string) {
        return (a, b) => {
            const aValue = a[name].toLowerCase();
            let bValue = b[name].toLowerCase();

            if (aValue < bValue) {
                return -1;
            }

            if (aValue > bValue) {
                return 1;
            }

            return 0;
        };
    }

}
