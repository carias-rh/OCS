import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { HomeGuardService } from './home-guard.service';

import { HomeComponent } from './components/home.component';
import { DisabledComponent } from './components/disabled.component';
import { AlreadyComponent } from './components/already.component';
import { AllcountriesComponent } from './components/allcountries/allcountries.component';
import { InfoComponent } from './components/info.component';

const routes: Routes = [
    {
        path: '',
        component: HomeComponent,
        canActivate: [HomeGuardService],
    },
    {
        path: 'disabled',
        component: DisabledComponent,
        canActivate: [HomeGuardService],
    },
    {
        path: 'already',
        component: AlreadyComponent,
        canActivate: [HomeGuardService],
    },
    {
        path: 'info',
        component: InfoComponent
    },
    {
        path: 'allcountries',
        component: AllcountriesComponent,
        canActivate: [HomeGuardService],
    },
];

@NgModule({
    imports: [RouterModule.forChild(routes)],
    exports: [RouterModule],
})
export class HomeRoutingModule {}
