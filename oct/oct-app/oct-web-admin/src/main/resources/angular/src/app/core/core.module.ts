import { NgModule, APP_INITIALIZER } from '@angular/core';
import { StoreModule } from '@ngrx/store';
import { StoreDevtoolsModule } from '@ngrx/store-devtools';
import { TranslateModule } from '@ngx-translate/core';

import { HTTP_INTERCEPTORS } from '@angular/common/http';
import {
    CachePreventionInterceptor,
    CorsSecurityInterceptor,
    CsrfPreventionInterceptor,
    EuLoginSessionTimeoutHandlingInterceptor,
    CoreModule as UxCoreModule,
    translateConfig,
} from '@eui/core';

import './operators';

import { appConfig } from '../../config/index';
import { environment } from '../../environments/environment';

import { REDUCER_TOKEN, getReducers, metaReducers } from './reducers/index';

import { LoginComponent } from './components/user/login.component';

import { SharedModule } from '../shared/shared.module';
import { ApplicationService } from './services/application.service';
import { AuthService } from './services/auth.service';
import { ApiService } from './services/api.service';
import { InitiativeService } from './services/initiative.service';
import { LanguageService } from './services/language.service';
import { AuthGuardService } from './services/auth-guard.service';
import { OnlineGuardService } from './services/online-guard.service';

export function initApp(app: ApplicationService) {
    return () => app.init();
}

@NgModule({
    imports: [
        SharedModule,
        UxCoreModule.forRoot({ appConfig: appConfig, environment: environment }),
        StoreModule.forRoot(REDUCER_TOKEN, { metaReducers }),
        !environment.production ? StoreDevtoolsModule.instrument({ maxAge: 50 }) : [],
        TranslateModule.forRoot(translateConfig),
    ],
    declarations: [
        LoginComponent
    ],
    exports: [
        SharedModule,
        LoginComponent
    ],
    providers: [
        {
            provide: REDUCER_TOKEN,
            deps: [],
            useFactory: getReducers
        },
        /* {
            provide: HTTP_INTERCEPTORS,
            useClass: CorsSecurityInterceptor,
            multi: true,
        },
        {
            provide: HTTP_INTERCEPTORS,
            useClass: EuLoginSessionTimeoutHandlingInterceptor,
            multi: true,
        },
        */
        {
            provide: HTTP_INTERCEPTORS,
            useClass: CsrfPreventionInterceptor,
            multi: true,
        },
        {
            provide: HTTP_INTERCEPTORS,
            useClass: CachePreventionInterceptor,
            multi: true,
        },
        {
            provide: APP_INITIALIZER,
            useFactory: initApp,
            deps: [ApplicationService],
            multi: true,
        },
        ApplicationService,
        LanguageService,
        InitiativeService,
        ApiService,
        AuthService,
        AuthGuardService,
        OnlineGuardService
    ]
})
export class CoreModule {

}
