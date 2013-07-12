package eu.trentorise.smartcampus.osm.android.views.overlay.compass;



public interface IOrientationConsumer
{
    void onOrientationChanged(float orientation, IOrientationProvider source);
}