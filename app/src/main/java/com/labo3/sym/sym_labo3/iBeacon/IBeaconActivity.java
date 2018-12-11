package com.labo3.sym.sym_labo3.iBeacon;

import android.Manifest;
import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.RemoteException;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.labo3.sym.sym_labo3.R;
import org.altbeacon.beacon.*;

import java.util.Collection;
import java.util.List;

public class IBeaconActivity extends AppCompatActivity implements BeaconConsumer {
	private BeaconManager beaconManager;
	private RecyclerView detectedIBeacons;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_ibeacon);
		
		detectedIBeacons = findViewById(R.id.activity_ibeacons_recyclerview);
		detectedIBeacons.setLayoutManager(new LinearLayoutManager(this));
		detectedIBeacons.setAdapter(new RecyclerViewAdapter());
		
		// on demande à l'utilisateur les permissions nécessaires
		Dexter.withActivity(this).withPermissions(Manifest.permission.INTERNET, Manifest.permission.ACCESS_NETWORK_STATE, Manifest.permission.ACCESS_COARSE_LOCATION,
				Manifest.permission.BLUETOOTH)
				.withListener(new MultiplePermissionsListener() {
					@Override
					public void onPermissionsChecked(MultiplePermissionsReport report) {
						List<PermissionGrantedResponse> permissionsGranted = report.getGrantedPermissionResponses();
						for (PermissionGrantedResponse permission : permissionsGranted) {
							if (permission.getPermissionName().equals(Manifest.permission.ACCESS_NETWORK_STATE)) {
								checkIfInternetIsEnabled();
							}
							if (permission.getPermissionName().equals(Manifest.permission.BLUETOOTH)) {
								checkIfBluetoothIsEnabled();
							}
							if (permission.getPermissionName().equals(Manifest.permission.ACCESS_FINE_LOCATION)) {
								checkIfLocationIsEnabled();
							}
						}
					}
					
					@Override
					public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
						token.continuePermissionRequest();
					}
				}).check();
		
		beaconManager = BeaconManager.getInstanceForApplication(this);
		beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24"));
		beaconManager.bind(this);
	}
	
	private void checkIfInternetIsEnabled() {
		ConnectivityManager cm = (ConnectivityManager) getBaseContext().getSystemService(Context.CONNECTIVITY_SERVICE);
		if (cm != null) {
			final NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
			if (activeNetwork == null || !activeNetwork.isConnected()) { // l'appareil n'est pas connecté à internet
				// on affiche une popup signalant qu'on showNFCTagContent besoin d'internet
				final AlertDialog.Builder builder = new AlertDialog.Builder(this);
				builder.setTitle("Please, enable Internet connection!");
				builder.setMessage("To be able to detect Beacons, Internet has to be enabled.");
				builder.setPositiveButton(android.R.string.ok, null);
				// lorsque l'utilisateur ferme la popup, on affiche la page des paramètres de l'appareil permettant d'activer les données cellulaires
				builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
					@Override
					@TargetApi(Build.VERSION_CODES.M)
					public void onDismiss(DialogInterface dialogInterface) {
						// ouvre la page des paramètres permettant d'activer les données cellulaires
						// fixme: y showNFCTagContent-t-il un équivalent à la méthode utilisée pour bluetooth pour la connection internet?
						Intent enableInternetIntent = new Intent(Settings.ACTION_DATA_ROAMING_SETTINGS);
						startActivity(enableInternetIntent);
					}
				});
				builder.show();
			}
		}
	}
	
	private void checkIfBluetoothIsEnabled() {
		BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		if (!bluetoothAdapter.isEnabled()) { // le bluetooth n'est pas activé
			// on affiche une popup signalant qu'on showNFCTagContent besoin du Bluetooth
			final AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle("Please, enable Bluetooth connection!");
			builder.setMessage("To be able to detect Beacons, Bluetooth has to be enabled.");
			builder.setPositiveButton(android.R.string.ok, null);
			// lorsque l'utilisateur ferme la popup, on affiche une popup permettant d'activer directement le Bluetooth
			builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
				@Override
				@TargetApi(Build.VERSION_CODES.M)
				public void onDismiss(DialogInterface dialogInterface) {
					// fixme: y showNFCTagContent-t-il un équivalent pour la connection internet et la localisation?
					Intent enableBluetoothIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
					startActivity(enableBluetoothIntent);
				}
			});
			builder.show();
		}
	}
	
	private void checkIfLocationIsEnabled() {
		LocationManager lm = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
		if (lm == null || !lm.isProviderEnabled(LocationManager.GPS_PROVIDER)) { // la localisation GPS n'est pas activée
			// on affiche une popup signalant qu'on showNFCTagContent besoin de la localisation GPS
			final AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle("Please, enable location!");
			builder.setMessage("To be able to detect Beacons, your location is needed.");
			builder.setPositiveButton(android.R.string.ok, null);
			builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
				@Override
				@TargetApi(Build.VERSION_CODES.M)
				public void onDismiss(DialogInterface dialogInterface) {
					// ouvre la page des paramètres permettant d'activer la localisation GPS
					// fixme: y showNFCTagContent-t-il un équivalent à la méthode utilisée pour bluetooth pour la localisation?
					Intent enableInternetIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
					startActivity(enableInternetIntent);
				}
			});
			builder.show();
		}
	}
	
	@Override
	public void onBeaconServiceConnect() {
		beaconManager.addRangeNotifier(new RangeNotifier() {
			@Override
			public void didRangeBeaconsInRegion(Collection<Beacon> collection, Region region) {
				final RecyclerViewAdapter adapter = (RecyclerViewAdapter) IBeaconActivity.this.detectedIBeacons.getAdapter();
				if (adapter != null) {
					adapter.setListIBeacons(collection);
				}
			}
		});
		
		try {
			beaconManager.startRangingBeaconsInRegion(new Region("myUniqueID", null, null, null));
		} catch (RemoteException e) {
			System.err.println(e.getMessage());
		}
	}
}
