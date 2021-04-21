import { Component, OnInit, ViewChild } from '@angular/core';

import { FormGroup } from '@angular/forms';
import { UxAppShellService } from '@eui/core';
import { InitiativeService } from '../../../../core/services/initiative.service';
import { Upload } from '../../../../shared/upload/upload';
import { UploadComponent } from '../../../../shared/upload/upload.component';
import { Observable } from 'rxjs/Rx';

@Component({
    selector: 'ocs-settings-certicate',
    templateUrl: 'certificate.component.html'
})

export class SettingsCertificateComponent implements OnInit {
    @ViewChild('upload') upload: UploadComponent;

    form: FormGroup;
    hasCertificate = false;
    alert: {type: string; label: string};
    file: File;

    urlCurrentCertificate: string;

    constructor(
        private uxAppShellService: UxAppShellService,
        private service: InitiativeService
    ) {}

    ngOnInit() {
        this.urlCurrentCertificate = this.service.getUrlCertificate();
        this.initSteps();
    }

    initSteps() {
        this.service.getSteps().subscribe(data => {
            this.hasCertificate = data.certificate;
        });
    }

    onAddedFile(uploads: Upload[]) {
        if (uploads.length) {
            this.file = uploads[0].file;
            this.onSave();
        } else {
            this.file = null;
        }
    }

    onCancel() {
        this.resetUpload();
    }

    onSave() {
        this.alert = null;
        this.uxAppShellService.isBlockDocumentActive = true;
        if (this.file) {
            let store$ = this.service.storeCertificate(this.file);
            let section$ = this.service.activeSection('certificate');

            Observable.concat(store$, section$).subscribe(
                () => {
                    this.setAlert('success', 'certificate.save_ok');
                    this.resetUpload();
                    this.uxAppShellService.isBlockDocumentActive = false;
                    this.initSteps();
                },
                () => {
                    this.setAlert('danger', 'certificate.save_err');
                    this.resetUpload();
                    this.uxAppShellService.isBlockDocumentActive = false;
                }
            );
        }
    }

    resetUpload() {
        this.file = null;
        this.upload.reset();
    }

    private setAlert(type: string, label: string) {
        this.alert = {
            type: type,
            label: label
        };
    }
}
