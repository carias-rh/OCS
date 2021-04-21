import { Component, OnInit, Input } from '@angular/core';

import { InitiativeService } from '../../core/services/initiative.service';

@Component({
    selector: 'app-title',
    templateUrl: 'title.component.html'
})
export class TitleComponent implements OnInit {
    // Check or close
    @Input() hasInitiativeTitle = false;
    @Input() title: string = null;
    @Input() subtitle: string = null;
    @Input() icon: string = '';

    get hasTitle() {
        if (this.hasInitiativeTitle) {
            return false;
        }

        return this.title && this.title.length > 0;
    }

    get hasSubtitle() {
        if (this.hasInitiativeTitle) {
            return false;
        }

        return this.subtitle && this.subtitle.length > 0;
    }

    get initiativeTitle() {
        let title = '';

        try {
            title = this.service.initiative.description.title;
        } catch (error) {
        }

        return title;
    }

    get styleIcon() {
        return 'title-' + this.icon;
    }

    constructor(private service: InitiativeService) {}

    ngOnInit() {}

}
