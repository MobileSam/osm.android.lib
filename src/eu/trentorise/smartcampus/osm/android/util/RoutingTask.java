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

public class RoutingTask extends AsyncTask<ArrayList<GeoPoint>,Integer,PathOverlay> {

	ProgressDialog dialog;
    Context mContext;
    Road road;
    MapView mapView;

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