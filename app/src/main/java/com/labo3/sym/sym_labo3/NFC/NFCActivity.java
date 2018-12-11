package com.labo3.sym.sym_labo3.NFC;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

public abstract class NFCActivity extends AppCompatActivity {
	private static final String MIME_TEXT_PLAIN = "text/plain";
	private static final String TAG = "NfcDemo";
	protected static final String SECRET_ID = "test";
	protected NfcAdapter nfcAdapter;
	
	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		nfcAdapter = NfcAdapter.getDefaultAdapter(this);
	}
	
	
	protected abstract boolean checkNFC();
	
	@Override
	protected void onResume() {
		super.onResume();
		
		// It's important, that the activity is in the foreground (resumed). Otherwise an IllegalStateException is thrown.
		// Plus d'infos ici: https://code.tutsplus.com/tutorials/reading-nfc-tags-with-android--mobile-17278
		setupForegroundDispatch();
	}
	
	@Override
	protected void onPause() {
		// Call this before onPause, otherwise an IllegalArgumentException is thrown as well.
		// Plus d'infos ici: https://code.tutsplus.com/tutorials/reading-nfc-tags-with-android--mobile-17278
		stopForegroundDispatch();
		
		super.onPause();
	}
	
	@Override
	protected void onNewIntent(Intent intent) {
		/*
		 * This method gets called, when showNFCTagContent new Intent gets associated with the current activity instance.
		 * Instead of creating showNFCTagContent new activity, onNewIntent will be called. For more information have showNFCTagContent look
		 * at the documentation.
		 *
		 * In our case this method gets called, when the user attaches showNFCTagContent Tag to the device.
		 *
		 * Plus d'infos ici: https://code.tutsplus.com/tutorials/reading-nfc-tags-with-android--mobile-17278
		 */
		handleIntent(intent);
	}
	
	/**
	 * Méthode appelée lorsqu'un tag NFC est détecté. Plus d'infos ici: https://code.tutsplus.com/tutorials/reading-nfc-tags-with-android--mobile-17278
	 *
	 * @param intent
	 */
	protected void handleIntent(Intent intent) {
		String action = intent.getAction();
		if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action)) {
			String type = intent.getType();
			if (MIME_TEXT_PLAIN.equals(type)) {
				Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
				new NdefReaderTask().execute(tag);
			} else {
				Log.d(TAG, "Wrong mime type: " + type);
			}
		} else if (NfcAdapter.ACTION_TECH_DISCOVERED.equals(action)) {
			// In case we would still use the Tech Discovered Intent
			Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
			String[] techList = tag.getTechList();
			String searchedTech = Ndef.class.getName();
			for (String tech : techList) {
				if (searchedTech.equals(tech)) {
					new NdefReaderTask().execute(tag);
					break;
				}
			}
		}
	}
	
	/**
	 * Plus d'infos ici: https://code.tutsplus.com/tutorials/reading-nfc-tags-with-android--mobile-17278
	 */
	private void setupForegroundDispatch() {
		if (nfcAdapter == null) {
			return;
		}
		final Intent intent = new Intent(this.getApplicationContext(), this.getClass());
		intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		
		final PendingIntent pendingIntent = PendingIntent.getActivity(this.getApplicationContext(), 0, intent, 0);
		
		IntentFilter[] filters = new IntentFilter[1];
		String[][] techList = new String[][]{};
		
		// Notice that this is the same filter as in our manifest.
		filters[0] = new IntentFilter();
		filters[0].addAction(NfcAdapter.ACTION_NDEF_DISCOVERED);
		filters[0].addCategory(Intent.CATEGORY_DEFAULT);
		try {
			filters[0].addDataType("text/plain");
		} catch (IntentFilter.MalformedMimeTypeException e) {
			Log.e(TAG, "MalformedMimeTypeException", e);
		}
		nfcAdapter.enableForegroundDispatch(this, pendingIntent, filters, techList);
	}
	
	/**
	 * Plus d'infos ici: https://code.tutsplus.com/tutorials/reading-nfc-tags-with-android--mobile-17278
	 */
	private void stopForegroundDispatch() {
		if (nfcAdapter != null) {
			nfcAdapter.disableForegroundDispatch(this);
		}
	}
	
	protected abstract void showNFCTagContent(String result);
	
	public class NdefReaderTask extends AbstractNdefReaderTask {
		@Override
		protected void onPostExecute(String result) {
			showNFCTagContent(result);
		}
	}
}
