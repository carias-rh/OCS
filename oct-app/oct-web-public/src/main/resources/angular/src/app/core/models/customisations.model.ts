export class Customisations {
    showProgressionBar: boolean;
    showDistributionMap: boolean;
    showRecentSupporters: boolean;
    facebookUrl: string;
    twitterUrl: string;
    callbackUrl: string;
    customLogo: boolean;
    optionalValidation: boolean;
    background: number;
    signatureGoal: number;

    get showFacebook() {
        return typeof this.facebookUrl === 'string' && this.facebookUrl !== '';
    }
    get showTwitter() {
        return typeof this.twitterUrl === 'string' && this.twitterUrl !== '';
    }
    get showSocialMedia() {
        return this.showFacebook && this.showTwitter;
    }

    constructor(data: Object = null) {
        if (data) {
            this.showProgressionBar = data['showProgressionBar'];
            this.showDistributionMap = data['showDistributionMap'];
            this.showRecentSupporters = data['showRecentSupporters'];
            this.facebookUrl = data['facebookUrl'];
            this.twitterUrl = data['twitterUrl'];
            this.callbackUrl = data['callbackUrl'];
            this.customLogo = data['customLogo'];
            this.optionalValidation = data['optionalValidation'];
            this.background = data['background'];
            this.signatureGoal = data['signatureGoal'];
        }
    }
}
