import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivate, RouterStateSnapshot, Router } from '@angular/router';

import { ApplicationService } from '../../core/services/application.service';

@Injectable()
export class SubmittedGuardService implements CanActivate {

    signatureIdentifier: string = null;

    constructor(
        private router: Router,
        private applicationService: ApplicationService
    ) { }

    canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot) {
        if (this.applicationService.signatureIdentifier) {
            return true;
        }

        this.signatureIdentifier = route.queryParams.uid;
        if (this.signatureIdentifier) {
            return true;
        }

        this.router.navigateByUrl('/screen/home');
        return false;
    }

}
