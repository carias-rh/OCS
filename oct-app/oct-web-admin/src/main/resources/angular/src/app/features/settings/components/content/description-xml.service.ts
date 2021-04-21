import { Initiative } from '../../../../core/models/initiative';
import { ContactList, Contact } from '../../../../core/models/contact';
import { Description } from '../../../../core/models/description';
import { isUndefined } from 'util';

export class DescriptionXmlService {

    private initiative: Initiative;
    private contacts: ContactList;
    private descriptions: Description[] = [];

    constructor() {
        this.init();
    }

    parseXml(xml: string) {
        let parser = new DOMParser();
        let xmlDoc = parser.parseFromString(xml, 'text/xml');

        this.init();
        let successfulParsing = this.parseInitiative(xmlDoc);
        if (successfulParsing) {
            this.parseOrganisers(xmlDoc);
            this.parseLanguages(xmlDoc);
        }
        return successfulParsing;
    }

    getInitiative() {
        return this.initiative;
    }

    getDescriptions() {
        return this.descriptions;
    }

    getContacts() {
        return this.contacts;
    }

    private init() {
        this.initiative = new Initiative();
        this.descriptions = [];
        this.contacts = { representative: null, substitute: null, members: [] };
    }

    private parseInitiative(xmlDoc: Document) {
        let successfulUpload = true;
        try {
            let attributes: any = xmlDoc.getElementsByTagName('initiative')[0].attributes;
            for (let attribute of attributes) {
                switch (attribute.name) {
                case 'registrationNumber':
                    this.initiative.registrationNumber = attribute.value;
                    break;
                case 'registrationDate':
                    this.initiative.registrationDate = attribute.value;
                    break;
                case 'deadline':
                    this.initiative.closingDate = attribute.value;
                    break;
                case 'url':
                    this.initiative.webpage = attribute.value;
                    break;
                case 'startOfTheCollectionPeriod':
                    this.initiative.startOfTheCollectionPeriod = attribute.value;
                    break;
                case 'endOfTheCollectionPeriod':
                    this.initiative.endOfTheCollectionPeriod = attribute.value;
                    break;
                }
            }
        } catch (e) {
            successfulUpload = false;
        }
        return successfulUpload;
    }

    private parseLanguages(xmlDoc: Document) {
        let languages: any = xmlDoc.getElementsByTagName('language');

        for (let language of languages) {
            let code = language.getAttribute('code');
            let original = language.getAttribute('original');
            let description = new Description(code);
            if (isUndefined(language.children)) {
                /* Internet Explorer */
                for (let i = 0; i < language.childNodes.length; i++) {
                    let children = language.childNodes[i];
                    if (children.nodeType === 1) {
                        if (children.tagName === 'title') {
                            description.title = children.textContent;
                        } else if (children.tagName === 'description') {
                            description.objectives = children.textContent;
                        } else if (children.tagName === 'partialRegistration') {
                            description.partialRegistration = children.textContent;
                        } else if (children.tagName === 'site') {
                            description.url = children.textContent;
                        }
                    }
                }
            } else {
                /* Other browsers */
                for (let children of language.children) {
                    if (children.tagName === 'title') {
                        description.title = children.textContent;
                    } else if (children.tagName === 'description') {
                        description.objectives = children.textContent;
                    } else if (children.tagName === 'partialRegistration') {
                        description.partialRegistration = children.textContent;
                    } else if (children.tagName === 'site') {
                        description.url = children.textContent;
                    }
                }
            }

            if (original === 'true') {
                this.initiative.description = description;
            }

            this.descriptions.push(description);
        }
    }

    private parseOrganisers(xmlDoc: Document) {
        let organisers: any = xmlDoc.getElementsByTagName('organiser');

        for (let organiser of organisers) {
            let firstname = '';
            let familyname = '';
            let email = '';
            if (isUndefined(organiser.children)) {
                /* Internet Explorer */
                for (let i = 0; i < organiser.childNodes.length; i++) {
                    let children = organiser.childNodes[i];
                    if (children.nodeType === 1) {
                        if (children.tagName === 'firstName') {
                            firstname = children.textContent;
                        } else if (children.tagName === 'familyName') {
                            familyname = children.textContent;
                        } else if (children.tagName === 'email') {
                            email = children.textContent;
                        }
                    }
                }
            } else {
                /* Other browsers */
                for (let children of organiser.children) {
                    if (children.tagName === 'firstName') {
                        firstname = children.textContent;
                    } else if (children.tagName === 'familyName') {
                        familyname = children.textContent;
                    } else if (children.tagName === 'email') {
                        email = children.textContent;
                    }
                }
            }

            let contact = new Contact();
            contact.name = firstname + ' ' + familyname;
            contact.email = email;

            // Add contact by type
            if (organiser.getAttribute('role') === 'representative') {
                this.contacts.representative = contact;
            } else if (organiser.getAttribute('role') === 'substitute') {
                this.contacts.substitute = contact;
            } else if (organiser.getAttribute('role') === 'member') {
                this.contacts.members.push(contact);
            }
        }
    }

}
