package funcloak.sos;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.software.shell.fab.ActionButton;

import java.util.List;
import java.util.Locale;

public class MainActivity extends Activity implements LocationListener {
    protected LocationManager locationManager;
    private static final int MY_PERMISSION_REQUEST_READ_CONTACTS = 1;
    TextView txtLat;
    protected boolean isGPSenabled, isNetworkenabled;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        txtLat = (TextView) findViewById(R.id.textview1);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

    }

    public void getLocation() {
        Log.d("TAG","gettingloc");
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
//            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
//            Location location = locationManager.getLastKnownLocation(locationManager.GPS_PROVIDER);
//            onLocationChanged(location);
            locationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);
            List<String> providers = locationManager.getProviders(true);
            Location bestLocation = null;
            for (String provider : providers) {
                Location l = locationManager.getLastKnownLocation(provider);
                if (l == null) {
                    continue;
                }
                if (bestLocation == null || l.getAccuracy() < bestLocation.getAccuracy()) {
                    // Found best last known location: %s", l);
                    bestLocation = l;
                }
            }
            onLocationChanged(bestLocation);
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    public void checkPermission (View view) {
        Log.d("TAG","here to check");
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            Log.d("TAG","lol patty");
            isGPSenabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            isNetworkenabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            if (isGPSenabled && isNetworkenabled) {
                getLocation();
            }
            else {
                Toast.makeText(this,"Enable location and Internet", Toast.LENGTH_SHORT).show();
            }
        }
        else {
            if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {
                Toast.makeText(this,
                        "To access the location",Toast.LENGTH_SHORT).show();
            }
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSION_REQUEST_READ_CONTACTS);
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int [] grantResults) {
        Log.d("TAG","on request permission result");
        if (requestCode == MY_PERMISSION_REQUEST_READ_CONTACTS) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLocation();
            }
            else {
                Toast.makeText(this,
                        "not yet some thing's wrong check the permissions",Toast.LENGTH_SHORT).show();
            }
        }
        else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
    @Override
    public void onLocationChanged(Location location) {
        txtLat = (TextView) findViewById(R.id.textview1);
        if (location != null) {
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            String zip = "";
            try {
                List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                Log.d("TAG",addresses.toString());
                if (addresses != null) {
                    Address address = addresses.get(0);
                    zip = address.getPostalCode();
                }
                txtLat.setText(zip);
            } catch (Exception e) {
                Toast.makeText(this,"you actually did well but please make sure that yu have a good connection speed",
                        Toast.LENGTH_SHORT).show();
            }
        }
        else {
            txtLat.setText("NO LOCATION FOUND");
        }

    }

    @Override
    public void onProviderDisabled(String provider) {
        Log.d("Latitude", "disable");
    }

    @Override
    public void onProviderEnabled(String provider) {
        Log.d("Latitude","enable");
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        Log.d("Latitude","status");
    }

    public void floatingButton(View view) {
        ActionButton fab = (ActionButton) findViewById(R.id.action_button);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, Reminder.class));
            }
        });
    }
}