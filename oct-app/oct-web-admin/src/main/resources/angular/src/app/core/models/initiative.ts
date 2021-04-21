import { Description } from './description';

export class Initiative {
    available: boolean;
    registrationNumber: string;
    registrationDate: string;
    closingDate: string;
    webpage: string;
    description: Description; // registration Description
    languages: any[];
    startOfTheCollectionPeriod: string;
    endOfTheCollectionPeriod: string;

    constructor() {
        this.description = new Description();
        this.languages = [];
    }
}
