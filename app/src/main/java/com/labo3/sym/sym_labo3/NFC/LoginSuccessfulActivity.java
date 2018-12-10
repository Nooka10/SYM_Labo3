package com.labo3.sym.sym_labo3.NFC;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.labo3.sym.sym_labo3.R;

/**
 * Classe gérant l'activité affichée lorsque l'utilisateur s'est correctement loggué.
 */
public class LoginSuccessfulActivity extends AppCompatActivity {
	private static final String MIME_TEXT_PLAIN = "text/plain";
	private static final String TAG = "NfcDemo";
	
	private NfcAdapter nfcAdapter;
	private TextView textView;
	private ProgressBar progressBar;
	private Button maxSecurityLevelButton;
	private Button mediumSecurityLevelButton;
	private Button lowSecurityLevelButton;
	private CountDownTimer countDownTimer;
	
	// enum des différents niveaux de sécurité
	private enum Security_level {
		AUTHENTICATE_MAX("MAX", 7000),
		AUTHENTICATE_MEDIUM("MEDIUM", 4000),
		AUTHENTICATE_LOW("LOW", 1000),
		AUTHENTICATE_UNAUTHORIZED("UNAUTHORIZED", 0);
		
		private String name;
		private int minValue;
		
		Security_level(String name, int minValue) {
			this.name = name;
			this.minValue = minValue;
		}
		
		public int getMinValue() {
			return minValue;
		}
		
		@Override
		public String toString() {
			return name;
		}
	}
	
	//FIXME: faut-il pouvoir rehausser le niveau de sécurité en étant loggué ? -> activer le NFC dans cette activité aussi..?
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login_successful);
		
		textView = findViewById(R.id.activity_login_successful_textView);
		progressBar = findViewById(R.id.activity_login_successful_security_level_progressBar);
		maxSecurityLevelButton = findViewById(R.id.activity_login_successful_max_security_level);
		mediumSecurityLevelButton = findViewById(R.id.activity_login_successful_medium_security_level);
		lowSecurityLevelButton = findViewById(R.id.activity_login_successful_low_security_level);
		
		progressBar.setMax(10000);
		progressBar.setProgress(10000);
		countDownTimer = new CountDownTimer(10000, 100) {
			public void onTick(long millisUntilFinished) {
				progressBar.setProgress((int) millisUntilFinished);
				String securityLevel = Security_level.AUTHENTICATE_MAX.toString();
				
				if (millisUntilFinished <= Security_level.AUTHENTICATE_LOW.getMinValue()) {
					securityLevel = Security_level.AUTHENTICATE_UNAUTHORIZED.toString();
					lowSecurityLevelButton.setEnabled(false);
				} else if (millisUntilFinished <= Security_level.AUTHENTICATE_MEDIUM.getMinValue()) {
					securityLevel = Security_level.AUTHENTICATE_LOW.toString();
					mediumSecurityLevelButton.setEnabled(false);
				} else if (millisUntilFinished <= Security_level.AUTHENTICATE_MAX.getMinValue()) {
					securityLevel = Security_level.AUTHENTICATE_MEDIUM.toString();
					maxSecurityLevelButton.setEnabled(false);
				}
				textView.setText(textView.getContext().getString(R.string.login_successful_activity_current_security_level_label, securityLevel));
			}
			
			public void onFinish() {
				progressBar.setProgress(0);
			}
		}.start();
		
		nfcAdapter = NfcAdapter.getDefaultAdapter(this);
		if (checkNFC()) { // si le NFC est disponible sur l'appareil, on configure la gestion d'un tag
			handleIntent(getIntent());
		}
	}
	
	/**
	 * Retourne true si l'appareil prend en charge le NFC et que celui-ci est activé, false sinon.
	 *
	 * @return true si l'appareil prend en charge le NFC et que celui-ci est activé, false sinon.
	 */
	private boolean checkNFC() {
		if (nfcAdapter == null) { // l'appareil ne supporte pas le NFC
			// on masque le switch permettant d'activer l'authentification par NFC
			Toast.makeText(this, R.string.activity_nfc_login_device_doesnt_support_NFC, Toast.LENGTH_LONG).show();
			return false;
		}
		
		if (!nfcAdapter.isEnabled()) { // l'appareil supporte le NFC, mais celui-ci n'est pas activé
			Toast.makeText(this, R.string.activity_nfc_login_NFC_isnt_activated, Toast.LENGTH_LONG).show();
			return false;
		}
		return true;
	}
	
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
		 * This method gets called, when a new Intent gets associated with the current activity instance.
		 * Instead of creating a new activity, onNewIntent will be called. For more information have a look
		 * at the documentation.
		 *
		 * In our case this method gets called, when the user attaches a Tag to the device.
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
	private void handleIntent(Intent intent) {
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
	
	// FIXME: doit-on faire en sorte d'éviter la dupplication de code, ou on s'en fiche pour ce labo?
	
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
	
	public class NdefReaderTask extends AbstractNdefReaderTask {
		@Override
		protected void onPostExecute(String result) {
			if (result != null) {
				countDownTimer.cancel();
				countDownTimer.start();
				maxSecurityLevelButton.setEnabled(true);
				mediumSecurityLevelButton.setEnabled(true);
				lowSecurityLevelButton.setEnabled(true);
			}
		}
	}
}
