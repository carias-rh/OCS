export interface ExportHistory {
    jobId: string;
    exportDate: string;
    countriesParam: string;
    dateRangeParam: string;
    exportProgress: number;
    duration: string;
    batchStatus: string;
    exitStatus: any;
    exportSummary: string;
    validationProgress: number;
    validationSummary: string;
}
