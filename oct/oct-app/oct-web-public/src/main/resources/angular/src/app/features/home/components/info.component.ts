import { Component, OnInit } from '@angular/core';
import { ApplicationService } from '../../../core/services/application.service';

@Component({
    templateUrl: './info.component.html'
})
export class InfoComponent implements OnInit {

    code: string;
    allowedCode = ['1', '2', '3', '4', '5'];

    constructor(private applicationService: ApplicationService) {}

    ngOnInit() {
        if (this.applicationService.errorCode && this.isAllowedCode()) {
            this.code = this.applicationService.errorCode;
        } else {
            this.code = '0';
        }
    }

    private isAllowedCode() {
        return this.allowedCode.find(code => code === this.applicationService.errorCode);
    }

}
