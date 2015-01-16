package ubicomp.bioscope.main;


import ubicomp.bioscope.R;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;


public class MainActivity extends ListActivity {

	// private TextView theText;
	private BluetoothLE ble = null;
	
	private Fragment[] fragments;
    private int previousPosition;
    private boolean[] isFragmentAdded;
	private FragmentManager fragmentManager;
	private FragmentTransaction fragmentTransaction;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		// theText = (TextView) findViewById(R.id.theText);
		
		// Create the sidebar_texts
		ArrayAdapter<CharSequence> arrAdapSidebar = ArrayAdapter.createFromResource(this, R.array.sidebar_texts, android.R.layout.simple_list_item_1);
		setListAdapter(arrAdapSidebar);
		
		ListView listview = getListView();
		listview.setOnItemClickListener(listViewOnItemClick);
		

		fragments = new Fragment[4];
		fragments[0] = new MobilityFragment();
		fragments[1] = new HeartRateFragment();
		fragments[2] = new BodyTemperatureFragment();
		fragments[3] = new SettingsFragment();

        previousPosition = -1;
        isFragmentAdded = new boolean[4];
        
 
        // New a BLE object
 		ble = new BluetoothLE(this);
 		ble.bleConnection();

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
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	private AdapterView.OnItemClickListener listViewOnItemClick = new AdapterView.OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            fragmentManager = getFragmentManager();
            fragmentTransaction = fragmentManager.beginTransaction();

            if( isFragmentAdded[position] ){
            	// theText.setText("Page: " + position + " Replace");
            	fragmentTransaction.hide(fragments[previousPosition]);
                fragmentTransaction.show(fragments[position]);
            }
            else{
                if(previousPosition != -1){
                	// theText.setText("Page: " + position + " Add");
                    isFragmentAdded[position] = true;
                    fragmentTransaction.hide(fragments[previousPosition]);
                    fragmentTransaction.add(R.id.fragment_frameLayout, fragments[position]);
                }
                else{
                    // theText.setText("Page: " + position + " Add");
                    isFragmentAdded[position] = true;
                    fragmentTransaction.add(R.id.fragment_frameLayout, fragments[position]);
                }
            }

            previousPosition = position;
			fragmentTransaction.commit();
			fragmentManager.executePendingTransactions();
		}
	};
	
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        
        ble.onBleActivityResult(requestCode, resultCode, data);
    }
}
