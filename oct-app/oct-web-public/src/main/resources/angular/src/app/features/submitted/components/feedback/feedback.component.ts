import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup } from '@angular/forms';

import { ApiOcsService } from '../../../../core/services/api-ocs.service';
import { ApplicationService } from '../../../../core/services/application.service';

@Component ({
    selector: 'app-feedback',
    templateUrl : './feedback.component.html',
})
export class FeedbackComponent implements OnInit {
    options = ['bad', 'fair', 'fine', 'good'];
    feedbackMaxLength = 300;
    defaultExperience = 'good';
    form: FormGroup;
    error = false;
    feedbackSubmitted = false;
    isBlocked = false;

    constructor(private service: ApiOcsService, private fb: FormBuilder, private applicationService: ApplicationService) {
        this.form = this.fb.group({
            experience: [this.defaultExperience],
            message: [null]
        });
    }

    public ngOnInit() {}

    public onSubmitFeedback() {
        this.isBlocked = true;
        this.error = false;

        const message = this.form.value.message;
        const experience = this.form.value.experience;
        this.service.submitFeedback(message, experience, this.applicationService.signatureIdentifier).subscribe(
            (res: any) => {
                if (+res.code === 200 && res.status === 'SUCCESS') {
                    this.isBlocked = false;
                    this.feedbackSubmitted = true;
                }
            },
            () => {
                this.feedbackSubmitted = true;
                this.error = true;
                this.isBlocked = false;
            }
        );
    }

    public isSubmittedOK(): boolean {
        return this.feedbackSubmitted && !this.error;
    }

    public isSubmittedError(): boolean {
        return this.feedbackSubmitted && this.error;
    }

}
