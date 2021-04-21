import { Component, Input } from '@angular/core';

@Component({
    selector: 'app-signature-alert',
    templateUrl: 'signature-alert.component.html'
})
export class SignatureAlertComponent {

    @Input() icon: string;
    @Input() title: string;
    @Input() subtitle: string;
    @Input() code: string;

    constructor() {}

}
