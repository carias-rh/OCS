import { Component, Input, OnInit, Output, EventEmitter, OnChanges, ViewChild, ElementRef, ViewEncapsulation } from '@angular/core';
import { FormGroup, FormBuilder } from '@angular/forms';

import { UploadEvent, FileSystemFileEntry } from 'ngx-file-drop';

import { Upload } from './upload';

@Component({
    selector: 'ocs-upload',
    templateUrl: 'upload.component.html',
    styleUrls: ['./upload.component.scss'],
    encapsulation: ViewEncapsulation.None,
})
export class UploadComponent implements OnInit, OnChanges {
    @ViewChild('inputFile') inputFile: ElementRef;

    @Input() allowedFiles = ['doc', 'docx', 'pdf', 'ppt', 'rtf', 'xls', 'xlsx'];
    @Input() documents: Upload[];
    @Input() label = 'upload.up-to-date';
    @Input() tableName = 'certificate.table-name';
    @Input() isMultiple = true;
    @Input() required = false;
    @Input() touched = false;
    @Input() viewMode = false;
    @Input() hasStatus: boolean;
    @Input() hasDate: boolean;
    @Input() deleting = false;
    @Input() size = '5 MB';
    @Input() showTable = true;
    @Input() showInfo = true;
    @Output() newDocuments = new EventEmitter();
    @Output() deleted = new EventEmitter();

    form: FormGroup;
    error = false;
    errorMessage = '';
    deleteWarning = 'confirm.delete-document';
    downloadUrl = null;
    extensionList = '';

    uploads: Array<Upload> = [];  // Lista de documentos a guardar
    deletedList: Array<Upload> = []; // Lista de documentos borrados, para eliminar del API

    constructor(
        private formBuilder: FormBuilder
    ) {
    }

    ngOnInit() {
        this.extensionList = this.allowedFiles.map(ext => ext.toLocaleUpperCase()).join(', ');
        this.allowedFiles.map(ext => {
            this.allowedFiles.push(ext.toLocaleUpperCase());
        });
        this.buildForm();
    }

    ngOnChanges(changes) {
        if (changes.documents) {
            this.uploads = this.documents || [];
            this.deletedList = [];
        }
        if (changes.touched && this.touched) {
            this.validateRequired();
        }
    }

    buildForm() {
        this.form = this.formBuilder.group({
            'file': [null]
        });
    }

    public dropped(event: UploadEvent) {
        const files = event.files;

        this.resetErrors();

        if (!this.isMultiple && files.length > 1) {
            this.error = true;
            this.errorMessage = 'error.one-file';
            return false;
        }

        for (const droppedFile of files) {
            if (droppedFile.fileEntry.isFile) {
                const fileEntry = droppedFile.fileEntry as FileSystemFileEntry;
                fileEntry.file((file: File) => {
                    if (this.validateFile(file)) {
                        this.pushFile(file);
                    }
                });
            } else {
                const fileEntry = droppedFile.fileEntry as FileSystemFileEntry;
            }
        }
    }

    public openDialog() {
        return this.inputFile.nativeElement.click();
    }

    public reset() {
        this.inputFile.nativeElement.value = '';
        this.resetErrors();

        while (this.uploads.length > 0) {
            this.removeFile(0);
        }
    }

    readFile(event: FileList) {
        if (event.length === 0) {
            return false;
        }

        this.resetErrors();
        const file = event[0];

        if (this.validateFile(file)) {
            this.pushFile(file);
            this.inputFile.nativeElement.value = '';
        }
    }

    hasFile (file) {
        return this.uploads.some(item => item.fileName === file.name && item.file.size === file.size);
    }

    validateFile(file: File): boolean {
        if (this.isMultiple && this.hasFile(file)) {
            this.error = true;
            this.errorMessage = 'upload.error.already-exist';
            return false;
        }

        if (!this.validateSize(file)) {
            this.error = true;
            this.errorMessage = 'upload.error.no-more' + this.size;
            return false;
        }

        if (!this.validateFileExtension(file)) {
            this.error = true;
            this.errorMessage = 'upload.error.extension-not-allowed';
            return false;
        }

        return true;
    }

    pushFile(file: File) {
        if (!this.isMultiple && this.uploads.length) {
            this.removeFile(0);
        }

        this.uploads.push(this.createDocumentUpload(file));
        this.onNewDocuments();
        this.touched = true;
    }

    /**
     * Move file to trash
     */
    removeFile(index: number) {
        const items = this.uploads.splice(index, 1);
        const item = items[0];

        if (item.id) {
            item.action = 2;
            this.deletedList.push(item);
        }
        this.touched = true;
    }

    createDocumentUpload(file: File): Upload {
        const doc = {
            id: null,
            fileName: file.name,
            date: this.formatDate(new Date()),
            status: 'Pending',
            visible: true,
            action: 1,
            file: file
        };

        return doc;
    }

    validateFileExtension(file): boolean {
        const extension = file.name.split('.').pop();

        if (this.allowedFiles.some(item => item === extension)) {
            return true;
        }
        return false;
    }

    validateSize(file): boolean {
        if (file.size < 5242880) {
            return true;
        }
        return false;
    }

    validateRequired() {
        if (this.required && this.touched && this.uploads.length === 0) {
            this.error = true;
            this.errorMessage = 'error.document-required';
        }
    }

    resetErrors() {
        this.error = false;
        this.errorMessage = '';
    }

    confirmDelete(index: number) {
        this.removeFile(index);
        this.validateRequired();
        this.onNewDocuments();
    }

    onDelete(index) {
        // TODO Confirmation of Delete
        /*
        const callback = () => this.confirmDelete(index);
        this.confirmAction.confirm(this.deleteWarning, callback);

        this.confirm$.confirm(message).subscribe(data => {
            data.status = true
        });
        */
        this.confirmDelete(index);
        this.deleting = true;
    }

    onNewDocuments() {
        this.newDocuments.emit(this.uploads);
        this.deleted.emit(this.deletedList);
    }

    onDownload(item) {
        // TODO Output download event
        // this.downloadUrl = this.initiativeService.downloadDocument(item.id);
    }

    public fileOver(event) {
        // console.log('fileOver: ', event);
    }

    public fileLeave(event) {
        // console.log('fileLeave: ', event);
    }

    private formatDate(date: Date): string {
        const day = '0' + date.getDate();
        const month = '0' + (date.getMonth() + 1);
        const year = date.getFullYear();

        return day.slice(0, 2) + '/' + month.slice(0, 2) + '/' + year;
    }

}
