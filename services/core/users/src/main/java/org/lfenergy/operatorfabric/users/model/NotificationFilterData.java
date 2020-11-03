/* Copyright (c) 2018-2020, RTE (http://www.rte-france.com)
 * See AUTHORS.txt
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 * This file is part of the OperatorFabric project.
 */


package org.lfenergy.operatorfabric.users.model;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

/**
 * NotificationFilter Model, documented at {@link NotificationFilter}
 *
 * {@inheritDoc}
 *
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NotificationFilterData implements NotificationFilter {
    private String process;
    private List<String> states;

    public NotificationFilterData(NotificationFilter notificationFilter) {
        this.process = notificationFilter.getProcess();
        this.states = new ArrayList<>(notificationFilter.getStates());
    }
}
