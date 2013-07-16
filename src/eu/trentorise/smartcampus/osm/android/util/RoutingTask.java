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
 * You have to instace a new AsyncTask like this: <BR>
 * RoutingTask myTask = new RoutingTask(Context, MapView);<BR>
 * myTask.execute(ArrayList<GeoPoint>);<BR>
 * Address result = myTask.get();<BR>
 * @return Address if it works, else null
 * @author Dylan Stenico
 */
public class RoutingTask extends AsyncTask<ArrayList<GeoPoint>,Integer,PathOverlay> {

	ProgressDialog dialog;
    Context mContext;
    Road road;
    MapView mapView;
    /**
     * @param mContext
     * the application context
     * @param mapView
     * a MapView object
     */
	public RoutingTask(Context mContext, MapView mapView) {
		super();
		this.mContext = mContext;
		dialog = new ProgressDialog(mContext);
		this.mapView = mapView;
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
		try{
			PathOverlay roadOverlay = result;
			mapView.getOverlays().add(roadOverlay);
			mapView.invalidate();
		}
		catch(Exception e){
			e.printStackTrace();
		}
		if(dialog.isShowing())
			dialog.dismiss();
	}
}