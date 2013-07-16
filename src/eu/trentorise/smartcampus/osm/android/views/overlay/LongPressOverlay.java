package eu.trentorise.smartcampus.osm.android.views.overlay;


import eu.trentorise.smartcampus.osm.android.views.MapView;
import eu.trentorise.smartcampus.osm.android.views.overlay.Overlay;

import android.content.Context;
import android.graphics.Canvas;
import android.util.Log;
import android.view.MotionEvent;

/**
 * A class for handling the long press gesture on the map(over 1000ms). You have to Override the OnLongPress(MotionEvent event) method. 
 * @author Dylan Stenico
 *
 */
public class LongPressOverlay extends Overlay
{
	long time = 0;
	float x, y;
	final float deltaX = 0;
	final float deltaY = 0;
	public Context mContext;
	public MapView mapView;
	
	public LongPressOverlay(Context ctx) {
		super(ctx);
		// TODO Auto-generated constructor stubs
		mContext = ctx;
	}


	@Override
	public boolean onTouchEvent(MotionEvent event, MapView mapView) 
	{   
		this.mapView = mapView;
		//---when user lifts his finger---
		//Log.d("motionEvent", Integer.toString(event.getPointerCount()));
		if (event.getAction() == event.ACTION_DOWN && event.getPointerCount() == 1){
			time = System.currentTimeMillis();
			x = event.getX();
			y = event.getY();
		}
		else if(event.getAction() == event.ACTION_UP && (System.currentTimeMillis() - time >= 1000)&& event.getPointerCount() == 1)
		{
			if((Math.abs(event.getX() - x) <= deltaX) && (Math.abs(event.getY() - y) <= deltaY))
			{
				onLongPressGesture(event);
				return true;
			}
			else if(event.getPointerCount() > 1)
				time = System.currentTimeMillis();
		}
		return false;
	}
	
/**
 * What to do when you make a long press on the map.
 * @param mapView
 * @param event
 */
	public void onLongPressGesture(MotionEvent event){
	}

	@Override
	protected void draw(Canvas arg0, MapView arg1, boolean arg2) {
		// TODO Auto-generated method stub

	}
}