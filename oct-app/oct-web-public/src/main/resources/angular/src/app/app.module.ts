import { NgModule, APP_INITIALIZER } from '@angular/core';
import { HTTP_INTERCEPTORS } from '@angular/common/http';
import { BrowserModule } from '@angular/platform-browser';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';

import { CachePreventionInterceptor, CsrfPreventionInterceptor } from '@eui/core';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { CoreModule } from './core/core.module';
import { ApplicationService } from './core/services/application.service';

export function initApp(app: ApplicationService) {
    return () => app.init();
}
@NgModule({
    declarations: [
        AppComponent,
    ],
    imports: [
        BrowserModule,
        BrowserAnimationsModule,
        CoreModule,
        AppRoutingModule,
    ],
    providers: [
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
        }
    ],
    exports: [
    ],
    bootstrap: [
        AppComponent
    ],
})
export class AppModule { }
