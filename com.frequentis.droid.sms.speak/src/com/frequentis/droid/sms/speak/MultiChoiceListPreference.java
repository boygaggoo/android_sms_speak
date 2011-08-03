package com.frequentis.droid.sms.speak;

import java.util.StringTokenizer;

import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnMultiChoiceClickListener;
import android.preference.ListPreference;
import android.preference.Preference;
import android.util.AttributeSet;

/**
 * A {@link Preference} that displays a list of entries as a dialog and allows
 * multiple selections.
 */
public class MultiChoiceListPreference extends ListPreference implements OnMultiChoiceClickListener {
	private boolean[] itemChecked;

	public MultiChoiceListPreference(Context context) {
		this(context, null);
	}

	public MultiChoiceListPreference(Context context, AttributeSet attributeSet) {
		super(context, attributeSet);
	}

	@Override protected void onPrepareDialogBuilder(Builder builder) {
		itemChecked = new boolean[getEntries().length];

		if (getValue() != null) {
			int i = 0;
			StringTokenizer tokens = new StringTokenizer(getValue(), ":");
			while (tokens.hasMoreTokens()) {
				String token = tokens.nextToken();
				String[] split = token.split("=");
				itemChecked[i++] = Boolean.parseBoolean(split.length == 2 ? split[1] : split[0]);
			}
		}
		else {
			CharSequence[] entryValues = getEntryValues();
			for (int i = 0; i < entryValues.length; i++) {
				itemChecked[i] = "true".equals(entryValues[i]);
			}
		}

		builder.setMultiChoiceItems(getEntries(), itemChecked, this);
	}

	@Override protected void onDialogClosed(boolean positiveResult) {
		if (!positiveResult) return;
		CharSequence[] entries = getEntries();
		if (entries == null || entries.length == 0) return;

		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < entries.length; i++) {
			builder.append(entries[i]);
			builder.append("=" + itemChecked[i]);
			if (i < itemChecked.length - 1) builder.append(":");
		}

		String value = builder.toString();
		setValue(value);
	}

	@Override public void onClick(DialogInterface dialog, int which, boolean isChecked) {
		itemChecked[which] = isChecked;
	}
}
