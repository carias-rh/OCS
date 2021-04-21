export class DocumentNumber {
    key: string;
    useDate: boolean;
    patterns: Array<string>;

    constructor(key: string, patterns: string | Array<string>, useDate = false) {
        this.key = key;

        if (typeof patterns === 'string') {
            this.patterns = [patterns];
        } else {
            this.patterns = patterns;
        }

        this.useDate = useDate;
    }

    public validate(value: string) {
        let valid = false;

        for (let i = 0; i < this.patterns.length; i++) {
            if (this.validatePattern(this.patterns[i], value)) {
                return true;
            }
        }

        return valid;
    }

    private validatePattern(pattern: string, value: string) {
        if (this.useDate) {
            return this.validateRegExpDate(pattern, value);
        }

        return this.validateRegExp(pattern, value);
    }

    private validateRegExp(pattern: string, value: string) {
        const regex = new RegExp(pattern);

        if (regex.test(value)) {
            return true;
        }

        return false;
    }

    /**
     * Validate a regular expresion with date groups
     *
     * The valid groups are: year2 (YY), year4 (YYYY), month (MM), day (DD)
     *
     * @param pattern
     * @param value
     */
    private validateRegExpDate(pattern: string, value: string) {
        const regex = new RegExp(pattern);

        if (!regex.test(value)) {
            return false;
        }

        const matches = <any>(regex.exec(value));

        if (matches === null) {
            return false;
        }

        for (const group in matches.groups) {
            if (!this.validateDateGroup(group, matches.groups[group])) {
                return false;
            }
        }

        return true;
    }

    /**
     * Validate a date group extracted from regular expresion
     *
     * @param key   the groupName
     * @param value
     */
    private validateDateGroup(key: string, value: string) {
        const val = Number(value);

        if (key === 'year2') {
            return val >= 0 && val <= 99;
        }

        if (key === 'year4') {
            return val >= 1900 && val <= 2100;
        }

        if (key === 'month') {
            return val >= 1 && val <= 12;
        }

        if (key === 'day') {
            return val >= 1 && val <= 31;
        }

        return true;
    }

}
