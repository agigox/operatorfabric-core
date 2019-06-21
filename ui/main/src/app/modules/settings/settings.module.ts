/* Copyright (c) 2018, RTE (http://www.rte-france.com)
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {SettingsRoutingModule} from "./settings-routing.module";
import { SettingsComponent } from './components/settings/settings.component';
import { BaseSettingComponent } from './components/settings/base-setting/base-setting.component';
import { TextSettingComponent } from './components/settings/text-setting/text-setting.component';
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import {TranslateModule} from "@ngx-translate/core";
import { EmailSettingComponent } from './components/settings/email-setting/email-setting.component';

@NgModule({
    declarations: [SettingsComponent, BaseSettingComponent, TextSettingComponent, EmailSettingComponent],
    imports: [
        CommonModule,
        FormsModule,
        ReactiveFormsModule,
        SettingsRoutingModule,
        TranslateModule,
    ]
})
export class SettingsModule {
}