import { Description } from './description.model';

export class Initiative {
    registrationNumber: string;
    registrationDate: string;
    closingDate: string;
    webpage: string;
    description: Description; // registration Description
    languages: any[];
    logo: string;
    signatureIdentifier: string;
    initiativeInfo?: any;

    constructor() {
        this.description = new Description();
        this.languages = [];
    }
}
