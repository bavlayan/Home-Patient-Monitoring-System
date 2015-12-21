package com.trablusgarb.e_healty;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.RandomAccessFile;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.xmlpull.v1.XmlSerializer;

import android.app.ActionBar;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.format.Time;
import android.util.Xml;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends Activity implements OnClickListener {

	private ListView terminal;
    private EditText sending;
    private Button send;
    ArrayAdapter term;

    // Message types sent from the BluetoothChatService Handler
    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;
    public static final int MESSAGE_DEVICE_NAME = 4;
    public static final int MESSAGE_TOAST = 5;

    // Key names received from the BluetoothChatService Handler
    public static final String DEVICE_NAME = "device_name";
    public static final String TOAST = "toast";

    private static final int REQUEST_ENABLE_BT = 3;
    
    
    public static final char PATIENT_POSITION_SENSORB = '1';
    public static final char GLUCOMETER_SENSOR = '2';
    public static final char BODY_TEMPRATURE_SENSOR = '3';
    public static final char BLOOD_PRESSURE_SENSOR = '4';
    public static final char PULSE_AND_OXYGEN_IN_BLOOD_SENSOR = '5';
    public static final char AIRFLOW_SENSOR = '6';
    public static final char GALVANIC_SKIN_RESPONSE_SENSOR = '7';
    public static final char ELECTROCARDIOGRAM_SENSOR = '8';
    public static final char ELECTROMYOGRAPHY_SENSOR = '9';
    
    public static String sensorName="";
    public static String patientName="";
    public static String patientSurname="";
    public static RandomAccessFile xmlFile;
    public static Calendar current_date_time;
    public static String current_hour ;
    public static String current_minutes ;
    public static String current_seconds ;  
    
    
    
    // Name of the connected device
    private String mConnectedDeviceName = null;
    // String buffer for outgoing messages
    private StringBuffer mOutStringBuffer;
    // Local Bluetooth adapter
    private BluetoothAdapter mBluetoothAdapter = null;
    // Member object for the chat services
    private BluetoothService mChatService = null;

	// butonlar
	
    Button btn_patien_position;
	Button btn_glucometer;
	Button btn_body_temparature;

	Button btn_blood_pressure;
	Button btn_pulse_oxygen;
	Button btn_airflow;

	Button btn_galvanic_skin_response;
	Button btn_electrocardiogram;
	Button btn_electromygraphs;
    
	ImageView sensor_img;
	
	boolean isMeasuring=false;
	boolean isBackPressed=true;
	boolean isConnected=false;
	
	String name;
	String surname;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
      //  setContentView(R.layout.activity_main);
        
        
        // hasta bilgilerini al
        patientName=getIntent().getExtras().getString("name");
        patientSurname=getIntent().getExtras().getString("surname");
            
  
     // file name tarih
        current_date_time  = Calendar.getInstance(); 
        SimpleDateFormat df = new SimpleDateFormat("dd_MM_yyyy");
        String fileName = df.format(current_date_time.getTime());
     
        xmlFile=OpenXMLFile("/sdcard/eHealth",fileName , patientName, patientSurname);
        choseSensor();
       
        // Get local Bluetooth adapter
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        // If the adapter is null, then Bluetooth is not supported
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, "Bluetooth is not available", Toast.LENGTH_LONG).show();
            CloseXML(xmlFile);
            finish();
            return;
        }     
    }


    @Override
    public void onStart() {
        super.onStart();        
        // If BT is not on, request that it be enabled.
        // setupChat() will then be called during onActivityResult
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
        // Otherwise, setup the chat session
        } else {
            if (mChatService == null) 
            	setupChat();
        }
    }

    @Override
    public synchronized void onResume() {
        super.onResume();
       
        // Performing this check in onResume() covers the case in which BT was
        // not enabled during onStart(), so we were paused to enable it...
        // onResume() will be called when ACTION_REQUEST_ENABLE activity returns.
        if (mChatService != null) {
            // Only if the state is STATE_NONE, do we know that we haven't started already
            if (mChatService.getState() == BluetoothService.STATE_NONE) {
              // Start the Bluetooth chat services
              mChatService.start();
            }
        }
    }

    private void setupChat() {

        // Initialize the BluetoothChatService to perform bluetooth connections
        mChatService = new BluetoothService(this, mHandler);

        // Initialize the buffer for outgoing messages
        mOutStringBuffer = new StringBuffer("");
    }
    
    
    @Override
    public synchronized void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // Stop the Bluetooth chat services
        if (mChatService != null) mChatService.stop();
    }

    
    
    @Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
    	if(isBackPressed){
    		CloseXML(xmlFile);
    		super.onBackPressed();
    	}
    	else{
    		choseSensor();
    	}
	}


    /**
     * Sends a message.
     * @param message  A string of text to send.
     */
    private void sendMessage(String message) {
        // Check that we're actually connected before trying anything
        if (mChatService.getState() != BluetoothService.STATE_CONNECTED) {
            Toast.makeText(this, "Not connected to device", Toast.LENGTH_SHORT).show();
            return;
        }

        // Check that there's actually something to send
        if (message.length() > 0) {
            // Get the message bytes and tell the BluetoothChatService to write
            byte[] send = message.getBytes();
            mChatService.write(send);

            // Reset out string buffer to zero and clear the edit text field
            mOutStringBuffer.setLength(0);
          //  mOutEditText.setText(mOutStringBuffer);
            
        }
    }

    private final void setStatus(CharSequence subTitle) {
        final ActionBar actionBar = getActionBar();
        actionBar.setSubtitle(subTitle);
    }
    
    // The Handler that gets information back from the BluetoothChatService
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
            case MESSAGE_STATE_CHANGE:

                switch (msg.arg1) {
                case BluetoothService.STATE_CONNECTED:
                    setStatus("Connected to " +mConnectedDeviceName);
                    isConnected=true;
                   // term.clear();
                    break;
                case BluetoothService.STATE_CONNECTING:
                    setStatus("Connecting");
                    break;
                case BluetoothService.STATE_LISTEN:
                case BluetoothService.STATE_NONE:
                    setStatus("Not Connected");
                    break;
                    default:
                    	isConnected=false;
                    	break;
                }
                break;
            case MESSAGE_WRITE:
                byte[] writeBuf = (byte[]) msg.obj;
                // construct a string from the buffer
                if(!isMeasuring)
                	break;
                String writeMessage = new String(writeBuf); 
               // term.add("Me:  " + writeMessage);
                break;
            case MESSAGE_READ:
                //byte[] readBuf = (byte[]) msg.obj;
                // construct a string from the valid bytes in the buffer
               // String readMessage = new String(readBuf, 0, msg.arg1);
            	if(!isMeasuring)
                	break;
              term.add((String)msg.obj);
              WriteXML(xmlFile, sensorName, (String)msg.obj);
                break;
            case MESSAGE_DEVICE_NAME:
                // save the connected device's name
                mConnectedDeviceName = msg.getData().getString("device_name");
                Toast.makeText(getApplicationContext(), "Connected to "
                               + mConnectedDeviceName, Toast.LENGTH_SHORT).show();
                break;
            case MESSAGE_TOAST:
                Toast.makeText(getApplicationContext(), msg.getData().getString(TOAST),
                               Toast.LENGTH_SHORT).show();
                break;
            }
        }
    };
    
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode) {
        case 1:
            // When DeviceListActivity returns with a device to connect
            if (resultCode == Activity.RESULT_OK) {
                connectDevice(data, true);
            }
            break;
        case 2:
            // When DeviceListActivity returns with a device to connect
            if (resultCode == Activity.RESULT_OK) {
                connectDevice(data, false);
            }
            break;
        case 3:
            // When the request to enable Bluetooth returns
            if (resultCode == Activity.RESULT_OK) {
                // Bluetooth is now enabled, so set up a chat session
                setupChat();
            } else {
                // User did not enable Bluetooth or an error occurred
                Toast.makeText(this,"Bluetooth was not enabled. Leaving ArduinoTerm.", Toast.LENGTH_SHORT).show();
                CloseXML(xmlFile);
                finish();
            }
        }
    }

    private void connectDevice(Intent data, boolean secure) {
        // Get the device MAC address
        String address = data.getExtras()
            .getString("DeviceAdress");
        // Get the BluetoothDevice object
        BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
        // Attempt to connect to the device
        mChatService.connect(device, secure);
    }
     
    
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
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
    
    public RandomAccessFile OpenXMLFile(String path,String fileName,String patient_name,String patient_surname){
    	
    	try {
			File file=new File(path);
			if(!file.exists())
				file.mkdir();
			path=path+"/"+patient_surname+"_"+patient_name;
			file=new File(path);
			if(!file.exists())
				file.mkdir();
			path=path+"/"+fileName+".xml";
			file=new File(path);
			if(file.exists()){
				RandomAccessFile randomFile = new RandomAccessFile(path, "rw");
				randomFile.seek(randomFile.length()-9);
				return randomFile;
			}
			else{
				RandomAccessFile randomFile = new RandomAccessFile(path, "rw");
				//randomFile.writeBytes("<xml version=\"1.0\" encoding=\"UTF-8\">");
				randomFile.writeBytes("<Health>");
				randomFile.writeBytes("<Patient>");
				randomFile.writeBytes("<Name>"+patient_name+"</Name>");
				randomFile.writeBytes("<Surname>"+patient_surname+"</Surname>");
				randomFile.writeBytes("</Patient>");
				return randomFile;
			}
        } catch (Exception e) {
        	return null;
		}	
    }
    public void WriteXML(RandomAccessFile randomFile,String sensor_name,String value){
    	try {
    		current_date_time=Calendar.getInstance();
    		 current_hour = String.valueOf(current_date_time.get(Calendar.HOUR_OF_DAY));
             current_minutes = String.valueOf(current_date_time.get(Calendar.MINUTE));
             current_seconds = String.valueOf(current_date_time.get(Calendar.SECOND));   
    		
			//randomFile.writeBytes("<"+sensor_name+">"+value+"</"+sensor_name+">");
			randomFile.writeBytes("<"+sensor_name+">");
			randomFile.writeBytes("<Time>");
			randomFile.writeBytes(current_hour+":"+current_minutes+":"+current_seconds);
			randomFile.writeBytes("</Time>");
			randomFile.writeBytes("<Value>");
			randomFile.writeBytes(value);
			randomFile.writeBytes("</Value>");
			randomFile.writeBytes("</"+sensor_name+">");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    public void CloseXML(RandomAccessFile randomFile){
    	try {
			randomFile.writeBytes("</Health>");
			randomFile.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    @Override
	public void onClick(View v) {
		// TODO Auto-generated method stub

    	if(!isConnected){
    		Toast.makeText(getApplicationContext(),"Connection not established", Toast.LENGTH_SHORT).show();
    		return;
    	}
    	
		int id=v.getId();
		switch (id) {
		case R.id.btn_patient_position:
			sensorName="PatientPosition";
			startMeasure(R.drawable.bodyposition_icon_2);
			sendMessage(String.valueOf(PATIENT_POSITION_SENSORB));
			break;

		case R.id.btn_glucometer:
			sensorName="Glucometer";
			startMeasure(R.drawable.glucometer_icon_2);
	        sendMessage(String.valueOf(GLUCOMETER_SENSOR));
			break;

		case R.id.btn_body_temparature:
			sensorName="BodyTemparature";
			startMeasure(R.drawable.temparature_2);
			sendMessage(String.valueOf(BODY_TEMPRATURE_SENSOR));
			break;

		case R.id.btn_blood_pressure:
			sensorName="BloodPressure";
			startMeasure(R.drawable.blood_icon_2);
			sendMessage(String.valueOf(BLOOD_PRESSURE_SENSOR));
			break;

		case R.id.btn_pulse_oxygen:
			sensorName="PulseOxygen";
			startMeasure(R.drawable.blood_icon_2);
			sendMessage(String.valueOf(PULSE_AND_OXYGEN_IN_BLOOD_SENSOR));
			break;
		case R.id.btn_airflow:
			sensorName="Airflow";
			startMeasure(R.drawable.airflow_icon_2);
			sendMessage(String.valueOf(AIRFLOW_SENSOR));
			break;

		case R.id.btn_galvanic_skin_response:
			sensorName="GalvanicSkinResponse";
			startMeasure(R.drawable.galvanic_icon2);
			sendMessage(String.valueOf(GALVANIC_SKIN_RESPONSE_SENSOR));			
			break;

		case R.id.btn_electrocardiogram:
			sensorName="Electrocardiogram";
			startMeasure(R.drawable.ekg_icon_2);
			sendMessage(String.valueOf(ELECTROCARDIOGRAM_SENSOR));
			break;
		case R.id.btn_electromygraphy:
			sensorName="electromygrahy";
			startMeasure(R.drawable.emg_icon_2);
			sendMessage(String.valueOf(ELECTROMYOGRAPHY_SENSOR));
			break;

		default:
			isMeasuring=false;
			break;
		}		

	}
    
    public void startMeasure(int id){
    	setContentView(R.layout.activity_main);
    	term=new ArrayAdapter<String>(this, R.layout.main_adapter);
        terminal=(ListView)findViewById(R.id.lvterm);
        terminal.setAdapter(term);
        sensor_img=(ImageView)findViewById(R.id.sensor_image);
        sensor_img.setImageResource(id);
        isMeasuring=true;
        isBackPressed=false;
    }
    
    public void choseSensor(){
      
    	try {
    		sendMessage("default");
		} catch (Exception e) {
			// TODO: handle exception
		}
    		
    	isMeasuring=false;
    	isBackPressed=true;
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
    
   
}
