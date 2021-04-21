import { Component, Input, OnInit } from '@angular/core';
import { ProgressionStatus } from '../../../../core/models/progressionStatus';

@Component({
    selector: 'ocs-total',
    templateUrl: 'total.component.html'
})

export class DashboardTotalComponent implements OnInit {
    @Input() data: ProgressionStatus;

    constructor() { }

    ngOnInit() {
    }
}
