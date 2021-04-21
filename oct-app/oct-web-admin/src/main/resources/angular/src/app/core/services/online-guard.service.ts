import { Injectable } from '@angular/core';
import { Router, ActivatedRouteSnapshot, RouterStateSnapshot } from '@angular/router';
import { ApplicationService } from './application.service';

@Injectable()
export class OnlineGuardService {
    constructor(private router: Router, private applicationService: ApplicationService) {}

    canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): boolean {
        return this.checkSystemState();
    }

    checkSystemState(): boolean {
        if (this.applicationService.systemState === 'ONLINE') {
            return true;
        }

        this.router.navigate(['/screen/settings']);

        return false;
    }
}
