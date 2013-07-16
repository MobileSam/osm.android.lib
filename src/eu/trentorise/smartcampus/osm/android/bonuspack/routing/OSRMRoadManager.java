package eu.trentorise.smartcampus.osm.android.bonuspack.routing;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import eu.trentorise.smartcampus.osm.android.bonuspack.utils.BonusPackHelper;
import eu.trentorise.smartcampus.osm.android.bonuspack.utils.HttpConnection;
import eu.trentorise.smartcampus.osm.android.bonuspack.utils.PolylineEncoder;
import eu.trentorise.smartcampus.osm.android.util.BoundingBoxE6;
import eu.trentorise.smartcampus.osm.android.util.GeoPoint;
import android.util.Log;

/** get a route between a start and a destination point.
 * It uses OSRM, a free open source routing service based on OpenSteetMap data. <br>
 * See https://github.com/DennisOSRM/Project-OSRM/wiki/Server-api<br>
 * 
 * It requests by default the OSRM demo site. 
 * Use setService() to request an other (for instance your own) OSRM service. <br> 
 * TODO: improve internationalization of instructions
 * @author M.Kergall
 */
public class OSRMRoadManager extends RoadManager {

	static final String OSRM_SERVICE = "http://router.project-osrm.org/viaroute?";
	//Note that the result of OSRM is quite close to Cloudmade NavEngine format:
	//http://developers.cloudmade.com/wiki/navengine/JSON_format

	protected String mServiceUrl;
	protected String mUserAgent;
	
	/** mapping from OSRM directions to MapQuest maneuver IDs: */
	static final HashMap<String, Integer> MANEUVERS;
	static {
		MANEUVERS = new HashMap<String, Integer>();
		MANEUVERS.put("0", 0); //No instruction
		MANEUVERS.put("1", 1); //Continue
		MANEUVERS.put("2", 6); //Slight right
		MANEUVERS.put("3", 7); //Right
		MANEUVERS.put("4", 8); //Sharp right
		MANEUVERS.put("5", 12); //U-turn
		MANEUVERS.put("6", 5); //Sharp left
		MANEUVERS.put("7", 4); //Left
		MANEUVERS.put("8", 3); //Slight left
		MANEUVERS.put("9", 24); //Arrived (at waypoint)
		MANEUVERS.put("10", 24); //"Head" => used by OSRM as the start node. Considered here as a "waypoint". 
		MANEUVERS.put("11-1", 27); //Round-about, 1st exit
		MANEUVERS.put("11-2", 28); //2nd exit, etc ...
		MANEUVERS.put("11-3", 29);
		MANEUVERS.put("11-4", 30);
		MANEUVERS.put("11-5", 31);
		MANEUVERS.put("11-6", 32);
		MANEUVERS.put("11-7", 33);
		MANEUVERS.put("11-8", 34); //Round-about, 8th exit
		MANEUVERS.put("15", 24); //Arrived
	}
	
	//From: Project-OSRM-Web / WebContent / localization / OSRM.Locale.en.js
	// driving directions
	// %s: road name
	// %d: direction => removed
	// <*>: will only be printed when there actually is a road name
	static final HashMap<String, Object> DIRECTIONS;
	static {
		DIRECTIONS = new HashMap<String, Object>();
		HashMap<String, String> directions;
		
		directions = new HashMap<String, String>();
		DIRECTIONS.put("en", directions);
		directions.put("0", "Unknown instruction< on %s>");
		directions.put("1","Continue< on %s>");
		directions.put("2","Turn slight right< on %s>");
		directions.put("3","Turn right< on %s>");
		directions.put("4","Turn sharp right< on %s>");
		directions.put("5","U-Turn< on %s>");
		directions.put("6","Turn sharp left< on %s>");
		directions.put("7","Turn left< on %s>");
		directions.put("8","Turn slight left< on %s>");
		directions.put("9","You have reached a waypoint of your trip");
		directions.put("10","<Go on %s>");
		directions.put("11-1","Enter roundabout and leave at first exit< on %s>");
		directions.put("11-2","Enter roundabout and leave at second exit< on %s>");
		directions.put("11-3","Enter roundabout and leave at third exit< on %s>");
		directions.put("11-4","Enter roundabout and leave at fourth exit< on %s>");
		directions.put("11-5","Enter roundabout and leave at fifth exit< on %s>");
		directions.put("11-6","Enter roundabout and leave at sixth exit< on %s>");
		directions.put("11-7","Enter roundabout and leave at seventh exit< on %s>");
		directions.put("11-8","Enter roundabout and leave at eighth exit< on %s>");
		directions.put("11-9","Enter roundabout and leave at nineth exit< on %s>");
		directions.put("15","You have reached your destination");
		
		directions = new HashMap<String, String>();
		DIRECTIONS.put("it", directions);
		directions.put("0","Istruzione sconosciuta< sur %s>");
		directions.put("1","Continua< sur %s>");
		directions.put("2","Gira leggermente a destra< sur %s>");
		directions.put("3","Gira a destra< sur %s>");
		directions.put("4","Gira molto a destra< sur %s>");
		directions.put("5","Fai inversione a U< sur %s>");
		directions.put("6","Gira fortemente a sinistra< sur %s>");
		directions.put("7","Gira a sinistra< sur %s>");
		directions.put("8","Gira leggermente a sinistra< sur %s>");
		directions.put("9","Hai raggiunto una tappa del tuo percorso");
		directions.put("10","<Vai %s>");
		directions.put("11-1","Fai la rotonda e prendi la prima uscita< sur %s>");
		directions.put("11-2","Fai la rotonda e prendi la seconda uscita< sur %s>");
		directions.put("11-3","Fai la rotonda e prendi la terza uscita< sur %s>");
		directions.put("11-4","Fai la rotonda e prendi la quarta uscita< sur %s>");
		directions.put("11-5","Fai la rotonda e prendi la quinta uscita< sur %s>");
		directions.put("11-6","Fai la rotonda e prendi la sesta uscita< sur %s>");
		directions.put("11-7","Fai la rotonda e prendi la settima uscita< sur %s>");
		directions.put("11-8","Fai la rotonda e prendi la ottava uscita< sur %s>");
		directions.put("11-9","Fai la rotonda e prendi la nona uscita< sur %s>");
		directions.put("15","hai raggiunto la tua destinazione");
		
		directions = new HashMap<String, String>();
		DIRECTIONS.put("pl", directions);
		directions.put("0", "Nieznana instrukcja<w %s>");
		directions.put("1","Kontynuuj jazdę<na %s>");
		directions.put("2","Skręć lekko w prawo<w %s>");
		directions.put("3","Skręć w prawo<w %s>");
		directions.put("4","Skręć ostro w prawo<w %s>");
		directions.put("5","Zawróć<na %s>");
		directions.put("6","Skręć ostro w lewo<w %s>");
		directions.put("7","Skręć w lewo<w %s>");
		directions.put("8","Skręć lekko w lewo<w %s>");
		directions.put("9","Dotarłeś do punktu pośredniego");
		directions.put("10","<Jedź %s>");
		directions.put("11-1","Wjedź na rondo i opuść je pierwszym zjazdem<w %s>");
		directions.put("11-2","Wjedź na rondo i opuść je drugim zjazdem<w %s>");
		directions.put("11-3","Wjedź na rondo i opuść je trzecim zjazdem<w %s>");
		directions.put("11-4","Wjedź na rondo i opuść je czwartym zjazdem<w %s>");
		directions.put("11-5","Wjedź na rondo i opuść je piątym zjazdem<w %s>");
		directions.put("11-6","Wjedź na rondo i opuść je szóstym zjazdem<w %s>");
		directions.put("11-7","Wjedź na rondo i opuść je siódmym zjazdem<w %s>");
		directions.put("11-8","Wjedź na rondo i opuść je ósmym zjazdem<w %s>");
		directions.put("11-9","Wjedź na rondo i opuść je dziewiątym zjazdem<w %s>");
		directions.put("15","Dotarłeś do celu podróży");
	}
	
	public OSRMRoadManager(){
		super();
		mServiceUrl = OSRM_SERVICE;
		mUserAgent = BonusPackHelper.DEFAULT_USER_AGENT; //set user agent to the default one. 
	}
	
	/** allows to request on an other site than OSRM demo site */
	public void setService(String serviceUrl){
		mServiceUrl = serviceUrl;
	}

	/** allows to send to OSRM service a user agent specific to the app, 
	 * instead of the default user agent of OSMBonusPack lib. 
	 */
	public void setUserAgent(String userAgent){
		mUserAgent = userAgent;
	}
	
	protected String getUrl(ArrayList<GeoPoint> waypoints){
		StringBuffer urlString = new StringBuffer(mServiceUrl);
		for (int i=0; i<waypoints.size(); i++){
			GeoPoint p = waypoints.get(i);
			urlString.append("&loc="+geoPointAsString(p));
		}
		urlString.append("&instructions=true&alt=false");
		urlString.append(mOptions);
		return urlString.toString();
	}

	@Override public Road getRoad(ArrayList<GeoPoint> waypoints) {
		String url = getUrl(waypoints);
		Log.d(BonusPackHelper.LOG_TAG, "OSRMRoadManager.getRoad:"+url);

		//String jString = BonusPackHelper.requestStringFromUrl(url);
		HttpConnection connection = new HttpConnection();
		connection.setUserAgent(mUserAgent);
		connection.doGet(url);
		String jString = connection.getContentAsString();
		connection.close();

		if (jString == null) {
			Log.e(BonusPackHelper.LOG_TAG, "OSRMRoadManager::getRoad: request failed.");
			return new Road(waypoints);
		}
		Locale l = Locale.getDefault();
		HashMap<String, String> directions = (HashMap<String, String>)DIRECTIONS.get(l.getLanguage());
		if (directions == null)
			directions = (HashMap<String, String>)DIRECTIONS.get("en");
		Road road = new Road();
		try {
			JSONObject jObject = new JSONObject(jString);
			String route_geometry = jObject.getString("route_geometry");
			road.mRouteHigh = PolylineEncoder.decode(route_geometry, 10);
			JSONArray jInstructions = jObject.getJSONArray("route_instructions");
			int n = jInstructions.length();
			RoadNode lastNode = null;
			for (int i=0; i<n; i++){
				JSONArray jInstruction = jInstructions.getJSONArray(i);
				RoadNode node = new RoadNode();
				int positionIndex = jInstruction.getInt(3);
				node.mLocation = road.mRouteHigh.get(positionIndex);
				node.mLength = jInstruction.getInt(2)/1000.0;
				node.mDuration = jInstruction.getInt(4); //Segment duration in seconds.
				String direction = jInstruction.getString(0);
				String roadName = jInstruction.getString(1);
				if (lastNode!=null && "1".equals(direction) && "".equals(roadName)){
					//node "Continue" with no road name is useless, don't add it
					lastNode.mLength += node.mLength;
					lastNode.mDuration += node.mDuration;
				} else {
					node.mManeuverType = getManeuverCode(direction);
					node.mInstructions = buildInstructions(direction, roadName, directions);
					//Log.d(BonusPackHelper.LOG_TAG, direction+"=>"+node.mManeuverType+"; "+node.mInstructions);
					road.mNodes.add(node);
					lastNode = node;
				}
			}
			JSONObject jSummary = jObject.getJSONObject("route_summary");
			road.mLength = jSummary.getInt("total_distance")/1000.0;
			road.mDuration = jSummary.getInt("total_time");
		} catch (JSONException e) {
			e.printStackTrace();
			return new Road(waypoints);
		}
		if (road.mRouteHigh.size()==0){
			//Create default road:
			road = new Road(waypoints);
		} else {
			road.buildLegs(waypoints);
			BoundingBoxE6 bb = BoundingBoxE6.fromGeoPoints(road.mRouteHigh);
			//Correcting osmdroid bug #359:
			road.mBoundingBox = new BoundingBoxE6(
				bb.getLatSouthE6(), bb.getLonWestE6(), bb.getLatNorthE6(), bb.getLonEastE6());
			road.mStatus = Road.STATUS_OK;
		}
		Log.d(BonusPackHelper.LOG_TAG, "OSRMRoadManager.getRoad - finished");
		return road;
	}
	
	protected int getManeuverCode(String direction){
		Integer code = MANEUVERS.get(direction);
		if (code != null)
			return code;
		else 
			return 0;
	}
	
	protected String buildInstructions(String direction, String roadName,
			HashMap<String, String> directions){
		if (directions == null)
			return null;
		direction = directions.get(direction);
		if (direction == null)
			return null;
		String instructions = null;
		if (roadName.equals(""))
			//remove "<*>"
			instructions = direction.replaceFirst("<[^>]*>", "");
		else {
			direction = direction.replace('<', ' ');
			direction = direction.replace('>', ' ');
			instructions = String.format(direction, roadName);
		}
		return instructions;
	}
}