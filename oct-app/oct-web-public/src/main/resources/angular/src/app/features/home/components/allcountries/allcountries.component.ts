import { Component, OnInit, ViewChild, ElementRef } from '@angular/core';

import { TranslateService } from '@ngx-translate/core';
import { UxAppShellService } from '@eui/core';

declare var jQuery: any;

import { InitiativeService } from '../../../../core/services/initiative.service';
import { LanguageService } from 'src/app/core/services/language.service';

import { CountryMap } from '../../../../core/models/countryMap.model';

@Component({
    templateUrl: './allcountries.component.html'
})
export class AllcountriesComponent implements OnInit {
    @ViewChild('map') map: ElementRef;

    mappingCode: Object;
    europeMapCodes: string[];
    distributionMap: CountryMap[] = [];
    countryList: CountryMap[] = [];
    total: number = 0;
    loadingMap: boolean = true;
    isMobile = false;
    showMap = true;
    showLegend = true;

    constructor(
        private iniciativeService: InitiativeService,
        private translate: TranslateService,
        private asService: UxAppShellService,
        private languageService: LanguageService
    ) {
        this.languageService.titleText = 'common.head-title.all-countries';
        this.languageService.setMetadata();

        this.europeMapCodes = [
            'at', 'be', 'bg', 'hr', 'cy', 'cz', 'dk', 'ee', 'fi', 'fr', 'de', 'gr', 'hu', 'ie',
            'it', 'lv', 'lt', 'lu', 'mt', 'nl', 'pl', 'pt', 'ro', 'sk', 'si', 'es', 'se', 'gb'
        ];

        // Mapping "country code in OCS" TO "country code in jVectorMap"
        this.mappingCode = {
            'ge': 'de',
            'uk': 'gb',
            'el': 'gr'
        };

        // Detected breakpoint
        this.asService.breakpoint$.subscribe(bkp => this.onBreakpointChange(bkp));

    }

    ngOnInit() {
        this.getDistributionMap();
        this.translate.onLangChange.subscribe(() => this.orderCountryList());
    }

    onBreakpointChange(bkp: string) {
        if ( bkp === 'sm' || bkp === 'xs') {
            this.isMobile = true;
            this.showMap = false;
            this.showLegend = false;
        } else {
            this.isMobile = false;
            this.showMap = true;
            this.showLegend = true;
        }
    }

    public labelStatus(country: CountryMap) {
        if (country.count === 0) {
            return 'map.status_any';
        }
        if (country.count < country.threshold) {
            return 'map.status_bellow';
        }
        if (country.count > country.threshold) {
            return 'map.status_reached';
        }

        return null;
    }

    private getDistributionMap() {
        this.iniciativeService.getDistributionMap().subscribe( (data: any[]) => {
            this.distributionMap = data;
            this.orderCountryList();
            this.initializeMap();
            data.forEach((item, index) => {
                this.total += item.count;
            });
        });
    }

    private orderCountryList() {
        this.distributionMap.forEach(item => item.countryName = this.translate.instant(item.label));
        this.countryList = this.distributionMap.sort(this.sortBy('countryName'));
    }

    private sortBy(property: string) {
        return (a, b) => {
            if (a[property] > b[property]) {
                return 1;
            }
            if (a[property] < b[property]) {
                return -1;
            }

            return 0;
        };
    }

    private initializeMap() {
        jQuery(this.map.nativeElement).vectorMap({
            map: 'europe_en',
            backgroundColor: 'white',
            borderColor: 'white',
            borderOpacity: 0.25,
            borderWidth: 1,
            color: '#dddddd',
            colors: this.getMapValues(),
            enableZoom: false,
            hoverColor: '#fdd0ce',
            hoverOpacity: null,
            normalizeFunction: 'linear',
            selectedColor: null,
            selectedRegions: null,
            showTooltip: true,
            onLabelShow: (event, label, code) => {
                this.tooltipSupporter(event, label, code);
            },
        });
    }

    private tooltipSupporter(event, label, code) {
        if (this.isEurope(code)) {
            let country = this.getCountryMap(code);
            if (!country) {
                country = new CountryMap(code);
            }
            const countryName = this.translate.instant(country.label);
            const text1 = this.translate.instant('map.statements-ocs');
            const text2 = this.translate.instant('map.threshold');
            const text3 = this.translate.instant('map.percentage');

            label.html(
                '<div class="ocs-maps-tip">' +
                '<span class="ocs-maps-tip--title">' + countryName + '</span>' +
                '<p><strong>' + text1 + '</strong>: <span>' + country.count.toLocaleString() + '</span></p>' +
                '<p><strong>' + text2 + '</strong>: <span>' + country.treshold.toLocaleString() + '</span></p>' +
                '<p><strong>' + text3 + '</strong>: <span>' + country.percentage + '</span>%</p>' +
                '</div>'
            );
        } else {
            event.preventDefault();
        }
    }

    private getMapValues() {
        let europe = {};

        // Default count 0 for all Europe countries
        this.europeMapCodes.forEach(code => {
            europe[code] = '#bd3530';
        });

        this.countryList.forEach(item => {
            if (item.count === 0) {
                europe[item.codeMap] = '#bd3530';
            } else if (item.count < item.treshold) {
                europe[item.codeMap] = '#51a1d1';
            } else {
                europe[item.codeMap] = '#1e6a68';
            }
        });

        return europe;
    }

    private isEurope(mapCode) {
        if (this.europeMapCodes.indexOf(mapCode) === -1) {
            return false;
        }

        return true;
    }

    private getCountryMap(codeMap) {
        let country = this.countryList.filter(item => item.codeMap === codeMap);
        if (country.length > 0) {
            return country[0];
        }

        return null;
    }

}
