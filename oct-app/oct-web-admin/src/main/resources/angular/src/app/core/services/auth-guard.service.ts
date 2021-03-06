import { Injectable } from '@angular/core';
import { Router, ActivatedRouteSnapshot, RouterStateSnapshot } from '@angular/router';

import { AuthService } from './auth.service';

@Injectable()
export class AuthGuardService {
    constructor(private authService: AuthService, private router: Router) {}

    canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): boolean {
        return this.checkLogin(state.url);
    }

    checkLogin(url: string): boolean {
        if (this.authService.isLoggedIn) {
            return true;
        }

        this.authService.redirectUrl = url;
        this.router.navigate(['/login']);

        return false;
    }
}
