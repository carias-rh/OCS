import { Injectable } from '@angular/core';
import { Router } from '@angular/router';

import { Auth } from '../models/auth.model';
import { BehaviorSubject } from 'rxjs/BehaviorSubject';

@Injectable()
export class AuthService {
    auth: Auth;
    isLoggedIn = false;
    isSessionExpired = false;
    redirectUrl: string;

    sessionExpire$ = new BehaviorSubject<boolean>(false);

    constructor() {
        this.auth = this.getSessionAuth();

        if (this.auth) {
            if (this.auth.isExpired()) {
                this.auth = null;
            } else {
                this.isLoggedIn = true;
                this.sessionExpire$.next(true);
            }
        }
    }

    login(username: string, token: string, expireTimeMinutes: number) {
        this.auth = new Auth(username, token);
        this.auth.time = new Date().toJSON();
        this.auth.extends(expireTimeMinutes);

        this.isLoggedIn = true;
        this.saveSessionAuth(this.auth);
        this.isSessionExpired = false;
        this.sessionExpire$.next(true);

        if (!this.redirectUrl) {
            this.redirectUrl = '/';
        }
    }

    logout(reload = true) {
        this.isLoggedIn = false;
        this.auth = null;
        this.redirectUrl = null;
        this.deleteSessionAuth();
        this.sessionExpire$.next(false);

        if (reload) {
            window.location.href = '';
        }
    }

    forceLogout() {
        this.isLoggedIn = false;
        this.auth = null;
        this.redirectUrl = null;
        this.deleteSessionAuth();
        this.isSessionExpired = true;
        this.sessionExpire$.next(false);

        window.location.href = '';
    }

    extendSession(authToken: string, expireTimeMinutes: number) {
        this.auth.token = authToken;
        this.auth.extends(expireTimeMinutes);
        this.saveSessionAuth(this.auth);
        this.sessionExpire$.next(true);
    }

    getSessionToken(): string {
        if (this.auth) {
            return this.auth.token;
        }

        return null;
    }

    private saveSessionAuth(auth: Auth) {
        sessionStorage.setItem('auth', JSON.stringify(auth));
    }

    private deleteSessionAuth() {
        sessionStorage.removeItem('auth');
    }

    private getSessionAuth(): Auth {
        let auth: Auth;

        try {
            const data = JSON.parse(sessionStorage.getItem('auth'));
            auth = new Auth(data.username, data.token);
            auth.time = data.time;
            auth.sessionExpire = data.sessionExpire;
        } catch (e) {
            auth = null;
        }

        return auth;
    }

}
