export class CountryMap {
    countryCode: number;
    count: number;
    threshold: number;

    constructor(countryCode, count = 0, threshold = 0) {
        this.countryCode = countryCode;
        this.count = count;
        this.threshold = threshold;
    }

    get label() {
        return 'country.' + this.countryCode;
    }

    percent() {
        let rate: number;

        if (this.threshold > 0) {
            rate = this.count / this.threshold * 100;
        } else {
            rate = 0;
        }

        return Math.round(rate * 100) / 100;
    }
}
