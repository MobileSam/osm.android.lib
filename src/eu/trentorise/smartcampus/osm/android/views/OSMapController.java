package eu.trentorise.smartcampus.osm.android.views;

import org.osmdroid.api.IGeoPoint;
import org.osmdroid.util.BoundingBoxE6;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapController;
import org.osmdroid.views.MapView;


public class OSMapController extends MapController {
	
	public OSMapController(OSMapView osmv) {
		 
		 super(osmv);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void animateTo(double latitude, double longitude) {
		// TODO Auto-generated method stub
		super.animateTo(latitude, longitude);
	}

	@Override
	public void animateTo(GeoPoint gp, AnimationType aAnimationType,
			int aSmoothness, int aDuration) {
		// TODO Auto-generated method stub
		super.animateTo(gp, aAnimationType, aSmoothness, aDuration);
	}

	@Override
	public void animateTo(GeoPoint gp, AnimationType aAnimationType) {
		// TODO Auto-generated method stub
		super.animateTo(gp, aAnimationType);
	}

	@Override
	public void animateTo(IGeoPoint point) {
		// TODO Auto-generated method stub
		super.animateTo(point);
	}

	@Override
	public void animateTo(int aLatitudeE6, int aLongitudeE6,
			AnimationType aAnimationType, int aSmoothness, int aDuration) {
		// TODO Auto-generated method stub
		super.animateTo(aLatitudeE6, aLongitudeE6, aAnimationType, aSmoothness,
				aDuration);
	}

	@Override
	public void animateTo(int aLatitudeE6, int aLongitudeE6,
			AnimationType aAnimationType) {
		// TODO Auto-generated method stub
		super.animateTo(aLatitudeE6, aLongitudeE6, aAnimationType);
	}

	@Override
	public void scrollBy(int x, int y) {
		// TODO Auto-generated method stub
		super.scrollBy(x, y);
	}

	@Override
	public void setCenter(IGeoPoint point) {
		// TODO Auto-generated method stub
		super.setCenter(point);
	}

	@Override
	public int setZoom(int zoomlevel) {
		// TODO Auto-generated method stub
		return super.setZoom(zoomlevel);
	}

	@Override
	public void stopAnimation(boolean jumpToTarget) {
		// TODO Auto-generated method stub
		super.stopAnimation(jumpToTarget);
	}

	@Override
	public boolean zoomIn() {
		// TODO Auto-generated method stub
		return super.zoomIn();
	}

	@Override
	public boolean zoomInFixing(GeoPoint point) {
		// TODO Auto-generated method stub
		return super.zoomInFixing(point);
	}

	@Override
	public boolean zoomInFixing(int xPixel, int yPixel) {
		// TODO Auto-generated method stub
		return super.zoomInFixing(xPixel, yPixel);
	}

	@Override
	public boolean zoomOut() {
		// TODO Auto-generated method stub
		return super.zoomOut();
	}

	@Override
	public boolean zoomOutFixing(GeoPoint point) {
		// TODO Auto-generated method stub
		return super.zoomOutFixing(point);
	}

	@Override
	public boolean zoomOutFixing(int xPixel, int yPixel) {
		// TODO Auto-generated method stub
		return super.zoomOutFixing(xPixel, yPixel);
	}

	@Override
	public void zoomToSpan(BoundingBoxE6 bb) {
		// TODO Auto-generated method stub
		super.zoomToSpan(bb);
	}

	@Override
	public void zoomToSpan(int reqLatSpan, int reqLonSpan) {
		// TODO Auto-generated method stub
		super.zoomToSpan(reqLatSpan, reqLonSpan);
	}
	

}
