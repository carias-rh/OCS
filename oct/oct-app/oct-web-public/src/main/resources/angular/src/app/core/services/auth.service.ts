import { Injectable } from '@angular/core';

import { Auth } from '../models/auth.model';

@Injectable()
export class AuthService {

    auth: Auth;
    isLoggedIn = false;
    isSessionExpired = false;
    redirectUrl: string;

    constructor() {
        this.auth = this.getSessionAuth();

        if (this.auth) {
            this.isLoggedIn = true;
        }
    }

    login(username: string, token: string) {
        this.auth = new Auth(username, token);
        this.auth.time = new Date().toJSON();
        this.isLoggedIn = true;
        this.saveSessionAuth(this.auth);
        this.isSessionExpired = false;

        if (!this.redirectUrl) {
            this.redirectUrl = '/';
        }
    }

    logout(reload = true) {
        this.isLoggedIn = false;
        this.auth = null;
        this.redirectUrl = null;
        this.deleteSessionAuth();

        if (reload) {
            window.location.href = '';
        }
    }

    forceLogout() {
        this.isLoggedIn = false;
        this.auth = null;
        this.redirectUrl = null;
        this.deleteSessionAuth();
        window.location.href = '';
        this.isSessionExpired = true;
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
            auth = JSON.parse(sessionStorage.getItem('auth'));
        } catch (e) {
            auth = null;
        }

        return auth;
    }

}
