package eu.trentorise.smartcampus.osm.android.util;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import eu.trentorise.smartcampus.osm.android.util.GeoPoint;

import android.app.ProgressDialog;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;
/**
 * A class for handling Geocoding and Reverse Geocoding with the android.location.Geocoder. Geocoding is the process of transforming a street address or other description of a 
 * location into a (latitude, longitude) coordinate. Reverse geocoding is the process of transforming a (latitude, longitude) coordinate into 
 * a (partial) address. The amount of detail in a reverse geocoded location description may vary, for example one might contain the full street 
 * address of the closest building, while another might contain only a city name and postal code.
 * @author dylan
 */
public class Geocoding {
	/**
	 * this class contains an AsyncTask that permits to convert a GeoPoint into an Address
	 * You have to instace a new AsyncTask like this:
	 * Geocoding.FromPointToAddress myTask = new Geocoding.FromPointToAddress(context);
	 * myTask.execute(double arg[0], double arg[1]);
	 * Address result = myTask.get();
	 * @return Address if it works, else null
	 */
	public static class FromPointToAddress extends AsyncTask<Double,Integer,Address> {
		ProgressDialog dialog;
		Context mContext;
		private boolean connectionError = false;
		private boolean addressError = false;
		List<Address> address;

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
			dialog = new ProgressDialog(mContext);
		}
		@Override
		protected void onPreExecute() {
			// TODO visualizzare il progress dialog
			dialog.setMessage("Loading...");
			dialog.show();
		}
		@Override
		protected Address doInBackground(Double... params) {
			Geocoder myGeocoder = new Geocoder(mContext,Locale.getDefault());
			try {
				address = myGeocoder.getFromLocation(params[0], params[1], 1);
				if(DEBUG_MODE)
					Log.d("indirizzo", params[0] + "  " + params[1]);
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
			if(!connectionError && !addressError){
				return address.get(0);
			}
			else{
				return null;
			}
		}

		@Override
		protected void onPostExecute(Address result) {
			// TODO togliere il progress dialog e, se andata bene, aggiornare la listView
			try{
				if(dialog.isShowing())
					dialog.dismiss();
			}catch(IllegalArgumentException e){
				e.printStackTrace();
			}
			if(DEBUG_MODE){
				try{
					Log.d("indirizzo", result.getAddressLine(0));
					Log.d("indirizzo", result.getAddressLine(1));
					Log.d("indirizzo", result.getAddressLine(2));
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

	/**
	 * this class contains an AsyncTask that permits to convert an Address into a GeoPoint
	 * You have to instace a new AsyncTask like this:
	 * Geocoding.FromAddressToPoint myTask = new Geocoding.FromAddressToPoint(context);
	 * myTask.execute(String address);
	 * GeoPoint result = myTask.get();
	 * @return GeoPoint if it works, else null
	 */

	public static class FromAddressToPoint extends AsyncTask<String,Integer,GeoPoint> {
		ProgressDialog dialog;
		Context mContext;
		String myAddress;
		boolean connectionError = false;
		boolean addressError = false;

		private boolean DEBUG_MODE = false;

		/**
		 * 
		 * @param mContext
		 * the Application Context
		 * @param debug
		 * set as true only to enter in debug mode and let the application write on the Log
		 */
		public FromAddressToPoint(Context mContext, boolean debug) {
			super();
			DEBUG_MODE = debug;
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
		protected GeoPoint doInBackground(String... params){
			myAddress = params[0];
			Geocoder myGeocoder = new Geocoder(mContext,Locale.ITALY);
			List<Address> risultati = null;
			Address address = null;
			try {
				risultati = myGeocoder.getFromLocationName(params[0], 1);
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
			if(connectionError || addressError){
				return null;
			}
			else{
				address = risultati.get(0);
				if(DEBUG_MODE)
					Log.d("COORDINATES", (double)address.getLatitude() + "    " +(double)address.getLongitude());
				return new GeoPoint((double)address.getLatitude(),(double)address.getLongitude());
			}
		}

		@Override
		protected void onPostExecute(GeoPoint result) {
			// TODO togliere il progress dialog e, se andata bene, aggiornare la listView

			if(dialog.isShowing())
				dialog.dismiss();
			if     (connectionError)  Toast.makeText(mContext, "Connection Error", Toast.LENGTH_SHORT).show();
			else if(addressError)     Toast.makeText(mContext, "Address Error",    Toast.LENGTH_SHORT).show();
		}
	}

}