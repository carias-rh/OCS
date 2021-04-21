import { Component, OnInit, OnDestroy } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';
import { InitiativeService } from '../../../../core/services/initiative.service';
import { Subscription } from 'rxjs/Subscription';

@Component({
    selector: 'app-shared-social',
    templateUrl: 'shared-social.component.html'
})
export class SharedSocialComponent implements OnInit, OnDestroy {

    callForActionMessage: string;
    twitterMessage: string;
    langChangeSubscription: Subscription;

    constructor(private service: InitiativeService, private translateService: TranslateService) {
        this.langChangeSubscription = translateService.onLangChange.subscribe(() => this.loadData());
    }

    get callbackUrl() {
        return this.service.cachedCustomisations.callbackUrl;
    }

    get twitterEncodedMessage() {
        return this.encode(this.twitterMessage);
    }

    get locationURL() {
        return encodeURIComponent(location.href);
    }

    ngOnInit() {
        this.loadData();
    }

    ngOnDestroy() {
        if (this.langChangeSubscription) {
            this.langChangeSubscription.unsubscribe();
        }
    }

    public encode(message: string) {
        return encodeURIComponent(message);
    }

    private loadData() {
        this.service.getCallForActionMessage().subscribe(data => this.callForActionMessage = data['message']);
        this.service.getTwitterMessage().subscribe(data => this.twitterMessage = data['message']);
    }

}
