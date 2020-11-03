/* Copyright (c) 2018-2020, RTE (http://www.rte-france.com)
 * See AUTHORS.txt
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 * This file is part of the OperatorFabric project.
 */


package org.lfenergy.operatorfabric.users.configuration.mongo;

import org.bson.Document;
import org.lfenergy.operatorfabric.users.model.NotificationFilterData;
import org.lfenergy.operatorfabric.users.model.NotificationFilter;
import org.springframework.core.convert.converter.Converter;


/**
 *
 * <p>Spring converter registered in mongo conversions</p>
 * <p>Converts {@link Document} to {@link NotificationFilter} using {@link NotificationFilterData} builder.</p>
 *
 */
public class NotificationFilterReadConverter implements Converter<Document, NotificationFilter> {
    @Override
    public NotificationFilter convert(Document source) {

        NotificationFilterData.NotificationFilterDataBuilder builder = NotificationFilterData.builder()
                                                        .process(source.getString("process"))
                                                        .states(source.getList("states", String.class));
        return builder.build();
    }
}
