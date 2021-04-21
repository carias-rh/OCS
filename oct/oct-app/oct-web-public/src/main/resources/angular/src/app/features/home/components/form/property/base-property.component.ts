import { Input, Output, EventEmitter } from '@angular/core';
import { Property } from '../../../../../core/models/property.model';

export class BasePropertyComponent {
    @Input() property: Property;
    @Input() submitted: boolean;
    @Output() propertyChange = new EventEmitter();

    constructor() {}

    get label() {
        return 'form.property.' + this.property.label;
    }

    get isError() {
        return this.submitted && this.property.error;
    }

    get feedbackClass() {
        return this.isError ? 'danger' : null;
    }

    public onChange(event) {
        this.propertyChange.emit(this.property);
    }

    protected validate() {
        if (this.submitted && this.property.required && this.property.value.trim() === '') {
            this.property.setError('oct.empty.property');
        }
    }

}
