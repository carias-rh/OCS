import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs/Observable';

import { environment } from '../../../environments/environment';
import { Progression } from '../models/progression.model';

@Injectable()
export class ApiOcsService {
    api = environment.apiBaseUrl;

    private customFilesPath = this.api + '/customfiles';
    private initiativePath = this.api + '/initiative';
    private signaturePath = this.api + '/signature';
    private reportPath = this.api + '/report';
    private managerPath = this.api + '/manager';
    private customisationPath = this.api + '/customisations';
    private captchaPath = this.api + '/captcha';
    private securityPath = this.api + '/security';
    private socialMediaPath = this.api + '/socialmedia';
    private requestTokenPath = this.api + '/token';
    private versionPath = this.api + '/version';
    private emailPath = this.api + '/email';

    constructor(private http: HttpClient) {}

    /**
     * INI02. Get default initiative description
     */
    public getDefaultInititative(): Observable<any> {
        let url = this.initiativePath + '/description';

        return this.http.get(url);
    }

    /**
     * INI03 - Get all initiative descriptions
     */
    public getLanguageDescriptions(): Observable<any> {
        let url = this.initiativePath + '/alldescriptions';

        return this.http.get(url);
    }

    /**
     * INI05 - Get initiative description by language code
     */
    public getDescriptionByLanguage(languageCode: string) {
        let url = this.initiativePath + '/descriptionByLang/';

        return this.http.get(url + languageCode);
    }

    /**
     * INI06 - Get initiative contacts
     */
    public getInitiativeContacts() {
        let url = this.initiativePath + '/contacts/';

        return this.http.get(url)
            .map((data: any) => data.contactDTOlist);
    }

    /**
     * INI09 - Available Languages
     */
    public getInitiativeLanguages() {
        let url = this.initiativePath + '/languages';

        return this.http.get(url)
            .map((data: any) => data.languages);
    }

    /**
     * SIG03 - Get metadata of the latest signatures by numeric range
     */
    public getLatestSignatures() {
        let url = this.signaturePath + '/lastSignatures';

        return this.http.get(url)
            .map((data: any) => data.metadatas);
    }

    /**
     * SIG04 - Insert signature
     */
    public insertSignature(country: string, properties: any[], captcha: any, optionalValidation = false) {
        let url = this.signaturePath + '/insertC';

        let captchaValidationDTO = {
            id: captcha.id,
            type: captcha.type,
            value: captcha.value
        };

        let signatureDTO = {
            country: country,
            properties: properties,
            optionalValidation: optionalValidation
        };

        let signatureCaptchaDTO = {
            signatureDTO: signatureDTO,
            captchaValidationDTO: captchaValidationDTO
        };

        return this.http.post(url, signatureCaptchaDTO);
    }

    /**
     * CUS01 - Show customisations
     */
    public getCustomizations() {
        const url = this.customisationPath + '/show';

        return this.http.get(url);
    }

    /**
     * REP01 - Get progression status
     */
    public getProgessStatus(): Observable<Progression> {
        const url = this.reportPath + '/progression';

        return this.http.get<Progression>(url);
    }

    /**
     * REP02 - Get distribution map
     */
    getDistributionMap() {
        const url = this.reportPath + '/map';

        return this.http.get(url)
            .map((data: any) => data.signatureCountryCount);
    }

    /**
     * REP03 - Submit Feedback.
     *
     * Pending: There is a feedback list in other api service?
     */
    public submitFeedback(comment: string, range: string, signatureIdentifier: string) {
        const url = this.reportPath + '/feedback';

        const now = new Date().toLocaleDateString('es', {
            day: '2-digit',
            month: '2-digit',
            year: 'numeric'
        });

        const feedbackDTO = {
            comment: comment,
            range: range,
            date: now,
            signatureIdentifier: signatureIdentifier
        };

        return this.http.post(url, feedbackDTO);
    }

    /**
     * MAN01 - Get collecting state
     *
     * CollectionMode ON : Support form is available
     * CollectionMode OFF : Support form hidden, display message system unavailable
     */
    public getCollectingState() {
        const url = this.managerPath + '/collectorstate';

        return this.http.get(url);
    }

    /**
     * MAN03 - Get a list of all countries
     */
    public getAllCountries() {
        const url = this.managerPath + '/countries';

        return this.http.get(url)
            .map((data: any) => data.countries);
    }

    /**
     * MAN04 - Get a list of all languages
     */
    public getAllLanguages() {
        const url = this.managerPath + '/languages';

        return this.http.get(url)
            .map((data: any) => data.languages);
    }

    /**
     * MAN08 - Get country property list
     */
    public getCountryProperties(countryCode: string) {
        const url = this.managerPath + '/countryProperties/' + countryCode;

        return this.http.get(url);
    }

    /**
     * SOC01 - Get social media message
     */
    public getSocialMediaMessage(socialMedia: string, language: string) {
        const url = this.socialMediaPath + '/' + socialMedia + '/' + language;

        return this.http.get(url);
    }

    /**
     * CAP01 - Obtain image captcha
     */
    public getImageCaptcha() {
        const url = this.captchaPath + '/image';

        return this.getNoCache(url);
    }

    /**
     * CAP02 - Obtain audio captcha
     */
    public getAudioCaptcha() {
        const url = this.captchaPath + '/audio';

        return this.getNoCache(url);
    }

    /**
     * SEC01 - Obtain security challenge
     */
    public getSecurityChallenge() {
        const url = this.securityPath + '/challenge';

        return this.http.get(url);
    }

    /**
     * SEC02 - Authenticate
     *
     * Simulate authentication error with username = 'error'
     */
    public authenticate(username: string, password: string, result: string) {
        let url = this.securityPath + '/authenticate';

        let authenticationDTO = {
            user: username,
            pwd: password,
            challengeResult: result
        };

        return this.http.post(url, authenticationDTO);
    }

    /**
     * MAN09 - Get system state
     * "initialized": boolean,
     * "collecting": boolean,
     * "online": boolean
     */
    public getSystemState(): Observable<{}> {
        const url = this.managerPath + '/state';

        return this.http.get(url);
    }

    /**
     * FIL03 - Download receipt
     *
     * Download the citizen’s statement of support receipt, containing the signature identifier, after a successful support
     */
    public getDownloadReceipt(uuid: string, languageCode: string) {
        const url = this.customFilesPath + '/receipt/' + uuid + '/' + languageCode;

        return url;
    }

    /**
     * FIL01 - Download custom file
     *
     * type: certificate | logo
     */
    public getDownloadCustomFile(type = 'certificate') {
        const url = this.customFilesPath + '/download/' + type;

        return url;
    }

    /**
     * FIL04 - Download encoded logo
     *
     * Download the citizen’s statement of support receipt, containing the signature identifier, after a successful support
     */
    public getDownloadEncodedLogo() {
        const url = this.customFilesPath + '/logo';

        return this.http.get(url);
    }

    /**
     * OCS Last update
     */
    public getLastBuildDate() {
        let url = this.versionPath;

        return this.http.get(url);
    }

    public getNoCache(url: string): Observable<Object> {
        let date = new Date();
        url = url + '?_=' + date.getTime();

        return this.http.get(url);
    }

    /* OCS Stay Informed */
    public saveEmail(email: string, languageCode: string, signatureIdentifier: string, initiativeSubscription: boolean) {
        let EmailDTO = {
            emailAddress: email,
            comunicationLanguage: languageCode,
            signatureIdentifier: signatureIdentifier,
            initiativeSubscription: initiativeSubscription
        };

        return this.http.post(this.emailPath, EmailDTO);
    }

}
