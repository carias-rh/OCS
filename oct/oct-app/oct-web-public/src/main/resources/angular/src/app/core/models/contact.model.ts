export class Contacts {
    representative: Contact;
    substitute: Contact;
    entity: Contact;
    constructor() {}
}

export class Contact {
    constructor(public name = null, public email = null, public country = null) {}
}
