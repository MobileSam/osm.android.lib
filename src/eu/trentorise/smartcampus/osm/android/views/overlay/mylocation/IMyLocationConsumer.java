package eu.trentorise.smartcampus.osm.android.views.overlay.mylocation;


import android.location.Location;

public interface IMyLocationConsumer
{
    void onLocationChanged(Location location, IMyLocationProvider source);
}