package eu.trentorise.smartcampus.osm.android.util;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import android.content.Context;
import android.os.AsyncTask;
import eu.trentorise.smartcampus.osm.android.bonuspack.routing.MapQuestRoadManager;
import eu.trentorise.smartcampus.osm.android.bonuspack.routing.Road;
import eu.trentorise.smartcampus.osm.android.bonuspack.routing.RoadNode;
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
public class GetInstructions{

	public static ArrayList<RoadNode> get(ArrayList<GeoPoint> waypoints, Context mContext){

		ArrayList<RoadNode> toReturn = null;
		RoutingTask route = new RoutingTask(mContext);
		route.execute(waypoints);
		Road mRoad = null;
		try {
			mRoad = route.get();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(mRoad != null){
			toReturn = new ArrayList<RoadNode>();
			for (int i=0; i<mRoad.mNodes.size(); i++){
				toReturn.add(mRoad.mNodes.get(i));
			}
		}
		return toReturn;
	}

	private static class RoutingTask extends AsyncTask<ArrayList<GeoPoint>,Integer,Road> {

		private Context mContext;
		//private ProgressDialog dialog;
		private static Road road;

		public RoutingTask(Context context) {
			super();
			mContext = context;
			//dialog = new ProgressDialog(mContext);
		}

		@Override
		protected void onPreExecute() {
			// TODO visualizzare il progress dialog
			//dialog.setMessage("Loading...");
			//dialog.show();
		}

		@Override
		protected Road doInBackground(ArrayList<GeoPoint>... params) {

			MapQuestRoadManager roadManager = new MapQuestRoadManager();
			road = roadManager.getRoad(params[0]);
			roadManager.addRequestOption("routeType=pedestrian");
			return road;
		}

		@Override
		protected void onPostExecute(Road result) {
			//if(dialog.isShowing())
				//dialog.dismiss();
		}
	}
	public static String fromKilometersToMeters(double kilometers){
		String toReturn = "";
		int km = (int) Math.floor(kilometers);
		int m = (int) ((kilometers - km) * 1000);
		if(km > 0) toReturn += km + "km ";
		return toReturn + m + "m";
	}
	public static String fromSecondToString(int second){
		String toReturn = "";
		int sec = second % 60;
		int min = ((second - sec) / 60) % 60;
		int hour =  (int) Math.floor(second / 3600);
		if(hour > 0) toReturn += hour +"h ";
		if(min > 0)  toReturn += min +"m ";
		return toReturn + sec + "s";
	}
}