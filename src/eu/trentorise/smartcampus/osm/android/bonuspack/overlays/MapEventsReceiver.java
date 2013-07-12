package eu.trentorise.smartcampus.osm.android.bonuspack.overlays;


import eu.trentorise.smartcampus.osm.android.api.IGeoPoint;

/**
 * Interface for objects that need to handle map events thrown by a MapEventsOverlay. 
 * @see MapEventsOverlay
 * @author M.Kergall
 */
public interface MapEventsReceiver {

	/**
	 * @param p the position where the event occurred. 
	 * @return true if the event has been "consumed" and should not be handled by other objects. 
	 */
	boolean singleTapUpHelper(IGeoPoint p);
	
	/**
	 * @param p the position where the event occurred. 
	 * @return true if the event has been "consumed" and should not be handled by other objects. 
	 */
	boolean longPressHelper(IGeoPoint p);	
}