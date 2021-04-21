import { DocumentValidator } from './document-validator';

/**
 * Bulgaria Validator for document types.
 *
 * Valid personal number: 7523169263, 7542011030
 *
 * Info: https://webgate.ec.europa.eu/CITnet/jira/browse/OCSUI-171
 */
export class BulgariaValidator extends DocumentValidator {

    public validateFormat(documentKey: string, value: string) {
        if (documentKey === 'personal.number') {
            return this.validatePattern('^[0-9]{10}$', value);
        }

        return true;
    }

    public validateChecksum(documentKey: string, value: string): boolean {
        if (documentKey === 'personal.number') {
            return this.validateCheckSumPersonalNumber(value);
        }

        return true;
    }

    private validateCheckSumPersonalNumber(value: string): boolean {
        const month = Number(value.substr(2, 2));
        if (!((month > 0 && month < 13) || (month > 20 && month < 33) || (month > 40 && month < 53))) {
            return false;
        }

        const day = Number(value.substr(4, 2));
        if (day < 1 || day > 31) {
            return false;
        }

        const digits = value.substr(0, value.length - 1);
        const checksum = value.substr(value.length - 1, 1);

        return Number(checksum) === this.getCheckSum(digits);
    }

    private getCheckSum(digits: string): number {
        const weights = [2, 4, 8, 5, 10, 9, 7, 3, 6];
        let total = 0;

        for (let i = 0; i < 9; ++i) {
            total += Number(digits[i]) * weights[i];
        }

        total = (total % 11) % 10;

        return total;
    }
}
