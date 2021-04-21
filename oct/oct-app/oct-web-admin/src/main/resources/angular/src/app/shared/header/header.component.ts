import { Component, OnInit, Input, Output, EventEmitter, OnDestroy } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';
import { Router } from '@angular/router';
import { UxService, UxAppShellService } from '@eui/core';
import { Subscription } from 'rxjs/Subscription';

import { InitiativeService } from '../../core/services/initiative.service';
import { Initiative } from '../../core/models/initiative';
import { ApplicationService } from '../../core/services/application.service';
import { ApiService } from '../../core/services/api.service';

@Component({
    selector : 'app-header',
    templateUrl: './header.component.html',
    styleUrls: ['header.component.scss'],
})
export class HeaderComponent implements OnInit, OnDestroy {
    initiative: Initiative;
    error = false;
    structureCompleted = false;
    certificateCompleted = false;
    stateSubscription: Subscription;

    constructor(private translate: TranslateService,
        public uxservice: UxService,
        private apiService: ApiService,
        private service: InitiativeService,
        private applicationService: ApplicationService,
        private router: Router,
        private uxAppShellService: UxAppShellService
    ) {}

    get systemState() {
        return this.applicationService.systemState;
    }

    get systemLabel() {
        return 'header.mode-' + this.systemState;
    }

    get collectionMode() {
        return this.applicationService.collectingMode;
    }

    get isCollectionMode() {
        return this.collectionMode.toUpperCase() === 'ON';
    }

    get collectionLabel() {
        return this.isCollectionMode ? 'header.collection-ON' : 'header.collection-OFF';
    }

    get collectionClass() {
        return this.isCollectionMode ? 'collection-on' : 'collection-off';
    }

    get isGoOnlineEnabled() {
        return this.systemState === 'OFFLINE' && this.structureCompleted && this.certificateCompleted;
    }

    ngOnInit() {
        this.service.getInitiative().subscribe(data => this.initiative = data);
        this.loadSteps();
        this.stateSubscription = this.service.stateChange$.subscribe(() => {
            this.loadSteps();
        });
    }

    ngOnDestroy() {
        if (this.stateSubscription) {
            this.stateSubscription.unsubscribe();
        }
    }

    loadSteps() {
        this.service.getSteps().subscribe(data => {
            this.structureCompleted = data.structure;
            this.certificateCompleted = data.certificate;
        });
    }

    switchToggled() {
        const collectionMode = this.isCollectionMode;

        this.uxAppShellService.isBlockDocumentActive = true;
        this.apiService.setCollectingState(!collectionMode).subscribe(
            data => {
                this.applicationService.collectingMode = data.collectionMode;
                this.uxAppShellService.isBlockDocumentActive = false;
            },
            () => {
                this.uxAppShellService.isBlockDocumentActive = false;
            }
        );
    }

    openModal() {
        this.uxservice.openModal('goonline');
    }

    onCloseModal() {
        this.uxservice.closeModal('goonline');
    }

    onGoOnline() {
        this.error = false;

        this.service.goOnline().subscribe(
            (response: any) => {
                this.applicationService.refreshSystemState();
                this.router.navigateByUrl('/screen/dashboard');
                this.uxservice.closeModal('goonline');
            },
            (error: any) => {
                this.error = true;
                this.uxservice.closeModal('goonline');
            }
        );
    }

}
