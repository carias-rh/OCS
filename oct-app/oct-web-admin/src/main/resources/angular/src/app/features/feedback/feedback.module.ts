import { NgModule } from '@angular/core';
import { FeedbackRoutingModule } from './feedback-routing.module';
import { FeedbackComponent } from './feedback.component';

import { SharedModule } from '../../shared/shared.module';
import { PaginatorModule } from 'primeng/paginator';
import { TableModule } from 'primeng/table';

@NgModule({
    imports: [
        SharedModule,
        FeedbackRoutingModule,
        PaginatorModule,
        TableModule
    ],
    declarations: [
        FeedbackComponent,
    ],
})
export class Module {
}
