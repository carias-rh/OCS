import { DocumentValidator } from './document-validator';

/**
 * Croatia Validator for document type.
 *
 * Valid personal Id: 12345678903
 *
 * Info: https://webgate.ec.europa.eu/CITnet/jira/browse/OCSUI-177
 */
export class CroatiaValidator extends DocumentValidator {

    public validateFormat(documentKey: string, value: string): boolean {
        if (documentKey === 'personal.id') {
            return this.validatePattern('^[0-9]{11}$', value);
        }

        return true;
    }

    public validateChecksum(documentKey: string, value: string): boolean {
        if (documentKey === 'personal.id') {
            return this.validateChecksumPersonalId(value);
        }

        return true;
    }

    private validateChecksumPersonalId(value: string) {
        const digits = value.substr(0, value.length - 1);
        const checksum = value.substr(value.length - 1, 1);

        return Number(checksum) === this.getCheckSum(digits);
    }

    private getCheckSum(digits: string) {
        const f = (x: number) => {
            const val = x % 10;
            return (val === 0) ? 10 : val;
        };

        let t = 10;
        for (let i = 0; i <= digits.length - 1; i++) {
            const digit = Number(digits[i]);
            t = (2 * f(t + digit)) % 11;
        }

        return (11 - t) % 10;
    }
}
