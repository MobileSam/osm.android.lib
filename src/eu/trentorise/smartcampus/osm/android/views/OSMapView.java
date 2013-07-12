package eu.trentorise.smartcampus.osm.android.views;

import java.util.ArrayList;
import java.util.List;

import org.metalev.multitouch.controller.MultiTouchController.PointInfo;
import org.metalev.multitouch.controller.MultiTouchController.PositionAndScale;
import org.osmdroid.ResourceProxy;
import org.osmdroid.api.IGeoPoint;
import org.osmdroid.events.MapListener;
import org.osmdroid.tileprovider.MapTileProviderBase;
import org.osmdroid.tileprovider.tilesource.ITileSource;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.BoundingBoxE6;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.ItemizedIconOverlay.OnItemGestureListener;
import org.osmdroid.views.overlay.ItemizedOverlayWithFocus;
import org.osmdroid.views.overlay.Overlay;
import org.osmdroid.views.overlay.OverlayItem;
import org.osmdroid.views.overlay.OverlayManager;
import org.osmdroid.views.overlay.PathOverlay;
import eu.trentorise.smartcampus.osm.android.util.RoutingTask;
import org.osmdroid.views.overlay.PathOverlay;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.widget.Scroller;


public class OSMapView extends MapView {


	private BoundingBoxE6 boundingBox;
	private final OSMapController mController;
	
	
	
	public OSMapView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mController = new OSMapController(this);
		super.setTileSource(TileSourceFactory.MAPNIK);
	}
	
	public OSMapView(Context arg0, int arg1, ResourceProxy arg2,
			MapTileProviderBase arg3, Handler arg4, AttributeSet arg5) {
		super(arg0, arg1, arg2, arg3, arg4, arg5);
		mController = new OSMapController(this);
		super.setTileSource(TileSourceFactory.MAPNIK);
	}

	public OSMapView(Context context, int tileSizePixels,
			ResourceProxy resourceProxy, MapTileProviderBase aTileProvider,
			Handler tileRequestCompleteHandler) {
		super(context, tileSizePixels, resourceProxy, aTileProvider,
				tileRequestCompleteHandler);
		mController = new OSMapController(this);
		super.setTileSource(TileSourceFactory.MAPNIK);
	}

	public OSMapView(Context context, int tileSizePixels,
			ResourceProxy resourceProxy, MapTileProviderBase aTileProvider) {
		super(context, tileSizePixels, resourceProxy, aTileProvider);
		mController = new OSMapController(this);
		super.setTileSource(TileSourceFactory.MAPNIK);
	}

	public OSMapView(Context context, int tileSizePixels,
			ResourceProxy resourceProxy) {
		super(context, tileSizePixels, resourceProxy);
		mController = new OSMapController(this);
		super.setTileSource(TileSourceFactory.MAPNIK);
	}

	public OSMapView(Context context, int tileSizePixels) {
		super(context, tileSizePixels);
		mController = new OSMapController(this);
		super.setTileSource(TileSourceFactory.MAPNIK);
	}
	
	/**
	 * Adds a new Overlay to the mapView containing the markers with a default GestureListener attached 
	 * @param items
	 * ArrayList of OverlayItem that will be added
	 */
	public void addMarkers(ArrayList<OverlayItem> items){
		ItemizedOverlayWithFocus<OverlayItem> currentLocationOverlay = new ItemizedOverlayWithFocus<OverlayItem>(items,new ItemizedIconOverlay.OnItemGestureListener<OverlayItem>() {

			@Override
			public boolean onItemSingleTapUp(final int index, final OverlayItem item) {
				return true;
			}

			@Override
			public boolean onItemLongPress(int arg0, OverlayItem arg1) {
				return false;
			}
		}, this.getResourceProxy());

		this.getOverlays().add(currentLocationOverlay);
	}

	/**
	 * Adds a new Overlay to the mapView containing the markers with an attached GestureListener
	 * @param items
	 * ArrayList of OverlayItem which will be added on the Overlay
	 * @param onGesture
	 * Gesture Listener that will be attached to each Marker
	 */
	public void addMarkers(ArrayList<OverlayItem> items,OnItemGestureListener<OverlayItem> onGesture){
		ItemizedOverlayWithFocus<OverlayItem> currentLocationOverlay = new ItemizedOverlayWithFocus<OverlayItem>(items,onGesture, this.getResourceProxy());
		this.getOverlays().add(currentLocationOverlay);
	}
	
	/**
	 * Add a new Overlay to the map containing the markers on each GeoPoint passed.
	 * (default Title and Description are added)
	 * @param points
	 * ArrayList of Geopoints that have to be added to the mapView
	 */
	public void addMarkersFromPoints(ArrayList<GeoPoint> points){
		
		ArrayList<OverlayItem> items = new ArrayList<OverlayItem>();
		for(GeoPoint point : points)
		{
			items.add(new OverlayItem("Title","Description",point));
		}
		ItemizedOverlayWithFocus<OverlayItem> currentLocationOverlay = new ItemizedOverlayWithFocus<OverlayItem>(items,new ItemizedIconOverlay.OnItemGestureListener<OverlayItem>() {

			@Override
			public boolean onItemSingleTapUp(final int index, final OverlayItem item) {
				return true;
			}

			@Override
			public boolean onItemLongPress(int arg0, OverlayItem arg1) {
				// TODO Auto-generated method stub
				return false;
			}
		}, this.getResourceProxy());

		this.getOverlays().add(currentLocationOverlay);
	}
	
/**
 * Build a {@link PathOverlay} by calling {@link RoutingTask} and draws it on the mapView
 * @param points
 * An ArrayList<GeoPoint> that represents the Points on which the path should pass
 */
	public void drawPath(ArrayList<GeoPoint> points)
	{
		RoutingTask route = new RoutingTask(getContext(), this);
		route.execute(points);
	}
	
	@Override
	public boolean canZoomIn() {
		// TODO Auto-generated method stub
		return super.canZoomIn();
	}

	@Override
	public boolean canZoomOut() {
		// TODO Auto-generated method stub
		return super.canZoomOut();
	}

	@Override
	protected boolean checkLayoutParams(android.view.ViewGroup.LayoutParams p) {
		// TODO Auto-generated method stub
		return super.checkLayoutParams(p);
	}

	@Override
	public void computeScroll() {
		// TODO Auto-generated method stub
		super.computeScroll();
	}

	@Override
	protected void dispatchDraw(Canvas c) {
		// TODO Auto-generated method stub
		super.dispatchDraw(c);
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		return super.dispatchTouchEvent(event);
	}

	@Override
	protected android.view.ViewGroup.LayoutParams generateDefaultLayoutParams() {
		// TODO Auto-generated method stub
		return super.generateDefaultLayoutParams();
	}

	@Override
	public android.view.ViewGroup.LayoutParams generateLayoutParams(
			AttributeSet attrs) {
		// TODO Auto-generated method stub
		return super.generateLayoutParams(attrs);
	}

	@Override
	protected android.view.ViewGroup.LayoutParams generateLayoutParams(
			android.view.ViewGroup.LayoutParams p) {
		// TODO Auto-generated method stub
		return super.generateLayoutParams(p);
	}

	@Override
	public BoundingBoxE6 getBoundingBox() {
		// TODO Auto-generated method stub
		return super.getBoundingBox();
	}

	@Override
	public BoundingBoxE6 getBoundingBox(int pViewWidth, int pViewHeight) {
		// TODO Auto-generated method stub
		return super.getBoundingBox(pViewWidth, pViewHeight);
	}

	@Override
	public OSMapController getController() {
		return this.mController;
	}

	@Override
	public Object getDraggableObjectAtPoint(PointInfo pt) {
		// TODO Auto-generated method stub
		return super.getDraggableObjectAtPoint(pt);
	}

	@Override
	public Rect getIntrinsicScreenRect(Rect reuse) {
		// TODO Auto-generated method stub
		return super.getIntrinsicScreenRect(reuse);
	}

	@Override
	public int getLatitudeSpan() {
		// TODO Auto-generated method stub
		return super.getLatitudeSpan();
	}

	@Override
	public int getLongitudeSpan() {
		// TODO Auto-generated method stub
		return super.getLongitudeSpan();
	}

	@Override
	public IGeoPoint getMapCenter() {
		// TODO Auto-generated method stub
		return super.getMapCenter();
	}

	@Override
	public float getMapOrientation() {
		// TODO Auto-generated method stub
		return super.getMapOrientation();
	}

	@Override
	public int getMaxZoomLevel() {
		// TODO Auto-generated method stub
		return super.getMaxZoomLevel();
	}

	@Override
	public int getMinZoomLevel() {
		// TODO Auto-generated method stub
		return super.getMinZoomLevel();
	}

	@Override
	public OverlayManager getOverlayManager() {
		// TODO Auto-generated method stub
		return super.getOverlayManager();
	}

	@Override
	public List<Overlay> getOverlays() {
		// TODO Auto-generated method stub
		return super.getOverlays();
	}

	@Override
	public void getPositionAndScale(Object obj,
			PositionAndScale objPosAndScaleOut) {
		// TODO Auto-generated method stub
		super.getPositionAndScale(obj, objPosAndScaleOut);
	}

	@Override
	public Projection getProjection() {
		// TODO Auto-generated method stub
		return super.getProjection();
	}

	@Override
	public ResourceProxy getResourceProxy() {
		// TODO Auto-generated method stub
		return super.getResourceProxy();
	}

	@Override
	public Rect getScreenRect(Rect arg0) {
		// TODO Auto-generated method stub
		return super.getScreenRect(arg0);
	}

	@Override
	public BoundingBoxE6 getScrollableAreaLimit() {
		// TODO Auto-generated method stub
		return super.getScrollableAreaLimit();
	}

	@Override
	public Scroller getScroller() {
		// TODO Auto-generated method stub
		return super.getScroller();
	}

	@Override
	public MapTileProviderBase getTileProvider() {
		// TODO Auto-generated method stub
		return super.getTileProvider();
	}

	@Override
	public Handler getTileRequestCompleteHandler() {
		// TODO Auto-generated method stub
		return super.getTileRequestCompleteHandler();
	}

	@Override
	public int getZoomLevel() {
		// TODO Auto-generated method stub
		return super.getZoomLevel();
	}

	@Override
	public int getZoomLevel(boolean aPending) {
		// TODO Auto-generated method stub
		return super.getZoomLevel(aPending);
	}

	@Override
	public void invalidateMapCoordinates(Rect dirty) {
		// TODO Auto-generated method stub
		super.invalidateMapCoordinates(dirty);
	}

	@Override
	public boolean isAnimating() {
		// TODO Auto-generated method stub
		return super.isAnimating();
	}

	@Override
	public boolean isUsingSafeCanvas() {
		// TODO Auto-generated method stub
		return super.isUsingSafeCanvas();
	}

	@Override
	protected void onAnimationEnd() {
		// TODO Auto-generated method stub
		super.onAnimationEnd();
	}

	@Override
	protected void onAnimationStart() {
		// TODO Auto-generated method stub
		super.onAnimationStart();
	}

	@Override
	public void onDetach() {
		// TODO Auto-generated method stub
		super.onDetach();
	}

	@Override
	protected void onDetachedFromWindow() {
		//		 TODO Auto-generated method stub
		super.onDetachedFromWindow();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		return super.onKeyUp(keyCode, event);
	}

	/**
	 * change the zoom Level of the mapview to the BoundingBox
	 * @param boundingBox
	 * BoundingBox to set the zoom to
	 */
	public void setBoundingBox(BoundingBoxE6 boundingBox) {
		this.boundingBox = boundingBox;
		this.invalidate();
		
	}
	
	/**
	 * change the zoom Level of the mapview to the maximum zoom level containing all the GeoPoints in the ArrayList passed
	 * @param points
	 * ArrayList containing the points that have to be on the screen
	 */
	public void setBoundingBox(ArrayList<GeoPoint> points) {
		this.boundingBox = BoundingBoxE6.fromGeoPoints(points);
		this.invalidate();
		
	}
	
	@Override
	protected void onLayout(boolean arg0, int arg1, int arg2, int arg3, int arg4) {
		// TODO Auto-generated method stub
		super.onLayout(arg0, arg1, arg2, arg3, arg4);
		if (this.boundingBox != null) {
	        this.zoomToBoundingBox(this.boundingBox);
	        this.boundingBox = null;
	    }
	}

	@Override
	protected void onMeasure(int arg0, int arg1) {
		// TODO Auto-generated method stub
		super.onMeasure(arg0, arg1);
	}

	@Override
	public boolean onTrackballEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		return super.onTrackballEvent(event);
	}

	@Override
	public void scrollTo(int arg0, int arg1) {
		// TODO Auto-generated method stub
		super.scrollTo(arg0, arg1);
	}

	@Override
	public void selectObject(Object arg0, PointInfo arg1) {
		// TODO Auto-generated method stub
		super.selectObject(arg0, arg1);
	}

	@Override
	public void setBackgroundColor(int pColor) {
		// TODO Auto-generated method stub
		super.setBackgroundColor(pColor);
	}

	@Override
	public void setBuiltInZoomControls(boolean on) {
		// TODO Auto-generated method stub
		super.setBuiltInZoomControls(on);
	}

	@Override
	public void setMapListener(MapListener ml) {
		// TODO Auto-generated method stub
		super.setMapListener(ml);
	}

	@Override
	public void setMapOrientation(float degrees) {
		// TODO Auto-generated method stub
		super.setMapOrientation(degrees);
	}

	@Override
	public void setMaxZoomLevel(Integer zoomLevel) {
		// TODO Auto-generated method stub
		super.setMaxZoomLevel(zoomLevel);
	}

	@Override
	public void setMinZoomLevel(Integer zoomLevel) {
		// TODO Auto-generated method stub
		super.setMinZoomLevel(zoomLevel);
	}

	@Override
	public void setMultiTouchControls(boolean on) {
		// TODO Auto-generated method stub
		super.setMultiTouchControls(on);
	}
	
	@Override
	public boolean setPositionAndScale(Object obj,
			PositionAndScale aNewObjPosAndScale, PointInfo aTouchPoint) {
		// TODO Auto-generated method stub
		return super.setPositionAndScale(obj, aNewObjPosAndScale, aTouchPoint);
	}

	@Override
	public void setScrollableAreaLimit(BoundingBoxE6 boundingBox) {
		// TODO Auto-generated method stub
		super.setScrollableAreaLimit(boundingBox);
	}

	@Override
	public void setTileSource(ITileSource aTileSource) {
		// TODO Auto-generated method stub
		super.setTileSource(aTileSource);
	}

	@Override
	public void setUseDataConnection(boolean aMode) {
		// TODO Auto-generated method stub
		super.setUseDataConnection(aMode);
	}

	@Override
	public void setUseSafeCanvas(boolean useSafeCanvas) {
		// TODO Auto-generated method stub
		super.setUseSafeCanvas(useSafeCanvas);
	}

	@Override
	public boolean useDataConnection() {
		// TODO Auto-generated method stub
		return super.useDataConnection();
	}

	@Override
	public void zoomToBoundingBox(BoundingBoxE6 boundingBox) {
		// TODO Auto-generated method stub
		super.zoomToBoundingBox(boundingBox);
	}

	
}
