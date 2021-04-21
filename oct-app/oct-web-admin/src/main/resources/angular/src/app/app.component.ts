import { Component, OnInit, Renderer, OnDestroy } from '@angular/core';
import { FormGroup, FormBuilder } from '@angular/forms';
import { ActivatedRoute, Router, NavigationEnd, ActivatedRouteSnapshot } from '@angular/router';

import { Observable } from 'rxjs/Observable';
import { Subscription } from 'rxjs/Subscription';
import { TranslateService } from '@ngx-translate/core';
import { UxLink, UxAppShellService, UxService } from '@eui/core';

import { ApplicationService } from './core/services/application.service';
import { LanguageService } from './core/services/language.service';
import { AuthService } from './core/services/auth.service';
import { ApiService } from './core/services/api.service';

@Component({
    selector: 'app-root',
    templateUrl: './app.component.html',
})
export class AppComponent implements OnInit, OnDestroy {
    public hasSidebar = false;
    public sidebarLinks: UxLink[] = [];
    menuLinks: UxLink[] = [];
    notificationLinks: UxLink[] = [];
    languages = [];
    formLanguage: FormGroup;
    translationSub: Subscription;
    expireSessionSub: Subscription;
    version: string;
    apiVersion: string;
    sessionCount = 0;
    extendedSession = false;

    constructor(
        private applicationService: ApplicationService,
        private apiService: ApiService,
        private languageService: LanguageService,
        private translateService: TranslateService,
        private fb: FormBuilder,
        private renderer: Renderer,
        private authService: AuthService,
        private router: Router,
        private route: ActivatedRoute,
        private appShell: UxAppShellService,
        private uxService: UxService

    ) {
        this.languages = this.applicationService.systemLanguages;
        this.apiService.getLastBuildDate().subscribe(data => this.apiVersion = data.apiVersion);

        this.translateService.onLangChange.subscribe(() => {
            this._createSidebarLinks();
            this.fixEuiAppShellState();
        });

        this.applicationService.systemState$
            .take(1)
            .subscribe(() => {
                this._createSidebarLinks();
                this.fixEuiAppShellState();
            });

        this.expireSessionSub = this.authService.sessionExpire$
            .switchMap(login => {
                if (login) {
                    const time = this.authService.auth.sessionExpire;
                    return Observable.of(time).delay(new Date(time - 60000));
                } else {
                    return Observable.never();
                }
            })
            .subscribe((time) => this.setSessionExpire());
    }

    get navigationLanguage() {
        return this.languageService.selectedLanguage;
    }

    get isLogged() {
        return this.authService.isLoggedIn;
    }

    get username() {
        return this.authService.auth.username;
    }

    ngOnInit() {
        this.checkSidebarData();

        this.formLanguage = this.fb.group({
            'language': [this.navigationLanguage]
        });
    }

    ngOnDestroy() {
        if (this.expireSessionSub) {
            this.expireSessionSub.unsubscribe();
        }
    }

    skipLink() {
        this.renderer.invokeElementMethod(document.querySelector('#content'), 'focus');
    }

    onLanguageChanged(language) {
        this.languageService.setNavigationLanguage(language, true);
    }

    onLogout() {
        this.apiService.logout(this.authService.getSessionToken()).subscribe(
            () => this.authService.logout()
        );
    }

    onExtendsSession(event: boolean) {
        if (event) {
            this.apiService.extendSession(this.authService.getSessionToken()).subscribe(res => {
                this.authService.extendSession(res.authToken, res.expireTimeMinutes);
                this.extendedSession = true;
            });
        }
    }

    private checkSidebarData() {
        this.getLastChildrenSnapshot().subscribe(
            route => {
                if (route.data['sidebar'] === false) {
                    this.hasSidebar = false;
                } else {
                    this.hasSidebar = true;
                }
                this._createSidebarLinks();
                this.fixEuiAppShellState();
            }
        );
    }

    private getLastChildrenSnapshot(): Observable<ActivatedRouteSnapshot> {
        return this.router.events
            .filter(event => event instanceof NavigationEnd)
            .map(() => this.route.snapshot)
            .map(route => {
                while (route.firstChild) {
                    route = route.firstChild;
                }
                return route;
            });
    }

    private fixEuiAppShellState() {
        const state = this.appShell.state;
        state.isSidebarInnerActive = this.hasSidebar;
        state.sidebarLinks = this.sidebarLinks;
        this.appShell.setState(state);
    }

    private _createSidebarLinks() {
        const systemState = this.applicationService.systemState;

        if (!this.hasSidebar) {
            this.sidebarLinks = [];
        } else {
            this.sidebarLinks = [
                new UxLink({
                    label: this.translateService.instant('menu.dashboard'),
                    url: '/screen/dashboard',
                    iconClass: 'fa fa-dashboard '
                }),
                new UxLink({
                    label: this.translateService.instant('menu.export'),
                    url: '/screen/export',
                    iconClass: 'fa fa-sign-out'
                }),
                new UxLink({
                    label: this.translateService.instant('menu.delete'),
                    url: '/screen/delete',
                    iconClass: 'fa fa-trash'
                }),
                new UxLink({
                    label: this.translateService.instant('menu.feedback'),
                    url: '/screen/feedback',
                    iconClass: 'fa fa-commenting-o'
                }),
                new UxLink({
                    label: this.translateService.instant('menu.settings'),
                    url: '/screen/settings',
                    iconClass: 'fa fa-cogs'
                })
            ];
        }
    }

    private setSessionExpire(time = 60) {
        let extendedSession = false;

        this.sessionCount = 60;
        Observable.interval(1000).take(60).subscribe(
            (val) => this.sessionCount--,
            (err) => {},
            () => {
                if (!this.extendedSession) {
                    this.onLogout();
                }
            }
        );
        this.uxService.openMessageBox('warningSession');
    }

}
