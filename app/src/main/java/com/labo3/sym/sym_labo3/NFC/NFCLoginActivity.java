package com.labo3.sym.sym_labo3.NFC;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.*;
import com.labo3.sym.sym_labo3.R;

/**
 * Classe gérant l'authentification d'un utilisateur à l'aide d'un couple email/mot de passe ainsi que d'une balise NFC. Le code gérant le NFC est grandement inspiré du
 * code trouvé ici: https://code.tutsplus.com/tutorials/reading-nfc-tags-with-android--mobile-17278
 */
public class NFCLoginActivity extends AppCompatActivity {
	private static final String MIME_TEXT_PLAIN = "text/plain";
	private static final String TAG = "NfcDemo";
	
	private static final String[] CREDENTIALS = new String[]{
			"test@sym.ch:aaaaaa", "toto@tutu.com_tata", "benoit@schopfer.ch:ben", "antoine@rochat.ch:toine", "jeremie@chatillon.ch:jerem"
	};
	
	// UI references.
	private TextView emailTextView;
	private EditText passwordEditText;
	private TextView infosTextView;
	private NfcAdapter nfcAdapter;
	private Button signInButton;
	private Switch activateNFCSwitch;
	private CountDownTimer countDownTimerBeforeResetNFC; // timer activé lorsqu'un tag NFC valide est détecté et à la fin duquel on reset validNFCTagDetected à false
	private boolean validNFCTagDetected = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_nfclogin);
		
		// ----------------------------------------------------- gestion Login -----------------------------------------------------
		
		emailTextView = (TextView) findViewById(R.id.email);
		passwordEditText = (EditText) findViewById(R.id.password);
		signInButton = (Button) findViewById(R.id.email_sign_in_button);
		
		// on set l'action effectuée lorsqu'on valide le texte entré dans le mot de passe
		passwordEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
				if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
					attemptLogin();
					return true;
				}
				return false;
			}
		});
		
		// on set l'action lorsqu'on clique sur le bouton de connexion
		signInButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				attemptLogin();
			}
		});
		
		// ----------------------------------------------------- gestion NFC -----------------------------------------------------
		nfcAdapter = NfcAdapter.getDefaultAdapter(this);
		infosTextView = (TextView) findViewById(R.id.activity_nfc_login_textview_infos);
		activateNFCSwitch = findViewById(R.id.activateLoginWithNFC);
		
		// on set l'action lorsqu'on active/désactive le switch
		activateNFCSwitch.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				if (checkNFC()) {
					if (!activateNFCSwitch.isChecked()) {
						signInButton.setText(R.string.action_sign_in_without_nfc);
					} else {
						signInButton.setText(R.string.action_sign_in_with_nfc);
					}
				}
			}
		});
		
		if (checkNFC()) { // si le NFC est disponible sur l'appareil, on configure la gestion d'un tag
			handleIntent(getIntent());
			activateNFCSwitch.setChecked(true);
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
			activateNFCSwitch.setVisibility(View.INVISIBLE);
			activateNFCSwitch.setChecked(false);
			Toast.makeText(this, R.string.activity_nfc_login_device_doesnt_support_NFC, Toast.LENGTH_LONG).show();
			return false;
		}
		
		if (!nfcAdapter.isEnabled()) { // l'appareil supporte le NFC, mais celui-ci n'est pas activé
			activateNFCSwitch.setChecked(false);
			Toast.makeText(this, R.string.activity_nfc_login_NFC_isnt_activated, Toast.LENGTH_LONG).show();
			return false;
		}
		return true;
	}
	
	/**
	 * Essaye de connecter l'utilisateur en fonction de l'email et du mot de passe entrés. Si une erreur se produit (email invalide, information manquante, etc), une
	 * erreur est affichée dans le champ correspondant et la tentative de connexion n'est pas effectuée
	 */
	private void attemptLogin() {
		// on reset les erreurs.
		emailTextView.setError(null);
		passwordEditText.setError(null);
		
		// on enregistre les valeurs de l'email et du mdp entrés au moment de la tentative de login.
		final String email = emailTextView.getText().toString();
		final String password = passwordEditText.getText().toString();
		
		boolean cancel = false;
		View focusView = null;
		
		// on check si l'utilsiateur a entré un mdp et s'il est valide.
		if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
			passwordEditText.setError(getString(R.string.error_invalid_password));
			focusView = passwordEditText;
			cancel = true;
		}
		
		// on check si l'adresse email entrée est valide.
		if (TextUtils.isEmpty(email)) {
			emailTextView.setError(getString(R.string.error_field_required));
			focusView = emailTextView;
			cancel = true;
		} else if (!isEmailValid(email)) {
			emailTextView.setError(getString(R.string.error_invalid_email));
			focusView = emailTextView;
			cancel = true;
		}
		
		if (cancel) { // si une erreur est détecté, on n'effectue pas la tentative de login et on focus le 1er champs posant problème
			focusView.requestFocus();
		} else { // aucune erreur détectée -> on effectue la tentative de connexion
			if (!isLoginInfosCorrect(email, password)) { // l'email/mdp entré n'est pas valide
				passwordEditText.setError(getString(R.string.error_incorrect_password));
				passwordEditText.requestFocus();
			} else { // email/mdp sont valides
				
				if (!activateNFCSwitch.isChecked()) { // L'authentification par NFC n'est pas activée -> on log l'utilisateur
					startLoginSuccessfulIntent();
				} else { // l'authentification par NFC est activée
					if (!validNFCTagDetected) { // le tag NFC n'a pas encore été scanné ou a été scanné il y a plus de 10 secondes
						infosTextView.setText(R.string.activity_nfc_login_connection_waiting_for_nfc_tag);
					} else { // le tag NFC a été scanné il y a moins de 10 secondes --> l'utilisateur est logué
						startLoginSuccessfulIntent();
					}
				}
			}
		}
	}
	
	/**
	 * Démarre l'activité affichée lorsque l'utilisateur s'est correctement connecté et termine l'activité courante.
	 */
	private void startLoginSuccessfulIntent() {
		Intent intent = new Intent(this, LoginSuccessfulActivity.class);
		startActivity(intent);
		finish();
	}
	
	/**
	 * Vérifie si l'email entré est valide.
	 *
	 * @param email, l'email à tester.
	 *
	 * @return true si l'email entrée est une adresse email valide (contient un @).
	 */
	private boolean isEmailValid(String email) {
		return email.contains("@");
	}
	
	/**
	 * Vérifie si le mot de passe entré est valide.
	 *
	 * @param password, le mot de passe à tester.
	 *
	 * @return true si le mot de passe entré est valide (> 4 caractères).
	 */
	private boolean isPasswordValid(String password) {
		return password.length() > 4;
	}
	
	/**
	 * Retourne true si les informations de connexion (login/mdp) sont valides.
	 *
	 * @param email,    l'email à tester.
	 * @param password, le mot de passe à tester.
	 *
	 * @return true si les informations de connexion sont valides, false sinon.
	 */
	private boolean isLoginInfosCorrect(String email, String password) {
		// on check la validité des couples email/mdp à l'aide du tableau CREDENTIALS.
		for (String credential : CREDENTIALS) {
			String[] pieces = credential.split(":");
			if (pieces[0].equals(email)) { // Vrai si l'email existe
				if (pieces[1].equals(password)) {
					// l'email existe et le mot de passe correspond -> les infos sont correctes --> on retourne true
					return true;
				}
			}
		}
		// aucun couple email/mdp ne correspond à ceux entrés --> infos incorrectes --> on retourne false
		return false;
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
	 * Méthode appelée lorsqu'un tag NFC est détecté.
	 * Plus d'infos ici: https://code.tutsplus.com/tutorials/reading-nfc-tags-with-android--mobile-17278
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
				validNFCTagDetected = true;
				attemptLogin();
				infosTextView.setText(R.string.activity_nfc_login_nfc_tag_scanned);
				if (countDownTimerBeforeResetNFC == null) { // on crée le timer pour la 1ère fois
					countDownTimerBeforeResetNFC = new CountDownTimer(10000, 1000) {
						/**
						 * Méthode appelée chaque seconde. Affiche le temps restant avant que le tag NFC doivent être rescanné.
						 * @param millisUntilFinished, le temps restant avant la fin du timer
						 */
						public void onTick(long millisUntilFinished) {
							infosTextView.setText(infosTextView.getContext().getString(R.string.activity_nfc_login_remaining_time_before_reset_nfc, millisUntilFinished / 1000));
						}
						
						/**
						 * Méthode appelée lorsque le timer s'est écoulé.
						 * Le tag NFC est invalidé. Il faudra le re-scanner pour pouvoir se connecter.
						 */
						public void onFinish() {
							// 10 secondes après que le tag NFC ait été scanné, si l'utilisateur ne s'est pas loggué, il lui faudra rescanner le tag NFC.
							infosTextView.setText("");
							validNFCTagDetected = false;
						}
					};
				} else { // un timer existe déjà -> on le réinitialise
					countDownTimerBeforeResetNFC.cancel();
				}
				// on démarre le timer
				countDownTimerBeforeResetNFC.start();
			}
		}
	}
}

