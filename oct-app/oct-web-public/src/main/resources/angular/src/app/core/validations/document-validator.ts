
export abstract class DocumentValidator {
    abstract validateFormat(documentKey: string, value: string): boolean;
    abstract validateChecksum(documentKey: string, value: string): boolean;

    protected validate(patterns: Array<string>, value: string): boolean {
        for (let i = 0; i < patterns.length; i++) {
            if (this.validatePattern(patterns[i], value)) {
                return true;
            }
        }

        return false;
    }

    protected validatePattern(pattern: string, value: string) {
        if (this.hasGroups(pattern)) {
            return this.validateRegExpDate(pattern, value);
        }

        return this.validateRegExp(pattern, value);
    }

    private validateRegExp(pattern: string, value: string) {
        const regex = new RegExp(pattern);

        return regex.test(value);
    }

    /**
     * Check if the pattern contains date groups to validate
     *
     * @param pattern
     */
    private hasGroups(pattern: string): boolean {
        const groups = ['<year2>', '<year4>', '<month>', '<day>'];

        return groups.some(group => pattern.includes(group));
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

        // We need to catch groups manually because Firefox and Explorer old versions don't use ECMAscript2018
        if (this.isInvalidDateGroup(pattern, matches)) {
            return false;
        }

        return true;
    }

    /**
     * Validate a date group extracted from regular expresion
     *
     * @param pattern
     * @param matches
     */
    private isInvalidDateGroup(pattern: string, matches) {
        const valid = [];

        if (pattern.includes('<year2>')) {
            valid.push(matches[1] >= 0 && matches[1] <= 99);
        }

        if (pattern.includes('<year4>')) {
            valid.push(matches[1] >= 1900 && matches[1] <= 2100);
        }

        if (pattern.includes('<month>')) {
            valid.push(matches[2] >= 1 && matches[2] <= 12);
        }

        if (pattern.includes('<day>')) {
            valid.push(matches[3] >= 1 && matches[3] <= 31);
        }

        return valid.some(v => v === false);
    }

}
