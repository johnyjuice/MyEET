package com.johnyjuice.juicyeet;

import android.content.Intent;
import com.johnyjuice.juicyeet.tiskarna.BluetoothService;
import com.johnyjuice.juicyeet.tiskarna.PrintPic;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import android.util.Log;


public class Tiskarna_activity extends AppCompatActivity {
	Button btnSearch;
	Button btnSend;
	Button poslat;
	EditText edtContext;
	private static final int REQUEST_ENABLE_BT = 2;
	private static String prc = "stara";
	BluetoothService mServiceS = null;
	BluetoothDevice con_dev = null;
	private static final int REQUEST_CONNECT_DEVICE = 1;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tiskarna);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		poslat = (Button) findViewById(R.id.btnSend);
		poslat.setOnClickListener(myhandler1);

		if (mServiceS != null){
			mServiceS.stop();
		mServiceS = null;
		}
		mServiceS = new BluetoothService(this, mHandlerA);
		if (mServiceS == null){
		mServiceS = new BluetoothService(this, mHandlerA);
			Toast.makeText(this, "BT", Toast.LENGTH_LONG).show();
		}
		if( mServiceS.isAvailable() == false ){
            Toast.makeText(this, "Bluetooth is not available", Toast.LENGTH_LONG).show();
            finish();
		}
	}

    @Override
    public void onStart() {
    	super.onStart();
    	try {
			btnSearch = (Button) this.findViewById(R.id.btnSearch);
			btnSearch.setOnClickListener(new ClickEvent());
			btnSend = (Button) this.findViewById(R.id.btnSend);
			edtContext = (EditText) findViewById(R.id.txt_content);

			if(prc == "stara"){
				btnSend.setEnabled(false);
				edtContext.setEnabled(false);
			}
		} catch (Exception ex) {
			Log.e("Kontrola",ex.getMessage());
		}

		if( mServiceS.isBTopen() == false)
		{
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
		}
    }

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (mServiceS != null)
			mServiceS.stop();
		mServiceS = null;
	}

	View.OnClickListener myhandler1 = new View.OnClickListener() {
		public void onClick(View v) {
		Handler handler = new Handler();
		handler.postDelayed(new Runnable() {
			public void run() {
				String msg = edtContext.getText().toString();
				if( msg.length() > 0 ){
				msg ="================================\n";
					mServiceS.sendMessage(msg+"", "CP852");

				}
				mServiceS.stop();
			}
		}, 2500);
		con_dev = mServiceS.getDevByMac(prc);
		mServiceS.connect(con_dev);
	}};

	protected void onClickZaved(View v){

		prc= PreferenceManager.getDefaultSharedPreferences(getBaseContext()).getString("tiskarna", "Zařízení nenalezeno");
		Toast.makeText(this, prc, Toast.LENGTH_LONG).show();
		if (prc != "Zařízení nenalezeno"){
			btnSend.setEnabled(true);
			edtContext.setEnabled(true);
		}
	}

	class ClickEvent implements View.OnClickListener {
		public void onClick(View v) {
			if (v == btnSearch) {			
				Intent serverIntent = new Intent(Tiskarna_activity.this,DeviceListActivity.class);
				startActivityForResult(serverIntent,REQUEST_CONNECT_DEVICE);
			}
		}
	}
	 /**
     Handler BluetoothService
     */
    private final  Handler mHandlerA = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
            case BluetoothService.MESSAGE_STATE_CHANGE:
                switch (msg.arg1) {
                case BluetoothService.STATE_CONNECTED:
                	Toast.makeText(getApplicationContext(), "Connect successful",
                            Toast.LENGTH_SHORT).show();

                    break;
                case BluetoothService.STATE_CONNECTING:
                	Log.d("Připo","Při.....");
                    break;
                case BluetoothService.STATE_LISTEN:
                case BluetoothService.STATE_NONE:
                	Log.d("State","None.....");
                    break;
                }
                break;
            case BluetoothService.MESSAGE_CONNECTION_LOST:
                Toast.makeText(getApplicationContext(), "Device connection was lost",
                               Toast.LENGTH_SHORT).show();

                break;
            case BluetoothService.MESSAGE_UNABLE_CONNECT:
            	Toast.makeText(getApplicationContext(), "Unable to connect device",
                        Toast.LENGTH_SHORT).show();
				btnSend.setEnabled(false);
				edtContext.setEnabled(false);
            	break;
            }
        }
    };
        
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {    
        switch (requestCode) {
        case REQUEST_ENABLE_BT:
            if (resultCode == Activity.RESULT_OK) {
            	Toast.makeText(this, "Bluetooth byl aktivován", Toast.LENGTH_LONG).show();
            } else {
            	finish();
            }
            break;
        case  REQUEST_CONNECT_DEVICE:
        	if (resultCode == Activity.RESULT_OK) {
                String address = data.getExtras()
                                     .getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
				prc = address;
				PreferenceManager.getDefaultSharedPreferences(getBaseContext()).edit().putString("tiskarna", prc).commit();

				btnSend.setEnabled(true);
				edtContext.setEnabled(true);
            }
            break;
        }
    } 
    
    @SuppressLint("SdCardPath")
	private void printImage() {
    	byte[] sendData = null;
    	PrintPic pg = new PrintPic();
    	pg.initCanvas(384);     
    	pg.initPaint();
    	pg.drawImage(0, 0, "/mnt/sdcard/icon.jpg");
    	sendData = pg.printDraw();
    	mServiceS.write(sendData);
    }
}
