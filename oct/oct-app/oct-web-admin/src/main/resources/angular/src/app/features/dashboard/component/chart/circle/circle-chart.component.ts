import { Component, Input, OnChanges, OnInit, SimpleChanges, OnDestroy } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';
import { Subscription } from 'rxjs/Subscription';

@Component({
    selector: 'ocs-circle-chart',
    templateUrl: 'circle-chart.component.html',
})

export class CircleChartComponent implements OnInit, OnChanges, OnDestroy {
    @Input() count: number = 0;
    @Input() threshold: number = 0;
    @Input() label = '';
    @Input() labelValue = 'dashboard.view.circle-label';

    percentage = 0;
    initialized = false;
    translateSubscription: Subscription;

    constructor(private translateService: TranslateService) {}

    ngOnInit() {
        this.setPercentage();

        this.translateSubscription = this.translateService.onLangChange.subscribe(() => {
            this.initialized = false;
            setTimeout(() => this.setPercentage());
        });
    }

    ngOnChanges(changes: SimpleChanges) {
        if (changes['count'] || changes['threshold']) {
            this.setPercentage();
        }
    }

    ngOnDestroy() {
        this.translateSubscription.unsubscribe();
    }

    private setPercentage() {
        this.count = this.count || 0;
        this.threshold = this.threshold || 0;
        let percentage = 0;

        if (this.count > 0 && this.threshold > 0) {
            // We use decimal to force display the value label
            percentage = Math.round(this.count / this.threshold * 100);
        }

        this.percentage = percentage;
        this.initialized = true;
    }

}
