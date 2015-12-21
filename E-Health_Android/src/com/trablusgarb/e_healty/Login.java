package com.trablusgarb.e_healty;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class Login extends Activity {
	EditText txt_name;
	EditText txt_surname;
	Button btn_login;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		
		txt_name=(EditText)findViewById(R.id.txt_name);
		txt_surname=(EditText)findViewById(R.id.txt_surname);
		btn_login=(Button)findViewById(R.id.btn_login);	
		btn_login.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if( txt_name.getText().length()>0 && txt_surname.getText().length()>0){
					login();
				}
				else
					Toast.makeText(getApplicationContext(), "Tüm alanlarý doldurun!", Toast.LENGTH_SHORT).show();

			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.login, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	public void login(){
		Intent i=new Intent(this,MainActivity.class);
		i.putExtra("name", txt_name.getText().toString());
		i.putExtra("surname", txt_surname.getText().toString());
		startActivity(i);
		finish();
	}
}
