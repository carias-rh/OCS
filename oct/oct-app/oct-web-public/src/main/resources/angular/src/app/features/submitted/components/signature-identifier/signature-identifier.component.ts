import { Component, OnInit } from '@angular/core';
import { UxAppShellService } from '@eui/core';

import { ApplicationService } from '../../../../core/services/application.service';
import { InitiativeService } from '../../../../core/services/initiative.service';

@Component({
    selector: 'app-signature-identifier',
    templateUrl : './signature-identifier.component.html',
})
export class SignatureIdentifierComponent implements OnInit {

    public today = Date.now();
    isMobile = false;

    constructor(
        private applicationService: ApplicationService,
        private service: InitiativeService,
        private asService: UxAppShellService
    ) {}

    ngOnInit() {
        this.asService.breakpoint$.subscribe(bkp => this.onBreakpointChange(bkp));
    }

    get signatureIdentifier() {
        return this.applicationService.signatureIdentifier;
    }

    get urlDownloadSignature() {
        return this.service.getUrlDownloadSignature(this.signatureIdentifier);
    }

    public OnDownloadSignature(): void {
        document.location.href = this.urlDownloadSignature;
    }

    onBreakpointChange(bkp: string) {
        if ( bkp === 'sm' || bkp === 'xs') {
            this.isMobile = true;
        } else {
            this.isMobile = false;
        }
    }

}
