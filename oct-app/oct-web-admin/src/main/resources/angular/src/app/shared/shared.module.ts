import { NgModule } from '@angular/core';
import { TranslateModule } from '@ngx-translate/core';
import { FileDropModule } from 'ngx-file-drop';

import { ChartModule } from 'primeng/chart';
import { ClipboardModule } from 'ngx-clipboard';

import { UxAllModule } from '@eui/core';
import { Error404Component } from './error/error404.component';
import { Error500Component } from './error/error500.component';
import { HeaderComponent } from './header/header.component';
import { UploadComponent } from './upload/upload.component';

@NgModule({
    imports: [
        UxAllModule,
        TranslateModule,
        FileDropModule,
        ChartModule,
        ClipboardModule
    ],
    declarations: [
        Error404Component,
        Error500Component,
        HeaderComponent,
        UploadComponent
    ],
    exports: [
        UxAllModule,
        TranslateModule,
        HeaderComponent,
        UploadComponent,
        ChartModule,
        ClipboardModule
    ]
})
export class SharedModule {}
