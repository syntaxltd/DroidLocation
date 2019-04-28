package ax.synt.droidlocation;

import android.location.Location;

interface DroidLocationListener {
    void onLocationPermissionGranted();
    void onLocationPermissionDenied();
    void onLocationReceived(Location location);
    void onLocationAddressReceived(String fullAddress);
    void onLocationProviderEnabled();
    void onLocationProviderDisabled();
}