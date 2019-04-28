package ax.synt.droidlocation.demo;

import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.LocationRequest;

import ax.synt.droidlocation.DroidLocationAppCompatActivity;
import ax.synt.droidlocation.DroidLocationRequest;
import ax.synt.droidlocation.DroidLocationRequestBuilder;


public class MainActivity extends DroidLocationAppCompatActivity implements View.OnClickListener {

    private Button requestSingleLocationButton;
    private Button requestLocationUpdatesButton;
    private Button stopLocationUpdatesButton;
    private TextView addresstext;
    private TextView addresstextfull;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViews();
    }

    private void findViews() {
        requestSingleLocationButton = (Button) findViewById(R.id.requestSingleLocationButton);
        requestLocationUpdatesButton = (Button) findViewById(R.id.requestLocationUpdatesButton);
        stopLocationUpdatesButton = (Button) findViewById(R.id.stopLocationUpdatesButton);
        addresstext = (TextView) findViewById(R.id.addresstext);
        addresstextfull = (TextView) findViewById(R.id.addresstextfull);

        requestSingleLocationButton.setOnClickListener(this);
        requestLocationUpdatesButton.setOnClickListener(this);
        stopLocationUpdatesButton.setOnClickListener(this);
    }


    @Override
    public void onLocationPermissionGranted() {
        showToast("Location permission granted");
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onLocationPermissionDenied() {
        showToast("Location permission denied");
    }

    @Override
    public void onLocationReceived(Location location) {
        showToast(location.getProvider() + "," + location.getLatitude() + "," + location.getLongitude());
        //addresstext.setText(DroidLocationAppCompatActivity.getAddress(this,location.getLatitude(),location.getLongitude(),false,false));
        //addresstextfull.setText(DroidLocationAppCompatActivity.getAddress(this,location.getLatitude(),location.getLongitude(),true,true));

        requestAddressServices(location);
    }

    @Override
    public void onLocationProviderEnabled() {
        showToast("Location services are now ON");
    }

    @Override
    public void onLocationProviderDisabled() {
        showToast("Location services are still Off");
    }

    @Override
    public void onLocationAddressReceived(String fullAddress) {
        showToast("Address => "+fullAddress);
        addresstextfull.setText(fullAddress);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.requestSingleLocationButton: {
                LocationRequest locationRequest = new LocationRequest()
                        .setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY)
                        .setInterval(5000)
                        .setFastestInterval(5000);
                DroidLocationRequest droidLocationRequest = new DroidLocationRequestBuilder()
                        .setLocationRequest(locationRequest)
                        .setFallBackToLastLocationTime(3000)
                        .build();
                requestSingleLocationFix(droidLocationRequest);
            }
            break;
            case R.id.requestLocationUpdatesButton: {
                LocationRequest locationRequest = new LocationRequest()
                        .setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY)
                        .setInterval(5000)
                        .setFastestInterval(5000);
                DroidLocationRequest droidLocationRequest = new DroidLocationRequestBuilder()
                        .setLocationRequest(locationRequest)
                        .setFallBackToLastLocationTime(3000)
                        .build();
                requestLocationUpdates(droidLocationRequest);
            }
            break;
            case R.id.stopLocationUpdatesButton:
                stopLocationUpdates();
                break;
        }
    }
}
