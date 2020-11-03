/* Copyright (c) 2018-2020, RTE (http://www.rte-france.com)
 * See AUTHORS.txt
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 * This file is part of the OperatorFabric project.
 */


package org.lfenergy.operatorfabric.users.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.*;

@Document(collection = "user_settings")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserSettingsData implements UserSettings {

    @Id
    private String login;
    private String description;
    private String timeZone;
    private String locale;
    @JsonIgnore
    @Singular("defaultTag")
    private Set<String> defaultTagsSet;
    private Boolean playSoundForAlarm;
    private Boolean playSoundForAction;
    private Boolean playSoundForCompliant;
    private Boolean playSoundForInformation;

    @JsonIgnore
    @Singular("notificationFilter")
    private Set<NotificationFilterData> notificationFiltersSet;

    public UserSettingsData(UserSettings settings) {
        this.login = settings.getLogin();
        this.description = settings.getDescription();
        this.timeZone = settings.getTimeZone();
        this.locale = settings.getLocale();

        if (settings.getDefaultTags() != null)
            this.defaultTagsSet = new HashSet<>(settings.getDefaultTags());
        else
            this.defaultTagsSet = null;
        this.playSoundForAlarm = settings.getPlaySoundForAlarm();
        this.playSoundForAction = settings.getPlaySoundForAction();
        this.playSoundForCompliant = settings.getPlaySoundForCompliant();
        this.playSoundForInformation = settings.getPlaySoundForInformation();

        if (settings.getNotificationFilters() != null)
            this.notificationFiltersSet = new HashSet<>((Collection<? extends NotificationFilterData>) settings.getNotificationFilters());
        else
            this.notificationFiltersSet = null;
    }

    public Set<String> getDefaultTagsSet() {
        if (this.defaultTagsSet == null)
            return Collections.emptySet();
        return defaultTagsSet;
    }

    @Override
    public List<String> getDefaultTags() {
        if (defaultTagsSet == null)
            return null;
        return new ArrayList<>(defaultTagsSet);
    }

    @Override
    public void setDefaultTags(List<String> defaultTags) {
        if (defaultTags != null)
            defaultTagsSet = new HashSet<>(defaultTags);
        else
            defaultTagsSet = null;
    }

    public UserSettingsData clearTags(){
        setDefaultTags(null);
        return this;
    }

    public Set<NotificationFilterData> getNotificationFiltersSet() {
        if (this.notificationFiltersSet == null)
            return Collections.emptySet();
        return notificationFiltersSet;
    }

    @Override
    public List<NotificationFilterData> getNotificationFilters() {
        if (notificationFiltersSet == null)
            return null;
        return new ArrayList<>(notificationFiltersSet);
    }

    @Override
    public void setNotificationFilters(List<? extends NotificationFilter> notificationFilters) {
        if (notificationFilters != null)
            notificationFiltersSet = new HashSet<>((Collection<? extends NotificationFilterData>) notificationFilters);
        else
            notificationFiltersSet = null;
    }

    /**
     * Create a new patched settings using this as reference and overriding fields from other parameter when field is not
     * null.
     * <br>
     * NB: resulting field defaultTags is the addition of collections from both sides
     * NB2: login cannot be changed
     *
     * @param other
     * @return
     */
    public UserSettingsData patch(UserSettings other) {
        UserSettingsData result = new UserSettingsData();
        result.login = this.login;
        result.description = other.getDescription() != null ? other.getDescription() : this.getDescription();
        result.timeZone = other.getTimeZone() != null ? other.getTimeZone() : this.getTimeZone();
        result.locale = other.getLocale() != null ? other.getLocale() : this.getLocale();
        if (other.getDefaultTags() != null)
            result.defaultTagsSet = new HashSet<>(other.getDefaultTags());
        else if (this.getDefaultTags() != null)
            result.defaultTagsSet = new HashSet<>(this.getDefaultTags());
        else
            result.defaultTagsSet = null;
        result.playSoundForAlarm = other.getPlaySoundForAlarm() != null ? other.getPlaySoundForAlarm() : this.getPlaySoundForAlarm();
        result.playSoundForAction = other.getPlaySoundForAction() != null ? other.getPlaySoundForAction() : this.getPlaySoundForAction();
        result.playSoundForCompliant = other.getPlaySoundForCompliant() != null ? other.getPlaySoundForCompliant() : this.getPlaySoundForCompliant();
        result.playSoundForInformation = other.getPlaySoundForInformation() != null ? other.getPlaySoundForInformation() : this.getPlaySoundForInformation();

        if (other.getNotificationFilters() != null)
            result.notificationFiltersSet = new HashSet<>((Collection<? extends NotificationFilterData>) other.getNotificationFilters());
        else if (this.getNotificationFilters() != null)
            result.notificationFiltersSet = new HashSet<>((Collection<? extends NotificationFilterData>) this.getNotificationFilters());
        else
            result.notificationFiltersSet = null;

        return result;
    }
}
