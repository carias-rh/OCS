import { Component, OnInit } from '@angular/core';

import { Observable } from 'rxjs/Observable';
import { UxAppShellService } from '@eui/core';

import { InitiativeService } from '../../../../core/services/initiative.service';
import { LanguageService } from '../../../../core/services/language.service';
import { DescriptionXmlService } from './description-xml.service';
import { Upload } from '../../../../shared/upload/upload';

import { Initiative } from '../../../../core/models/initiative';
import { Description } from '../../../../core/models/description';
import { ContactList } from '../../../../core/models/contact';
import { Language } from '../../../../core/models/language';

@Component({
    selector: 'ocs-settings-content',
    templateUrl: 'content.component.html',
    styleUrls: ['content.component.scss']
})

export class SettingsContentComponent implements OnInit {

    initiative: Initiative;
    description: Description;
    descriptions: Description[];
    languages: Language[];
    contacts: ContactList;
    isBlocked = false;
    alert: {type: string; message: string};
    file: File;
    confirmUpload = false;

    constructor(
        private service: InitiativeService,
        private languageService: LanguageService,
        private descriptionXmlService: DescriptionXmlService,
        private uxAppShellService: UxAppShellService
    ) {}

    ngOnInit() {
        this.loadDataFromApi();
    }

    onUpload(uploads: Upload[]) {
        this.file = uploads[0].file;
        this.alert = null;
        this.confirmUpload = false;

        if (this.file.type === 'text/xml') {
            this.uxAppShellService.isBlockDocumentActive = true;
            let reader = new FileReader();
            reader.addEventListener('loadend', (event: any) => {
                const xml = event.target.result.trim();
                this.loadDescriptionXml(xml);
                this.uxAppShellService.isBlockDocumentActive = false;
            });
            reader.readAsText(this.file);
        } else {
            this.setAlert('danger', 'structure.err_extension');
        }
    }

    onSelectLanguage(language) {
        this.selectLanguage(language.code);
    }

    onCancel() {
        this.alert = null;
        this.file = null;
        this.loadDataFromApi();
    }

    onConfirm() {
        if (this.file) {
            const insert$ = this.service.insertXML(this.file);
            const section$ = this.service.activeSection('structure');

            Observable.concat(insert$, section$).subscribe(
                response => {
                    this.confirmUpload = false;
                    this.setAlert('success', 'structure.save_ok', true);
                    this.service.clearInitiative();
                },
                error => {
                    let errorCode = error.status;
                    let errorMessage = 'structure.save_err';
                    if (errorCode === 409) {
                        errorMessage = 'structure.err_extension';
                    }
                    this.setAlert('danger', errorMessage, true);
                }
            );
        }
    }

    isInitialized() {
        return this.initiative && this.initiative.available;
    }

    private loadDataFromApi() {
        this.isBlocked = true;
        this.confirmUpload = false;

        const initiative$ = this.service.getInitiative();
        const descriptions$ = this.service.getAllInitiativeDescriptions();
        const contacts$ = this.service.getInitiativeContacts();

        Observable.zip(initiative$, descriptions$, contacts$).subscribe(data => {
            this.initiative = data[0];
            this.description = this.initiative.description;

            this.descriptions = data[1] || [];

            const codeList = this.descriptions.map(item => item.languageCode);
            this.languages = this.languageService.createLanguageList(codeList);

            this.contacts = data[2];

            if (!this.isInitialized()) {
                this.setAlert('warning', 'structure.initiative-notfound', false);
            }

            this.isBlocked = false;
        });
    }

    private loadDescriptionXml(xml: string) {
        if (xml) {
            let successfulUpload = this.descriptionXmlService.parseXml(xml);
            if (successfulUpload) {
                this.initiative = this.descriptionXmlService.getInitiative();
                this.initiative.available = true;
                this.descriptions = this.descriptionXmlService.getDescriptions();
                this.contacts = this.descriptionXmlService.getContacts();
                this.description = this.initiative.description;

                const codeList = this.descriptions.map(item => item.languageCode);
                this.languages = this.languageService.createLanguageList(codeList);

                this.setAlert('warning', 'structure.confirm-title', false);
                this.confirmUpload = true;
            } else {
                this.setAlert('danger', 'structure.save_err', true);
            }
        }

        return false;
    }

    private selectLanguage(code: string) {
        this.description = this.descriptions.find(item => item.languageCode === code);
    }

    private resetUpload() {
        this.file = null;
    }

    private setAlert(type: string, label: string, reset: boolean = true) {
        this.alert = {
            type: type,
            message: label
        };
        window.scrollTo(0, 0);
        if (reset) {
            this.resetUpload();
        }
    }

}
