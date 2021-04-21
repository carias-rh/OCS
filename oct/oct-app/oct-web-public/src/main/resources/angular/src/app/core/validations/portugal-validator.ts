import { DocumentValidator } from './document-validator';

/**
 * Portugal Validator for document types.
 *
 * Bilhete de identidade:
 * Passaporte:
 * Cartão de Cidadão: 000000000zz4, 076940535zx6
 *
 * Info: https://webgate.ec.europa.eu/CITnet/jira/browse/OCSUI-189
 */
export class PortugalValidator extends DocumentValidator {

    public validateFormat(documentKey: string, value: string): boolean {
        // Bilhete de identidade
        if (documentKey === 'id.card') {
            return this.validatePattern('^[0-9]{1,8}$', value); // 1 to 8 digits
        }

        // Passaporte
        if (documentKey === 'passport') {
            return this.validatePattern('^[a-zA-Z]{1,2}[0-9]{6}$', value); // LNNNNNN or LLNNNNNN
        }

        // Cartão de Cidadão
        if (documentKey === 'citizens.card') {
            return this.validatePattern('^[0-9]{9}[a-zA-Z]{2}[0-9]{1}$', value); // NNNNNNNNNLLN
        }

        return true;
    }

    public validateChecksum(documentKey: string, value: string): boolean {
        // Cartão de Cidadão
        if (documentKey === 'citizens.card') {
            return this.validateCheckSumCitizensCard(value);
        }

        return true;
    }

    private validateCheckSumCitizensCard(value: string): boolean {
        const digits = value.split('');
        digits[9] = digits[9].toLowerCase();
        digits[10] = digits[10].toLowerCase();
        const valueConverted = this.convertValues(digits);

        return this.getCheckSum(valueConverted);
    }

    private getCheckSum(code: string[]) {
        let num0 = (+code[0] * 2) >= 10 ? (+code[0] * 2) - 9 : +code[0] * 2;
        let num1 = +code[1];
        let num2 = (+code[2] * 2) >= 10 ? (+code[2] * 2) - 9 : +code[2] * 2;
        let num3 = +code[3];
        let num4 = (+code[4] * 2) >= 10 ? (+code[4] * 2) - 9 : +code[4] * 2;
        let num5 = +code[5];
        let num6 = (+code[6] * 2) >= 10 ? (+code[6] * 2) - 9 : +code[6] * 2;
        let num7 = +code[7];
        let num8 = (+code[8] * 2) >= 10 ? (+code[8] * 2) - 9 : +code[8] * 2;
        let num9 = +code[9];
        let num10 = (+code[10] * 2) >= 10 ? (+code[10] * 2) - 9 : +code[10] * 2;
        let num11 = +code[11];

        let sum = num0 + num1 + num2 + num3 + num4 + num5 + num6 + num7 + num8 + num9 + num10 + num11;
        const checksum = sum % 10;

        if (checksum === 0) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Tabela de Conversão do Alfabeto
     * A -> 10, B -> 11, C -> 12, D -> 13, E -> 14, F -> 15, ..., Y -> 34, Z -> 35
     */
    private convertValues(digits: string[]) {
        const alphabet = ['a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k',
            'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'];

        digits.map(item => {
            let found = alphabet.find(a => a === item);

            if (found) {
                let position = digits.indexOf(found);
                let newValue = alphabet.findIndex(f => f === found) + 10;
                digits.splice(position, 1, newValue.toString());
            }
        });

        return digits;
    }

}
