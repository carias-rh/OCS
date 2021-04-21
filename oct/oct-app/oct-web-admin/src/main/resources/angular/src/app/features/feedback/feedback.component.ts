import { Component, OnInit, ViewChild, ViewEncapsulation, Input, OnDestroy } from '@angular/core';
import { ApiService } from '../../core/services/api.service';
import { TranslateService } from '@ngx-translate/core';
import { Subscription } from 'rxjs/Subscription';

@Component({
    templateUrl: 'feedback.component.html',
    styleUrls: ['feedback.component.scss'],
    encapsulation: ViewEncapsulation.None,
})

export class FeedbackComponent implements OnInit, OnDestroy {
    @Input() data: any[];
    @ViewChild('pchart') pchart;

    feedbackStats = { goodCount: 0, fineCount: 0, fairCount: 0, badCount: 0, totCount: 0 };
    datacircle;
    options;
    allFeedbacks = [];
    feedbacksLoading = false;
    hasFeedbacks = false;
    defaultStart = 0;
    defaultOffset = 5;

    loading: boolean;
    languageSubscription: Subscription;

    constructor(private api: ApiService, private translate: TranslateService) {
        this.datacircle = {
            labels: ['Good', 'Fine', 'Fair', 'Bad'],
            datasets: [
                {
                    data: [0, 0, 0, 0],
                    borderWidth: 2,
                    borderColor: [
                        'rgba(4, 37, 76, 1)',
                        'rgba(52, 101, 53, 1)',
                        'rgba(147, 90, 60, 1)',
                        'rgba(127, 54, 51, 1)',
                    ],
                    backgroundColor: [
                        '#004494',
                        '#4caf50',
                        '#ff9800',
                        '#bd3530'
                    ],
                    hoverBackgroundColor: [
                        '#4388da',
                        '#7fe383',
                        '#e5b977',
                        '#ea7b77'
                    ]
                }
            ]
        };

        this.options = {
            cutoutPercentage: 75,
            tooltips: {
                enabled: true
            },
            legend: {
                position: 'left',
            }
        };
    }

    ngOnInit() {
        this.translateLabels();
        this.getFeedbacksData();
        this.languageSubscription = this.translate.onLangChange.subscribe(() => this.updateChart());
    }

    ngOnDestroy() {
        if (this.languageSubscription) {
            this.languageSubscription.unsubscribe();
        }
    }

    getFeedbacksData() {
        this.api.getFeedbackStats().subscribe(res => {
            this.feedbackStats = res;
            if (this.feedbackStats.totCount > 0) {
                this.datacircle.datasets[0].data = [
                    this.feedbackStats.goodCount,
                    this.feedbackStats.fineCount,
                    this.feedbackStats.fairCount,
                    this.feedbackStats.badCount
                ];
                this.hasFeedbacks = true;
            }
        });

    }

    getAllFeedbacks(start, offset) {
        this.feedbacksLoading = true;
        this.api.getAllFeedbacks(start, offset).subscribe(res => {
            this.allFeedbacks = res;
            this.feedbacksLoading = false;
        });
    }

    loadFeedbacks(event) {
        Promise.resolve(null).then(() => this.getAllFeedbacks(event.first, event.rows));
    }

    percentage(num) {
        let percent = 0;

        if ( this.feedbackStats.totCount > 0) {
            percent = (num / this.feedbackStats.totCount);
        }
        return percent;
    }

    private updateChart() {
        this.translateLabels();
        this.pchart.refresh();
    }

    private translateLabels() {
        this.datacircle.labels = [
            this.translate.instant('dashboard.feedback.experience.good'),
            this.translate.instant('dashboard.feedback.experience.fine'),
            this.translate.instant('dashboard.feedback.experience.fair'),
            this.translate.instant('dashboard.feedback.experience.bad')
        ];
    }
}
