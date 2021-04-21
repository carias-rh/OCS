export class CountryMap {
    countryCode: string;
    count: number;
    treshold: number;
    percentage: number;
    countryName: string;

    constructor(countryCode, count = 0, treshold = 0, percentage = 0) {
        this.countryCode = countryCode;
        this.count = count;
        this.treshold = treshold;
        this.percentage = percentage;
    }

    get threshold() {
        return this.treshold;
    }

    get label() {
        return 'common.country.' + this.countryCode;
    }

    get codeMap() {
        if (this.countryCode === 'ge') {
            return 'de';
        }

        if (this.countryCode === 'uk') {
            return 'gb';
        }

        if (this.countryCode === 'el') {
            return 'gr';
        }

        return this.countryCode;
    }

}
