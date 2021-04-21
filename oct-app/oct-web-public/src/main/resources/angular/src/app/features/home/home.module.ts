import { NgModule } from '@angular/core';

import { SharedModule } from '../../shared/shared.module';

import { HomeRoutingModule } from './home-routing.module';

import { SupportFormService } from './components/form/support-form.service';
import { CaptchaService } from '../../core/services/captcha.service';

import { HomeComponent } from './components/home.component';
import { SignatoriesComponent } from './components/signatories/signatories.component';
import { RecentComponent } from './components/recent/recent.component';
import { FormComponent } from './components/form/form.component';
import { CarrouselComponent } from './components/carrousel/carrousel.component';
import { InitiativeComponent } from './components/initiative/initiative.component';
import { DisabledComponent } from './components/disabled.component';
import { AlreadyComponent } from './components/already.component';
import { AllcountriesComponent } from './components/allcountries/allcountries.component';
import { DocumentPropertyComponent } from './components/form/property/document-property.component';
import { PropertyComponent } from './components/form/property/property.component';
import { CaptchaPropertyComponent } from './components/form/property/captcha-property.component';
import { SupportCountryComponent } from './components/form/support-country/support-country.component';
import { InfoComponent } from './components/info.component';

@NgModule({
    imports: [
        SharedModule,
        HomeRoutingModule,
    ],
    declarations: [
        HomeComponent,
        SignatoriesComponent,
        RecentComponent,
        SupportCountryComponent,
        FormComponent,
        CarrouselComponent,
        InitiativeComponent,
        DisabledComponent,
        AlreadyComponent,
        InfoComponent,
        AllcountriesComponent,
        DocumentPropertyComponent,
        PropertyComponent,
        CaptchaPropertyComponent,

    ],
    providers: [
        SupportFormService,
        CaptchaService
    ],
})
export class HomeModule {
}
