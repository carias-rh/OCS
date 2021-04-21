import { OnInit, Component, Output, EventEmitter } from '@angular/core';
import { FormBuilder, FormGroup } from '@angular/forms';

import 'rxjs/add/observable/interval';
import { UxService, UxAppShellService } from '@eui/core';
import * as moment from 'moment';

import { InitiativeService } from '../../../core/services/initiative.service';
import { Initiative } from '../../../core/models/initiative';
import { ApiService } from '../../../core/services/api.service';
import { Observable } from 'rxjs/Observable';

enum AlertType {
    None = 0,
    Error = 1,
    Warning = 2,
    Info = 3,
    Success = 4,
    Stripped = 5
}

enum ExportStatus {
    NotModified = 304,
    NotAcceptable = 406,
    ExpectationFailed = 417,
    Conflict = 409
}

@Component({
    selector: 'ocs-countries-export',
    templateUrl: 'countries.component.html'
})
export class CountriesExportComponent implements OnInit {
    @Output() exported = new EventEmitter<boolean>();

    formExport: FormGroup;
    initiative: Initiative;

    countries: any[];
    selected: string[] = [];
    count: Object = [];
    loadingExport = false;
    loading = false;
    timeout = false;
    exportWritingStarted = false;
    stopConfirmation = false;
    loadingActivity: string;
    alert: {
        type: string,
        label: string
    };
    warning: string;

    progressbar: {
        enabled: boolean,
        class: string,
        width: string,
        value: number
    };

    totalSelected = 0;
    selectAll = true;
    totalCount = 0;
    totalSelectedLabel = this.totalSelected.toLocaleString();
    totalCountLabel = this.totalCount.toLocaleString();

    private STOP_EXPORT_MESSAGE_LABEL = 'dashboard.export.err.stop';
    private EXPORT_ERROR_GENERIC_LABEL = 'dashboard.export.err.500';
    private EXPORT_SUCCESS_MESSAGE_LABEL = 'dashboard.export.ok';
    private EXPORT_WARNING_SERVER_MESSAGE_LABEL = 'dashboard.export.warning-server';
    private EXPORT_INIT_MESSAGE_LABEL = 'dashboard.export.info-starting';
    private LOADING_MESSAGE_GENERIC = 'dashboard.export.loading';
    private EXPORTING_MESSAGE = 'dashboard.export.info-exporting';
    private VALIDATING_MESSAGE = 'dashboard.export.info-validating';
    private UPDATING_MESSAGE = 'dashboard.export.updating';

    private batchStatusStarted = 'STARTED';
    private batchStatusCompleted = 'COMPLETED';
    private batchStatusFailed = 'FAILED';
    private batchStatusStopped = 'STOPPED';

    private lastRunningExportUUID = '';
    private lastRunningExportInfo = null;

    constructor(
        private fb: FormBuilder,
        private service: InitiativeService,
        private api: ApiService,
        private uxService: UxService,
        private uxAppShellService: UxAppShellService
    ) {
        this.formBuild();
    }

    ngOnInit() {
        this.initLastExportProccess();

        this.service.getInitiative().subscribe(data => this.initiative = data);
        this.service.getCountries().subscribe(data => {
            this.countries = data;
        });
    }

    formBuild() {
        this.formExport = this.fb.group({
            'exportDateFrom': [null],
            'exportDateTo': [null],
        });

        this.formExport.valueChanges.subscribe(() => {
            this.resetProgressBar();
            this.updateCounts();
        });
    }

    get isProgressBar() {
        return this.progressbar && this.progressbar.enabled;
        // return true;
    }

    setInitiativeDates() {
        const today = new Date();
        let start = this.inputToDate(this.initiative.registrationDate);
        let end = this.inputToDate(this.initiative.closingDate);

        if (end.getTime() > today.getTime()) {
            end = today;
        }

        this.resetProgressBar();
        this.formExport.setValue({
            exportDateFrom: start,
            exportDateTo: end
        });
    }

    isExportDisabled() {
        return this.totalSelected === 0 || this.loadingExport || this.loading;
    }

    export() {
        if (this.isExportDisabled()) {
            return;
        }

        this.loadingExport = true;
        this.loading = true;
        this.timeout = true;
        let exportAPIreturn = false;

        let start = this.getStartDate();
        let end = this.getEndDate();

        this.initExportStatus();

        this.api.export(this.selected, start, end).subscribe(
            (data) => {
                this.getProgressInfo();
                this.getExportHistory();
            },
            (error) => {
                let exportStatus = error.status;
                let errorMessageLabel = 'dashboard.export.err.500';
                let style = AlertType.Error;

                let errorType = 'error';
                if (exportStatus === ExportStatus.NotModified) {
                    errorMessageLabel = 'dashboard.export.err.304';
                    errorType = 'warning';
                    style = AlertType.Warning;
                } else if (exportStatus === ExportStatus.Conflict) {
                    errorMessageLabel = 'dashboard.export.err.409';

                } else if (exportStatus === ExportStatus.NotAcceptable) {
                    errorMessageLabel = 'dashboard.export.err.406';
                } else if (exportStatus === ExportStatus.ExpectationFailed) {
                    errorMessageLabel = 'dashboard.export.err.417';
                }
                this.setAlert(style, errorMessageLabel);
                this.setProgressBarStyle(style, 100);
                this.loadingExport = false;
                this.loading = false;
                this.exportWritingStarted = false;
                exportAPIreturn = true;
                return;
            }
        );

    }

    initExportStatus() {
        if (this.loadingExport) {
            this.loadingActivity = this.LOADING_MESSAGE_GENERIC;
            this.setAlert(AlertType.Info, this.EXPORT_INIT_MESSAGE_LABEL);
            this.setProgressBarStyle(AlertType.Info, 0);
        }
    }

    setAlert(type: number, label: string) {
        let typeString = '';

        switch (type) {
        case AlertType.Info:
            typeString = 'info';
            break;

        case AlertType.Warning:
            typeString = 'warning';
            break;

        case AlertType.Error:
            typeString = 'error';
            break;

        case AlertType.Success:
            typeString = 'ok';
            break;

        default:
            this.alert = null;
            return;
        }

        this.alert = {
            type: typeString,
            label: label
        };
    }

    // Review
    stopExportConfirm() {
        this.uxService.openMessageBox('stopDialog');
        this.stopConfirmation = true;
    }

    // Review
    stopExport() {
        this.api.stopExport(this.lastRunningExportUUID).subscribe(
            (data) => {
                this.setExportStopped();
            },
            (error) => {
                this.setExportStopped();
            }
        );
    }

    // Review
    setExportFail() {
        // this.lastRunningExportUUID = '';
        this.setProgressBarStyle(AlertType.Error, 100);
        this.setAlert(AlertType.Error, this.EXPORT_ERROR_GENERIC_LABEL);
        this.getExportHistory();
        this.loadingExport = false;
        this.loading = false;
        this.exportWritingStarted = false;
    }

    // Review
    setExportWarning(percentage) {
        this.setProgressBarStyle(AlertType.Info, percentage);
        // this.setAlert(AlertType.Info, message);
        this.warning = this.EXPORT_WARNING_SERVER_MESSAGE_LABEL;
    }

    // Review
    setExportInfo(infoText, percentage) {
        this.setProgressBarStyle(AlertType.Info, percentage);
        this.setAlert(AlertType.Info, infoText);
    }

    // Review
    setExportSuccess() {
        this.setProgressBarStyle(AlertType.Success, 100);
        this.setAlert(AlertType.Success, this.EXPORT_SUCCESS_MESSAGE_LABEL);
        this.lastRunningExportUUID = '';
    }

    // Review
    setExportStopped() {
        this.loadingExport = false;
        this.loading = false;
        this.exportWritingStarted = false;
        this.setAlert(AlertType.Error, this.STOP_EXPORT_MESSAGE_LABEL);
        this.setProgressBarStyle(AlertType.Error, 100);
        this.lastRunningExportUUID = '';
        this.getExportHistory();
    }

    checkAllCountries() {
        if ( this.selectAll === false ) {
            this.selected = [];
            this.totalSelected = 0;
            this.totalSelectedLabel = this.totalSelected.toLocaleString();

        } else {
            this.selected = this.countries.map( country => country.id);
            this.totalSelected = this.totalCount;
            this.totalSelectedLabel = this.totalSelected.toLocaleString();
        }

        this.selectAll = !this.selectAll;

    }

    onSelectCountry(code) {
        if (!this.loadingExport) {
            this.resetProgressBar();
            if (this.isCountryChecked(code)) {
                const index = this.selected.indexOf(code);
                this.selected.splice(index, 1);
                this.totalSelected = this.totalSelected - this.getCount(code);
                this.totalSelectedLabel = this.totalSelected.toLocaleString();
            } else {
                this.selected.push(code);
                this.totalSelected = this.totalSelected + this.getCount(code);
                this.totalSelectedLabel = this.totalSelected.toLocaleString();
            }
        }
    }

    isCountryChecked(countryCode) {
        return this.selected.indexOf(countryCode) !== -1;
    }

    getCount(code) {
        if (this.count.hasOwnProperty(code) && this.count[code]) {
            return this.count[code];
        }

        return 0;
    }

    getCountFormatted(code) {
        if (this.count.hasOwnProperty(code) && this.count[code]) {
            return this.count[code].toLocaleString();
        }

        return 0;
    }

    openStopDialog() {
        this.uxService.openMessageBox('stopDialog');
    }

    onClickStopDialog(confirm) {
        if (confirm === true) {
            this.stopExport();
            this.stopConfirmation = false;
        } else {
            this.stopConfirmation = false;
        }
    }

    private resetProgressBar() {
        this.setProgressBarStyle(0, 0);
        this.setAlert(0, '');
    }

    private updateCounts() {
        if (!this.loadingExport) {
            let all = this.countries.map(country => country.id);
            let start = this.getStartDate();
            let end = this.getEndDate();

            this.totalCount = 0;
            this.totalSelected = 0;
            this.count = {};
            this.uxAppShellService.isBlockDocumentActive = true;

            this.service.getCountExportByContries(all, start, end).subscribe(
                data => {
                    data.list.forEach(item => {
                        this.count[item.countryCode] = item.count;
                        this.totalCount += item.count;
                        if (this.isCountryChecked(item.countryCode)) {
                            this.totalSelected += item.count;
                        }
                    });
                    this.totalSelectedLabel = this.totalSelected.toLocaleString();
                    this.totalCountLabel = this.totalCount.toLocaleString();
                    this.uxAppShellService.isBlockDocumentActive = false;
                },
                error => {
                    this.uxAppShellService.isBlockDocumentActive = false;
                }
            );
        }
    }

    private initLastExportProccess() {
        this.api.getExportStatus().subscribe(data => {
            this.lastRunningExportInfo = data;
            if (this.lastRunningExportInfo && this.lastRunningExportInfo.batchStatus === this.batchStatusStarted) {
                this.loadingExport = true;
                this.loading = true;
                this.initExportStatus();
                this.getProgressInfo();
            } else {
                this.loadingExport = false;
                this.loading = false;
            }
        });
    }

    // Review
    private getProgressInfo() {
        let firstHistoryRefresh = true;
        let sameProgressInfoCounter = 0;
        let progressInfoSubscription = Observable.interval(3000)
            .switchMap(() => this.api.getExportStatus())
            .subscribe((data) => {
                this.lastRunningExportUUID = data.jobId;

                if (this.lastRunningExportInfo == null) {
                    this.lastRunningExportInfo = data;
                }

                let exportPercentage = data.exportProgress;
                let exportSummary = data.exportSummary;
                let validationPercentage = data.validationProgress;
                let validationSummary = data.validationSummary;

                // failure
                if (exportSummary === this.batchStatusFailed ||
                    validationSummary === this.batchStatusFailed ||
                    data.batchStatus === this.batchStatusFailed) {
                    progressInfoSubscription.unsubscribe();
                    return;
                }

                // stopped by the user
                if (data.batchStatus === this.batchStatusStopped) {
                    this.setExportStopped();
                    progressInfoSubscription.unsubscribe();
                    return;
                }

                // completed
                if (data.batchStatus === this.batchStatusCompleted) {
                    progressInfoSubscription.unsubscribe();
                    this.setExportSuccess();
                    this.loadingExport = false;
                    this.loading = false;
                    this.exportWritingStarted = false;
                    sameProgressInfoCounter = 0;
                    this.getExportHistory();
                    return;
                }

                // on going
                let batchStatus = '0 / 0';
                if (data.exportSummary) {
                    batchStatus = exportSummary;
                    this.exportWritingStarted = true;
                    this.loadingActivity = this.EXPORTING_MESSAGE;
                    if (firstHistoryRefresh) {
                        this.getExportHistory();
                        firstHistoryRefresh = false;
                    }
                }

                let stepPercentage = exportPercentage;
                if (exportPercentage === 100) {
                    if (validationSummary) {
                        batchStatus = validationSummary;
                        this.loadingActivity = this.VALIDATING_MESSAGE;
                    }
                    stepPercentage = validationPercentage;
                }

                if (validationPercentage === 100) {
                    stepPercentage = 100;
                }

                // server stuck management

                if (this.sameProgress(data) && this.loadingExport) {
                    sameProgressInfoCounter++;
                } else {
                    sameProgressInfoCounter = 0;
                }

                if (sameProgressInfoCounter > 10) {
                    if (sameProgressInfoCounter < 20) {
                        this.setExportWarning(stepPercentage);
                    } else {
                        this.setExportFail();
                        this.stopExport();
                        sameProgressInfoCounter = 0;
                        this.loadingExport = false;
                        this.loading = false;
                        this.getExportHistory();
                        this.exportWritingStarted = false;
                        this.getExportHistory();
                        return;
                    }
                }

                let tempStatusSplit = batchStatus.split('/');
                let statusOngoing = (+tempStatusSplit[0]).toLocaleString();
                let statusToDo = (+tempStatusSplit[1]).toLocaleString();
                let statusMessageLocalized = statusOngoing + ' / ' + statusToDo;

                let statusInfoMessage = statusMessageLocalized + ' (' + stepPercentage + '%)';
                this.setExportInfo(statusInfoMessage, stepPercentage);
            });
    }

    // Review
    private sameProgress(data) {
        let exportPercentage = data.exportProgress;
        let exportSummary = data.exportSummary;
        let validationPercentage = data.validationProgress;
        let validationSummary = data.validationSummary;

        if (exportPercentage !== this.lastRunningExportInfo.exportProgress && exportPercentage != null) {
            return false;
        }
        if (exportSummary !== this.lastRunningExportInfo.exportSummary && exportSummary != null) {
            return false;
        }
        if (validationPercentage !== this.lastRunningExportInfo.validationProgress && validationPercentage != null) {
            return false;
        }
        if (validationSummary !== this.lastRunningExportInfo.validationSummary && validationSummary != null) {
            return false;
        }

        return true;
    }

    private setProgressBarStyle(style: number, barPercentage: number) {
        const classes = [null, 'danger', 'warning', 'info', 'success', 'striped'];
        this.progressbar = {
            enabled: style !== 0,
            class: 'progress-bar-' + classes[style],
            width: barPercentage + '%',
            value: barPercentage
        };
    }

    // Review : Actualizar el listado inferior de history
    private getExportHistory() {
        this.exported.emit(true);
    }

    private inputToDate(inputValue: any): Date {
        const date = moment(inputValue, 'DD-MM-YYYY');
        return date.toDate();
    }

    private getStartDate(): string {
        const startDate = this.formExport.get('exportDateFrom').value;

        if (startDate) {
            return moment(startDate).format('DD/MM/YYYY');
        }

        return null;
    }

    private getEndDate(): string {
        const endDate = this.formExport.get('exportDateTo').value;

        if (endDate) {
            return moment(endDate).format('DD/MM/YYYY');
        }

        return null;
    }
}
