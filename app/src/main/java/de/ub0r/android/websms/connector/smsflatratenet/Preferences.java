/*
 * Copyright (C) 2012 Felix Bechstein
 * 
 * This file is part of WebSMS.
 * 
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 3 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program; If not, see <http://www.gnu.org/licenses/>.
 */
package de.ub0r.android.websms.connector.smsflatratenet;

import android.app.AlertDialog;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;

import de.ub0r.android.websms.connector.common.ConnectorPreferenceActivity;

/**
 * Preferences.
 *
 * @author flx
 */
public final class Preferences extends ConnectorPreferenceActivity implements
        OnPreferenceClickListener {

    /**
     * Preference key: enabled.
     */
    static final String PREFS_ENABLED = "enable";

    /**
     * Preference's name: user's app key.
     */
    static final String PREFS_PASSWORD = "password";

    @SuppressWarnings("deprecation")
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.addPreferencesFromResource(R.xml.prefs);
        this.findPreference("appkey_help").setOnPreferenceClickListener(this);
        this.findPreference("support").setOnPreferenceClickListener(this);
    }

    @Override
    public boolean onPreferenceClick(final Preference preference) {
        if ("appkey_help".equals(preference.getKey())) {
            AlertDialog.Builder b = new AlertDialog.Builder(this);
            b.setTitle(R.string.connector_appkey_help_title);
            b.setMessage(R.string.connector_appkey_help);
            b.setCancelable(true);
            b.show();
            return true;
        }
        if ("support".equals(preference.getKey())) {
            AlertDialog.Builder b = new AlertDialog.Builder(this);
            b.setTitle(R.string.connector_support);
            b.setMessage(R.string.connector_support_hint);
            b.setCancelable(true);
            b.show();
            return true;
        }
        return false;

    }
}
