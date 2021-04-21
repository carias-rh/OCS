export class Contact {
    name: string;
    email: string;
}
export class ContactList {
    representative: Contact;
    substitute: Contact;
    members: Contact[];
}
