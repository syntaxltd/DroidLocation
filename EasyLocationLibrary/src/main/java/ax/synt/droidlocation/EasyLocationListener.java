package ax.synt.droidlocation;

import android.location.Location;

interface EasyLocationListener {
    void onLocationPermissionGranted();
    void onLocationPermissionDenied();
    void onLocationReceived(Location location);
    void onLocationProviderEnabled();
    void onLocationProviderDisabled();
}