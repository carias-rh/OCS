import { Component, OnInit, ViewEncapsulation, DoCheck } from '@angular/core';
import { FormGroup, FormBuilder, FormControl, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { ClipboardService } from 'ngx-clipboard';

import { ApiService } from '../../services/api.service';
import { AuthService } from '../../services/auth.service';

@Component({
    templateUrl: './login.component.html',
    styleUrls: ['login.component.scss'],
    encapsulation: ViewEncapsulation.None,
})
export class LoginComponent implements OnInit {
    form: FormGroup;
    error = false;

    constructor(
        private service: ApiService,
        private authService: AuthService,
        private fb: FormBuilder,
        private router: Router,
        private clipService: ClipboardService
    ) {
        this.form = this.fb.group({
            username: ['', Validators.required],
            password: ['', Validators.required],
            challenge: [],
            result: ['', Validators.required]
        });
    }

    get username(): FormControl {
        return <FormControl>this.form.get('username');
    }

    get password(): FormControl {
        return <FormControl>this.form.get('password');
    }

    get challenge(): FormControl {
        return <FormControl>this.form.get('challenge');
    }

    get result(): FormControl {
        return <FormControl>this.form.get('result');
    }

    ngOnInit() {
        this.refreshChallenge();
    }

    public onLogin() {
        this.service.authenticate(this.username.value, this.password.value, this.result.value).subscribe(
            (res: any) => {
                if (res.status && res.status === 'OK') {
                    this.authService.login(this.username.value, res.authToken, res.expireTimeMinutes);
                    this.router.navigate([this.authService.redirectUrl]);
                } else {
                    this.authService.logout(false);
                    this.error = true;
                    this.refreshChallenge();
                }
            },
            (err) => {
                this.authService.logout(false);
                this.error = true;
                this.refreshChallenge();
            }
        );
    }

    public onCopy() {
        this.clipService.copyFromContent(this.challenge.value);
    }

    private refreshChallenge() {
        this.service.getSecurityChallenge().subscribe((data: any) => {
            this.challenge.setValue(data.challenge);
        });
    }
}
