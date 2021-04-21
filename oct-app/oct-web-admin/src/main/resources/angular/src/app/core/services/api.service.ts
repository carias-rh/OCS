import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';

import { environment } from '../../../environments/environment';
import { Observable } from 'rxjs/Observable';

import { AuthService } from './auth.service';
import { Customisations } from '../models/customisations';
import { ProgressionStatus } from '../models/progressionStatus';

@Injectable()
export class ApiService {

    private host = environment.apiHost;

    private api = 'api/';
    private customFilesPath = this.api + 'customfiles/';
    private initiativePath = this.api + 'initiative';
    private signaturePath = this.api + 'signature';
    private reportPath = this.api + 'report';
    private managerPath = this.api + 'manager';
    private customisationPath = this.api + 'customisations';
    private captchaPath = this.api + 'captcha';
    private securityPath = this.api + 'security';
    private socialMediaPath = this.api + 'socialmedia';
    private requestTokenPath = this.api + 'token';
    private versionPath = this.api + 'version';
    private exportPath = this.api + 'export';

    constructor(
        private http: HttpClient,
        private authService: AuthService
    ) {}

    /**
     * Request token API
     */
    public getRequestToken() {
        const url = this.host + this.requestTokenPath + '/request';
        return this.http.get(url);
    }

    /**
     * INI02b
     */
    public insertXML(file: any) {
        const url = this.host + this.initiativePath + '/insertXML';
        const form = new FormData();

        form.append('file', file, file.name);
        form.append('type', 'description');

        return this.http.post(url, form, this.getAuthUploadHeaders());
    }

    /**
     * INI02. Get default initiative description
     */
    public getDefaultInititative() {
        const url = this.host + this.initiativePath + '/description';

        return this.http.get(url)
            .catch(error => this.handleError(error));
    }

    /**
     * INI03 - Get all initiative descriptions
     */
    public getLanguageDescriptions() {
        const url = this.host + this.initiativePath + '/alldescriptions';

        return this.http.get(url)
            .catch(error => this.handleError(error));
    }

    /**
     * INI05 - Get initiative description by language code
     */
    public getDescriptionByLanguage(languageCode: string) {
        const url = this.host + this.initiativePath + '/descriptionByLang/';

        return this.http.get(url + languageCode)
            .catch(error => this.handleError(error));
    }

    /**
     * INI06 - Get initiative contacts
     */
    public getInitiativeContacts() {
        const url = this.host + this.initiativePath + '/contacts/';

        return this.http.get(url)
            .map((data: any) => data.contactDTOlist)
            .catch(error => this.handleError(error));
    }

    /**
     * INI08 - Days left to the end of initiative
     */
    public getDaysLeft() {
        const url = this.host + this.initiativePath + '/daysleft';

        return this.http.get(url)
            .catch(error => this.handleError(error));
    }

    /**
     * INI09 - Available Languages
     */
    public getInitiativeLanguages() {
        const url = this.host + this.initiativePath + '/languages';

        return this.http.get(url)
            .map((data: any) => data.languages)
            .catch(error => this.handleError(error));
    }

    /**
     * SIG03 - Get metadata of the latest signatures by numeric range
     */
    public getLatestSignatures(range: number = 5) {
        const url = this.host + this.signaturePath + '/byNumericRange/' + range;

        return this.http.get(url)
            .map((data: any) => data.metadatas)
            .catch(error => this.handleError(error));
    }

    /**
     * SIG05 - Delete a specific statement
     *
     * @param id
     */
    public deleteSignature(id: string) {
        const url = this.host + this.signaturePath + '/delete';
        const body = {
            value: id
        };

        return this.http
            .post(url, body, this.getAuthHeaders())
            .catch(error => this.handleError(error));
    }

    /**
     * CUS01 - Show customisations
     */
    public getCustomizations(): Observable<Customisations> {
        const url = this.host + this.customisationPath + '/show';

        return this.http.get(url)
            .catch(error => this.handleError(error));
    }

    /**
     * CUS02 - Update customisations
     * @Secured
     */
    public updateCustomisations(custom: Customisations) {
        const url = this.host + this.customisationPath + '/update';

        return this.http
            .post(url, custom, this.getAuthHeaders())
            .catch(error => this.handleError(error));
    }

    /**
     * REP01 - Get progression status
     */
    public getProgessStatus(): Observable<ProgressionStatus> {
        const url = this.host + this.reportPath + '/progression';

        return this.http.get(url)
            .catch(error => this.handleError(error));
    }

    /**
     * REP02 - Get distribution map
     */
    getDistributionMap() {
        const url = this.host + this.reportPath + '/map';

        return this.http.get(url)
            .map((data: any) => data.signatureCountryCount)
            .catch(error => this.handleError(error));
    }

    /**
     * REP03 - Submit Feedback.
     *
     * Pending: There is a feedback list in other api service?
     */
    public submitFeedback(comment: string, level: string) {
        const url = this.host + this.reportPath + '/feedback';
        const now = new Date().toLocaleDateString('es', {
            day: '2-digit',
            month: '2-digit',
            year: 'numeric'
        });

        const feedbackRangeDTO = {
            label: level
        };

        const feedbackDTO = {
            feedbackComment: comment,
            feedbackRangeDTO: feedbackRangeDTO,
            feedbackDate: now
        };

        return this.http
            .post(url, feedbackDTO)
            .catch(error => this.handleError(error));
    }

    public getAllFeedbacks(start: number, offset: number) {
        const url = this.host + this.reportPath + '/feedbacks/' + start + '/' + offset;

        return this.http
            .get(url, this.getAuthUploadHeaders())
            .map((data: any) => data.feedbackDTOlist)
            .catch(error => this.handleError(error));
    }

    public getFeedbackStats() {
        const url = this.host + this.reportPath + '/feedbackStats';

        return this.http
            .get(url, this.getAuthUploadHeaders())
            .catch(error => this.handleError(error));
    }

    /**
     * REP04 - Evolution Map
     */
    public getEvolutionMap() {
        const url = this.host + this.reportPath + '/evolutionMap';

        return this.http.get(url, this.getAuthUploadHeaders())
            .map((data: any) => data.evolutionMapEntryDTOs)
            .catch(error => this.handleError(error));
    }

    /**
     * REP05 - Get top 7 distribution map
     */
    public getTop7DistributionMap(): Observable<any[]> {
        const url = this.host + this.reportPath + '/top7map';

        return this.http.get(url, this.getAuthUploadHeaders())
            .map((data: any) => data.signatureCountryCount)
            .catch(error => this.handleError(error));
    }

    /**
     * MAN01 - Get collecting state
     *
     * CollectionMode ON : Support form is available
     * CollectionMode OFF : Support form hidden, display message system unavailable
     */
    public getCollectingState() {
        const url = this.host + this.managerPath + '/collectorstate';

        return this.http.get(url)
            .catch(error => this.handleError(error));
    }

    /**
     * MAN02 - Set collecting state
     *
     * @param state
     */
    public setCollectingState(state: boolean) {
        const url = this.host + this.managerPath + '/collectorsetstate';
        const body = {
            collectionMode: state ? 'on' : 'off'
        };

        return this.http
            .post(url, body, this.getAuthHeaders())
            .catch(error => this.handleError(error));
    }

    /**
     * MAN03 - Get a list of all countries
     */
    public getAllCountries() {
        const url = this.host + this.managerPath + '/countries';

        return this.http.get(url)
            .map((data: any) => data.countries)
            .catch(error => this.handleError(error));
    }

    /**
     * MAN04 - Get a list of all languages
     */
    public getAllLanguages() {
        const url = this.host + this.managerPath + '/languages';

        return this.http.get(url)
            .map((data: any) => data.languages)
            .catch(error => this.handleError(error));
    }

    /**
     * MAN05 - Go online
     * @Secured
     */
    public goOnline() {
        const url = this.host + this.managerPath + '/goonline';

        return this.http
            .get(url, this.getAuthHeaders())
            .catch(error => this.handleError(error));
    }

    /**
     * MAN06 Get initialisation steps state
     * @Secured
     */
    public getInitializationSteps() {
        const url = this.host + this.managerPath + '/stepstate';

        return this.http
            .get(url, this.getAuthHeaders())
            .catch(error => this.handleError(error));
    }

    /**
     * MAN07 Set initialisation steps state
     *
     * step: "STRUCTURE","CERTIFICATE","PERSONALISE","SOCIAL","LIVE"
     * @Secured
     */
    public setInitializationStepState(step: string, active: boolean) {
        const url = this.host + this.managerPath + '/setstepstate';
        const postData = {
            step: step,
            active: active
        };

        return this.http
            .post(url, postData, this.getAuthHeaders())
            .catch(error => this.handleError(error));
    }

    /**
     * MAN08 - Get country property list
     */
    public getCountryProperties(countryCode: string) {
        const url = this.host + this.managerPath + '/countryProperties/' + countryCode;

        return this.http.get(url)
            .catch(error => this.handleError(error));
    }

    /**
     * SOC01 - Get social media message
     */
    public getSocialMediaMessage(socialMedia: string, language: string) {
        const url = this.host + this.socialMediaPath + '/' + socialMedia + '/' + language;

        return this.http.get(url)
            .catch(error => this.handleError(error));
    }

    public updateSocialMediaMessage(socialMedia: string, language: string, message: string) {
        const url = this.host + this.socialMediaPath + '/update';
        const data = {
            socialMedia: socialMedia,
            message: message,
            languageCode: language
        };

        return this.http
            .post(url, data, this.getAuthHeaders())
            .catch(error => this.handleError(error));
    }

    /**
     * CAP01 - Obtain image captcha
     */
    public getImageCaptcha() {
        const url = this.host + this.captchaPath + '/image';

        return this.http.get(url)
            .catch(error => this.handleError(error));
    }

    /**
     * CAP02 - Obtain audio captcha
     */
    public getAudioCaptcha() {
        const url = this.host + this.captchaPath + '/audio';

        return this.http.get(url)
            .catch(error => this.handleError(error));
    }

    /**
     * SEC01 - Obtain security challenge
     */
    public getSecurityChallenge() {
        const url = this.host + this.securityPath + '/challenge';

        return this.http.get(url)
            .catch(error => this.handleError(error));
    }

    /**
     * SEC02 - Authenticate
     *
     * Simulate authentication error with username = 'error'
     */
    public authenticate(username: string, password: string, result: string) {
        const url = this.host + this.securityPath + '/authenticate';
        const authenticationDTO = {
            user: username,
            pwd: password,
            challengeResult: result
        };

        return this.http
            .post(url, authenticationDTO)
            .catch(error => this.handleError(error));
    }

    public extendSession(authToken: string) {
        const url = this.host + this.securityPath + '/extendSession';

        return this.http
            .post(url, authToken, this.getAuthHeaders())
            .catch(error => this.handleError(error));
    }

    public logout(authToken: string) {
        const url = this.host + this.securityPath + '/logout';

        const authTokenDTO = {
            value: authToken
        };

        return this.http
            .post(url, authTokenDTO, this.getAuthHeaders())
            .catch(error => this.handleError(error));
    }

    /**
     * MAN09 - Get system state
     *
     * "mode": "ONLINE|OFFLINE"
     *
     * OFFLINE: Display login (support form in private mode)
     * ONLINE: Show support form, public access
     *
     */
    public getSystemState(): Observable<any> {
        const url = this.host + this.managerPath + '/mode';

        return this.http
            .get(url, this.getAuthHeaders())
            .catch(error => this.handleError(error));
    }

    /**
     * FIL01 - Download custom file
     *
     * type: certificate | logo
     *
     */
    public getDownloadCustomFile(type = 'certificate') {
        const url = this.host + this.customFilesPath + 'download/' + type;

        return url;
    }

    /**
     * FIL02 - Store Custom File
     * @secured
     *
     * type     logo|certificate|user_manual|description
     */
    public storeCustomFile(file: any, type: string = 'certificate') {
        const url = this.host + this.customFilesPath + 'uploadForm';
        const form = new FormData();

        form.append('file', file, file.name);
        form.append('type', type);

        return this.http
            .post(url, form, this.getAuthUploadHeaders())
            .catch(error => this.handleError(error));
    }

    /**
     * FIL03 - Download receipt
     *
     * Download the citizen’s statement of support receipt, containing the signature identifier, after a successful support
     */
    public getDownloadReceipt(uuid: string, languageCode: string) {
        const url = this.host + this.customFilesPath + 'receipt/' + uuid + '/' + languageCode;

        return url;
    }

    /**
     * FIL04 - Download encoded logo
     *
     * Download the citizen’s statement of support receipt, containing the signature identifier, after a successful support
     */
    public getDownloadEncodedLogo() {
        const url = this.host + this.customFilesPath + 'logo';

        return this.http.get(url)
            .catch(error => this.handleError(error));
    }

    /**
     * EXP01 - Export statements of support
     * @param countries
     * @param startDate
     * @param endDate
     * @Secured
     */
    public export(countries: string[], startDate: string = null, endDate: string = null) {
        const url = this.host + this.exportPath;
        const body = {
            countries: countries,
            startDate: startDate,
            endDate: endDate
        };

        return this.http
            .post(url, body, this.getAuthHeaders())
            .catch(error => this.handleError(error));
    }

    public getExportStatus() {
        const url = this.host + this.exportPath + '/status';

        return this.http
            .get(url, this.getAuthHeaders())
            .catch(error => this.handleError(error));
    }

    public getExportHistory() {
        const url = this.host + this.exportPath + '/history';

        return this.http
            .get(url, this.getAuthHeaders())
            .map((data: any) => data.exportHistoryList)
            .catch(error => this.handleError(error));
    }

    /**
     * EXP03 - Count statements of support
     *
     * @param countries
     * @param startDate
     * @param endDate
     * @Secured
     */
    public exportCountByCountry(countries: string[], startDate: string = null, endDate: string = null) {
        const url = this.host + this.exportPath + '/countByCountry';
        const body = {
            countries: countries,
            startDate: startDate,
            endDate: endDate
        };

        return this.http
            .post(url, body, this.getAuthHeaders())
            .catch(error => this.handleError(error));
    }

    public stopExport(uuid: string) {
        const url = this.host + this.exportPath + '/stop';
        const body = {
            value: uuid
        };

        return this.http
            .post(url, body, this.getAuthHeaders())
            .catch(error => this.handleError(error));
    }

    /**
     * OCS Last update
     */
    public getLastBuildDate() {
        const url = this.host + this.versionPath;

        return this.http.get(url)
            .catch(error => this.handleError(error));
    }

    // multipart/form-data
    public getAuthHeaders() {
        const headers = {
            'Content-Type': 'application/json',
            'Authorization': 'Bearer ' + this.authService.getSessionToken()
        };

        return this.getHeaders(headers);
    }

    public getAuthUploadHeaders() {
        const headers = {
            'Authorization': 'Bearer ' + this.authService.getSessionToken()
        };

        return this.getHeaders(headers);
    }

    private getHeaders(headers) {
        const httpOptions = {
            headers: new HttpHeaders(headers)
        };

        return httpOptions;
    }

    private handleError(error) {
        if (error.status === 401 && this.authService.isLoggedIn) {
            this.authService.forceLogout();
            this.logout(this.authService.getSessionToken()).subscribe(
                () => this.authService.forceLogout()
            );
        }

        if (error.status === 500) {
            // this.router.navigate(['/error']);
        }

        // Fix HttpErrorResponse with 200 OK
        if (error.status === 200 && error.statusText === 'OK') {
            return Observable.of(null);
        }

        return Observable.throw(error);
    }

}
