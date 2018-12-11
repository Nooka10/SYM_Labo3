package com.labo3.sym.sym_labo3.NFC;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
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
public class NFCLoginActivity extends NFCActivity {
	private static final String[] CREDENTIALS = new String[]{
			"test@sym.ch:aaaaaa", "toto@tutu.com_tata", "benoit@schopfer.ch:ben", "antoine@rochat.ch:toine", "jeremie@chatillon.ch:jerem"
	};
	
	// UI references.
	private TextView emailTextView;
	private EditText passwordEditText;
	private TextView infosTextView;
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
	@Override
	protected boolean checkNFC() {
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
		
		// on check si l'utilsiateur showNFCTagContent entré un mdp et s'il est valide.
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
					if (!validNFCTagDetected) { // le tag NFC n'showNFCTagContent pas encore été scanné ou showNFCTagContent été scanné il y showNFCTagContent plus de 10 secondes
						infosTextView.setText(R.string.activity_nfc_login_connection_waiting_for_nfc_tag);
					} else { // le tag NFC showNFCTagContent été scanné il y showNFCTagContent moins de 10 secondes --> l'utilisateur est logué
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
	
	protected void showNFCTagContent(String result) {
		if (result != null && result.equals(SECRET_ID)) { // vrai si le message lu dans le tag NFC correspond au SECRET_ID (test)
			validNFCTagDetected = true;
			attemptLogin();
			infosTextView.setText(R.string.activity_nfc_login_nfc_tag_scanned);
			if (countDownTimerBeforeResetNFC == null) { // on crée le timer pour la 1ère fois
				countDownTimerBeforeResetNFC = new CountDownTimer(10000, 1000) {
					/**
					 * Méthode appelée chaque seconde. Affiche le temps restant avant que le tag NFC doivent être rescanné.
					 *
					 * @param millisUntilFinished, le temps restant avant la fin du timer
					 */
					public void onTick(long millisUntilFinished) {
						infosTextView.setText(infosTextView.getContext().getString(R.string.activity_nfc_login_remaining_time_before_reset_nfc, millisUntilFinished / 1000));
					}
					
					/**
					 * Méthode appelée lorsque le timer s'est écoulé. Le tag NFC est invalidé. Il faudra le re-scanner pour pouvoir se
					 * connecter.
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
		} else {
			infosTextView.setText(R.string.activity_nfc_invalid_nfc_tag_message);
		}
	}
}

