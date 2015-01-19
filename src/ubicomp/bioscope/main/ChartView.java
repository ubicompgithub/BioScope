package ubicomp.bioscope.main;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

public class ChartView extends View {
	
	private Paint min_paint = new Paint();
	private int cord_x;
	private ArrayList<Integer> cord_y = new ArrayList<Integer>();
	private static final int INITIAL_X = 100;
	private static final int INITIAL_Y = 400;
	private static final int INTERVAL = 45;
	private static final int MAX_DATA_PER_CHART = 30;
	private int tempData;
	
	private int count = 0; // FOR TEST!!!!!!!!!!!!!!!!!!!!!!!
	
	public ChartView(Context context, AttributeSet attrs) {
		super(context, attrs);
		min_paint.setColor(Color.RED);
		min_paint.setStrokeWidth(4);
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		// drawLineChart(canvas);
		drawLineChart(canvas, tempData);
	}

	public void drawData(int data){
		tempData = data;
		this.invalidate();
	}


	private void drawLineChart(Canvas canvas, int data){
		if ( cord_y.size() == 0){
	        cord_y.add(INITIAL_Y);
			return;
		}
		
        cord_y.add(data);
        cord_x = INITIAL_X;
        if( cord_y.size() > MAX_DATA_PER_CHART ){
			cord_y.remove(0);
		}
        
		for(int idx = 0; idx < cord_y.size()-2; idx++){
			canvas.drawLine(cord_x, cord_y.get(idx), cord_x + INTERVAL, cord_y.get(idx+1), min_paint);
			cord_x = cord_x + INTERVAL;
			
			// Log.d("drawLineChart", "MSG: " + cord_y.get(idx));  // FOR TEST!!!!!!!!!!!!!!!!!!!!!!!
		}
		
		count++;  // FOR TEST!!!!!!!!!!!!!!!!!!!!!!!
		// Log.d("drawLineChart", "============= " + count + " =====================");  // FOR TEST!!!!!!!!!!!!!!!!!!!!!!!
	}


	// private void drawLineChart(Canvas canvas) {
	// 	if ( cord_y.size() == 0){
	//         cord_y.add(INITIAL_Y);
	// 		return;
	// 	}
		
	// 	Random rand = new Random();
 //        cord_y.add( 400 + rand.nextInt(200) - 100 );
 //        cord_x = INITIAL_X;
 //        if( cord_y.size() > MAX_DATA_PER_CHART ){
	// 		cord_y.remove(0);
	// 	}
        
	// 	for(int idx = 0; idx < cord_y.size()-2; idx++){
	// 		canvas.drawLine(cord_x, cord_y.get(idx), cord_x + INTERVAL, cord_y.get(idx+1), min_paint);
	// 		cord_x = cord_x + INTERVAL;
			
	// 		Log.d("drawLineChart", "MSG: " + cord_y.get(idx));  // FOR TEST!!!!!!!!!!!!!!!!!!!!!!!
	// 	}
		
	// 	count++;  // FOR TEST!!!!!!!!!!!!!!!!!!!!!!!
	// 	Log.d("drawLineChart", "============= " + count + " =====================");  // FOR TEST!!!!!!!!!!!!!!!!!!!!!!!
	// }
}

