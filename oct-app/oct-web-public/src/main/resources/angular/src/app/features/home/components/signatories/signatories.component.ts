import { Component, Input, OnInit } from '@angular/core';

import { InitiativeService } from '../../../../core/services/initiative.service';

import { Initiative } from '../../../../core/models/initiative.model';
import { Progression } from '../../../../core/models/progression.model';
import { Customisations } from '../../../../core/models/customisations.model';

@Component({
    selector: 'app-signatories',
    templateUrl: 'signatories.component.html',
    styleUrls: ['signatories.component.scss']
})

export class SignatoriesComponent implements OnInit {

    @Input() isProgress: boolean;
    initiative: Initiative;
    progress: Progression;
    customisations = new Customisations();

    constructor(private service: InitiativeService) {}

    get showFacebook() {
        return this.customisations.showFacebook;
    }

    get showTwitter() {
        return this.customisations.showTwitter;
    }

    ngOnInit() {
        if (this.isProgress) {
            this.service.getProgessStatus().subscribe(data => this.progress = data);
        }
        this.service.getInitiative().subscribe(data => this.initiative = data);
        this.service.getCustomisations().subscribe(data => this.customisations = new Customisations(data));
    }

}
