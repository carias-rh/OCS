import { NgModule } from '@angular/core';
import { SettingsRoutingModule } from './settings-routing.module';
import { SettingsComponent } from './settings.component';

import { SharedModule } from '../../shared/shared.module';
import { SettingsCertificateComponent } from './components/certificate/certificate.component';
import { SettingsCustomiseComponent } from './components/customise/customise.component';
import { SettingsContentComponent } from './components/content/content.component';
import { SettingsMakeItSocialComponent } from './components/makeitsocial/makeitsocial.component';
import { DescriptionXmlService } from './components/content/description-xml.service';

@NgModule({
    imports: [
        SharedModule,
        SettingsRoutingModule,
    ],
    declarations: [
        SettingsComponent,
        SettingsCertificateComponent,
        SettingsCustomiseComponent,
        SettingsContentComponent,
        SettingsMakeItSocialComponent
    ],
    providers: [
        DescriptionXmlService
    ]
})
export class Module {
}
