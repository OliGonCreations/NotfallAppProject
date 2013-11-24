package com.oligon.emergency;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;
import java.util.Locale;

import co.juliansuarez.libwizardpager.wizard.LocationHelper;

public class ActivityMap extends SherlockActivity {

    private GoogleMap map;
    private String currentPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
        Location location = null;
        try {
            location = (Location) getIntent().getExtras().get("location");
        } catch (Exception e) {
            e.printStackTrace();
            new LocationWorker().execute();
        }
        if(location != null) {
            LatLng position = new LatLng(location.getLatitude(), location.getLongitude());
            map.addMarker(new MarkerOptions().position(position));
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(position, 15), 1800, null);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getSupportMenuInflater().inflate(R.menu.activity_map, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_about:
                ActivityMain.showAboutDialog(this);
                return true;
            case R.id.action_settings:
                startActivity(new Intent(this, ActivitySettings.class));
                return true;
            case R.id.action_getposition:
                new LocationWorker().execute();
                return true;
            case R.id.action_share_location:
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, currentPosition);
                sendIntent.setType("text/plain");
                startActivity(sendIntent);
                return true;
            default:
                return false;
        }
    }

    private class LocationWorker extends AsyncTask<Void, Void, Void> {

        private LocationHelper myLocationHelper = new LocationHelper(getApplicationContext());
        private String markerTitle = "";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Toast.makeText(getApplicationContext(), R.string.getlocation, Toast.LENGTH_SHORT).show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            while (!myLocationHelper.gotLocation()) {
            }
            try {
                Geocoder geo = new Geocoder(getApplicationContext(), Locale.getDefault());

                List<Address> addresses;
                addresses = geo.getFromLocation(myLocationHelper.getLat(), myLocationHelper.getLong(), 1);
                if (addresses != null && addresses.size() > 0) {
                    Address returnedAddress = addresses.get(0);

                    StringBuilder strReturnedAddress = new StringBuilder();
                    strReturnedAddress.append(": ");
                    for (int i = 0; i < returnedAddress.getMaxAddressLineIndex() - 1; i++) {
                        strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("\n");
                    }
                    strReturnedAddress.append(returnedAddress.getAddressLine(returnedAddress.getMaxAddressLineIndex() - 1));
                    markerTitle = strReturnedAddress.toString();
                    currentPosition = "Meine aktuelle Position" + markerTitle + "\nKoordinaten:\nLat:" + myLocationHelper.getLat() + "\nLong:" + myLocationHelper.getLong();
                } else {
                    markerTitle = "";
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            LatLng position = new LatLng(myLocationHelper.getLat(), myLocationHelper.getLong());
            map.clear();
            map.addMarker(new MarkerOptions().position(position).title("Du" + markerTitle));
            map.addCircle(new CircleOptions().center(position).radius(myLocationHelper.getAccuracy()).strokeColor(getResources().getColor(android.R.color.darker_gray)).strokeWidth(5f));
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(position, 15), 1800, null);
        }
    }
}
