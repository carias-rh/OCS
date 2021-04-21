import { NgModule } from '@angular/core';
import { DeleteRoutingModule } from './delete-routing.module';
import { DeleteComponent } from './delete.component';

import { SharedModule } from '../../shared/shared.module';

@NgModule({
    imports: [
        SharedModule,
        DeleteRoutingModule,
    ],
    declarations: [
        DeleteComponent,
    ],
})
export class Module {
}
