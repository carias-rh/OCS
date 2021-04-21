import { DocumentValidator } from './document-validator';

/**
 * Poland Validator for document types.
 *
 * Numer ewidencyjny PESEL. 49021405089 51032302082 83091406135 02062800691
 *
 * Info: https://webgate.ec.europa.eu/CITnet/jira/browse/OCSUI-221
 */
export class PolandValidator extends DocumentValidator {

    public validateFormat(documentKey: string, value: string): boolean {
        // Numer ewidencyjny PESEL. YYXXDDNNNNN
        if (documentKey === 'national.id.number') {
            if (this.validatePattern('^[0-9]{11}$', value)) {
                const digits = value.split('');
                const year = Number(digits[0] + digits[1]);
                const month = Number(digits[2] + digits[3]);
                const day = Number(digits[4] + digits[5]);
                if (year < 0 || year > 99) {
                    return false;
                }
                if (!this.isCorrectNumMonth(month)) {
                    return false;
                }
                if (day < 1 || day > 31) {
                    return false;
                }
                return true;
            } else {
                return false;
            }
        }

        return true;
    }

    public validateChecksum(documentKey: string, value: string): boolean {
        // Numer ewidencyjny PESEL.
        if (documentKey === 'national.id.number') {
            return this.validateCheckNationalIdNum(value);
        }

        return true;
    }

    private validateCheckNationalIdNum(value: string): boolean {
        const digits = value.split('');

        return this.getCheckSum(digits);
    }

    private getCheckSum(digits: string[]) {
        const sum = Number(digits[0]) * 1
            + Number(digits[1]) * 3
            + Number(digits[2]) * 7
            + Number(digits[3]) * 9
            + Number(digits[4]) * 1
            + Number(digits[5]) * 3
            + Number(digits[6]) * 7
            + Number(digits[7]) * 9
            + Number(digits[8]) * 1
            + Number(digits[9]) * 3;

        let num = sum % 10;
        let checksum: number;

        if (num === 0) {
            checksum = 0;
        } else {
            checksum = 10 - num;
        }

        return checksum === Number(digits[10]) ? true : false;
    }

    private isCorrectNumMonth(month: number): boolean {
        if (month >= 1 && month <= 12
        || month >= 21 && month <= 32
        || month >= 41 && month <= 52
        || month >= 61 && month <= 72
        || month >= 81 && month <= 92) {
            return true;
        }

        return false;
    }

}
