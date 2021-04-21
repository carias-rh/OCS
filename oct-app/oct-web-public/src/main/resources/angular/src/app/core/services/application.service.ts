import { Injectable } from '@angular/core';
import { Observable } from 'rxjs/Observable';
import { InitiativeService } from './initiative.service';
import { LanguageService } from './language.service';
import { parse as parseUrl } from 'url';
import { parse as parseQueryString, ParsedUrlQuery } from 'querystring';

@Injectable()
export class ApplicationService {
    alreadySupported = false;
    systemState: any;
    location: string;
    params = {};
    controlParams = ['lg', 'form', 'uid', 'code', 'ref'];
    signatureIdentifier: string = null;
    defaultCountryForm: string;
    errorCode: string;
    errorRef: string;

    constructor(private initiativeService: InitiativeService, private languageService: LanguageService) {
        this.location = window.location.href;
        this.setQueryParams();
    }

    init(): Promise<any> {
        return Observable.zip(
            this.initiativeService.getSystemState(),
            this.initiativeService.getSystemLanguages(),
            this.initiativeService.getSystemCountries(),
            this.initiativeService.getCustomisations(),
            this.initiativeService.getInitiative()
        )
        .do(data => this.initialize(data[0], data[1]))
        .mapTo(true)
        .toPromise();
    }

    public isCollecting() {
        return this.systemState && this.systemState.collecting;
    }

    public isInitialized() {
        return this.systemState && this.systemState.initialized;
    }

    private initialize(systemState, systemLanguages) {
        this.systemState = systemState;
        this.languageService.loadNavigationLanguages(systemLanguages, this.getQueryParam('lg'));
        this.defaultCountryForm = this.getQueryParam('form');
        this.signatureIdentifier = this.getQueryParam('uid');
        this.errorCode = this.getQueryParam('code');
        this.errorRef = this.getQueryParam('ref');
    }

    private setQueryParams() {
        try {
            const url = this.location.replace('/#/', '/');
            const obj = parseUrl(url);
            const queryString: ParsedUrlQuery = parseQueryString(<string>obj.query);

            this.controlParams.forEach(param => {
                if (queryString.hasOwnProperty(param)) {
                    this.params[param] = <string>queryString[param];
                }
            });
        } catch (e) {
            this.params = null;
        }
    }

    private getQueryParam(key: string) {
        if (this.params.hasOwnProperty(key)) {
            return this.params[key];
        } else {
            return null;
        }
    }

}
