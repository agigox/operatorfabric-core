/* Copyright (c) 2018-2020, RTE (http://www.rte-france.com)
 * See AUTHORS.txt
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 * This file is part of the OperatorFabric project.
 */

/* Copyright (c) 2020, RTE (http://www.rte-france.com)
 * See AUTHORS.txt
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 * This file is part of the OperatorFabric project.
 */

import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {UserCardComponent} from './usercard.component';
import {UserCardRoutingModule} from './usercard-routing.module';
import {TranslateModule} from '@ngx-translate/core';
import {FlatpickrModule} from 'angularx-flatpickr';
import {ArchivesModule} from '../archives/archives.module';
import {SingleFilterModule} from '../../components/share/single-filter/single-filter.module';
import {DatetimeFilterModule} from '../../components/share/datetime-filter/datetime-filter.module';
import {TextAreaModule} from '../../components/share/text-area/text-area.module';
import {CardsModule} from '../cards/cards.module';
import {NgbModule} from '@ng-bootstrap/ng-bootstrap';

@NgModule({
    declarations: [UserCardComponent],
    imports: [
        CommonModule,
        FormsModule,
        ReactiveFormsModule,
        UserCardRoutingModule,
        TranslateModule,
        FlatpickrModule.forRoot(),
        ArchivesModule,
        SingleFilterModule,
        DatetimeFilterModule,
        TextAreaModule,
        CardsModule,
        NgbModule
    ]
})
export class UserCardModule {
}
