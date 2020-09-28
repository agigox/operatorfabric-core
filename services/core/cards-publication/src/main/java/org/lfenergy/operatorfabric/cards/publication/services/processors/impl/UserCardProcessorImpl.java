/* Copyright (c) 2018-2020, RTE (http://www.rte-france.com)
 * See AUTHORS.txt
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 * This file is part of the OperatorFabric project.
 */

package org.lfenergy.operatorfabric.cards.publication.services.processors.impl;

import org.lfenergy.operatorfabric.cards.publication.model.CardPublicationData;
import org.lfenergy.operatorfabric.cards.publication.services.processors.UserCardProcessor;
import org.lfenergy.operatorfabric.users.model.ComputedPerimeter;
import org.lfenergy.operatorfabric.users.model.CurrentUserWithPerimeters;
import org.lfenergy.operatorfabric.users.model.RightsEnum;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class UserCardProcessorImpl implements UserCardProcessor {

    public String processPublisher(CardPublicationData card, CurrentUserWithPerimeters user) {

        if(!isAuthorizedCard(card,user)){
            throw new AccessDeniedException("user not authorized, the card is rejected");
        }

        Optional<List<String>> entitiesUser= Optional.ofNullable(user.getUserData().getEntities());

        //take first entity of the user as the card publisher id
        if(entitiesUser.isPresent() && !entitiesUser.get().isEmpty()) {
            card.setPublisher(entitiesUser.get().get(0));
            return entitiesUser.get().get(0);

        }
        //no possible calculation of publisher id from card and user arguments,
        // throw a runtime exception to be handled by Mono.onErrorResume()
        throw new IllegalArgumentException("action not authorized, the card is rejected");

    }

    protected boolean isAuthorizedCard(CardPublicationData card, CurrentUserWithPerimeters user){

        boolean ret=false;
        Optional<ComputedPerimeter> computedPerimeter=user.getComputedPerimeters().stream().
                filter(x->x.getState().equalsIgnoreCase(card.getState()) && x.getProcess().equalsIgnoreCase(card.getProcess())).findFirst();
        if(computedPerimeter.isPresent()){
            if(RightsEnum.WRITE.equals(computedPerimeter.get().getRights())|| RightsEnum.RECEIVEANDWRITE.equals(computedPerimeter.get().getRights()))
             ret=true;
        }

        return ret;
    }

}
