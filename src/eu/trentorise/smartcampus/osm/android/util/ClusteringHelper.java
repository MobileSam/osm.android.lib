package eu.trentorise.smartcampus.osm.android.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.util.Log;
import android.util.SparseArray;
import eu.trentorise.smartcampus.osm.android.ResourceProxy;
import eu.trentorise.smartcampus.osm.android.ResourceProxy.bitmap;
import eu.trentorise.smartcampus.osm.android.views.MapView;
import eu.trentorise.smartcampus.osm.android.views.overlay.ItemizedIconOverlay;
import eu.trentorise.smartcampus.osm.android.views.overlay.OverlayItem;

public class ClusteringHelper {

	private static final String TAG = "MapManager.ClusteringHelper";

	static ResourceProxy mProxy;
	Context context;
	private static final int DENSITY_X = 10;
	private static final int DENSITY_Y = 15;
	public static final String TITLE_CLUSTERED = "clusteredmarker";
	private static List<List<List<OverlayItem>>> grid = new ArrayList<List<List<OverlayItem>>>();
	private static SparseArray<int[]> item2group = new SparseArray<int[]>();
	public synchronized static <T extends OverlayItem, Item extends OverlayItem> List<T> cluster(Context mContext, MapView map,	Collection<Item> objects) {
		mProxy = map.getResourceProxy();
		item2group.clear();
		// 2D array with some configurable, fixed density
		grid.clear();

		for (int i = 0; i <= DENSITY_X; i++) {
			ArrayList<List<OverlayItem>> column = new ArrayList<List<OverlayItem>>(DENSITY_Y + 1);
			for (int j = 0; j <= DENSITY_Y; j++) {
				column.add(new ArrayList<OverlayItem>());
			}
			grid.add(column);
		}

		BoundingBoxE6 bb = map.getBoundingBox();
		GeoPoint lu = new GeoPoint(bb.getLatNorthE6(),bb.getLonWestE6());
		GeoPoint rd = new GeoPoint(bb.getLatSouthE6(),bb.getLonEastE6());
		int step = (int) (Math.abs((lu.getLongitudeE6()) - (rd.getLongitudeE6())) / DENSITY_X);
		// compute leftmost bound of the affected grid:
		// this is the bound of the leftmost grid cell that intersects
		// with the visible part
		int startX = (int) ((lu.getLongitudeE6()) - (lu.getLongitudeE6() % step));
		if (lu.getLongitudeE6() < 0) {
			startX -= step;
		}
		// compute bottom bound of the affected grid

		int startY = (int) ((rd.getLatitudeE6()) - ((rd.getLatitudeE6()) % step));

		if (lu.getLatitudeE6() < 0) {

			startY -= step;

		}
		int endX = startX + (DENSITY_X + 1) * step;

		int endY = startY + (DENSITY_Y + 1) * step;


		int idx = 0;

		try {
			for (Item basicObject : objects) {
				GeoPoint objLatLng = getGeoPointFromBasicObject(basicObject);
				if (objLatLng != null && (objLatLng.getLongitudeE6() >= startX) && (objLatLng.getLongitudeE6() <= endX)	&& (objLatLng.getLatitudeE6() >= startY) && (objLatLng.getLatitudeE6() <= endY)) {
					int binX = (int) (Math.abs((objLatLng.getLongitudeE6()) - startX) / step);
					int binY = (int) (Math.abs((objLatLng.getLatitudeE6()) - startY) / step);
					item2group.put(idx, new int[] { binX, binY });
					// just push the reference
					grid.get(binX).get(binY).add(basicObject);
				}
				idx++;
			}
		} catch (ConcurrentModificationException ex) {
			Log.e(TAG, ex.toString());
		}
		if (map.getZoomLevel() == map.getMaxZoomLevel()) {
			for (int i = 0; i < grid.size(); i++) {
				for (int j = 0; j < grid.get(0).size(); j++) {
					List<OverlayItem> curr = grid.get(i).get(j);
					if (curr.size() == 0)
						continue;
					if (i > 0) {
						if (checkDistanceAndMerge(i - 1, j, curr))
							continue;
					}
					if (j > 0) {
						if (checkDistanceAndMerge(i, j - 1, curr))
							continue;
					}
					if (i > 0 && j > 0) {
						if (checkDistanceAndMerge(i - 1, j - 1, curr))
							continue;
					}

				}
			}

		}

		// generate markers

		List<T> markers = new ArrayList<T>();


		for (int i = 0; i < grid.size(); i++) {

			for (int j = 0; j < grid.get(i).size(); j++) {

				List<OverlayItem> markerList = grid.get(i).get(j);

				if (markerList.size() > 1) {

					markers.add((T) createGroupMarker(mContext, map, markerList, i, j));

				} else if (markerList.size() == 1) {

					// draw single marker
					markers.add( (T) createSingleMarker(mContext,markerList.get(0), i, j));

				}
			}

		}

		return markers;

	}

	public static <T extends OverlayItem> void render(MapView map, List<T> markers) {

			map.addMarkers((ArrayList<T>) markers);
			if(map.getOverlays().get(map.getOverlays().size()-2) instanceof ItemizedIconOverlay<?>)
				map.getOverlays().remove(map.getOverlays().size()-2);
	}


	private static <T extends OverlayItem> T createSingleMarker(Context mContext,T item, int x, int y) {

		GeoPoint latLng = getGeoPointFromBasicObject(item);

		Bitmap icon = mProxy.getBitmap(bitmap.marker_poi_generic);
		Drawable bd = new BitmapDrawable(writeOnMarker(mContext, icon,""));

		T marker =  (T) new OverlayItem(x + ":" + y,"",latLng);
		marker.setMarker(bd);

		//Log.d(TAG,"single");
		return marker;
	}

	private static <T extends OverlayItem> T createGroupMarker(Context mContext, MapView map, List<T> markerList, int x,int y) {
		T item = markerList.get(0);
		GeoPoint latLng = getGeoPointFromBasicObject(item);
		Bitmap icon = mProxy.getBitmap(bitmap.marker_poi_generic);
		Drawable bd = new BitmapDrawable(writeOnMarker(mContext, icon,Integer.toString(markerList.size())));
		T marker =  (T) new OverlayItem(x + ":" + y,"",latLng);
		marker.setMarker(bd);
		//Log.d(TAG,marker.getTitle());
		return marker;

	}

	private static Bitmap writeOnMarker(Context mContext, Bitmap icon, String text) {

		float scale = mContext.getResources().getDisplayMetrics().density;
		Bitmap bitmap = icon;
		Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
		paint.setTextAlign(Align.CENTER);
		paint.setTextSize(scale * 14);
		paint.setAntiAlias(true);
		paint.setARGB(255, 255, 255, 255);
		Canvas canvas = new Canvas(bitmap);
		Rect bounds = new Rect();
		paint.getTextBounds(text, 0, text.length(), bounds);
		float x = bitmap.getWidth() / 2;
		float y = bitmap.getHeight() / 2;
		canvas.drawText(text, x, y, paint);
		return bitmap;

	}

	public static List<OverlayItem> getFromGridId(String id) {
		try {
			String[] parsed = id.split(":");
			int x = Integer.parseInt(parsed[0]);
			int y = Integer.parseInt(parsed[1]);
			return grid.get(x).get(y);
		} catch (Exception e) {
			return null;
		}
	}


	private static  <T extends OverlayItem> boolean checkDistanceAndMerge(int i, int j, List<T> curr) {
		List<OverlayItem> src = grid.get(i).get(j);
		if (src.size() == 0) {
			return false;
		}
		GeoPoint srcLatLng = getGeoPointFromBasicObject(src.get(0));
		GeoPoint currLatLng = getGeoPointFromBasicObject(curr.get(0));

		if (srcLatLng != null && currLatLng != null) {
			float[] dist = new float[3];
			Location.distanceBetween(srcLatLng.getLatitudeE6() / 1E6, srcLatLng.getLongitudeE6() / 1E6, currLatLng.getLatitudeE6() / 1E6, currLatLng.getLongitudeE6() / 1E6,dist);

			if (dist[0] < 20) {
				src.addAll(curr);
				curr.clear();
				return true;
			}
		}
		return false;

	}

	private static <T extends OverlayItem> GeoPoint getGeoPointFromBasicObject(T object) {

		GeoPoint geoPoint = null;
		geoPoint = object.getPoint();
		return geoPoint;
	}

}
