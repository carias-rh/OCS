import { DocumentValidator } from './document-validator';

/**
 * Belgium Validator for document types.
 *
 * National id number: 85.07.30-033.28, (example with '2': 17.07.30-033.84)
 *
 * Info: https://webgate.ec.europa.eu/CITnet/jira/browse/OCSUI-185
 */
export class BelgiumValidator extends DocumentValidator {

    public validateFormat(documentKey: string, value: string): boolean {
        if (documentKey === 'national.id.number') {
            // tslint:disable-next-line:max-line-length
            return this.validatePattern('^((?!=<year2>)[0-9]{2})[\.]((?!=<month>)[0-9]{2})[\.]((?!=<day>)[0-9]{2})[\-][0-9]{3}[\.][0-9]{2}$', value);
        }

        return true;
    }

    public validateChecksum(documentKey: string, value: string): boolean {
        if (documentKey === 'national.id.number') {
            return this.validateCheckSumNationalId(value);
        }

        return true;
    }

    private validateCheckSumNationalId(value: string): boolean {
        let valid = false;

        let digits = value.split('.').join('').split('-').join('').slice(0, - 2);
        const checksum = value.split('.').pop();

        if (Number(checksum) === this.getCheckSum(digits)) {
            valid = true;
        } else {
            digits = 2 + digits;
            valid = Number(checksum) === this.getCheckSum(digits);
        }

        return valid;
    }

    private getCheckSum(digits: string) {
        return 97 - (Number(digits) % 97);
    }

}
