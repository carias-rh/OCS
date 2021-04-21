import { Component, OnInit, ViewEncapsulation, ViewChild } from '@angular/core';
import { HistoryExportComponent } from './history/history.component';

@Component({
    templateUrl: './export.component.html',
    styleUrls: ['./export.component.scss'],
    encapsulation: ViewEncapsulation.None
})
export class ExportComponent implements OnInit {
    @ViewChild('history') history: HistoryExportComponent;

    constructor() {}

    ngOnInit() {}

    OnExport() {
        this.history.forceRefresh();
    }
}
