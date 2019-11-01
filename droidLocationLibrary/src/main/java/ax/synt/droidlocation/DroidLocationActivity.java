package ax.synt.droidlocation;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


public abstract class DroidLocationActivity extends AppCompatActivity implements DroidLocationListener {
    private DroidLocationDelegate droidLocationDelegate;

    protected Location getLastKnownLocation() {
        return droidLocationDelegate.getLastKnownLocation();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        droidLocationDelegate = new DroidLocationDelegate(this,this);
        droidLocationDelegate.onCreate();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        droidLocationDelegate.showLocationSettingDialog(requestCode);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        droidLocationDelegate.onRequestPermissionsResult(requestCode, grantResults);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        droidLocationDelegate.onDestroy();
    }

    protected void requestLocationUpdates(DroidLocationRequest droidLocationRequest) {
        droidLocationDelegate.requestLocationUpdates(droidLocationRequest);
    }


    protected void requestSingleLocationFix(DroidLocationRequest droidLocationRequest) {
        droidLocationDelegate.requestSingleLocationFix(droidLocationRequest);
    }

    protected void stopLocationUpdates() {
        droidLocationDelegate.stopLocationUpdates();
    }
}