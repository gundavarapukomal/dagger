package com.madebyatomicrobot.dagger;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.madebyatomicrobot.dagger.MainActivityFragment.MainActivityFragmentHost;

import java.util.List;

import javax.inject.Inject;

public class MainActivity extends AppCompatActivity implements MainActivityFragmentHost, LocationListener {

    @Inject
    LocationManager locationManager;
    @Inject
    List<Location> allLocations;

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        getAppComponent().inject(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    @Override
    protected void onResume () {
        super.onResume();
        List<String> providers = locationManager.getAllProviders();
        boolean atLeastOneProviderEnabled = false;
        for (String provider : providers) {
            if (locationManager.isProviderEnabled(provider)) {
                locationManager.requestLocationUpdates(provider, 0, 0, this);
                atLeastOneProviderEnabled = true;
            }
        }
        if (!atLeastOneProviderEnabled) {
            findMainFragment().showMessage("No location providers enabled.");
        }
    }

    @Override
    protected void onPause () {
        locationManager.removeUpdates(this);
        super.onPause();
    }

    @Override
    public void inflatefgmnt (MainActivityFragment fragment) {
        getAppComponent().inject(fragment);
    }

    private ApplicationComponent getAppComponent () {
        return ((MainApplication) getApplication()).getComponent();
    }

    private void locationUpdated (Location location) {
        allLocations.add(location);
        String message = String.format("Total location updates: %d.  You are now at: %.2f, %.2f", allLocations.size(), location.getLatitude(), location.getLongitude());
        findMainFragment().showMessage(message);
    }

    public void onLocationChanged (Location location) {
        locationUpdated(location);
    }

    public void onStatusChanged (String provider, int status, Bundle extras) {
        // Don't care
    }

    public void onProviderEnabled (String provider) {
        // Don't care
    }

    public void onProviderDisabled (String provider) {
        // Don't care
    }

    private MainActivityFragment findMainFragment () {
        return (MainActivityFragment) getSupportFragmentManager().findFragmentById(R.id.fragment);
    }
}
