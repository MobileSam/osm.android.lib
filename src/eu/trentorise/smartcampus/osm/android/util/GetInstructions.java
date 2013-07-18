package eu.trentorise.smartcampus.osm.android.util;

import java.util.ArrayList;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

import android.content.Context;
import android.os.AsyncTask;
import eu.trentorise.smartcampus.osm.android.bonuspack.routing.MapQuestRoadManager;
import eu.trentorise.smartcampus.osm.android.bonuspack.routing.Road;
import eu.trentorise.smartcampus.osm.android.bonuspack.routing.RoadNode;
/**
 * Class to get the RoadNodes between a start and a destination point, going through a list of waypoints. It uses MapQuest open, public and free API, based on OpenStreetMap data. 
 * See http://open.mapquestapi.com/guidance <BR>
 * This class contains an AsyncTask that permits to get the RoadNodes
 * @author Dylan Stenico
 */
public class GetInstructions{
	private static Locale mLocale;
	private static String mRoadType;
	/**
	 * @param waypoints
	 * An ArrayList contains all the GeoPoint you want the route pass through
	 * @param mContext
	 * @param locale
	 * for the language. e.g. IT_it
	 * @param roadType
	 * use the constans e.g. MapQuestRoadManager.PEDESTRI
	 * @return
	 * ArrayList<RoadNodes> all the breakpoints
	 */
	public static ArrayList<RoadNode> get(ArrayList<GeoPoint> waypoints, Context mContext, Locale locale, String roadType){
		
		mLocale = locale;
		mRoadType = roadType;
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
			RoadNode tmp = new RoadNode();
			tmp.mDuration = mRoad.mDuration;
			tmp.mLength = mRoad.mLength;
			tmp.mInstructions = "All itinerary";
			toReturn.add(tmp);
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

			MapQuestRoadManager roadManager = new MapQuestRoadManager(mLocale, mRoadType);
			road = roadManager.getRoad(params[0]);
			return road;
		}

		@Override
		protected void onPostExecute(Road result) {
			//if(dialog.isShowing())
				//dialog.dismiss();
		}
	}
}