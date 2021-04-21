import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivate, RouterStateSnapshot, Router } from '@angular/router';

import { ApplicationService } from '../../core/services/application.service';
import { AuthService } from 'src/app/core/services/auth.service';

@Injectable()
export class HomeGuardService implements CanActivate {

    constructor(
        private applicationService: ApplicationService,
        private router: Router,
        private authService: AuthService
    ) { }

    canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot) {
        if (this.redirectToLogin()) {
            this.router.navigateByUrl('/login');
            return false;
        }

        if (this.redirectToNotInitialized()) {
            this.router.navigateByUrl('/not-initialized');
            return false;
        }

        if (this.redirectToDisabled(state)) {
            this.router.navigateByUrl('/screen/home/disabled');
            return false;
        }

        if (this.redirectToAlready(state)) {
            this.router.navigateByUrl('/screen/home/already');
            return true;
        }

        if (this.redirectToHome(state)) {
            this.router.navigateByUrl('/screen/home');
            return false;
        }

        return true;
    }

    private redirectToLogin() {
        const initialized = this.applicationService.systemState.initialized;
        const online = this.applicationService.systemState.online;

        // If status is not initialized: first login, after "Not initialized page"
        if (!initialized && !this.authService.isLoggedIn) {
            return true;
        }

        // If the aplication is initialized but is not online: protect by login
        if (initialized && !online && !this.authService.isLoggedIn) {
            return true;
        }

        return false;
    }

    private redirectToNotInitialized() {
        if (this.applicationService.systemState.initialized === false) {
            return true;
        }

        return false;
    }

    private redirectToDisabled(state: RouterStateSnapshot) {
        if (state.url === '/screen/home/disabled') {
            return false;
        }

        if (this.applicationService.isCollecting()) {
            return false;
        }

        return true;
    }

    private redirectToAlready(state: RouterStateSnapshot) {
        if (state.url === '/screen/home' && this.applicationService.alreadySupported) {
            return true;
        }

        return false;
    }

    private redirectToHome(state: RouterStateSnapshot) {
        if (state.url === '/screen/home/disabled') {
            return this.applicationService.isCollecting() === true;
        }

        if (state.url === '/screen/home/already') {
            return this.applicationService.alreadySupported === false;
        }

        return false;
    }

}
