package com.labo3.sym.sym_labo3.Captors;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;
import com.labo3.sym.sym_labo3.R;

public class CompassActivity extends AppCompatActivity implements SensorEventListener {
	
	//opengl
	private OpenGLRenderer opglr = null;
	private GLSurfaceView m3DView = null;
	
	private SensorManager sensorManager;
	private Sensor accelerometerSensor;
	private Sensor magneticSensor;
	
	private float[] rotationMatix = {
			1f, 0f, 0f, 0f,
			0f, 1f, 0f, 0f,
			0f, 0f, 1f, 0f,
			0f, 0f, 0f, 1f
	};
	
	private float[] gravity = {0f, 0f, 0f};
	private float[] geoMagnetic = {0f, 0f, 0f};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// we need fullscreen
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
		// we initiate the view
		setContentView(R.layout.activity_compass);
		
		// link to GUI
		this.m3DView = findViewById(R.id.compass_opengl);
		
		//we create the 3D renderer
		this.opglr = new OpenGLRenderer(getApplicationContext());
		
		//init opengl surface view
		this.m3DView.setRenderer(this.opglr);
		
		// on initialise les capteurs
		sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
		accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		magneticSensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
		
		sensorManager.registerListener(this, accelerometerSensor, SensorManager.SENSOR_DELAY_GAME);
		sensorManager.registerListener(this, magneticSensor, SensorManager.SENSOR_DELAY_GAME);
	}
	
	protected void onResume() {
		super.onResume();
		sensorManager.registerListener(this, accelerometerSensor, SensorManager.SENSOR_DELAY_GAME);
		sensorManager.registerListener(this, magneticSensor, SensorManager.SENSOR_DELAY_GAME);
	}
	
	protected void onPause() {
		super.onPause();
		sensorManager.unregisterListener(this);
	}
	
	@Override
	public void onSensorChanged(SensorEvent sensorEvent) {
		// On commence par vérifier que le sensor est fiable
		if (sensorEvent.accuracy == SensorManager.SENSOR_STATUS_UNRELIABLE)
			return;
		
		/* On récupère le type d'événement dans un switch, et en fonction, on récupère les valeurs
		 * et on les met dans la bonne matrice */
		switch (sensorEvent.sensor.getType()) {
			case Sensor.TYPE_ACCELEROMETER:
				this.gravity = sensorEvent.values;
				break;
			
			case Sensor.TYPE_MAGNETIC_FIELD:
				this.geoMagnetic = sensorEvent.values;
				break;
		}
		
		
		boolean isValid = SensorManager.getRotationMatrix(rotationMatix, null, gravity, geoMagnetic);
		
		if (isValid) {
			rotationMatix = this.opglr.swapRotMatrix(rotationMatix);
		}
	}
	
	@Override
	public void onAccuracyChanged(Sensor sensor, int i) {
	
	}
}
