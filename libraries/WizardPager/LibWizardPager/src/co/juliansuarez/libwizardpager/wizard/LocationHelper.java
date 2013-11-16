package co.juliansuarez.libwizardpager.wizard;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

public class LocationHelper {

    private LocationManager locationManager;
    private MyLocationListener locationListener;
    private float latitude = 0.0f;
    private float longitude = 0.0f;
    private boolean gotLocation = false;
    private float accuracy = 0.0f;


    public LocationHelper(Context context) {
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        locationListener = new MyLocationListener();
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
    }


    public class MyLocationListener implements LocationListener {

        public void onLocationChanged(Location location) {
            latitude = (float) location.getLatitude();
            longitude = (float) location.getLongitude();
            accuracy = location.getAccuracy();

            locationManager.removeUpdates(locationListener);

            gotLocation = true;
        }

        public void onProviderDisabled(String provider) {
        }

        public void onProviderEnabled(String provider) {
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {
        }
    }

    public void killLocationServices() {
        locationManager.removeUpdates(locationListener);
    }

    public float getLat() {
        return latitude;
    }

    public float getLong() {
        return longitude;
    }

    public float getAccuracy() {
        return accuracy;
    }

    public Boolean gotLocation() {
        return gotLocation;
    }

}
