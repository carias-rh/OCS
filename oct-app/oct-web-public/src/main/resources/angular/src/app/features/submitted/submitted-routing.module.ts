import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { SubmittedComponent } from './components/submitted.component';
import { SubmittedGuardService } from './submitted-guard.service';

const routes: Routes = [
    {
        path: '',
        component: SubmittedComponent,
        canActivate: [SubmittedGuardService]
    }
];

@NgModule({
    imports: [RouterModule.forChild(routes)],
    exports: [RouterModule],
})
export class SubmittedRoutingModule {}
