import { NgModule } from '@angular/core';
import { DashboardRoutingModule } from './dashboard-routing.module';
import { DashboardComponent } from './dashboard.component';

import { SharedModule } from '../../shared/shared.module';

import { DashboardTop7Component } from './component/top7/top7.component';
import { DashboardTotalComponent } from './component/total/total.component';
import { DashboardEvolutionTotalComponent } from './component/evolution-total/evolution-total.component';
import { DashboardEvolutionCountryComponent } from './component/evolution-country/evolution-country.component';
import { CircleChartComponent } from './component/chart/circle/circle-chart.component';
import { EvolutionChartComponent } from './component/chart/evolution/evolution-chart.component';

@NgModule({
    imports: [
        SharedModule,
        DashboardRoutingModule,
    ],
    declarations: [
        DashboardComponent,
        DashboardTop7Component,
        DashboardTotalComponent,
        DashboardEvolutionTotalComponent,
        DashboardEvolutionCountryComponent,
        CircleChartComponent,
        EvolutionChartComponent,
    ],
    exports: [
    ]
})
export class Module {
}
