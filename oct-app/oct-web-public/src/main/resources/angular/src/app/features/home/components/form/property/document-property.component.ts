import { Component, Input, Output, EventEmitter, OnInit } from '@angular/core';

import { Property } from '../../../../../core/models/property.model';
import { DocumentValidatorService } from '../../../../../core/services/document-validator.service';

@Component({
    selector: 'app-document-property',
    templateUrl: './document-property.component.html'
})
export class DocumentPropertyComponent implements OnInit {

    @Input() property: Property;
    @Input() submitted = false;
    @Input() countryCode = '';
    @Input() validateOnBlur = true;
    @Output() propertyChange = new EventEmitter();

    documentType = null;
    validationKey = null;
    mask;

    get isError() {
        return this.submitted && this.property.error;
    }

    get feedbackClass() {
        return this.isError ? 'danger' : null;
    }

    constructor(private documentValidator: DocumentValidatorService) {}

    ngOnInit() {
        if (this.property.options) {
            this.documentType = this.property.options[0].id;
        }
        this.setValidationKey();
        this.mask = this.countryMask();
    }

    public onSelectDocumentType(id) {
        this.property.label = id;
        this.property.value = '';
        this.property.clearError();
        this.setValidationKey();
        this.onChange();
    }

    public onChange() {
        this.validate();
        this.propertyChange.emit(this.property);
    }

    onBlur() {
        this.validate(this.validateOnBlur);
    }

    private setValidationKey() {
        this.validationKey = this.countryCode + '.' + this.property.label;
    }

    private countryMask() {
        if (this.countryCode === 'be') {
            return [/\d/, /\d/, '.', /\d/, /\d/, '.', /\d/, /\d/, '-', /\d/, /\d/, /\d/, '.', /\d/, /\d/];
        }

        return false;
    }

    private validate(force = false) {
        if (!this.submitted && !force) {
            return;
        }

        let errorCode = null;
        let valid = this.property.value.trim() !== '';

        const validator = this.documentValidator.getValidator(this.countryCode, this.property.label);

        if (valid) {
            valid = validator.validateFormat(this.property.label, this.property.value);
            if (!valid) {
                errorCode = 'document.' + this.validationKey;
            }
        }

        if (valid) {
            valid = validator.validateChecksum(this.property.label, this.property.value);
            if (!valid) {
                errorCode = 'document.invalid';
            }
        }

        if (valid) {
            this.property.clearError();
        } else {
            this.property.setError(errorCode);
        }
    }

}
