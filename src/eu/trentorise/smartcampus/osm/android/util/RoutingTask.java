package eu.trentorise.smartcampus.osm.android.util;

import java.util.ArrayList;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import eu.trentorise.smartcampus.osm.android.bonuspack.routing.MapQuestRoadManager;
import eu.trentorise.smartcampus.osm.android.bonuspack.routing.Road;
import eu.trentorise.smartcampus.osm.android.bonuspack.routing.RoadManager;
import eu.trentorise.smartcampus.osm.android.views.MapView;
import eu.trentorise.smartcampus.osm.android.views.overlay.PathOverlay;
/**
 * Class to get a route between a start and a destination point, going through a list of waypoints. It uses MapQuest open, public and free API, based on OpenStreetMap data. 
 * See http://open.mapquestapi.com/guidance <BR>
 * This class contains an AsyncTask that permits to get the route
 * You have to allocate a new AsyncTask like this: <BR>
 * RoutingTask myTask = new RoutingTask(Context, MapView, draw);<BR>
 * myTask.execute(ArrayList<GeoPoint>);<BR>
 * @return a PathOverlay
 * @author Dylan Stenico
 */
public class RoutingTask extends AsyncTask<ArrayList<GeoPoint>,Integer,PathOverlay> {

	ProgressDialog dialog;
	Context mContext;
	Road road;
	MapView mapView;
	boolean draw;
	/**
	 * @param mContext
	 * the application context
	 * @param mapView
	 * a MapView object
	 * @param draw
	 * set as true only to draw the path on the mapView. If you only want to get the PathOverley set as false and do task.get();
	 */
	public RoutingTask(Context mContext, MapView mapView, boolean draw) {
		super();
		this.mContext = mContext;
		dialog = new ProgressDialog(mContext);
		this.mapView = mapView;
		this.draw = draw;
	}

	@Override
	protected void onPreExecute() {
		// TODO visualizzare il progress dialog
		dialog.setMessage("Loading...");
		dialog.show();
	}

	@Override
	protected PathOverlay doInBackground(ArrayList<GeoPoint>... params) {
		RoadManager roadManager = new MapQuestRoadManager();
		road = roadManager.getRoad(params[0]);
		roadManager.addRequestOption("routeType=pedestrian");
		return RoadManager.buildRoadOverlay(road, mapView.getContext());
	}

	@Override
	protected void onPostExecute(PathOverlay result) {
		// TODO togliere il progress dialog e, se andata bene, aggiornare la listView
		if(draw){
			try{
				PathOverlay roadOverlay = result;
				mapView.getOverlays().add(roadOverlay);
				mapView.invalidate();
			}
			catch(Exception e){
				e.printStackTrace();
			}
		}
		if(dialog.isShowing())
			dialog.dismiss();
	}
}