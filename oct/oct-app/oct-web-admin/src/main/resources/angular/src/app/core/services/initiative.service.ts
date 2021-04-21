import { Injectable } from '@angular/core';
import { Router } from '@angular/router';

import { TranslateService } from '@ngx-translate/core';
import { Observable } from 'rxjs/Observable';
import { Subject } from 'rxjs/Subject';

import { ApiService } from './api.service';

import { Initiative } from '../models/initiative';
import { Description } from '../models/description';
import { Contact, ContactList } from '../models/contact';
import { Customisations } from '../models/customisations';
import { CountryMap } from '../models/countryMap';

@Injectable()
export class InitiativeService {

    public isInitiative: boolean;
    public config: any;
    public initiative: Initiative;
    public defaultLanguage: string = 'en';
    public optionsBoolean: any;
    public cacheLang: any = {};
    public cacheCountries: any[];
    public registrationDate: string;
    public stateChange$ = new Subject();

    constructor(
        private api: ApiService,
        private translate: TranslateService,
        private router: Router
    ) {
        this.config = {};
        this.init();
    }

    public init() {}

    public getSystemLanguages() {
        return this.api.getInitiativeLanguages();
    }

    public getInitiative(): Observable<Initiative> {
        if (this.initiative) {
            return Observable.of(this.initiative);
        }

        return this.api.getDefaultInititative()
            .map(data => this.parseDefaultInitiative(data))
            .do(initiative => this.initiative = initiative);
    }

    public getInitiativeByLanguage(language): Observable<Initiative> {
        return this.api.getDescriptionByLanguage(language)
            .map(data => this.parseDefaultInitiative(data))
            .do(initiative => this.initiative = initiative);
    }

    public clearInitiative() {
        this.initiative = null;
        return this.getInitiative();
    }

    public getRegistrationDate() {
        return this.initiative.registrationDate;
    }

    /**
     * Sort function for compare optionSelect object
     */
    public compareOptions(a, b) {
        let aText = a.text.toLowerCase();
        let bText = b.text.toLowerCase();

        if (aText < bText) {
            return -1;
        }
        if (aText > bText) {
            return 1;
        }

        return 0;
    }

    public getInitiativeContacts() {
        return this.api.getInitiativeContacts()
            .map(data => this.parseContacts(data));
    }

    public getAllInitiativeDescriptions(): Observable<Description[]> {
        return this.api.getLanguageDescriptions()
            .map(res => res.descriptions);
    }

    public getCustomisations() {
        return this.api.getCustomizations();
    }

    public saveCustomisations(custom: Customisations) {
        return this.api.updateCustomisations(custom);
    }

    public insertXML(file: any) {
        return this.api.insertXML(file);
    }

    public getUrlCertificate() {
        return this.api.getDownloadCustomFile('certificate');
    }

    public getUrlLogo() {
        return this.api.getDownloadCustomFile('logo');
    }

    public getDownloadEncodedLogo() {
        return this.api.getDownloadEncodedLogo();
    }

    public storeLogo(file: any) {
        if (!file) {
            return Observable.of(null);
        }

        return this.api.storeCustomFile(file, 'logo');
    }

    public storeCertificate(file: any) {
        return this.api.storeCustomFile(file, 'certificate');
    }

    public getSteps() {
        return this.api.getInitializationSteps();
    }

    public activeSection(section: string) {
        let state: string;

        if (section === 'personalize') {
            state = 'PERSONALISE';
        } else {
            state = section.toUpperCase();
        }
        // this.sections.saved = section;

        return this.api.setInitializationStepState(state, true)
            .do(response => {
                if (response.code === 200 && response.status === 'SUCCESS') {
                    this.stateChange$.next(true);
                }
            });
    }

    public getCallForActionMessage(language: string) {
        return this.api.getSocialMediaMessage('callForAction', language);
    }

    public getTwitterMessage(language: string) {
        return this.api.getSocialMediaMessage('twitter', language);
    }

    public saveTwitterMessage(language: string, message: string) {
        return this.api.updateSocialMediaMessage('twitter', language, message);
    }

    public saveCallForActionMessage(language: string, message: string) {
        return this.api.updateSocialMediaMessage('callForAction', language, message);
    }

    public getProgressionStatus() {
        return this.api.getProgessStatus();
    }

    public getTop7DistributionMap(): Observable<CountryMap[]> {
        return this.api.getTop7DistributionMap()
            .map(list => list.map(data => new CountryMap(data.countryCode, data.count, data.treshold)));
    }

    public getEvolutionMap() {
        return this.api.getEvolutionMap();
    }

    public getCountries() {
        if (this.cacheCountries) {
            return Observable.of(this.cacheCountries)
                .map(countries => this.translateCountries(countries));
        }

        return this.getAllCountries()
            .do(countries => this.cacheCountries = countries)
            .map(countries => this.translateCountries(countries));
    }

    public getAllCountries() {
        return this.api.getAllCountries()
            .map(data => data.map(item => item.countryCode));
    }

    /**
     * Get count for countries
     */
    public getCountExportByContries(countryList: string[], startDate = null, endDate = null) {
        return this.api.exportCountByCountry(countryList, startDate, endDate);
    }

    public goOnline() {
        return this.api.goOnline();
    }

    public getDaysLeft() {
        return this.api.getDaysLeft()
            .map(data => data.value);
    }

    public deleteSignature(index: number, id: string) {
        return this.api.deleteSignature(id)
            .map(res => {
                return { index: index, id: id, error: false };
            })
            .catch(error => Observable.of({ index: index, id: id, error: true, status: error.status }));
    }

    public error500(text) {
        this.router.navigate(['/error']);
    }

    private translateCountries(countries) {
        return countries
            .map(item => {
                return { id: item, text: this.translate.instant('country.' + item) };
            })
            .sort(this.compareOptions);
    }

    private parseDefaultInitiative(data): Initiative {
        this.initiative = new Initiative();

        if (data) {
            this.initiative.available = true;
            this.initiative.registrationNumber = data.initiativeInfo.registrationNumber;
            this.initiative.registrationDate = data.initiativeInfo.registrationDate;
            this.initiative.closingDate = data.initiativeInfo.closingDate;
            this.initiative.webpage = data.initiativeInfo.url;
            this.initiative.description = new Description(
                data.descriptions[0].languageCode,
                data.descriptions[0].title,
                data.descriptions[0].objectives,
                data.descriptions[0].url,
                data.descriptions[0].partialRegistration
            );
        } else {
            this.initiative.available = false;
        }

        return this.initiative;
    }

    private parseContacts(data): ContactList {
        let representative: Contact = null;
        let substitute: Contact = null;
        let members: Contact[] = [];

        for (let item of data) {
            let contact = new Contact();
            contact.email = item.email;
            contact.name = item.firstName + ' ' + item.familyName;

            if (item.role === 'representative') {
                representative = contact;
            } else if (item.role === 'substitute') {
                substitute = contact;
            } else {
                members.push(contact);
            }
        }

        let contacts = {
            representative: representative,
            substitute: substitute,
            members: members
        };

        return contacts;
    }

}
