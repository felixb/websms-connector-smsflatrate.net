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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import org.apache.http.message.BasicNameValuePair;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import de.ub0r.android.websms.connector.common.BasicConnector;
import de.ub0r.android.websms.connector.common.ConnectorCommand;
import de.ub0r.android.websms.connector.common.ConnectorSpec;
import de.ub0r.android.websms.connector.common.ConnectorSpec.SubConnectorSpec;
import de.ub0r.android.websms.connector.common.Log;
import de.ub0r.android.websms.connector.common.Utils;
import de.ub0r.android.websms.connector.common.WebSMSException;

/**
 * AsyncTask to manage IO to smsflatrate.net API.
 * 
 * @author flx
 */
public final class ConnectorSmsflatratenet extends BasicConnector {
	/** Tag for output. */
	private static final String TAG = "smsflatrate.net";

	/** smsflatrate.net gateway URL. */
	private static final String URL = "http://www.smsflatrate.net/appkey.php";

	/** SubConnector id: Gateway 1. */
	private static final String ID_GW1 = "1";
	/** SubConnector id: Gateway 2. */
	private static final String ID_GW2 = "2";
	/** SubConnector id: Gateway 3. */
	private static final String ID_GW3 = "3";
	/** SubConnector id: Gateway 4. */
	private static final String ID_GW4 = "4";
	/** SubConnector id: Gateway 10. */
	private static final String ID_GW10 = "10";
	/** SubConnector id: Gateway 11. */
	private static final String ID_GW11 = "11";
	/** SubConnector id: Gateway 20. */
	private static final String ID_GW20 = "20";
	/** SubConnector id: Gateway 21. */
	private static final String ID_GW21 = "21";
	/** SubConnector id: Gateway 30. */
	private static final String ID_GW30 = "30";

	@Override
	public ConnectorSpec initSpec(final Context context) {
		String name = context.getString(R.string.connector_name);
		ConnectorSpec c = new ConnectorSpec(name);
		c.setAuthor(context.getString(R.string.connector_author));
		c.setBalance(null);
		c.setCapabilities(ConnectorSpec.CAPABILITIES_UPDATE
				| ConnectorSpec.CAPABILITIES_SEND
				| ConnectorSpec.CAPABILITIES_PREFS);
		SharedPreferences p = PreferenceManager
				.getDefaultSharedPreferences(context);
		if (p.getBoolean("show_gw" + ID_GW1, true)) {
			c.addSubConnector(ID_GW1,
					context.getString(R.string.connector_gw1_name),
					SubConnectorSpec.FEATURE_MULTIRECIPIENTS
							| SubConnectorSpec.FEATURE_SENDLATER);
		}
		if (p.getBoolean("show_gw" + ID_GW2, true)) {
			c.addSubConnector(ID_GW2,
					context.getString(R.string.connector_gw2_name),
					SubConnectorSpec.FEATURE_MULTIRECIPIENTS
							| SubConnectorSpec.FEATURE_SENDLATER);
		}
		if (p.getBoolean("show_gw" + ID_GW3, false)) {
			c.addSubConnector(ID_GW3,
					context.getString(R.string.connector_gw3_name),
					SubConnectorSpec.FEATURE_MULTIRECIPIENTS
							| SubConnectorSpec.FEATURE_SENDLATER);
		}
		if (p.getBoolean("show_gw" + ID_GW4, false)) {
			c.addSubConnector(ID_GW4,
					context.getString(R.string.connector_gw4_name),
					SubConnectorSpec.FEATURE_MULTIRECIPIENTS
							| SubConnectorSpec.FEATURE_SENDLATER);
		}
		if (p.getBoolean("show_gw" + ID_GW10, false)) {
			c.addSubConnector(ID_GW10,
					context.getString(R.string.connector_gw10_name),
					SubConnectorSpec.FEATURE_MULTIRECIPIENTS
							| SubConnectorSpec.FEATURE_SENDLATER);
		}
		if (p.getBoolean("show_gw" + ID_GW11, false)) {
			c.addSubConnector(ID_GW11,
					context.getString(R.string.connector_gw11_name),
					SubConnectorSpec.FEATURE_MULTIRECIPIENTS
							| SubConnectorSpec.FEATURE_SENDLATER);
		}
		if (p.getBoolean("show_gw" + ID_GW20, true)) {
			c.addSubConnector(ID_GW20,
					context.getString(R.string.connector_gw20_name),
					SubConnectorSpec.FEATURE_MULTIRECIPIENTS
							| SubConnectorSpec.FEATURE_SENDLATER);
		}
		if (p.getBoolean("show_gw" + ID_GW21, true)) {
			c.addSubConnector(ID_GW21,
					context.getString(R.string.connector_gw21_name),
					SubConnectorSpec.FEATURE_MULTIRECIPIENTS
							| SubConnectorSpec.FEATURE_SENDLATER);
		}
		if (p.getBoolean("show_gw" + ID_GW30, true)) {
			c.addSubConnector(ID_GW30,
					context.getString(R.string.connector_gw30_name),
					SubConnectorSpec.FEATURE_MULTIRECIPIENTS
							| SubConnectorSpec.FEATURE_SENDLATER);
		}
		return c;
	}

	@Override
	public ConnectorSpec updateSpec(final Context context,
			final ConnectorSpec connectorSpec) {
		final SharedPreferences p = PreferenceManager
				.getDefaultSharedPreferences(context);
		if (p.getBoolean(Preferences.PREFS_ENABLED, false)) {
			if (p.getString(Preferences.PREFS_PASSWORD, "").length() > 0) {
				connectorSpec.setReady();
			} else {
				connectorSpec.setStatus(ConnectorSpec.STATUS_ENABLED);
			}
		} else {
			connectorSpec.setStatus(ConnectorSpec.STATUS_INACTIVE);
		}
		return connectorSpec;
	}

	/**
	 * Check return code from smsflatrate.net.
	 * 
	 * @param context
	 *            {@link Context}
	 * @param ret
	 *            return code
	 * @return true if no error code
	 */
	private static boolean checkReturnCode(final Context context, final int ret) {
		Log.d(TAG, "ret=" + ret);
		switch (ret) {
		case 100:
			return true;
		case 110:
			throw new WebSMSException(context, R.string.connector_error_110);
		case 140:
			throw new WebSMSException(context, R.string.connector_error_140);
		case 120:
			throw new WebSMSException(context, R.string.error_balance);
		case 130:
			throw new WebSMSException(context, R.string.error_input);
		case 131:
			throw new WebSMSException(context, R.string.error_recipient);
		case 132:
			throw new WebSMSException(context, R.string.error_sender);
		case 133:
			throw new WebSMSException(context, R.string.error_text);
		case 150:
			throw new WebSMSException(context, R.string.connector_error_150);
		case 404:
			throw new WebSMSException(context, R.string.connector_error_404);
		default:
			throw new WebSMSException(context, R.string.error, " code: " + ret);
		}
	}

	@Override
	protected String getParamUsername() {
		return "lizenz";
	}

	@Override
	protected String getParamPassword() {
		return "appkey";
	}

	@Override
	protected String getParamRecipients() {
		return null;
	}

	@Override
	protected String getParamSender() {
		return "from";
	}

	@Override
	protected String getParamText() {
		return "text";
	}

	@Override
	protected String getParamSendLater() {
		return "time";
	}

	@Override
	protected String getSendLater(final long sendLater) {
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(sendLater);
		return new SimpleDateFormat("dd.MM.yyyy-kk:mm").format(cal.getTime());
	}

	@Override
	protected String getUsername(final Context context,
			final ConnectorCommand command, final ConnectorSpec cs) {
		return "217075044";
	}

	@Override
	protected String getPassword(final Context context,
			final ConnectorCommand command, final ConnectorSpec cs) {
		return PreferenceManager.getDefaultSharedPreferences(context)
				.getString(Preferences.PREFS_PASSWORD, "");
	}

	@Override
	protected String getRecipients(final ConnectorCommand command) {
		return null;
	}

	@Override
	protected String getSender(final Context context,
			final ConnectorCommand command, final ConnectorSpec cs) {
		return Utils.international2oldformat(Utils.getSender(context,
				command.getDefSender()));
	}

	@Override
	protected String getUrlBalance(final ArrayList<BasicNameValuePair> d) {
		d.add(new BasicNameValuePair("request", "credits"));
		return URL;
	}

	@Override
	protected String getUrlSend(final ArrayList<BasicNameValuePair> d) {
		return URL;
	}

	@Override
	protected boolean usePost(final ConnectorCommand command) {
		return false;
	}

	@Override
	protected String getParamSubconnector() {
		return "type";
	}

	@Override
	protected void addExtraArgs(final Context context,
			final ConnectorCommand command, final ConnectorSpec cs,
			final ArrayList<BasicNameValuePair> d) {
		d.add(new BasicNameValuePair("aid", "6446"));

		if (command.getType() == ConnectorCommand.TYPE_SEND) {
			String r = Utils.joinRecipientsNumbers(
					Utils.national2international(command.getDefPrefix(),
							command.getRecipients()), ";", true).replaceAll(
					"\\+", "00");
			if (!TextUtils.isEmpty(r)) {
				d.add(new BasicNameValuePair("to", r));
				if (r.contains(";")) {
					d.add(new BasicNameValuePair("bulk", "1"));
				}
			}
			if (ID_GW30.equals(command.getSelectedSubConnector())) {
				SharedPreferences p = PreferenceManager
						.getDefaultSharedPreferences(context);
				String speaker = p.getString("speaker", "Stefan");
				d.add(new BasicNameValuePair("voice", speaker));
			}
		}
	}

	@Override
	protected void parseResponse(final Context context,
			final ConnectorCommand command, final ConnectorSpec cs,
			final String htmlText) {
		if (command.getType() == ConnectorCommand.TYPE_UPDATE) {
			if (TextUtils.isEmpty(htmlText)) {
				throw new WebSMSException(context, R.string.connector_error_140);
			}
			if (htmlText.contains(".")) {
				cs.setBalance(htmlText.trim() + "\u20AC");
			} else {
				cs.setBalance(htmlText.trim() + " Flat");
			}
		} else if (command.getType() == ConnectorCommand.TYPE_SEND) {
			if (TextUtils.isEmpty(htmlText)) {
				throw new WebSMSException(context, R.string.error);
			}
			int resp = -1;
			try {
				resp = Integer.parseInt(htmlText.trim());
			} catch (NumberFormatException e) {
				throw new WebSMSException(context, R.string.error);
			}
			checkReturnCode(context, resp);
		} else {
			throw new IllegalArgumentException("unknown ConnectorCommand: "
					+ command.getType());
		}
	}
}
