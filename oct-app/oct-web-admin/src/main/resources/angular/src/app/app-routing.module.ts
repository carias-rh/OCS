import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { LoginComponent } from './core/components/user/login.component';
import { Error404Component } from './shared/error/error404.component';
import { Error500Component } from './shared/error/error500.component';
import { AuthGuardService } from './core/services/auth-guard.service';
import { OnlineGuardService } from './core/services/online-guard.service';

const routes: Routes = [
    { path: '', redirectTo: 'screen/dashboard', pathMatch: 'full' },
    { path: 'index.jsp', redirectTo: 'screen/dashboard' },
    {
        path: 'login',
        component: LoginComponent,
        data: { sidebar: false }
    },
    {
        path: 'screen/dashboard',
        loadChildren: './features/dashboard/dashboard.module#Module',
        canActivate: [AuthGuardService, OnlineGuardService]
    },
    {
        path: 'screen/export',
        loadChildren: './features/export/export.module#Module',
        canActivate: [AuthGuardService, OnlineGuardService]
    },
    {
        path: 'screen/delete',
        loadChildren: './features/delete/delete.module#Module',
        canActivate: [AuthGuardService, OnlineGuardService]
    },
    {
        path: 'screen/feedback',
        loadChildren: './features/feedback/feedback.module#Module',
        canActivate: [AuthGuardService, OnlineGuardService]
    },
    {
        path: 'screen/settings',
        loadChildren: './features/settings/settings.module#Module',
        canActivate: [AuthGuardService]
    },
    { path: 'screen/error', component: Error500Component },
    { path: '**', component: Error404Component },
];

@NgModule({
    imports: [
        RouterModule.forRoot(routes, {
            useHash: true,
        }),
    ],
})
export class AppRoutingModule {}
