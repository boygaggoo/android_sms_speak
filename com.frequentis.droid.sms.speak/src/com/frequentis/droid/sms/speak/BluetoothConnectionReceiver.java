package com.frequentis.droid.sms.speak;

import java.util.HashSet;
import java.util.Set;

import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class BluetoothConnectionReceiver extends BroadcastReceiver {
	private static final Set<String> permittedActions = new HashSet<String>();

	static {
		permittedActions.add(BluetoothDevice.ACTION_ACL_CONNECTED);
		permittedActions.add(BluetoothDevice.ACTION_ACL_DISCONNECTED);
	}

	@Override public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
		if (!permittedActions.contains(action)) return;

		SharedPreferences preferences = context.getSharedPreferences(PreferencesConstants.BLUETOOTH, Context.MODE_PRIVATE);
		Editor editor = preferences.edit();
		boolean connected = intent.getAction().equals(BluetoothDevice.ACTION_ACL_CONNECTED);
		editor.putBoolean(deviceName(intent), connected);
		editor.commit();
	}

	private static String deviceName(Intent intent) {
		BluetoothDevice device = bluetoothDevice(intent);
		String rawName = device != null ? device.getName() : "";
		return rawName == null ? "" : rawName.trim();
	}

	private static BluetoothDevice bluetoothDevice(Intent intent) {
		return intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
	}
}
