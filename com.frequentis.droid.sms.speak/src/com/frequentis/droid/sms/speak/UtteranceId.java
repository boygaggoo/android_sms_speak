package com.frequentis.droid.sms.speak;

import java.util.HashMap;

import android.speech.tts.TextToSpeech;

public final class UtteranceId {
	private HashMap<String, String> params = new HashMap<String, String>();
	private int utteranceId = 0;

	public HashMap<String, String> next() {
		utteranceId++;
		params.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, current());
		return params;
	}

	public String current() {
		return Integer.toString(utteranceId);
	}
}
