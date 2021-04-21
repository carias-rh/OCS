import { Component, Input, OnDestroy, OnInit, ViewChild, OnChanges } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';
import { Subscription } from 'rxjs/Subscription';

@Component({
    selector: 'ocs-evolution-chart',
    templateUrl: 'evolution-chart.component.html'
})

export class EvolutionChartComponent implements OnInit, OnChanges, OnDestroy {

    @ViewChild('pChart') pChart;
    @Input() data;
    options: any;
    dataChart: any;
    langSubscription: Subscription;

    constructor(private translate: TranslateService) {
        this.setOptions();
    }

    ngOnInit() {
        this.setDataChart();
        this.langSubscription = this.translate.onLangChange.subscribe(() => this.updateTranslations());
    }

    ngOnChanges() {
        if (this.dataChart) {
            this.updateDataChart();
            this.pChart.reinit();
        }
    }

    ngOnDestroy() {
        if (this.langSubscription) {
            this.langSubscription.unsubscribe();
        }
    }

    private setDataChart() {
        const chartLabels = this.getLabels();
        const chartData = this.data.map(item => item.count);

        this.dataChart = {
            labels: chartLabels,
            datasets: [
                {
                    label: ' ' + this.translate.instant('dashboard.view.statements'),
                    backgroundColor: '#076ABB',
                    borderColor: '#1E88E5',
                    data: chartData
                },
            ]
        };
    }

    private updateDataChart() {
        const chartData = this.data.map(item => item.count);
        this.dataChart.datasets[0].data = chartData;
    }

    private updateTranslations() {
        this.dataChart.datasets[0].label = ' ' + this.translate.instant('dashboard.view.statements');
    }

    private setOptions() {
        this.options = {
            barPercentage: 0.5,
            gridlines: {
                display: false
            },
            scales: {
                xAxes: [{
                    barPercentage: 0.4,       // Default 0.9
                    categoryPercentage: 0.8,  // Default 0.8
                    gridLines: {
                        display: false
                    }
                }],
                yAxes: [{
                    gridLines: {
                        display: false
                    },
                    ticks: {
                        beginAtZero: true
                    }
                }]
            },
            legend: {
                display: false
            }
        };
    }

    private getLabels() {
        const labels = [];

        let year = null;
        this.data.forEach(item => {
            let label: string;
            const month = ('00' + item.month).substr(-2, 2);
            if (item.year === year) {
                label = month;
            } else {
                label = month + '-' + item.year;
                year = item.year;
            }
            labels.push(label);
        });

        return labels;
    }

}
