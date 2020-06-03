/* Copyright (c) 2020, RTE (http://www.rte-france.com)
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */


import { Component, OnInit } from '@angular/core';
import {Observable, combineLatest} from 'rxjs';
import {Store} from '@ngrx/store';
import {AppState} from '@ofStore/index';
import {buildConfigSelector} from '@ofSelectors/config.selectors';
import { FormGroup, FormControl } from '@angular/forms';
import { SendArchiveQuery } from '@ofStore/actions/archive.actions';
import { DateTimeNgb } from '@ofModel/datetime-ngb.model';
import { NgbDateStruct, NgbTimeStruct } from '@ng-bootstrap/ng-bootstrap';
import { TimeService } from '@ofServices/time.service';
import { TranslateService } from '@ngx-translate/core';
import { map } from 'rxjs/operators';
import { UserService } from '@ofServices/user.service';

export enum FilterDateTypes {
  PUBLISH_DATE_FROM_PARAM = 'publishDateFrom',
  PUBLISH_DATE_TO_PARAM = 'publishDateTo',
  ACTIVE_FROM_PARAM = 'activeFrom',
  ACTIVE_TO_PARAM = 'activeTo'

}

export const checkElement = (enumeration: typeof FilterDateTypes, value: string): boolean => {
  let result = false;
  if (Object.values(enumeration).includes(value)) {
    result = true;
  }
  return result;
};

export const transformToTimestamp = (date: NgbDateStruct, time: NgbTimeStruct): string => {
  return new DateTimeNgb(date, time).formatDateTime();
};

@Component({
  selector: 'of-archive-filters',
  templateUrl: './archive-filters.component.html',
  styleUrls: ['./archive-filters.component.css']
})
export class ArchiveFiltersComponent implements OnInit {

  tags$: Observable<string []>;
  processes$: Observable<string []>;
  size$: Observable<number>;
  first$: Observable<number>;

  archiveForm: FormGroup;

  constructor(private store: Store<AppState>, private timeService: TimeService, private translateService: TranslateService, private userService: UserService) {
    this.archiveForm = new FormGroup({
      tags: new FormControl(''),
      process: new FormControl(),
      publishDateFrom: new FormControl(''),
      publishDateTo: new FormControl(''),
      activeFrom: new FormControl(''),
      activeTo: new FormControl(''),
    });
  }


  ngOnInit() {

    // -------- [OC-932] In the Archives filters, make a filter field visible or not based on the user group(s) -------- //
    this.store.select(buildConfigSelector('archive.filters.tags'))
      .subscribe(tags => {
        if (!tags['filter-by-group']) {
          this.tags$ = this.store.select(buildConfigSelector('archive.filters.tags.list'));
        } else {
          this.userService.getUserGroups().subscribe(
              groups => {
        
                console.log("GROUPS");
                console.log(groups);
                this.tags$ = this.store.select(buildConfigSelector('archive.filters.tags.list'))
                  .pipe(
                    map(tags => tags.filter(tag => tag.group ? groups.includes(tag.group) : true))
                  )
              },
              err => {
                console.error('ERROR' + err);
                this.tags$ = this.store.select(buildConfigSelector('archive.filters.tags.list'));
              }
            )
        }
      })

    // this.tags$ = this.store.select(buildConfigSelector('archive.filters.tags.list'));
    // ------------------------------------------------------------------------------------------------------------------ //

    this.processes$ = this.store.select(buildConfigSelector('archive.filters.process.list'));
    this.size$ = this.store.select(buildConfigSelector('archive.filters.page.size'));
    this.first$ = this.store.select(buildConfigSelector('archive.filters.page.first'));
  }

  /**
   * Transorm the filters list to Map
   */
  filtersToMap = (filters: any): Map<string, string[]> => {
    const params = new Map();
    Object.keys(filters).forEach(key => {
      const element = filters[key];
        // if the form element is date
      if (element) {
        if (checkElement(FilterDateTypes, key)) {
          const {date, time} = element;
          if (date) {
            const dateString = transformToTimestamp(date, time);
            params.set(key, [this.timeService.toNgBTimestamp(dateString)]);
          }
        } else {
          if (element.length) {
            params.set(key, element);
          }
        }
      }
    });
    return params;
  }

  sendQuery(): void {
    const {value} = this.archiveForm;
    const params = this.filtersToMap(value);
    this.size$.subscribe(size => params.set('size', [size.toString()]));
    this.first$.subscribe(first => params.set('page', [first.toString()]));
    this.store.dispatch(new SendArchiveQuery({params}));
  }

}

