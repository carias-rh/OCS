import { Component, OnInit, Renderer } from '@angular/core';
import { UxLanguage, UxEuLanguages, UxAppShellService } from '@eui/core';
import { Router, NavigationEnd } from '@angular/router';

import { InitiativeService } from './core/services/initiative.service';
import { LanguageService } from './core/services/language.service';
import { ApiOcsService } from './core/services/api-ocs.service';
import { ApplicationService } from './core/services/application.service';
import { Subscription } from 'rxjs/Subscription';

declare var require: any;

@Component({
    selector: 'app-root',
    templateUrl: './app.component.html'
})
export class AppComponent implements OnInit {

    themeClass = 'theme';
    version: any;
    languages: string;
    initialized = false;
    showNavbar = false;
    apiVersion: string;
    selectedLanguage: UxLanguage;
    subscriptionBreakpoint: Subscription;
    isHome: boolean;

    constructor(
        private languageService: LanguageService,
        private initiativeService: InitiativeService,
        private renderer: Renderer,
        private api: ApiOcsService,
        private app: ApplicationService,
        private asService: UxAppShellService,
        private router: Router
    ) {
        this.asService.breakpoint$.subscribe(bkp => this.onBreakpointChange(bkp));

        this.languageService.titleText = 'common.page-title';
        this.router.events.subscribe(data => {
            if (data instanceof NavigationEnd) {
                if (data.url === '/screen/home') {
                    this.isHome = true;
                } else {
                    this.isHome = false;
                }
            }
        });
    }

    get navigationLanguage() {
        return this.languageService.selectedLanguage;
    }

    ngOnInit() {
        this.app.init().then(() => {
            this.initiativeService.getSystemLanguages().subscribe(data => {
                this.selectedLanguage = UxEuLanguages.languagesByCode[this.navigationLanguage];
                const arrLang = [];
                data.map(items => {
                    arrLang.push(items.id);
                });
                this.languages = arrLang.join();
            });

            this.loadCustomisations();

            this.api.getLastBuildDate().subscribe(
                (data: {apiVersion: string, buildDate: string, projectVersion: any}) => {
                    this.apiVersion = data.apiVersion;
                }
            );
        });
    }

    skipLink() {
        this.renderer.invokeElementMethod(document.querySelector('#content'), 'focus');
    }

    onLanguageChanged(language: UxLanguage) {
        this.selectedLanguage = language;
        this.languageService.setNavigationLanguage(language.code, true);
    }

    private loadCustomisations() {
        this.initiativeService.getCustomisations().subscribe(
            customisations => {
                this.themeClass = 'theme' + (customisations.background || 1);
                this.initialized = true;
            }
        );
    }

    private onBreakpointChange(bkp: string) {
        if (bkp === 'sm' || bkp === 'xs') {
            this.showNavbar = true;
        } else {
            this.showNavbar = false;
        }
    }

}
