import { NgModule } from '@angular/core';
import { TranslateModule } from '@ngx-translate/core';
import { UxAllModule } from '@eui/core';

import { PdfViewerModule } from 'ng2-pdf-viewer';
import { ClipboardModule } from 'ngx-clipboard';
import { TextMaskModule } from 'angular2-text-mask';

import { SupportComponent } from './support/support.component';
import { TitleComponent } from './title/title.component';
import { Error404Component } from './error/error404.component';
import { Error500Component } from './error/error500.component';
import { PrivacyComponent } from './privacy-statement/privacy.component';
import { NotInitializedComponent } from './error/not-initialized.component';
import { StayInformedComponent } from '../features/submitted/components/stay-informed/stay-informed.component';
import { SignatureAlertComponent } from './signature-alert/signature-alert.component';
import { LoginComponent } from './login/login.component';
import { ConformityComponent } from './conformity/conformity.component';

@NgModule({
    imports: [
        UxAllModule,
        TranslateModule,
        ClipboardModule,
        PdfViewerModule,
        TextMaskModule
    ],
    declarations: [
        SupportComponent,
        Error404Component,
        Error500Component,
        NotInitializedComponent,
        TitleComponent,
        PrivacyComponent,
        StayInformedComponent,
        SignatureAlertComponent,
        LoginComponent,
        ConformityComponent
    ],
    exports: [
        UxAllModule,
        TranslateModule,
        ClipboardModule,
        SupportComponent,
        TitleComponent,
        StayInformedComponent,
        SignatureAlertComponent,
        TextMaskModule
    ]
})
export class SharedModule {}
