package com.frequentis.droid.sms.speak;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.StringTokenizer;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

public class Preferences {
	private static final String TAG = "Preferences";
	private SharedPreferences preferences;
	private Context context;
	private PropertyChangeSupport propertyChangeSupport;

	public Preferences(Context context, PropertyChangeListener propertyChangeListener) {
		this.context = context;
		preferences = PreferenceManager.getDefaultSharedPreferences(context);
		propertyChangeSupport = new PropertyChangeSupport(this);

		if (propertyChangeListener != null) {
			propertyChangeSupport.addPropertyChangeListener(propertyChangeListener);
		}
	}

	public boolean isSpeakEnabled() {
		return preferences.getBoolean(context.getString(R.string.KEY_PREF_SPEAK_ENABLED), true);
	}

	public boolean isConnectionEnabled() {
		return preferences.getBoolean(context.getString(R.string.KEY_PREF_CONNECTION_ENABLED), true);
	}

	public void setSpeakEnabled(boolean enabled) {
		Log.d(TAG, "speak_enabled: " + enabled);
		propertyChangeSupport.firePropertyChange(new PropertyChangeEvent(this, context.getString(R.string.KEY_PREF_SPEAK_ENABLED), null,
				enabled));
	}

	public void setConnectionEnabled(boolean enabled) {
		Log.d(TAG, "connection_enabled: " + enabled);
		propertyChangeSupport.firePropertyChange(new PropertyChangeEvent(this, context.getString(R.string.KEY_PREF_CONNECTION_ENABLED),
				null, enabled));
	}

	private boolean isBluetoothDeviceConnected(String deviceName) {
		SharedPreferences preferences = context.getSharedPreferences(PreferencesConstants.BLUETOOTH, Context.MODE_PRIVATE);
		return preferences.getBoolean(deviceName, false);
	}

	public boolean isActiveForBluetoothDevice(String deviceName) {
		String value = preferences.getString(context.getString(R.string.KEY_PREF_BT_DEVICES), null);
		if (value == null) return false;
		else {
			StringTokenizer tokens = new StringTokenizer(value, ":");
			while (tokens.hasMoreTokens()) {
				String token = tokens.nextToken();
				String[] split = token.split("=");
				if (split[0].equals(deviceName)) return Boolean.parseBoolean(split[1]) && isBluetoothDeviceConnected(deviceName);
			}

			return false;
		}
	}

	public boolean isActiveForSSID(String ssid) {
		String value = preferences.getString(context.getString(R.string.KEY_PREF_WIFI_NETWORKS), null);
		if (value == null) return false;
		else {
			StringTokenizer tokens = new StringTokenizer(value, ":");
			while (tokens.hasMoreTokens()) {
				String token = tokens.nextToken();
				String[] split = token.split("=");
				if (split[0].equals(ssid)) return Boolean.parseBoolean(split[1]);
			}

			return false;
		}
	}
}
