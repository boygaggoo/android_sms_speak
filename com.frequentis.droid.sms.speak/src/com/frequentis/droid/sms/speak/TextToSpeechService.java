package com.frequentis.droid.sms.speak;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.app.IntentService;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.speech.tts.TextToSpeech;
import android.util.Log;

public class TextToSpeechService extends IntentService implements TextToSpeech.OnInitListener, TextToSpeech.OnUtteranceCompletedListener {
	private static final String TAG = "TextToSpeechService";
	private UtteranceId utteranceId = new UtteranceId();
	private String text; // text to speak (from intent).

	// synchronize shutdown with end of utterance.
	private Object monitor = new Object();
	private TextToSpeech tts;

	public TextToSpeechService() {
		super("tts-service-worker"); // worker thread name.
	}

	@Override protected void onHandleIntent(Intent intent) {
		if (!isEnabled()) return;

		// called inside worker thread.
		text = intent.getExtras().getString("TEXT");
		tts = new TextToSpeech(this, this);

		// wait until utterance has completed.
		synchronized (monitor) {
			try {
				monitor.wait();
			}
			catch (InterruptedException exception) {}
		}

		tts.shutdown();
	}

	private boolean isEnabled() {
		Preferences preferences = new Preferences(this, null);
		if (!preferences.isSpeakEnabled()) return false;
		else {
			if (!preferences.isConnectionEnabled()) return true;
			else {
				boolean enabled = false;
				BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
				if (bluetoothAdapter.isEnabled()) {
					List<BluetoothDevice> deviceList = new ArrayList<BluetoothDevice>(bluetoothAdapter.getBondedDevices());
					for (BluetoothDevice device : deviceList) {
						enabled |= preferences.isActiveForBluetoothDevice(device.getName().trim());
						Log.d(TAG, "BT-device: " + device.getName() + ", enabled: " + enabled);
					}
				}

				WifiManager wifiManager = (WifiManager) getSystemService(WIFI_SERVICE);
				WifiInfo wifiInfo = wifiManager.getConnectionInfo();
				if (wifiInfo != null) {
					enabled |= preferences.isActiveForSSID(wifiInfo.getSSID());
					Log.d(TAG, "SSID: " + wifiInfo.getSSID() + ", enabled: " + enabled);
				}

				return enabled;
			}
		}
	}

	@Override public void onInit(int status) {
		if (status == TextToSpeech.SUCCESS) {
			tts.setOnUtteranceCompletedListener(this);
			tts.setLanguage(Locale.GERMAN);
			tts.speak(text, TextToSpeech.QUEUE_ADD, utteranceId.next());
		}
	}

	@Override public void onUtteranceCompleted(String id) {
		// called inside some binder thread.
		if (id.equals(utteranceId.current())) {
			synchronized (monitor) {
				monitor.notifyAll();
			}
		}
	}
}
