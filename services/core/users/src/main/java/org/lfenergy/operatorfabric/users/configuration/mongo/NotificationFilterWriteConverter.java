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
import org.springframework.core.convert.converter.Converter;

import java.util.List;

/**
 *
 * <p>Spring converter to register {@link NotificationFilterData} in mongoDB</p>
 * <p>Converts {@link NotificationFilterData} to {@link Document} </p>
 * <p>Needed after upgrade to spring-boot 2.2.4.RELEASE</p>
 */
public class NotificationFilterWriteConverter implements Converter<NotificationFilterData, Document> {

    @Override
    public Document convert(NotificationFilterData source) {
        Document result = new Document();

        String process = source.getProcess();
        result.append("process", process);

        List<String> states = source.getStates();
        result.append("states", states);

        return result;
    }
}
