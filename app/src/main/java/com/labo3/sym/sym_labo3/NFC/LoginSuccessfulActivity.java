package com.labo3.sym.sym_labo3.NFC;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.labo3.sym.sym_labo3.R;

/**
 * Classe gérant l'activité affichée lorsque l'utilisateur s'est correctement loggué.
 */
public class LoginSuccessfulActivity extends NFCActivity {
	
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
		
		if (checkNFC()) { // si le NFC est disponible sur l'appareil, on configure la gestion d'un tag
			handleIntent(getIntent());
		}
	}
	
	/**
	 * Retourne true si l'appareil prend en charge le NFC et que celui-ci est activé, false sinon.
	 *
	 * @return true si l'appareil prend en charge le NFC et que celui-ci est activé, false sinon.
	 */
	@Override
	protected boolean checkNFC() {
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
	
	protected void showNFCTagContent(String result) {
		if (result != null && result.equals(SECRET_ID)) { // vrai si le message lu dans le tag NFC correspond au SECRET_ID (test)
			countDownTimer.cancel();
			countDownTimer.start();
			maxSecurityLevelButton.setEnabled(true);
			mediumSecurityLevelButton.setEnabled(true);
			lowSecurityLevelButton.setEnabled(true);
		}
	}
}
