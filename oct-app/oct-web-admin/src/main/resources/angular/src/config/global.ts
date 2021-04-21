import { GlobalConfig } from '@eui/core';

export const GLOBAL: GlobalConfig = {
    appTitle: 'eUI-app',
    languages: ['en', 'fr'],
    defaultLanguage: 'en',
    i18nFolders: ['i18n-eui', 'i18n'], // 'i18n-ecl'
    availableLanguages: [], // empty array to include all languages
    languageList: {
        bg: 'български (bg)',
        cs: 'čeština (cs)',
        da: 'dansk (da)',
        de: 'Deutsch (de)',
        et: 'eesti (et)',
        el: 'ελληνικά (el)',
        en: 'English (en)',
        es: 'español (es)',
        fr: 'français (fr)',
        ga: 'Gaeilge (ga)',
        hr: 'hrvatski (hr)',
        it: 'italiano (it)',
        lv: 'latviešu (lv)',
        lt: 'lietuvių (lt)',
        hu: 'magyar (hu)',
        mt: 'Malti (mt)',
        nl: 'Nederlands (nl)',
        pl: 'polski (pl)',
        pt: 'português (pt)',
        ro: 'română (ro)',
        sk: 'slovenčina (sk)',
        sl: 'slovenščina (sl)',
        fi: 'suomi (fi)',
        sv: 'svenska (sv)'
    }
};
