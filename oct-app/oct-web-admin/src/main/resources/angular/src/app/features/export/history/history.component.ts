import { Component, OnInit, Input } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';
import { ApiService } from '../../../core/services/api.service';
import { ExportHistory } from '../../../core/models/exporthistory.model';

@Component({
    selector: 'ocs-history-export',
    templateUrl: 'history.component.html',
})

export class HistoryExportComponent implements OnInit {

    @Input() data: ExportHistory[];

    constructor(private translate: TranslateService, private api: ApiService) {
        translate.onLangChange.subscribe(() => this.forceRefresh());
    }

    ngOnInit() {
        this.init();
        this.forceRefresh();
    }

    init() {
    }

    forceRefresh() {
        this.api.getExportHistory().subscribe(data => {
            if (data.length > 0) {
                this.data = data;
                this.init();
            }
        });
    }
}
