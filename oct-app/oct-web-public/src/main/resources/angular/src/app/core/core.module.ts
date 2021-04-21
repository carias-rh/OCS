import { NgModule } from '@angular/core';
import { TranslateModule } from '@ngx-translate/core';
import { StoreModule } from '@ngrx/store';
import { StoreDevtoolsModule } from '@ngrx/store-devtools';
import { CoreModule as UxCoreModule, translateConfig } from '@eui/core';

import './operators';
import { REDUCER_TOKEN, getReducers, metaReducers } from './reducers/index';

import { appConfig } from '../../config/index';
import { environment } from '../../environments/environment';

import { SharedModule } from '../shared/shared.module';
import { ApiOcsService } from './services/api-ocs.service';
import { ApplicationService } from './services/application.service';
import { InitiativeService } from './services/initiative.service';
import { LanguageService } from './services/language.service';
import { HomeGuardService } from '../features/home/home-guard.service';
import { SubmittedGuardService } from '../features/submitted/submitted-guard.service';
import { DocumentValidatorService } from './services/document-validator.service';
import { AuthService } from './services/auth.service';

@NgModule({
    imports: [
        SharedModule,
        UxCoreModule.forRoot({ appConfig: appConfig, environment: environment }),
        StoreModule.forRoot(REDUCER_TOKEN, { metaReducers }),
        !environment.production ? StoreDevtoolsModule.instrument({ maxAge: 50 }) : [],
        TranslateModule.forRoot(translateConfig),
    ],
    declarations: [
    ],
    exports: [
        SharedModule,
    ],
    providers: [
        {
            provide: REDUCER_TOKEN,
            deps: [],
            useFactory: getReducers
        },
        ApiOcsService,
        InitiativeService,
        LanguageService,
        ApplicationService,
        HomeGuardService,
        DocumentValidatorService,
        SubmittedGuardService,
        AuthService
    ]
})
export class CoreModule {}
