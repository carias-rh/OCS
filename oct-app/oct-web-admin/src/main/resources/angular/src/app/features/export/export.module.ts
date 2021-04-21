import { NgModule } from '@angular/core';
import { ExportRoutingModule } from './export-routing.module';
import { ExportComponent } from './export.component';

import { SharedModule } from '../../shared/shared.module';
import { CountriesExportComponent } from './countries/countries.component';
import { HistoryExportComponent } from './history/history.component';
import { PaginatorModule } from 'primeng/paginator';
import { TableModule } from 'primeng/table';

@NgModule({
    imports: [
        SharedModule,
        ExportRoutingModule,
        PaginatorModule,
        TableModule

    ],
    declarations: [
        ExportComponent,
        CountriesExportComponent,
        HistoryExportComponent
    ],
})
export class Module {
}
