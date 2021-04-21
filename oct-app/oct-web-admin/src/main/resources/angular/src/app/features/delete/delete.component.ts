import { Component, OnInit } from '@angular/core';
import { FormGroup, FormBuilder, FormArray, FormControl, Validators } from '@angular/forms';
import { InitiativeService } from '../../core/services/initiative.service';
import { TranslateService } from '@ngx-translate/core';
import { Observable } from 'rxjs/Observable';

@Component({
    templateUrl: './delete.component.html'
})
export class DeleteComponent implements OnInit {

    form: FormGroup;
    rows = 0;
    deleting = false;
    submitted = false;
    alert: {type: string, label: string};
    errors = 0;
    inputs: any[] = [];

    constructor (
        private fb: FormBuilder,
        private initiativeService: InitiativeService,
        private translate: TranslateService) {}

    ngOnInit() {
        this.formBuild();
    }

    formBuild() {
        this.form = this.fb.group ({
            'sig': new FormArray([]),
        });
        this.onAddRow();
    }

    onAddRow() {
        const control = new FormControl('', Validators.required);
        (<FormArray>this.form.get('sig')).push(control);
        this.rows = this.getRows().length;
        this.resetErrors();
    }

    getRows() {
        return (<FormArray>this.form.get('sig')).controls;
    }

    onDeleteRow(index) {
        (<FormArray>this.form.get('sig')).removeAt(index);
        this.rows = this.getRows().length;
        this.resetErrors();
    }

    onReset() {
        this.formBuild();
        this.resetErrors();
        this.deleting = false;

    }

    resetErrors() {
        this.submitted = false;
        this.alert = null;
        this.errors = 0;
        this.inputs = [];
    }

    onDeleteAll() {
        const ids: string[] = this.form.get('sig').value;
        this.resetErrors();

        // Validate signatures
        ids.forEach(( value, index) => {
            if ( value.trim() === '') {
                this.inputs.push({ index: index, value: value, error: true, label: 'error_req' });
                this.errors++;
            } else {
                this.inputs.push({ index: index, value: value, error: false, label: null });
            }
        });

        let fields = this.inputs.filter(item => !item.error);
        if ( fields.length === 0 && this.errors) {
            this.alert = { type: 'danger', label: 'dashboard.withdraw.error' };
            this.submitted = true;
        } else {
            this.deleteSignatures(fields);
        }

    }

    deleteSignatures(fields: any[]) {
        this.deleting = true;

        Observable.from(fields)
            .map(field => this.initiativeService.deleteSignature(field.index, field.value))
            .mergeAll()
            .subscribe((data: any) => {
                if (data.error) {
                    this.inputs[data.index].error = true;
                    if ( data.status === 417 ) {
                        this.inputs[data.index].label = 'error_uuid';
                    } else {
                        this.inputs[data.index].label = 'error_gen';

                    }
                    this.errors++;
                }
            },
            error => {},
            () => {
                this.deleting = false;
                this.submitted = true;
                if ( this.errors) {
                    this.alert = { type: 'danger', label: 'dashboard.withdraw.error' };
                } else {
                    this.alert = { type: 'success', label: 'dashboard.withdraw.success' };
                }
            });
    }

    onCloseAlert() {
        this.resetErrors();
    }

    isError(index) {
        return this.submitted && this.inputs[index] && this.inputs[index].error;
    }

    getError(index) {
        const label = 'dashboard.withdraw.' + this.inputs[index].label;
        return this.translate.instant(label, { id: this.inputs[index].value });
    }
}
