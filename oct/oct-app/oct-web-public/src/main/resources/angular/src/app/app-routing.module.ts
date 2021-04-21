import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { Error500Component } from './shared/error/error500.component';
import { Error404Component } from './shared/error/error404.component';
import { PrivacyComponent } from './shared/privacy-statement/privacy.component';
import { NotInitializedComponent } from './shared/error/not-initialized.component';
import { LoginComponent } from './shared/login/login.component';
import { ConformityComponent } from './shared/conformity/conformity.component';

const routes: Routes = [
    { path: '', redirectTo: 'screen/home', pathMatch: 'full' },
    { path: 'index.jsp', redirectTo: 'screen/home' },
    { path: 'screen/home', loadChildren: './features/home/home.module#HomeModule' },
    { path: 'screen/submitted', loadChildren: './features/submitted/submitted.module#SubmittedModule' },
    { path: 'screen/privacy', component: PrivacyComponent },
    { path: 'screen/conformity', component: ConformityComponent },
    { path: 'login', component: LoginComponent },
    { path: 'error', component: Error500Component },
    { path: 'not-initialized', component: NotInitializedComponent },
    { path: '**', component: Error404Component }
];

@NgModule({
    imports: [
        RouterModule.forRoot(routes, {
            // Tell the router to use the HashLocationStrategy.
            useHash: true,
            enableTracing: false
        })
    ],
    exports: [RouterModule],
})
export class AppRoutingModule {}
