package com.trablusgarb.e_healty;



import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class Start extends Activity implements OnClickListener {

	Button btn_patien_position;
	Button btn_glucometer;
	Button btn_body_temparature;

	Button btn_blood_pressure;
	Button btn_pulse_oxygen;
	Button btn_airflow;

	Button btn_galvanic_skin_response;
	Button btn_electrocardiogram;
	Button btn_electromygraphs;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_start);

		btn_patien_position=(Button) findViewById(R.id.btn_patient_position);
		btn_patien_position.setOnClickListener(this);

		btn_glucometer=(Button) findViewById(R.id.btn_glucometer);
		btn_glucometer.setOnClickListener(this);
		
		btn_body_temparature=(Button) findViewById(R.id.btn_body_temparature);
		btn_body_temparature.setOnClickListener(this);
		
		btn_blood_pressure=(Button) findViewById(R.id.btn_blood_pressure);
		btn_blood_pressure.setOnClickListener(this);
		
		btn_pulse_oxygen=(Button) findViewById(R.id.btn_pulse_oxygen);
		btn_pulse_oxygen.setOnClickListener(this);
		
		btn_airflow=(Button) findViewById(R.id.btn_airflow);
		btn_airflow.setOnClickListener(this);
		
		btn_galvanic_skin_response=(Button) findViewById(R.id.btn_galvanic_skin_response);
		btn_galvanic_skin_response.setOnClickListener(this);
		
		btn_electrocardiogram=(Button) findViewById(R.id.btn_electrocardiogram);
		btn_electrocardiogram.setOnClickListener(this);
		
		btn_electromygraphs=(Button) findViewById(R.id.btn_electromygraphy);
		btn_electromygraphs.setOnClickListener(this);
		
	}
	
	public void startMeasure(char sensor_type){
		Intent i=new Intent(this,MainActivity.class);
		i.putExtra("sensorType", sensor_type);
		startActivity(i);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.start, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.connectDevice) {
       	 Intent i=new Intent(this,Devices.class);
        
            startActivityForResult(i, 1);
            return true;
       }
       return super.onOptionsItemSelected(item);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub

		int id=v.getId();
		switch (id) {
		case R.id.btn_patient_position:  

			break;

		case R.id.btn_glucometer:
			startMeasure('A');

			break;

		case R.id.btn_body_temparature:


			break;

		case R.id.btn_blood_pressure:

			break;

		case R.id.btn_pulse_oxygen:

			break;
		case R.id.btn_airflow:

			break;

		case R.id.btn_galvanic_skin_response:

			break;

		case R.id.btn_electrocardiogram:

			break;
		case R.id.btn_electromygraphy:

			break;

		default:
			break;
		}		

	}
}
