import { Language } from './language.model';

export class Email {
    languageCode: Language;
    email: string;
    generalSubscription: boolean;
    initiativeSubscription: boolean;
    constructor() {
    }
}

export class Contact {
    constructor(public name = null, public email = null, public country = null, generalSubscription = false,
    initiativeSubscription = false) {
    }
}
