package eu.trentorise.smartcampus.osm.android.bonuspack.routing;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import android.content.Context;
import android.util.Log;

import eu.trentorise.smartcampus.osm.android.bonuspack.routing.Road;
import eu.trentorise.smartcampus.osm.android.bonuspack.routing.RoadNode;
import eu.trentorise.smartcampus.osm.android.bonuspack.utils.PolylineEncoder;
import eu.trentorise.smartcampus.osm.android.util.BoundingBoxE6;
import eu.trentorise.smartcampus.osm.android.util.GeoPoint;


public class XMLParser {
	InputStream in_s;
	Context mContext;
	XMLParser(Context mContext,InputStream is){
		in_s = is;
		this.mContext =mContext;
	}

	public Road getRoad(){
		XmlPullParserFactory pullParserFactory;
		Road toRtn = null;
		try {
			pullParserFactory = XmlPullParserFactory.newInstance();
			XmlPullParser parser = pullParserFactory.newPullParser();
			parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
			parser.setInput(in_s, null);
			toRtn =parseXML(parser);
		} catch (XmlPullParserException e) {
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return toRtn;
	}
	private Road parseXML(XmlPullParser parser) throws XmlPullParserException,IOException{
		BoundingBoxE6 mBoundingbox = null;
		ArrayList<RoadNode> road = null;
		ArrayList<GeoPoint> myPoliLine = null;
		GeoPoint ul = null;
		GeoPoint lr = null;
		GeoPoint np = null;
		double nlat = 999;
		double nlon = 999;
		double lat = 999;
		double lon = 999;
		double nDistance = -1;
		double nTime = -1;
		boolean hasHighway = false;
		String informations = null;
		String iconUrl = null;
		int turnType = -1;
		int direction = -1;		
		int eventType = parser.getEventType();
		RoadNode node = null;
		Road fullroad = null;
		while (eventType != XmlPullParser.END_DOCUMENT){
			//Log.w("parser", "while");
			String name = null;
			switch (eventType){
			case XmlPullParser.START_DOCUMENT:
				//Log.w("parser", "START_DOCUMENT");
				break;
			case XmlPullParser.START_TAG:
				name = parser.getName();
				//Log.w("parser", "START_TAG");

				//if (name.equals("route")){
				//Log.w("parserName", name);
				//road = new ArrayList<RoadNode>();
				/*} else if (road != null){*/
				if(mBoundingbox == null){
					//Log.w("parserName", "boundingboxNull");
					if (name.equals("lat")){
						lat = Double.parseDouble(parser.nextText());
						//Log.w("bblat", Double.toString(lat));
					} else if (name.equals("lng")){
						lon = Double.parseDouble(parser.nextText());
						//Log.w("bblon", Double.toString(lon));
					}
				}
				if (name.equals("legs")){
					fullroad = new Road();
				} else if (fullroad != null){
					if (road == null && name.equals("distance")){
						fullroad.mLength=Double.parseDouble(parser.nextText());
						//Log.w("parserFullRoad", Double.toString(fullroad.mLength));
					}else if (road == null && name.equals("time")){
						fullroad.mDuration=Double.parseDouble(parser.nextText());
					}else if (road == null && name.equals("maneuvers")){
						road = new ArrayList<RoadNode>();
					}else if (road != null){
						if (np == null){
							if (name.equals("lat")){
								nlat = Double.parseDouble(parser.nextText());
								//Log.w("parserLat",Double.toString(nlat));
							}
							else if (name.equals("lng")){
								nlon = Double.parseDouble(parser.nextText());
								//Log.w("parserLon",Double.toString(nlon));
							}
						}
						if (name.equals("distance") && nDistance == -1){
							nDistance = Double.parseDouble(parser.nextText());
							//Log.w("parserDistance", Double.toString(nDistance));
						} else if (name.equals("time") && nTime == -1){
							nTime = Double.parseDouble(parser.nextText());
						}else if (name.equals("turnType") && turnType == -1){
							turnType = Integer.parseInt(parser.nextText());
						}else if (name.equals("direction") && direction == -1){
							direction = Integer.parseInt(parser.nextText());
						}else if (name.equals("narrative") && informations == null){
							informations = parser.nextText();
						}else if (name.equals("iconUrl") && iconUrl == null){
							iconUrl = parser.nextText();
							Log.d("url", iconUrl);
						}else if (name.equals("hasHighway")){
							hasHighway = Boolean.parseBoolean(parser.nextText());
						}else if(name.equals("shapePoints")){
							String shape = parser.nextText();
							fullroad.mRouteHigh = PolylineEncoder.decode(shape, 10);
						}
					}
				}	
				break;
			case XmlPullParser.END_TAG:
				name = parser.getName();
				//Log.w("endtag", name);
				if(name.equals("ul") && lat != 999 && lon!= 999){
					ul = new GeoPoint(lat,lon);
					lat = 999;
					lon = 999;
				}else if(name.equals("lr") && lat != 999 && lon!= 999){
					lr = new GeoPoint(lat,lon);  
					lat = 999;
					lon = 999;
				}else if(name.equals("boundingBox") && lr != null && ul != null){
					mBoundingbox = new BoundingBoxE6(ul.getLatitudeE6(),lr.getLongitudeE6(), lr.getLatitudeE6(), ul.getLongitudeE6());
					ul = null;
					lr = null;
				}else if(name.equals("startPoint") && nlat != 999 && nlon!= 999){
					//Log.d("geoPoint", "new GeoPoint");
					np = new GeoPoint(nlat, nlon);
					nlat = 999;
					nlon = 999;
				}else if(name.equals("maneuver") && node == null && np != null && road != null){
					node = new RoadNode();
					node.mDuration = nTime;
					node.mLength = nDistance;
					node.mInstructions = informations;
					node.mManeuverType = turnType;
					node.mLocation = np;
					np = null;
					informations = null;
					nTime = -1;
					nDistance = -1;
					turnType = -1;
					node.mIconUrl = iconUrl;
					node.mDirection = direction;
					road.add(node);
					iconUrl = null;
					direction = -1;
					//Log.e("road", "added");
					node = null;
				}else if(name.equals("maneuvers") && road != null){
					fullroad.mNodes = road;
				}else if(name.equals("legs")){
					fullroad.hasHighway = hasHighway;
				}
//				else if(name.equals("latLng") && myPoliLine != null){
//					//Log.e("Point" + myPoliLine.size(), nlat +"---" +nlon);
//					myPoliLine.add(new GeoPoint(nlat,nlon));
//				}
//				else if(name.equals("shapePoints")&& myPoliLine != null){
//					fullroad.mRouteHigh = myPoliLine;
//				}
			}
			eventType = parser.next();
			//Log.w("parser", Integer.toString(eventType));
		}
		return fullroad;
	}
}