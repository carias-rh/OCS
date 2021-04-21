import { Property } from './property.model';

export class SupportForm {
    countryCode: string;
    personalFields: Property[] = [];
    addressFields: Property[] = [];
    documentFields: Property[] = [];
    documentType: Property;
    documentTypes: string[];
    certify: Property;
    privacy: Property;
    captcha: Property;

    constructor(countryCode: string) {
        this.countryCode = countryCode;
        this.personalFields = [];
        this.addressFields = [];
        this.documentFields = [];
        this.documentTypes = [];
        this.documentType = null;
        this.certify = new Property();
        this.privacy = new Property();
        this.captcha = new Property();
    }

    /**
     * Check if the current selected country need to show the label permanent residence
     */
    get hasPermanentResidence() {
        if (this.countryCode === 'fi') {
            return true;
        }

        return false;
    }

    public addPersonalField(property: Property) {
        property.index = this.personalFields.length;
        this.personalFields.push(property);
    }

    public addAddressField(property: Property) {
        property.index = this.addressFields.length;
        this.addressFields.push(property);
    }

    public addDocumentField(property: Property) {
        if (this.documentType === null) {
            this.documentType = property;
            this.documentTypes.push(property.label);
        } else {
            this.documentTypes.push(property.label);
        }
    }

    public updateProperty(property: Property) {
        if (property.group === 1) {
            this.personalFields[property.index] = property;
        } else if (property.group === 2) {
            this.addressFields[property.index] = property;
        } else if (property.group === 3) {
            this.documentType = property;
        }
    }

    public hasPersonalFields() {
        return this.personalFields.length > 0;
    }

    public hasAddressFields() {
        return this.addressFields.length > 0;
    }

    public hasDocumentsFields() {
        return this.documentType !== null;
    }

    public clearErrors() {
        this.certify.clearError();
        this.privacy.clearError();
        this.captcha.clearError();

        this.personalFields.forEach((field, index) => {
            this.personalFields[index].clearError();
        });

        this.addressFields.forEach((field, index) => {
            this.addressFields[index].clearError();
        });

        this.documentFields.forEach((field, index) => {
            this.documentFields[index].clearError();
        });

    }

}
