import { Injectable, Inject } from '@angular/core';
import { Subject } from 'rxjs/Subject';
import { CONFIG_TOKEN } from '@eui/core';

import { ApiService } from './api.service';
import { Observable } from 'rxjs/Observable';
import { LanguageService } from './language.service';
import { parse as parseUrl } from 'url';
import { parse as parseQueryString, ParsedUrlQuery } from 'querystring';

@Injectable()
export class ApplicationService {
    public systemState: string;
    public collectingMode: string;
    public systemLanguages: any[];
    public systemState$: Subject<string>;

    constructor(
        @Inject(CONFIG_TOKEN) private config: any,
        private api: ApiService,
        private languageService: LanguageService
    ) {
        this.systemState$ = new Subject();
    }

    public init(): Promise<boolean> {
        const source1$ = this.api.getSystemState().map(data => data.mode);
        const source2$ = this.api.getCollectingState().map(data => data.collectionMode);
        const source3$ = this.api.getAllLanguages().map(data => this.parseSystemLanguages(data));

        return Observable.zip(source1$, source2$, source3$)
            .do(data => this.initialize(data[0], data[1], data[2]))
            .mapTo(true)
            .toPromise();
    }

    public refreshSystemState() {
        const systemState = this.systemState;

        this.api.getSystemState().map(data => data.mode).subscribe(mode => {
            if (mode !== systemState) {
                this.systemState = mode;
                this.systemState$.next(this.systemState);
            }
        });
    }

    private initialize(systemState, collectionMode, systemLanguages) {
        this.systemState = systemState;
        this.collectingMode = collectionMode;
        this.systemLanguages = systemLanguages;

        this.languageService.loadNavigationLanguages(systemLanguages, this.getQueryLanguage());
    }

    private parseSystemLanguages(data) {
        const languages = [];

        data.forEach(item => {
            languages.push({
                id: item.languageCode,
                text: this.config.global.languageList[item.languageCode],
                displayOrder: item.displayOrder
            });
        });

        return languages
            .sort(this.compareLanguageOptions)
            .map(item => {
                return { id: item.id, value: item.text };
            });
    }

    /**
     * Sort function for compare optionSelect object
     */
    private compareLanguageOptions(a, b) {
        let aOrder = a.displayOrder;
        let bOrder = b.displayOrder;

        if (aOrder < bOrder) {
            return -1;
        }
        if (aOrder > bOrder) {
            return 1;
        }
        return 0;
    }

    private getQueryLanguage(): string {
        let language: string;

        try {
            const url = window.location.href.replace('/#/', '/');
            const obj = parseUrl(url);
            const queryString: ParsedUrlQuery = parseQueryString(<string>obj.query);
            if (queryString.hasOwnProperty('lg')) {
                language = <string>queryString['lg'];
            }
        } catch (e) {
            language = null;
        }

        return language;
    }

}
