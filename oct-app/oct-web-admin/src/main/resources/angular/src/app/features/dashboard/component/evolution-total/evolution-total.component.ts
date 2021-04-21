import { Component, Input } from '@angular/core';

@Component({
    selector: 'ocs-evolution-total',
    templateUrl: 'evolution-total.component.html'
})

export class DashboardEvolutionTotalComponent {

    @Input() data;
    showChart = true;

    constructor() {}

    onButtonClicked(event) {
        this.showChart = event.id === 'chart';
    }

}
