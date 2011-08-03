package com.frequentis.droid.sms.speak;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;

public class MainActivity extends PreferenceActivity implements PropertyChangeListener {
	private Preferences preferences;
	private CheckBoxPreference speakEnabled;
	private CheckBoxPreference connectionEnabled;
	private ListPreference blueboothDevices;
	private ListPreference wifiNetworks;

	@Override public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.settings);

		preferences = new Preferences(this, this);
		speakEnabled = (CheckBoxPreference) findPreference("speak_enabled");
		connectionEnabled = (CheckBoxPreference) findPreference("connection_enabled");
		blueboothDevices = (ListPreference) findPreference("bt_devices");
		wifiNetworks = (ListPreference) findPreference("wifi_networks");

		speakEnabled.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
			@Override public boolean onPreferenceChange(Preference preference, Object newValue) {
				preferences.setSpeakEnabled(Boolean.valueOf(true).equals(newValue));
				return true;
			}
		});

		connectionEnabled.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
			@Override public boolean onPreferenceChange(Preference preference, Object newValue) {
				preferences.setConnectionEnabled(Boolean.valueOf(true).equals(newValue));
				return true;
			}
		});

		connectionEnabled.setEnabled(speakEnabled.isChecked());
		blueboothDevices.setEnabled(speakEnabled.isChecked() && connectionEnabled.isChecked());
		wifiNetworks.setEnabled(speakEnabled.isChecked() && connectionEnabled.isChecked());

		// MultiSelectListPreference only available since: Since: API Level 11
		setBluetoothDevices(blueboothDevices);
		setWifiNetworks(wifiNetworks);
	}

	private void setBluetoothDevices(ListPreference preference) {
		BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		List<BluetoothDevice> deviceList = new ArrayList<BluetoothDevice>(bluetoothAdapter.getBondedDevices());

		CharSequence[] entries = new CharSequence[deviceList.size()];
		CharSequence[] entryValues = new CharSequence[deviceList.size()];

		for (int i = 0; i < deviceList.size(); i++) {
			entries[i] = deviceList.get(i).getName().trim();
			entryValues[i] = "false";
		}

		preference.setEntries(entries);
		preference.setEntryValues(entryValues);
	}

	public void setWifiNetworks(ListPreference preference) {

		// Requires permission: android.permission.ACCESS_WIFI_STATE.
		WifiManager wifiManager = (WifiManager) getSystemService(WIFI_SERVICE);
		List<WifiConfiguration> configuredNetworks = wifiManager.getConfiguredNetworks();

		CharSequence[] entries = new CharSequence[configuredNetworks.size()];
		CharSequence[] entryValues = new CharSequence[configuredNetworks.size()];

		for (int i = 0; i < configuredNetworks.size(); i++) {
			entries[i] = configuredNetworks.get(i).SSID.replace('"', ' ').trim();
			entryValues[i] = "false";
		}

		preference.setEntries(entries);
		preference.setEntryValues(entryValues);
	}

	@Override public void propertyChange(PropertyChangeEvent event) {
		String propertyName = event.getPropertyName();
		if (propertyName.equals(getString(R.string.KEY_PREF_SPEAK_ENABLED))) {
			connectionEnabled.setEnabled((Boolean) event.getNewValue());
			boolean enabled = ((Boolean) event.getNewValue()) && connectionEnabled.isChecked();
			blueboothDevices.setEnabled(enabled);
			wifiNetworks.setEnabled(enabled);
		}
		else if (propertyName.equals(getString(R.string.KEY_PREF_CONNECTION_ENABLED))) {
			boolean enabled = ((Boolean) event.getNewValue());
			blueboothDevices.setEnabled(enabled);
			wifiNetworks.setEnabled(enabled);
		}
	}
}