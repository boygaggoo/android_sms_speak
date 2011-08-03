package com.frequentis.droid.sms.speak;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;

/**
 * A BroadcastReceiver object is only valid for the duration of the call to
 * onReceive(Context, Intent). Once your code returns from this function, the
 * system considers the object to be finished and no longer active.
 * <p>
 * This has important repercussions to what you can do in an onReceive(Context,
 * Intent) implementation: anything that requires asynchronous operation is not
 * available, because you will need to return from the function to handle the
 * asynchronous operation, but at that point the BroadcastReceiver is no longer
 * active and thus the system is free to kill its process before the
 * asynchronous operation completes.
 * <p>
 * In particular, you may not show a dialog or bind to a service from within a
 * BroadcastReceiver. For the former (dialog), you should instead use the
 * NotificationManager API. For the latter (service), you can use
 * Context.startService() to send a command to the service.
 */
public class SMSBroadcastReceiver extends BroadcastReceiver {
	// NOTE: The capital 'T' in 'Telephony'!
	private static String SMS_RECEIVED = "android.provider.Telephony.SMS_RECEIVED";

	@Override
	public void onReceive(Context context, Intent intent) {
		if (!intent.getAction().equals(SMS_RECEIVED)) {
			// Not what we expected...
			return;
		}

		final String message = messageBody(intent.getExtras());
		if (message != null) {
			Intent service = new Intent(context, TextToSpeechService.class);
			service.putExtra("TEXT", message);
			context.startService(service);
		}
	}

	private String messageBody(Bundle bundle) {
		if (bundle == null) {
			return null;
		}

		Object[] pdus = (Object[]) bundle.get("pdus");
		if (pdus == null || pdus.length == 0) {
			return null;
		}

		StringBuilder body = new StringBuilder();
		for (int i = 0; i < pdus.length; i++) {
			body.append(SmsMessage.createFromPdu((byte[]) pdus[i]).getMessageBody());
		}

		return body.toString();
	}
}
