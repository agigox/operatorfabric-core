

import {Injectable} from '@angular/core';
import {HttpClient, HttpParams, HttpUrlEncodingCodec} from '@angular/common/http';
import {environment} from '@env/environment';
import {from, Observable, of, throwError} from 'rxjs';
import {TranslateLoader} from '@ngx-translate/core';
import {catchError, filter, map, reduce, switchMap, tap} from 'rxjs/operators';
import {LightCard} from '@ofModel/light-card.model';
import {Action, Third, ThirdMenu, ResponseBtnColorEnum} from '@ofModel/thirds.model';
import {Card} from '@ofModel/card.model';

@Injectable()
export class ThirdsService {
    readonly thirdsUrl: string;
    private urlCleaner: HttpUrlEncodingCodec;
    private thirdCache = new Map();

    constructor(private httpClient: HttpClient,
    ) {
        this.urlCleaner = new HttpUrlEncodingCodec();
        this.thirdsUrl = `${environment.urls.thirds}`;
    }

    queryThirdFromCard(card: Card): Observable<Third> {
        return this.queryThird(card.publisher, card.publisherVersion);
    }

    queryThird(thirdName: string, version: string): Observable<Third> {
        const key = `${thirdName}.${version}`;
        const third = this.thirdCache.get(key);
        if (third) {
            return of(third);
        }
        return this.fetchThird(thirdName, version)
            .pipe(
                tap(t => {
                    if (t) {
                        Object.setPrototypeOf(t, Third.prototype);
                    }
                }),
                tap(t => {
                    if (t) {
                        this.thirdCache.set(key, t);
                    }
                })
            );
    }

    private fetchThird(publisher: string, version: string): Observable<Third> {
        const params = new HttpParams()
            .set('version', version);
        return this.httpClient.get<Third>(`${this.thirdsUrl}/${publisher}/`, {
            params
        });
    }
    queryMenuEntryURL(thirdMenuId: string, thirdMenuVersion: string, thirdMenuEntryId: string): Observable<string> {
        return this.queryThird(thirdMenuId, thirdMenuVersion).pipe(
            switchMap(third => {
                const entry = third.menuEntries.filter(e => e.id === thirdMenuEntryId);
                if (entry.length === 1) {
                    return entry;
                } else {
                    throwError(new Error('No such menu entry.'));
                }
            }),
            catchError((err, caught) => {
                console.log(err);
                return throwError(err);
            }),
            map(menuEntry => menuEntry.url)
        );
    }
    
    fetchHbsTemplate(publisher: string, version: string, name: string, locale: string): Observable<string> {
        const params = new HttpParams()
            .set('locale', locale)
            .set('version', version);
        return this.httpClient.get(`${this.thirdsUrl}/${publisher}/templates/${name}`, {
            params,
            responseType: 'text'
        });
    }

    computeThirdCssUrl(publisher: string, styleName: string, version: string) {
        // manage url character encoding
        const resourceUrl = this.urlCleaner.encodeValue(`${this.thirdsUrl}/${publisher}/css/${styleName}`);
        const versionParam = new HttpParams().set('version', version);
        return `${resourceUrl}?${versionParam.toString()}`;
    }

    private convertJsonToI18NObject(locale, publisher: string, version: string) {
        return r => {
            const object = {};
            object[publisher] = {};
            object[publisher][version] = r;
            return object;
        };
    }

    askForI18nJson(publisher: string, locale: string, version?: string): Observable<any> {
        let params = new HttpParams().set('locale', locale);
        if (version) {
            /*
            `params` override needed otherwise only locale is use in the request.
            It's so because HttpParams.set(...) return a new HttpParams,
            and basically that's why HttpParams can be set with fluent API...
             */
            params = params.set('version', version);
        }
        return this.httpClient.get(`${this.thirdsUrl}/${publisher}/i18n`, {params})
            .pipe(
                map(this.convertJsonToI18NObject(locale, publisher, version))
                , catchError(error => {
                    console.error(`error trying fetch i18n of '${publisher}' version:'${version}' for locale: '${locale}'`);
                    return error;
                })
            );
    }

    computeThirdsMenu(): Observable<ThirdMenu[]> {
        return this.httpClient.get<Third[]>(`${this.thirdsUrl}/`).pipe(
            switchMap(ts => from(ts)),
            filter((t: Third) => !(!t.menuEntries)),
            map(t =>
                new ThirdMenu(t.name, t.version, t.i18nLabelKey, t.menuEntries)
            ),
            reduce((menus: ThirdMenu[], menu: ThirdMenu) => {
                menus.push(menu);
                return menus;
            }, [])
        );
    }


    fetchActionMapFromLightCard(card: LightCard) {
        return this.fetchActionMap(card.publisher, card.process, card.state, card.publisherVersion);
    }

    fetchActionMap(publisher: string, process: string, state: string, apiVersion?: string) {
        let params: HttpParams;
        if (apiVersion) {
            params = new HttpParams().set('apiVersion', apiVersion);
        }
        return this.httpClient.get(`${this.thirdsUrl}/${publisher}/${process}/${state}/actions`, {
            params,
            responseType: 'text'
        }).pipe(map((json: string) => {
            // json empty in this case no action
            if (json.length > 1) {
                const obj = JSON.parse(json);
                return new Map<string, Action>(Object.entries(obj));
            }
            return new Map<string, Action>();
        }));
    }
    getResponseBtnColorEnumValue(responseBtnColorEnum: ResponseBtnColorEnum): string {
        switch (responseBtnColorEnum) {
            case 'RED':
                return 'btn-danger';
            case 'GREEN':
                return 'btn-success'
            case 'YELLOW':
                return 'btn-warning';
            default:
                return 'btn-success';
        }
    }
}

export class ThirdsI18nLoader implements TranslateLoader {

    constructor(thirdsService: ThirdsService) {
    }

    getTranslation(lang: string): Observable<any> {
        return of({});
    }

}

export function ThirdsI18nLoaderFactory(thirdsService: ThirdsService): TranslateLoader {
    return new ThirdsI18nLoader(thirdsService);
}
