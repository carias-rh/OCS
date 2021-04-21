import { Component, OnInit } from '@angular/core';

import { InitiativeService } from '../../../../core/services/initiative.service';

@Component({
    selector: 'app-recent',
    templateUrl: 'recent.component.html',
    styleUrls: ['recent.component.scss']
})

export class RecentComponent implements OnInit {
    supporters: any[];

    constructor(private initiativeService: InitiativeService) {}

    ngOnInit() {
        this.initiativeService.getRecentSignatures()
            .map(data => this.parseLatestSignatures(data))
            .subscribe(data => this.supporters = data);
    }

    private parseLatestSignatures(data) {
        const items = data.map(item => {
            item.label = 'common.country.' + item.country;
            return item;
        });

        return items;
    }

}
