export class Property {
    id: number;
    group: number;
    label: string;
    priority: number;
    required: number;
    type: string;
    value: string;
    error: boolean = false;
    errorMessage: string;
    options?: any[];
    index?: number;
    other?: string;

    constructor(id = null, group = null, label = null, priority = null, required = null, type = null) {
        this.id = id;
        this.group = group;
        this.label = label;
        this.priority = priority;
        this.required = required;
        this.type = type;
    }

    get inputType() {
        if (this.type === 'COUNTRY') {
            return 'country';
        }
        if (this.type === 'NATIONALITY') {
            return 'select';
        }
        if (this.type === 'DATE') {
            return 'date';
        }

        return 'text';
    }

    clearError() {
        this.error = false;
        this.errorMessage = null;
    }

    setError(text: string) {
        this.error = true;
        this.errorMessage = text;
    }
}
