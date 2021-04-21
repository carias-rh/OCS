import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { SettingsComponent } from './settings.component';

import { SettingsCertificateComponent } from './components/certificate/certificate.component';
import { SettingsCustomiseComponent } from './components/customise/customise.component';
import { SettingsContentComponent } from './components/content/content.component';
import { SettingsMakeItSocialComponent } from './components/makeitsocial/makeitsocial.component';

const routes: Routes = [
    { path: '', component: SettingsComponent, children: [
        { path: '', redirectTo: 'content' },
        { path: 'certificate', component: SettingsCertificateComponent },
        { path: 'customise', component: SettingsCustomiseComponent },
        { path: 'content' , component: SettingsContentComponent },
        { path: 'makeitsocial', component: SettingsMakeItSocialComponent }
    ]
    },
];

@NgModule({
    imports: [
        RouterModule.forChild(routes)
    ],
})
export class SettingsRoutingModule {}
