import { DocumentValidator } from './document-validator';

/**
 * Spain Validator for document types.
 *
 * DNI:
 * Passaporte:
 *
 * Info: https://webgate.ec.europa.eu/CITnet/jira/browse/OCSUI-216
 */
export class SpainValidator extends DocumentValidator {

    public validateFormat(documentKey: string, value: string): boolean {
        // Documento Nacional de Identidad
        if (documentKey === 'id.card') {
            return this.validatePattern('^[0-9]{8}[a-zA-Z]{1}$', value); // NNNNNNNNL
        }

        // Pasaporte
        if (documentKey === 'passport') {
            return this.validatePattern('^[a-zA-Z]{3}[0-9]{6}$', value); // LLLNNNNNN
        }

        return true;
    }

    public validateChecksum(documentKey: string, value: string): boolean {
        // Documento Nacional de Identidad
        if (documentKey === 'id.card') {
            return this.validateCheckIdCard(value);
        }

        return true;
    }

    private validateCheckIdCard(value: string): boolean {
        const digits = Number(value.slice(0, -1));
        const letter = value.slice(-1).toLocaleLowerCase();
        const rest = digits % 23;

        return this.getCheckSumLetter(rest, letter);
    }

    private getCheckSumLetter(rest: number, letter: string) {
        if (rest === 0 && letter === 't') { return true; }
        if (rest === 1 && letter === 'r') { return true; }
        if (rest === 2 && letter === 'w') { return true; }
        if (rest === 3 && letter === 'a') { return true; }
        if (rest === 4 && letter === 'g') { return true; }
        if (rest === 5 && letter === 'm') { return true; }
        if (rest === 6 && letter === 'y') { return true; }
        if (rest === 7 && letter === 'f') { return true; }
        if (rest === 8 && letter === 'p') { return true; }
        if (rest === 9 && letter === 'd') { return true; }
        if (rest === 10 && letter === 'x') { return true; }
        if (rest === 11 && letter === 'b') { return true; }
        if (rest === 12 && letter === 'n') { return true; }
        if (rest === 13 && letter === 'j') { return true; }
        if (rest === 14 && letter === 'z') { return true; }
        if (rest === 15 && letter === 's') { return true; }
        if (rest === 16 && letter === 'q') { return true; }
        if (rest === 17 && letter === 'v') { return true; }
        if (rest === 18 && letter === 'h') { return true; }
        if (rest === 19 && letter === 'l') { return true; }
        if (rest === 20 && letter === 'c') { return true; }
        if (rest === 21 && letter === 'k') { return true; }
        if (rest === 22 && letter === 'e') { return true; }

        return false;
    }

}
