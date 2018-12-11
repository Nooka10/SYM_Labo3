package com.labo3.sym.sym_labo3.QRCode;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.labo3.sym.sym_labo3.R;

public class QRCodeActivity extends AppCompatActivity {
	
	private TextView textView;
	private Button button;
	
	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_qrcode);
		
		textView = (TextView) findViewById(R.id.qrcode_activity_previous_qrcode_textview);
		button = (Button) findViewById(R.id.qrcode_activity_scan_qrcode_button);
		
		textView.setMovementMethod(new ScrollingMovementMethod());
		
		if (savedInstanceState != null) {
			textView.setText(savedInstanceState.getString("previousScannedQRCode"));
		}
		
		button.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				startNewBarecodeIntent();
			}
		});
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putString("previousScannedQRCode", textView.getText().toString());
	}
	
	private void startNewBarecodeIntent() {
		IntentIntegrator integrator = new IntentIntegrator(this);
		integrator.setPrompt(getString(R.string.qrcode_activity_barcode_prompt));
		integrator.setBeepEnabled(false);
		integrator.setBarcodeImageEnabled(true);
		integrator.setOrientationLocked(false);
		integrator.initiateScan();
	}
	
	// Get the results:
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
		if (result != null) {
			if (result.getContents() == null) {
				Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show();
			} else {
				Toast.makeText(this, "Scanned: " + result.getContents(), Toast.LENGTH_LONG).show();
				textView.setText(textView.getContext().getString(R.string.qrcode_activity_previous_scanned_qrcodes, textView.getText(), result.getContents()));
			}
		} else {
			super.onActivityResult(requestCode, resultCode, data);
		}
	}
}
