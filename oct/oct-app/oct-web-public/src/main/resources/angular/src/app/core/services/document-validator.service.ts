import { Injectable } from '@angular/core';
import { CroatiaValidator } from '../validations/croatia-validator';
import { DocumentCountryValidator } from '../validations/document-country-validator';

import { DocumentValidator } from '../validations/document-validator';
import { BulgariaValidator } from '../validations/bulgaria-validator';
import { BelgiumValidator } from '../validations/belgium-validator';
import { LithuaniaValidator } from '../validations/lithuania-validator';
import { PortugalValidator } from '../validations/portugal-validator';
import { EstoniaValidator } from '../validations/estonia-validator';
import { SpainValidator } from '../validations/spain-validator';
import { PolandValidator } from '../validations/poland-validator';

@Injectable()
export class DocumentValidatorService {

    countryRules = {};

    constructor() {}

    public getValidator(countryCode: string, documentType: string) {
        let adapter = this.getAdapter(countryCode, documentType);
        return adapter;
    }

    private getAdapter(countryCode: string, documentType: string): DocumentValidator {
        const key = countryCode + '.' + documentType;

        /* Custom Country Validator */

        /**
         * BULGARIA
         *
         * Единен граждански номер. NNNNNNNN
         */
        if (key === 'bg.personal.number') {
            return new BulgariaValidator();
        }

        /**
         * CROATIA
         *
         * Osobni identifikacijski broj. NNNNNNNNNNN
         */
        if (key === 'hr.personal.id') {
            return new CroatiaValidator();
        }

        /**
         * BELGIUM
         *
         * Numéro de Registre national. YY.MM.DD-NNN.NN
         */
        if (key === 'be.national.id.number') {
            return new BelgiumValidator();
        }

        /**
         * LITHUANIA
         *
         * Asmens kodas. GYYMMDDNNNN
         */
        if (key === 'lt.personal.number') {
            return new LithuaniaValidator();
        }

        /**
         * PORTUGAL
         *
         * Bilhete de identidade. 1 to 8 digits
         * Passaporte. LNNNNNN
         * Cartão de Cidadão. NNNNNNNNNLLN
         */
        if (countryCode === 'pt') {
            return new PortugalValidator();
        }

        /**
         * ESTONIA
         *
         * Isikukood. NNNNNNNNNNN
         */
        if (key === 'ee.personal.number') {
            return new EstoniaValidator();
        }

        /**
         * SPAIN
         *
         * Documento Nacional de Identidad. NNNNNNNNL
         * Pasaporte. LLLNNNNNN
         */
        if (countryCode === 'es') {
            return new SpainValidator();
        }

        /**
         * POLAND
         *
         * Numer ewidencyjny PESEL. NNNNNNNNNNN
         */
        if (countryCode === 'pl') {
            return new PolandValidator();
        }

        /* Generic Country Validator */

        // Austria passport
        if (key === 'at.passport') {
            return new DocumentCountryValidator('^[a-zA-Z]{1}[0-9]{7}$'); // LNNNNNNN
        }

        // Austria ID card
        if (key === 'at.id.card') {
            return new DocumentCountryValidator('^[0-9]{8}$'); // NNNNNNNN
        }

        // Cyprus id card
        if (key === 'cy.id.card') {
            return new DocumentCountryValidator('^[0-9]{1,10}$'); // 1 to 10 digits
        }

        // Cyprus passport
        if (key === 'cy.passport') {
            return new DocumentCountryValidator([
                '^[B|C|E|J][0-9]{6}$',          // [B,C,E,J]NNNNNN
                '^[D|S]P[0-9]{7}$',             // [D,S][P]NNNNNNN
                '^K[0-9]{8}$'                   // [K]NNNNNNNN
            ]);
        }

        // Czechia passport
        if (key === 'cz.passport') {
            return new DocumentCountryValidator('^[0-9]{7,8}$'); // NNNNNNN or NNNNNNNN
        }

        // Czechia ID card
        if (key === 'cz.id.card') {
            return new DocumentCountryValidator([
                '^[0-9]{9}$',                       // NNNNNNNNN
                '^[0-9]{6}[a-zA-Z]{2}[0-9]{2}$',    // NNNNNNLLNN
                '^[0-9]{6}[a-zA-Z]{2}$',            // NNNNNNLL
                '^[a-zA-Z]{2}[0-9]{6}$'             // LLNNNNNN
            ]);
        }

        // France passport
        if (key === 'fr.passport') {
            return new DocumentCountryValidator('^[0-9]{2}[a-zA-Z]{2}[0-9]{4}[0-9]$'); // NNLLNNNNN
        }

        // France ID card
        if (key === 'fr.id.card') {
            return new DocumentCountryValidator([
                '^[a-zA-Z0-9]{7}$',     // AAAAAAA
                '^[a-zA-Z0-9]{12}$'     // AAAAAAAAAAAA
            ]);
        }

        // Hungary ID card
        if (key === 'hu.id.card') {
            return new DocumentCountryValidator([
                '^[0-9]{6}[a-zA-Z]{2}$',    // NNNNNNLL
                '^[a-zA-Z]{4,5}[0-9]{6}$',  // LLLLLNNNNNN or LLLLNNNNNN
                '^[a-zA-Z]{2}[0-9]{6}$'     // LLNNNNNN
            ]);
        }

        // Hungary passport
        if (key === 'hu.passport') {
            return new DocumentCountryValidator('^[a-zA-Z]{2}[0-9]{6,7}$'); // LLNNNNNN or LLNNNNNNN
        }

        // Hungary personal number
        if (key === 'hu.personal.number') {
            return new DocumentCountryValidator('^[0-9]{11}$'); // NNNNNNNNNNN
        }

        // Italy passport
        if (key === 'it.passport') {
            return new DocumentCountryValidator([
                '^[a-zA-Z]{2}[0-9]{7}$',    // LLNNNNNNN
                '^[a-zA-Z]{1}[0-9]{6}$',    // LNNNNNN
                '^[0-9]{6}[a-zA-Z]{1}$'     // NNNNNNL
            ]);
        }

        // Italy ID card
        if (key === 'it.id.card') {
            return new DocumentCountryValidator([
                '^[a-zA-Z]{2}[0-9]{6,8}$',              // LLNNNNNN or LLNNNNNNN or LLNNNNNNNN
                '^[a-zA-Z]{2}[0-9]{5}[a-zA-Z]{2}$',     // LLNNNNNLL
                '^[0-9]{7}[a-zA-Z]{2}$'                 // NNNNNNNLL
            ]);
        }

        // Romania
        if (key === 'ro.id.card') {
            return new DocumentCountryValidator('^[a-zA-Z]{2}[0-9]{6}$'); // LLNNNNNN
        }

        // Romania passport
        if (key === 'ro.passport') {
            return new DocumentCountryValidator('^[0-9]{8,9}$'); // NNNNNNNN or NNNNNNNNN
        }

        // Romania personal.id
        if (key === 'ro.personal.id') {
            return new DocumentCountryValidator('^[0-9]{13}$');  // NNNNNNNNNNNNN
        }

        // Sweden personal number - Custom Dates
        if (key === 'se.personal.number') {
            return new DocumentCountryValidator([
                '^((?!=<year2>)[0-9]{2})((?!=<month>)[0-9]{2})((?!=<day>)[0-9]{2})\-[0-9]{4}$', // YYMMDD-NNNN
                '^((?!=<year4>)[0-9]{4})((?!=<month>)[0-9]{2})((?!=<day>)[0-9]{2})\-[0-9]{4}$', // YYYYMMDD-NNNN
                '^((?!=<year2>)[0-9]{2})((?!=<month>)[0-9]{2})((?!=<day>)[0-9]{2})[0-9]{4}$',     // YYMMDDNNNN
                '^((?!=<year4>)[0-9]{4})((?!=<month>)[0-9]{2})((?!=<day>)[0-9]{2})[0-9]{4}$'      // YYYYMMDDNNNN
            ]);
        }

        // Malta ID card
        if (key === 'mt.id.card') {
            return new DocumentCountryValidator('^[0-9]{7}[a-zA-Z]{1}$'); // NNNNNNNL
        }

        // Latvia personal ID
        if (key === 'lv.personal.id') {
            return new DocumentCountryValidator('^[0-9]{11}$'); // NNNNNNNNNNN
        }

        // Slovenia. Enotna matična številka občana. Custom dates
        if (key === 'si.personal.number') {
            return new DocumentCountryValidator('^[0-9]{13}$'); // NNNNNNNNNNNNN
        }

        return null;
    }

}
