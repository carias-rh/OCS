import { Component, OnInit, Input, ElementRef, ViewChild, AfterViewChecked, OnDestroy, AfterViewInit } from '@angular/core';
import { TranslateService, LangChangeEvent } from '@ngx-translate/core';
import { Subscription } from 'rxjs/Subscription';

import { InitiativeService } from '../../../../core/services/initiative.service';
import { LanguageService } from '../../../../core/services/language.service';

import { Initiative } from '../../../../core/models/initiative.model';
import { Description } from '../../../../core/models/description.model';
import { Contacts } from '../../../../core/models/contact.model';

@Component({
    selector: 'app-initiative',
    templateUrl: 'initiative.component.html'
})

export class InitiativeComponent implements OnInit, AfterViewChecked, OnDestroy, AfterViewInit {

    @ViewChild('descUrl') descriptionUrl: ElementRef;
    @ViewChild('imgLogo') imgLogo: ElementRef;
    @Input() hasTitle = false;
    @Input() signatore = false;

    initiative: Initiative;
    description: Description;
    descriptions: Description[] = [];
    contacts: Contacts;
    subscriptionLanguage: Subscription;
    isShow = false;
    setFocus = false;
    customLogo = false;
    partiallyRegistered: boolean;
    partialRegistration: string;

    constructor(
        private service: InitiativeService,
        private translateService: TranslateService,
        private languageService: LanguageService
    ) {
        this.subscriptionLanguage = this.translateService.onLangChange.subscribe((params: LangChangeEvent) => {
            if (this.existInitiativeLanguage(params.lang)) {
                this.selectLanguage(params.lang);
            } else {
                this.forceENLanguage();
            }
        });
    }

    ngOnInit() {
        this.service.getInitiative().subscribe(data => {
            this.initiative = data;
            this.partiallyRegistered = data.initiativeInfo.partiallyRegistered;
            this.partialRegistration = this.initiative.description.partialRegistration;
        });
        this.service.getContacts().subscribe(data => this.contacts = data);
        this.service.getInitiativeDescriptions().subscribe(data => {
            this.descriptions = data;
            if (this.existInitiativeLanguage(this.languageService.selectedLanguage)) {
                this.selectLanguage(this.languageService.selectedLanguage);
            } else {
                this.forceENLanguage();
            }
        });
        this.service.getCustomisations().subscribe(data => this.customLogo = data.customLogo);
    }

    ngAfterViewInit() {
        this.setDefaultLogo();
    }

    ngOnDestroy() {
        if (this.subscriptionLanguage) {
            this.subscriptionLanguage.unsubscribe();
        }
    }

    ngAfterViewChecked(): void {
        if (this.setFocus) {
            this.descriptionUrl.nativeElement.focus();
            this.setFocus = false;
        }
    }

    toggleMore() {
        this.isShow = !this.isShow;
        if (this.isShow) {
            this.setFocus = true;
        }
    }

    setDefaultLogo() {
        if ( this.customLogo ) {
            this.imgLogo.nativeElement.src = this.service.getUrlLogo();
        } else {
            this.imgLogo.nativeElement.src = 'assets/img/logo.png';
        }
    }

    private selectLanguage(languageCode: string) {
        this.description = this.descriptions.find(item => item.languageCode === languageCode);
        this.service.initiative.description = this.description;
    }

    private existInitiativeLanguage(languageCode: string) {
        if (this.descriptions && this.descriptions.find(item => item.languageCode === languageCode)) {
            return true;
        }

        return false;
    }

    private forceENLanguage() {
        if (this.descriptions.length > 0) {
            this.selectLanguage('en');
        }
    }

}
