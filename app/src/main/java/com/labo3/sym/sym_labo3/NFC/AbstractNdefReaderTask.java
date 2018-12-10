package com.labo3.sym.sym_labo3.NFC;

import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.os.AsyncTask;
import android.util.Log;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;

/**
 * Background task for reading the data. Do not block the UI thread while reading.
 * Plus d'infos ici: https://code.tutsplus.com/tutorials/reading-nfc-tags-with-android--mobile-17278
 */
public abstract class AbstractNdefReaderTask extends AsyncTask<Tag, Void, String> {
	private static final String TAG = "NfcDemo";
	// FIXME: je comprend pas à quoi sert ce TAG... ?
	
	@Override
	protected String doInBackground(Tag... params) {
		Tag tag = params[0];
		
		Ndef ndef = Ndef.get(tag);
		if (ndef == null) {
			// NDEF is not supported by this Tag.
			return null;
		}
		NdefMessage ndefMessage = ndef.getCachedNdefMessage();
		NdefRecord[] records = ndefMessage.getRecords();
		for (NdefRecord ndefRecord : records) {
			if (ndefRecord.getTnf() == NdefRecord.TNF_WELL_KNOWN && Arrays.equals(ndefRecord.getType(), NdefRecord.RTD_TEXT)) {
				try {
					return readText(ndefRecord);
				} catch (UnsupportedEncodingException e) {
					Log.e(TAG, "Unsupported Encoding", e);
				}
			}
		}
		return null;
	}
	
	/**
	 * Lit les informations du tag NFC détecté
	 * @param record, le tag NFC détecté
	 * @return
	 * @throws UnsupportedEncodingException
	 *
	 * Plus d'infos ici: https://code.tutsplus.com/tutorials/reading-nfc-tags-with-android--mobile-17278
	 */
	private String readText(NdefRecord record) throws UnsupportedEncodingException {
		/*
		 * See NFC forum specification for "Text Record Type Definition" at 3.2.1
		 *
		 * http://www.nfc-forum.org/specs/
		 *
		 * bit_7 defines encoding
		 * bit_6 reserved for future use, must be 0
		 * bit_5..0 length of IANA language code
		 */
		
		byte[] payload = record.getPayload();
		
		// Get the Text Encoding
		String textEncoding = ((payload[0] & 128) == 0) ? "UTF-8" : "UTF-16";
		
		// Get the Language Code
		int languageCodeLength = payload[0] & 51;
		
		// String languageCode = new String(payload, 1, languageCodeLength, "US-ASCII");
		// e.g. "en"
		
		// Get the Text
		return new String(payload, languageCodeLength + 1, payload.length - languageCodeLength - 1, textEncoding);
	}
	
	@Override
	protected abstract void onPostExecute(String result);
}