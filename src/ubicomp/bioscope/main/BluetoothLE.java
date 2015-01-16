package ubicomp.bioscope.main;

import java.util.List;
import java.util.UUID;

import ubicomp.bioscope.R;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.IBinder;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

@SuppressLint("NewApi")
public class BluetoothLE {
	private static final String TAG = "BluetoothLE";
	
	//Notifiation UUID
	private static final UUID SERVICE4 = UUID.fromString("0000fff0-0000-1000-8000-00805f9b34fb");
    private static final UUID SERVICE4_CONFIG_CHAR = UUID.fromString("0000fff4-0000-1000-8000-00805f9b34fb");
    private static final UUID SERVICE4_SIMPLE_PROFILE = UUID.fromString("0000fff1-0000-1000-8000-00805f9b34fb");
    
	// Intent request codes
 	private static final int REQUEST_CONNECT_DEVICE = 1;
    private static final int REQUEST_ENABLE_BT = 2;
	
	private Activity activity = null;
	private BluetoothAdapter mBluetoothAdapter;
	
	private String mDeviceAddress;
    private BluetoothLeService mBluetoothLeService;
    private boolean mConnected = false;

    private BluetoothGattCharacteristic mNotifyCharacteristic;
    
    private long steps = 0;
    
     
	// Code to manage Service lifecycle.
	private final ServiceConnection mServiceConnection = new ServiceConnection() {
	    @Override
	    public void onServiceConnected(ComponentName componentName, IBinder service) {
	        mBluetoothLeService = ((BluetoothLeService.LocalBinder) service).getService();
	        if (!mBluetoothLeService.initialize()) {
	            Log.e(TAG, "Unable to initialize Bluetooth");
	            activity.finish();
	        }
	        // Automatically connects to the device upon successful start-up initialization.
	        mBluetoothLeService.connect(mDeviceAddress);
	    }
	
	    @Override
	    public void onServiceDisconnected(ComponentName componentName) {
	        mBluetoothLeService = null;
	    }
	};
	
	// Handles various events fired by the Service.
    // ACTION_GATT_CONNECTED: connected to a GATT server.
    // ACTION_GATT_DISCONNECTED: disconnected from a GATT server.
    // ACTION_GATT_SERVICES_DISCOVERED: discovered GATT services.
    // ACTION_DATA_AVAILABLE: received data from the device.  This can be a result of read
    //                        or notification operations.
    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {
                mConnected = true;
                Toast.makeText(activity, "BLE connected", Toast.LENGTH_SHORT).show();
            } else if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) {
                mConnected = false;
                Toast.makeText(activity, "BLE disconnected!", Toast.LENGTH_SHORT).show();
            } else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
                // Show all the supported services and characteristics on the user interface.
            	List<BluetoothGattService> gattServices = mBluetoothLeService.getSupportedGattServices();
            	mNotifyCharacteristic = gattServices.get(3).getCharacteristic(SERVICE4_CONFIG_CHAR);
            	mBluetoothLeService.setCharacteristicNotification(mNotifyCharacteristic, true);
//                Toast.makeText(activity, "ACTION_GATT_SERVICES_DISCOVERED", Toast.LENGTH_SHORT).show();
            } else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
            	byte[] data = intent.getByteArrayExtra(BluetoothLeService.EXTRA_DATA);
        		
        		switch(data[0]) {
        		case 2: // Acc
        			int acc = data[1];
        			Log.i("BLE", "Acc " + acc + " ");
        			switch(acc) {
        			case 0: //Lying
        				break;
        			case 1: //Standing
        				break;
        			case 2: //Step
        				steps++;
        				TextView stepTextView = (TextView)activity.findViewById(R.id.text_steps);
            			if(stepTextView!=null)
            				stepTextView.setText(steps + " steps");
        				break;
        			}
        			break;
        		case 3: // Temp
        			double temp = ByteConversion.toInt(data[1], data[2])*0.0625;
        			Log.i("BLE", "Temp " + temp + " ");
        			TextView tempTextView = (TextView)activity.findViewById(R.id.textView2);
        			if(tempTextView!=null)
        				tempTextView.setText(temp + " ");
        			break;
        		}
            }
        }
    };
	
	public BluetoothLE(Activity activity) {
		this.activity = activity;
		
		// Use this check to determine whether BLE is supported on the device.  Then you can
        // selectively disable BLE-related features.
        if (!activity.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(activity, "BLE not supported!", Toast.LENGTH_SHORT).show();
            activity.finish();
        }

        // Initializes a Bluetooth adapter.  For API level 18 and above, get a reference to
        // BluetoothAdapter through BluetoothManager.
        final BluetoothManager bluetoothManager =
                (BluetoothManager) activity.getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();

        // Checks if Bluetooth is supported on the device.
        if (mBluetoothAdapter == null) {
            Toast.makeText(activity, "Bluetooth is not supported!", Toast.LENGTH_SHORT).show();
            activity.finish();
            return;
        }
	}
	
	public boolean bleConnection() {
		
		// Ensures Bluetooth is enabled on the device.  If Bluetooth is not currently enabled,
        // fire an intent to display a dialog asking the user to grant permission to enable it.
        if (!mBluetoothAdapter.isEnabled()) {
            if (!mBluetoothAdapter.isEnabled()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                activity.startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            }
        }
        else {
        	Intent serverIntent = new Intent(activity, DeviceListActivity.class);
    		activity.startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);
        }
		
		return false;
	}
	
	private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
        return intentFilter;
    }
	
	public void onBleActivityResult(int requestCode, int resultCode, Intent data) {
//    	Toast.makeText(activity.getApplicationContext(), "onBleActivityResult", Toast.LENGTH_SHORT).show();
		
		switch (requestCode) {
        case REQUEST_CONNECT_DEVICE:
        	
        	// Get the device MAC address
        	mDeviceAddress = data.getExtras().getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
            Toast.makeText(activity, "Bluetooth address: " + mDeviceAddress, Toast.LENGTH_SHORT).show();
            
            Intent gattServiceIntent = new Intent(activity, BluetoothLeService.class);
            activity.bindService(gattServiceIntent, mServiceConnection, Context.BIND_AUTO_CREATE);
            
            activity.registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
            
        	break;
        case REQUEST_ENABLE_BT:
        	// When the request to enable Bluetooth returns
            if (resultCode == Activity.RESULT_OK) {
                // Bluetooth is now enabled
            	Intent serverIntent = new Intent(activity, DeviceListActivity.class);
        		activity.startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);
            } else{
                // User did not enable Bluetooth or an error occured
                Toast.makeText(activity, "Bluetooth did not enable!", Toast.LENGTH_SHORT).show();
                activity.finish();
            }
        	break;
		}
		
	}
	
	
}
