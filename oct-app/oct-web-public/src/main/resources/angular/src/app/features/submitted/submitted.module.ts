import { NgModule } from '@angular/core';

import { SharedModule } from '../../shared/shared.module';
import { SubmittedRoutingModule } from './submitted-routing.module';

import { SubmittedComponent } from './components/submitted.component';
import { SignatureIdentifierComponent } from './components/signature-identifier/signature-identifier.component';
import { SharedSocialComponent } from './components/shared-social/shared-social.component';
import { FeedbackComponent } from './components/feedback/feedback.component';

@NgModule({
    imports: [
        SharedModule,
        SubmittedRoutingModule

    ],
    declarations: [
        SubmittedComponent,
        SignatureIdentifierComponent,
        SharedSocialComponent,
        FeedbackComponent,
    ],
    providers: [
    ],
})
export class SubmittedModule {}
