import { DocumentValidator } from './document-validator';

export class DocumentCountryValidator extends DocumentValidator {
    patterns: string[];

    constructor(patterns: string | string[]) {
        super();
        if (typeof patterns === 'string') {
            this.patterns = [patterns];
        } else {
            this.patterns = patterns;
        }
    }

    public validateFormat(documentKey: string, value: string): boolean {
        return this.validate(this.patterns, value);
    }

    public validateChecksum(documentKey: string, value: string): boolean {
        return true;
    }
}
