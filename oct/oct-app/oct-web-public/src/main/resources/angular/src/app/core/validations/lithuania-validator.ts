import { DocumentValidator } from './document-validator';

/**
 * Lithuania Validator for document types.
 *
 * Personal identification number: 28603301236, 48507201231
 *
 * Info: https://webgate.ec.europa.eu/CITnet/jira/browse/OCSUI-178
 */
export class LithuaniaValidator extends DocumentValidator {

    public validateFormat(documentKey: string, value: string): boolean {
        if (documentKey === 'personal.number') {
            return this.validatePattern('^[1-6]{1}((?!=<year2>)[0-9]{2})((?!=<month>)[0-9]{2})((?!=<day>)[0-9]{2})[0-9]{4}$', value);
        }

        return true;
    }

    public validateChecksum(documentKey: string, value: string): boolean {
        if (documentKey === 'personal.number') {
            return this.validateCheckSumPersonalIdNumber(value);
        }

        return true;
    }

    private validateCheckSumPersonalIdNumber(value: string): boolean {
        const checksum = value.slice(-1);

        return Number(checksum) === this.getCheckSum(value);
    }

    private getCheckSum(code: string) {
        let b = 1, c = 3, d = 0, e = 0, i, digit;

        for (i = 0; i < 10; i++) {
            digit = parseInt(code[i], 10);
            d += digit * b;
            e += digit * c;
            b++;
            if (b === 10) {
                b = 1;
            }
            c++;
            if (c === 10) {
                c = 1;
            }
        }

        d = d % 11;
        e = e % 11;

        if (d < 10) {
            return d;
        } else if (e < 10) {
            return e;
        } else {
            return 0;
        }
    }

}
