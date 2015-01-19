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
import android.widget.TextView;
import ubicomp.bioscope.R;


public class MobilityFragment extends Fragment{
	
	private View view;
	private ChartView chartView;
	
	private Timer timerForPlot = new Timer();

	private int elapsedSeconds = 0;
	private int totalSteps = 0;

	private TextView chartSwitchSecond, chartSwitchMinute, chartSwitchDay;
	private TextView stepsPerSecValue;

	private TextView stepsText;
	
    private TimerTask task = new TimerTask(){
        @Override
        public void run() {
        	elapsedSeconds++;
        	if(elapsedSeconds <= 90){
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
			
		
			

			// Random rand = new Random();
			// chartView.drawData( 400 + rand.nextInt(200) - 100 );
			int tempInt = (int) ( Double.parseDouble( stepsPerSecValue.getText().toString() ) );
			chartView.drawData( 400 + 50 * tempInt );
			
			
		 }
    };
	
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        timerForPlot.scheduleAtFixedRate(task, 0, 1000);

        
    }
	
	
	@Override
	public View onCreateView (LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
		
		view = inflater.inflate(R.layout.fragment_mobility, container, false);
		chartView = (ChartView) view.findViewById(R.id.chartView);
		
		chartSwitchSecond = (TextView) view.findViewById(R.id.text_second);
		chartSwitchMinute = (TextView) view.findViewById(R.id.text_minute);
		chartSwitchDay = (TextView) view.findViewById(R.id.text_day);
		chartSwitchSecond.setOnClickListener(new ChartSecondOnClickListener());
        chartSwitchMinute.setOnClickListener(new ChartMinuteOnClickListener());
        chartSwitchDay.setOnClickListener(new ChartDayOnClickListener());

		stepsText = (TextView) view.findViewById(R.id.text_steps);
		stepsPerSecValue = (TextView) view.findViewById(R.id.text_steps_per_second_value);

		return view;
	}
	
	
	private class ChartSecondOnClickListener implements View.OnClickListener {

		@Override
		public void onClick(View v) {
			chartSwitchSecond.setBackgroundResource(R.drawable.switch_plot_bg_selected);
			chartSwitchMinute.setBackgroundResource(R.drawable.switch_plot_bg);
			chartSwitchDay.setBackgroundResource(R.drawable.switch_plot_bg);
		}
	}
	private class ChartMinuteOnClickListener implements View.OnClickListener {

		@Override
		public void onClick(View v) {
			chartSwitchSecond.setBackgroundResource(R.drawable.switch_plot_bg);
			chartSwitchMinute.setBackgroundResource(R.drawable.switch_plot_bg_selected);
			chartSwitchDay.setBackgroundResource(R.drawable.switch_plot_bg);
		}
	}
	private class ChartDayOnClickListener implements View.OnClickListener {

		@Override
		public void onClick(View v) {
			chartSwitchSecond.setBackgroundResource(R.drawable.switch_plot_bg);
			chartSwitchMinute.setBackgroundResource(R.drawable.switch_plot_bg);
			chartSwitchDay.setBackgroundResource(R.drawable.switch_plot_bg_selected);
		}
	}
	
	public void updateTotalSteps(int steps){
		TextView stepTextView = (TextView) view.findViewById(R.id.text_steps);
		stepTextView.setText(steps + " steps");
	}
}
