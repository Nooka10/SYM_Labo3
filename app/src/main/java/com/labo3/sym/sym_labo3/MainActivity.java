package com.labo3.sym.sym_labo3;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import com.labo3.sym.sym_labo3.NFC.NFCLoginActivity;

public class MainActivity extends AppCompatActivity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		// on récupère les éléments de l'activité
		Button nfcLoginButton = findViewById(R.id.mainActivity_NFCLogin);
		Button codesBarresButton = findViewById(R.id.mainActivity_CodesBarres);
		Button iBeaconButton = findViewById(R.id.mainActivity_IBeacon);
		Button capteursButton = findViewById(R.id.mainActivity_Capteurs);
		
		nfcLoginButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent intent = new Intent(MainActivity.this, NFCLoginActivity.class);
				startActivity(intent);
			}
		});
		
	}
}


