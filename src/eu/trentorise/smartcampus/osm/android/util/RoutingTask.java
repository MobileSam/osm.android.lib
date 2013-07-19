package eu.trentorise.smartcampus.osm.android.util;

import java.net.SocketException;
import java.util.ArrayList;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.Log;
import eu.trentorise.smartcampus.osm.android.ResourceProxy;
import eu.trentorise.smartcampus.osm.android.ResourceProxy.bitmap;
import eu.trentorise.smartcampus.osm.android.bonuspack.overlays.ExtendedOverlayItem;
import eu.trentorise.smartcampus.osm.android.bonuspack.overlays.ItemizedOverlayWithBubble;
import eu.trentorise.smartcampus.osm.android.bonuspack.routing.MapQuestRoadManager;
import eu.trentorise.smartcampus.osm.android.bonuspack.routing.Road;
import eu.trentorise.smartcampus.osm.android.bonuspack.routing.RoadManager;
import eu.trentorise.smartcampus.osm.android.bonuspack.routing.RoadNode;
import eu.trentorise.smartcampus.osm.android.views.MapView;
import eu.trentorise.smartcampus.osm.android.views.overlay.OverlayItem;
import eu.trentorise.smartcampus.osm.android.views.overlay.PathOverlay;
/**
 * Class to get a route between a start and a destination point, going through a list of waypoints. It uses MapQuest open, public and free API, based on OpenStreetMap data. 
 * See http://open.mapquestapi.com/guidance
 * @author Dylan Stenico
 */
public class RoutingTask extends AsyncTask<ArrayList<GeoPoint>,Integer,Road> {

	private static Drawable icon;
	private Context mContext;
	//private ProgressDialog dialog;
	private Road road;
	ArrayList<GeoPoint> myPoints;
	private Locale mLocale;
	private String mRoadType;

	private RoutingTask(Context context, MapView mapView, Locale locale, String roadType) {
		super();
		this.mContext = context;
		//dialog = new ProgressDialog(mContext);
		if(mapView != null){
			ResourceProxy mProxy = mapView.getResourceProxy();
			icon = mProxy.getDrawable(bitmap.marker_node);
		}
		this.mLocale = locale;
		this.mRoadType = roadType;
	}
	/**
	 * @param waypoints
	 * points the road will pass through
	 * @param mContext
	 * tha context
	 * @param locale
	 * use e.g. Locale.ITALY
	 * @param roadType
	 * the type of route you want...pedestrian, bicycle e.g. use MapQuestRoadManager.PEDESTRIAN
	 * @return
	 * arrayList of RoadNode
	 */
	public static ArrayList<RoadNode> getRoadNodes(ArrayList<GeoPoint> waypoints, Context mContext, Locale locale, String roadType){
		ArrayList<RoadNode> toReturn = null;
		Road mRoad = null;

		RoutingTask myTask = new RoutingTask(mContext, null, locale, roadType);
		myTask.execute(waypoints);
		try {
			mRoad = myTask.get();
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
			tmp.mIconUrl = ("http://www.gambassigena.it/img/components/misc/piedi.png");
			tmp.mManeuverType = 99;
			for (int i=0; i<mRoad.mNodes.size(); i++){
				toReturn.add(mRoad.mNodes.get(i));
			}
		}
		return toReturn;
	}
	/**
	 * get the road between two or more waypoints
	 * @param waypoints
	 * @param mContext
	 * @param locale
	 * @param roadType
	 * @return
	 */
	public static Road getRoad(ArrayList<GeoPoint> waypoints, Context mContext, Locale locale, String roadType){
		Road mRoad = null;

		RoutingTask myTask = new RoutingTask(mContext, null, locale, roadType);
		myTask.execute(waypoints);
		try {
			mRoad = myTask.get();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return mRoad;
	}
	/**
	 * @param waypoints
	 * points the road will pass through
	 * @param mContext
	 * tha context
	 * @param locale
	 * use e.g. Locale.ITALY
	 * @param roadType
	 * the type of route you want...pedestrian, bicycle e.g. use MapQuestRoadManager.PEDESTRIAN
	 * @return
	 * true if it works, else false
	 */
	public static boolean drawPath(ArrayList<GeoPoint> waypoints, MapView mapView, Context mContext, Locale locale, String roadType){

		Road mRoad = null;

		RoutingTask myTask = new RoutingTask(mContext, mapView, locale, roadType);
		myTask.execute(waypoints);

		try {
			mRoad = myTask.get();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(mRoad != null){
			PathOverlay myOverlay = RoadManager.buildRoadOverlay(mRoad, mapView.getContext());
			if(true){//myOverlay.getNumberOfPoints() > 2){
				//draw path
				mapView.getOverlays().add(myOverlay);
				//add markers
				final ArrayList<ExtendedOverlayItem> roadItems = new ArrayList<ExtendedOverlayItem>();
				ItemizedOverlayWithBubble<ExtendedOverlayItem> roadNodes = new ItemizedOverlayWithBubble<ExtendedOverlayItem>(mContext, roadItems, mapView);
				mapView.getOverlays().add(roadNodes);

				for (int i=0; i<mRoad.mNodes.size(); i++){
					RoadNode node = mRoad.mNodes.get(i);
					Log.d("time", Double.toString(node.mDuration));
					Log.d("time", Integer.toString(i));  
					ExtendedOverlayItem nodeMarker = new ExtendedOverlayItem(node.mInstructions, "Time: " +fromSecondToString((int)node.mDuration)+ "\nLenght: " + fromKilometersToMeters(node.mLength), node.mLocation);
					nodeMarker.setMarkerHotspot(OverlayItem.HotspotPlace.BOTTOM_CENTER);
					nodeMarker.setMarker(icon);
					roadNodes.addItem(nodeMarker);
				}
				mapView.invalidate();
			}
			else
				return false;
		}
		else 
			return false;
		return true;
	}
	/**
	 * @param waypoints
	 * points the road will pass through
	 * @param mapView
	 * mapView
	 * @param mContext
	 * tha context
	 * @param locale
	 * use e.g. Locale.ITALY
	 * @param roadType
	 * the type of route you want...pedestrian, bicycle e.g. use MapQuestRoadManager.PEDESTRIAN
	 * @return
	 * true if it works, else null
	 */
	public static boolean drawPathWithoutMarkers(ArrayList<GeoPoint> waypoints, MapView mapView, Context mContext, Locale locale, String roadType){

		Road mRoad = null;

		RoutingTask myTask = new RoutingTask(mContext, mapView, locale, roadType);
		myTask.execute(waypoints);

		try {
			mRoad = myTask.get();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(mRoad != null){
			//draw path
			PathOverlay myOverlay = RoadManager.buildRoadOverlay(mRoad, mapView.getContext());
			if(myOverlay.getNumberOfPoints() > 2){
				mapView.getOverlays().add(myOverlay);
				mapView.invalidate();
			}
			else
				return false;
		}
		else 
			return false;
		return true;
	}

	@Override
	protected void onPreExecute() {
		// TODO visualizzare il progress dialog
		//dialog.setMessage("Loading...");
		//dialog.show();
	}

	@Override
	protected Road doInBackground(ArrayList<GeoPoint>... params) {

		RoadManager roadManager = new MapQuestRoadManager(mLocale, mRoadType, mContext);
		road = roadManager.getRoad(params[0]);
		return road;
	}

	@Override
	protected void onPostExecute(Road result) {
		//if(dialog.isShowing())
		//dialog.dismiss();
	}

	private static String fromKilometersToMeters(double kilometers){
		String toReturn = "";
		int km = (int) Math.floor(kilometers);
		int m = (int) ((kilometers - km) * 1000);
		if(km > 0) toReturn += km + "km ";
		return toReturn + m + "m";
	}
	private static String fromSecondToString(int second){
		String toReturn = "";
		int sec = second % 60;
		int min = ((second - sec) / 60) % 60;
		int hour =  (int) Math.floor(second / 3600);
		if(hour > 0) toReturn += hour +"h ";
		if(min > 0)  toReturn += min +"m ";
		return toReturn + sec + "s";
	}
}