package com.frequentis.droid.sms.speak.test;

import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Parcel;
import android.test.AndroidTestCase;

import com.frequentis.droid.sms.speak.BluetoothConnectionReceiver;

public class BluetoothConnectionReceiverTest extends AndroidTestCase {
	private BluetoothConnectionReceiver receiver;

	@Override protected void setUp() throws Exception {
		super.setUp();
		receiver = new BluetoothConnectionReceiver();
	}

	public void testInvalidAction() {
		Intent intent = new Intent("action.not.permitted");
		receiver.onReceive(getContext(), intent);
	}

	public void testDeviceConnected() {
		Intent intent = new Intent(BluetoothDevice.ACTION_ACL_CONNECTED);
		Parcel parcel = Parcel.obtain();
		parcel.writeString("00:11:22:33:AA:BB");
		parcel.setDataPosition(0);
		BluetoothDevice device = BluetoothDevice.CREATOR.createFromParcel(parcel);
		intent.putExtra(BluetoothDevice.EXTRA_DEVICE, device);
		receiver.onReceive(getContext(), intent);
	}
}
