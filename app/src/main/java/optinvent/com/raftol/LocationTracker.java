package optinvent.com.raftol;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

public class LocationTracker implements LocationListener {
    private String TAG = "LocationTracker";
    private LocationManager locationManager;
    Location location;
    private Context context;
    private boolean running = false;

    private Boolean checkLocationPermission(Context context) {
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        if (locationManager != null && !locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
            return false;
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((Activity)context, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            ActivityCompat.requestPermissions((Activity)context, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
            return false;
        }
        return true;
    }

    LocationTracker(Context _context) {
        context = _context;
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        if (checkLocationPermission(context)) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 10, this);
        }
    }

    void startUpdates() {
        if (checkLocationPermission(context)) {
            if (!running)
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 10, this);
            running = true;
        }
        else
            Log.e(TAG, "No GPS permission");
    }

    void stopUpdates() {
        if (running)
            locationManager.removeUpdates(this);
        running = false;
    }

    Location getLocation() {
        return (location);
    }

    @Override
    public void onLocationChanged(Location newLocation) {
        location = newLocation;
        Log.d(TAG, "GPS Bearing: " + location.getBearing());
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }
}
