export class Auth {
    public time: string;
    public sessionExpire = 0;

    constructor(public username: string, public token: string) {
    }

    public extends(minutes: number) {
        this.sessionExpire = Date.now() + minutes * 60 * 1000;
    }

    public isExpired(): boolean {
        const currentTime = Date.now();

        if (this.sessionExpire < currentTime) {
            return true;
        }

        return false;
    }
}
