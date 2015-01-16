package ubicomp.bioscope.main;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import ubicomp.bioscope.main.ChartView;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import ubicomp.bioscope.R;


public class MobilityFragment extends Fragment {
	
	private View view;
	private ChartView chartView;
	
	private Timer timerForPlot = new Timer();
	private int elapse = 0;
	
    private TimerTask task = new TimerTask(){
        @Override
        public void run() {
        	elapse++;
        	if(elapse <= 90){
        		Message message = new Message();
                message.what = 1;
                handler.sendMessage(message);
        	}
        	else{
        		timerForPlot.cancel();
        	}
        }
    };
    
    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler(){
		 public void handleMessage(Message msg){
			super.handleMessage(msg);
			// chartView.invalidate();

			Random rand = new Random();
			chartView.drawData( 400 + rand.nextInt(200) - 100 );
			
		 }
    };
	
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        timerForPlot.scheduleAtFixedRate(task, 0, 100);
        
    }
	
	
	@Override
	public View onCreateView (LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
		
		view = inflater.inflate(R.layout.fragment_mobility, container, false);
		chartView = (ChartView) view.findViewById(R.id.chartView);
		
		return view;
	}
}
