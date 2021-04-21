import { DocumentValidator } from './document-validator';

/**
 * Estonia Validator for document types.
 *
 * Isikukood. 37605030299, 37508166515, 30001010004
 *
 * Info: https://webgate.ec.europa.eu/CITnet/jira/browse/OCSUI-200
 */
export class EstoniaValidator extends DocumentValidator {

    public validateFormat(documentKey: string, value: string): boolean {
        // Isikukood
        if (documentKey === 'personal.number') {
            return this.validatePattern('^[0-9]{11}$', value); // NNNNNNNNNNN
        }

        return true;
    }

    public validateChecksum(documentKey: string, value: string): boolean {
        // Isikukood
        if (documentKey === 'personal.number') {
            return this.validateCheckSumPersonalNum(value);
        }

        return true;
    }

    private validateCheckSumPersonalNum(value: string): boolean {
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
