package eu.trentorise.smartcampus.osm.android.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

import eu.trentorise.smartcampus.osm.android.bonuspack.location.GeocoderNominatim;
import eu.trentorise.smartcampus.osm.android.util.GeoPoint;

import android.app.ProgressDialog;
import android.content.Context;
import android.location.Address;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;
/**
 * A class for handling Geocoding and Reverse Geocoding with the osm.android.bonuspack.location.GeocoderNominatim;. Geocoding is the process of transforming a street address or other description of a 
 * location into a (latitude, longitude) coordinate. Reverse geocoding is the process of transforming a (latitude, longitude) coordinate into 
 * a (partial) address. The amount of detail in a reverse geocoded location description may vary, for example one might contain the full street 
 * address of the closest building, while another might contain only a city name and postal code.
 * @author Dylan Stenico
 */
public class OSMGeocoding {

	private static class FromPointToAddress extends AsyncTask<GeoPoint,Integer,List<Address>> {
		//ProgressDialog dialog;
		Context mContext;
		private boolean connectionError = false;
		private boolean addressError = false;

		private boolean DEBUG_MODE = false;

		/**
		 * @param mContext
		 * the Application Context
		 * @param debug
		 * set as true only to enter in debug mode and let the application write on the Log
		 */
		public FromPointToAddress(Context mContext, boolean debug) {
			super();
			DEBUG_MODE = debug;
			this.mContext = mContext;
			//dialog = new ProgressDialog(mContext);
		}
		@Override
		protected void onPreExecute() {
			// TODO visualizzare il progress dialog
			//dialog.setMessage("Loading...");
			//dialog.show();
		}
		@Override
		protected List<Address> doInBackground(GeoPoint... params) {
			GeocoderNominatim myGeocoder = new GeocoderNominatim(mContext,Locale.getDefault());
			List<Address> address = null;
			try {
				address = myGeocoder.getFromLocation(params[0].getLatitudeE6()/1E6, params[0].getLongitudeE6()/1E6, 5);
				if(DEBUG_MODE && address != null)
					Log.d("indirizzo", params[0].getLatitudeE6() / 1E6 + "  " + params[0].getLongitudeE6() / 1E6);
			}
			catch (IOException e) {
				connectionError = true;
				e.printStackTrace();
			}
			catch(IllegalArgumentException e){
				addressError = true;
				e.printStackTrace();
			}
			if((address == null || address.size() == 0) && !connectionError) addressError = true;
			return address;
		}

		@Override
		protected void onPostExecute(List<Address> result) {
			// TODO togliere il progress dialog e, se andata bene, aggiornare la listView
//			try{
//				if(dialog.isShowing())
//					dialog.dismiss();
//			}catch(IllegalArgumentException e){
//				e.printStackTrace();
//			}
			if(DEBUG_MODE && result != null){
				try{
					for(Address mResult : result){
						Log.d("indirizzo", "*************************");
						Log.d("indirizzo", mResult.getAddressLine(0));
						Log.d("indirizzo", mResult.getAddressLine(1));
						Log.d("indirizzo", mResult.getAddressLine(2));
					}
				}
				catch(IllegalArgumentException e){
					e.printStackTrace();
				}
				catch(NullPointerException e){
					e.printStackTrace();
				}}
			if     (connectionError)  Toast.makeText(mContext, "Connection Error", Toast.LENGTH_SHORT).show();
			else if(addressError)     Toast.makeText(mContext, "Address Error",    Toast.LENGTH_SHORT).show();
		}
	}

	private static class FromAddressToPoint extends AsyncTask<String,Integer,ArrayList<GeoPoint>> {
		ProgressDialog dialog;
		Context mContext;
		boolean connectionError = false;
		boolean addressError = false;

		/**
		 * @param mContext
		 * the Application context
		 */
		public FromAddressToPoint(Context mContext) {
			super();
			this.mContext = mContext;
			dialog = new ProgressDialog(mContext);
		}

		@Override
		protected void onPreExecute() {
			// TODO visualizzare il progress dialog
			dialog.setMessage("Loading...");
			dialog.show();
		}
		@Override
		protected ArrayList<GeoPoint> doInBackground(String... params){
			GeocoderNominatim myGeocoder = new GeocoderNominatim(mContext,Locale.ITALY);
			List<Address> risultati = null;
			ArrayList<GeoPoint> toReturn = null;
			try {
				risultati = myGeocoder.getFromLocationName(params[0], 5);
			} 
			catch (IOException e) {
				connectionError = true;
				e.printStackTrace();
			}
			catch(IllegalArgumentException e){
				addressError = true;
				e.printStackTrace();
			}
			if((risultati == null || risultati.size() == 0) && !connectionError) addressError = true;
			if(!connectionError && !addressError){
				toReturn = new ArrayList<GeoPoint>();
				for(int i = 0; i < risultati.size(); i++){
					Address address = risultati.get(i);
					//Log.d("COORDINATES", (double)address.getLatitude() + "    " +(double)address.getLongitude());
					toReturn.add(new GeoPoint((double)address.getLatitude(),(double)address.getLongitude()));
				}
			}
			return toReturn;
		}

		@Override
		protected void onPostExecute(ArrayList<GeoPoint> result) {
			// TODO togliere il progress dialog e, se andata bene, aggiornare la listView

			if(dialog.isShowing())
				dialog.dismiss();
			if     (connectionError)  Toast.makeText(mContext, "Connection Error", Toast.LENGTH_SHORT).show();
			else if(addressError)     Toast.makeText(mContext, "Address Error",    Toast.LENGTH_SHORT).show();
		}
	}
	/**
	 * Returns an array of Addresses that are known to describe the area immediately surrounding the given latitude and longitude. The returned addresses will be localized for the locale provided to this class's constructor.
	 * @param mAddress
	 * a String describes the location
	 * @param mContext
	 * the Application context
	 * @return
	 * ArrayList of GeoPoint
	 */
	public static ArrayList<GeoPoint> FromAddressToPoint(String mAddress, Context mContext){
		OSMGeocoding.FromAddressToPoint getPoint = new OSMGeocoding.FromAddressToPoint(mContext);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
			getPoint.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, mAddress);
		else
			getPoint.execute(mAddress);
		ArrayList<GeoPoint> result = null;
		try {
			result = getPoint.get();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * Returns a list of Addresses that are known to describe the named location, which may be a place name such as "Dalvik, Iceland", an address such as "1600 Amphitheatre Parkway, Mountain View, CA", 
	 * an airport code such as "SFO", etc.. .
	 * @param mPoint
	 * a Point describe the position
	 * @param mContext
	 * the Application context
	 * @return
	 * List of Address
	 */
	public static List<Address> FromPointToAddress(GeoPoint mPoint, Context mContext){
		OSMGeocoding.FromPointToAddress getAddress = new OSMGeocoding.FromPointToAddress(mContext, false);
		
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
			getAddress.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, mPoint);
		else
			getAddress.execute(mPoint);
		
		List<Address> result = null;
		try {
			result = getAddress.get();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}
}
