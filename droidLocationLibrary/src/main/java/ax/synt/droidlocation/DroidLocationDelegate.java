package ax.synt.droidlocation;


import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.provider.Settings;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.appcompat.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;

class DroidLocationDelegate {
    private static final int PERMISSIONS_REQUEST = 100;
    private static final int ENABLE_LOCATION_SERVICES_REQUEST = 101;
    private static final int GOOGLE_PLAY_SERVICES_ERROR_DIALOG = 102;


    private final Activity activity;
    private final DroidLocationListener droidLocationListener;
    private final LocationBroadcastReceiver locationReceiver;
    private LocationManager mLocationManager;
    private int mLocationFetchMode;
    private LocationRequest mLocationRequest;
    private GoogleApiAvailability googleApiAvailability;
    private DroidLocationRequest droidLocationRequest;
    private AddressResultReceiver resultReceiver;

    DroidLocationDelegate(Activity activity, DroidLocationListener droidLocationListener) {
        this.activity = activity;
        this.droidLocationListener = droidLocationListener;
        locationReceiver = new LocationBroadcastReceiver(droidLocationListener);

        resultReceiver = new AddressResultReceiver(new Handler());
    }


    private boolean isLocationEnabled() {
        return isGPSLocationEnabled()
                || isNetworkLocationEnabled();
    }

    private boolean isGPSLocationEnabled() {
        return mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    private boolean isNetworkLocationEnabled() {
        return mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    private void openLocationSettings() {
        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        activity.startActivityForResult(intent, ENABLE_LOCATION_SERVICES_REQUEST);
    }

    void stopLocationUpdates() {
        Intent intent = new Intent(activity, LocationBgService.class);
        intent.setAction(AppConstants.ACTION_LOCATION_FETCH_STOP);
        activity.startService(intent);
    }

    private void isProperRequest(DroidLocationRequest droidLocationRequest) {
        if (droidLocationRequest == null)
            throw new IllegalStateException("droidLocationRequest can't be null");

        if (droidLocationRequest.locationRequest == null)
            throw new IllegalStateException("locationRequest can't be null");
        this.droidLocationRequest = droidLocationRequest;
    }

    private void startLocationBGService(LocationRequest locationRequest, long fallBackToLastLocationTime) {
        if (!isLocationEnabled())
            showLocationSettingDialog(-1);
        else {
            Intent intent = new Intent(activity, LocationBgService.class);
            intent.setAction(AppConstants.ACTION_LOCATION_FETCH_START);
            intent.putExtra(IntentKey.LOCATION_REQUEST, locationRequest);
            intent.putExtra(IntentKey.LOCATION_FETCH_MODE, mLocationFetchMode);
            intent.putExtra(IntentKey.FALLBACK_TO_LAST_LOCATION_TIME, fallBackToLastLocationTime);
            activity.startService(intent);
        }
    }

    private boolean hasLocationPermission() {
        return ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    private void showPermissionRequireDialog() {
        String title = TextUtils.isEmpty(droidLocationRequest.locationPermissionDialogTitle) ? activity.getString(R.string.location_permission_dialog_title) : droidLocationRequest.locationPermissionDialogTitle;
        String message = TextUtils.isEmpty(droidLocationRequest.locationPermissionDialogMessage) ? activity.getString(R.string.location_permission_dialog_message) : droidLocationRequest.locationPermissionDialogMessage;
        String negativeButtonTitle = TextUtils.isEmpty(droidLocationRequest.locationPermissionDialogNegativeButtonText) ? activity.getString(android.R.string.cancel) : droidLocationRequest.locationPermissionDialogNegativeButtonText;
        String positiveButtonTitle = TextUtils.isEmpty(droidLocationRequest.locationPermissionDialogPositiveButtonText) ? activity.getString(android.R.string.ok) : droidLocationRequest.locationPermissionDialogPositiveButtonText;
        new AlertDialog.Builder(activity)
                .setCancelable(true)
                .setTitle(title)
                .setMessage(message)
                .setNegativeButton(negativeButtonTitle, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        droidLocationListener.onLocationPermissionDenied();
                    }
                })
                .setPositiveButton(positiveButtonTitle, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        requestPermission();
                    }
                }).create().show();
    }

    @Deprecated
    // warning: Use @
    private void showLocationServicesRequireDialog() {
        if(droidLocationRequest == null) return;

        String title = TextUtils.isEmpty(droidLocationRequest.locationSettingsDialogTitle) ? activity.getString(R.string.location_services_off) : droidLocationRequest.locationSettingsDialogTitle;
        String message = TextUtils.isEmpty(droidLocationRequest.locationSettingsDialogMessage) ? activity.getString(R.string.open_location_settings) : droidLocationRequest.locationSettingsDialogMessage;
        String negativeButtonText = TextUtils.isEmpty(droidLocationRequest.locationSettingsDialogNegativeButtonText) ? activity.getString(android.R.string.cancel) : droidLocationRequest.locationSettingsDialogNegativeButtonText;
        String positiveButtonText = TextUtils.isEmpty(droidLocationRequest.locationSettingsDialogPositiveButtonText) ? activity.getString(android.R.string.ok) : droidLocationRequest.locationSettingsDialogPositiveButtonText;
        new AlertDialog.Builder(activity)
                .setCancelable(true)
                .setTitle(title)
                .setMessage(message)
                .setNegativeButton(negativeButtonText, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        droidLocationListener.onLocationProviderDisabled();
                    }
                })
                .setPositiveButton(positiveButtonText, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        openLocationSettings();
                    }
                })
                .create().show();
    }

    /*
    * @param {requestcode}
    * use -1 if no requestcode is available
    * */
    public void showLocationSettingDialog(int requestcode) {

        requestcode = (requestcode == -1) ? ENABLE_LOCATION_SERVICES_REQUEST : requestcode;

        GoogleApiClient mGoogleApiClient = new GoogleApiClient.Builder(activity)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();

        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);//Setting priotity of Location request to high
        locationRequest.setInterval(30 * 1000);
        locationRequest.setFastestInterval(5 * 1000);//5 sec Time interval for location update
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);
        builder.setAlwaysShow(false); //this is the key ingredient to show dialog always when GPS is off

        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build());

        final int finalRequestcode = requestcode;
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult result) {
                final Status status = result.getStatus();
                final LocationSettingsStates state = result.getLocationSettingsStates();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        // All location settings are satisfied. The client can initialize location
                        // requests here.

                        if (isLocationEnabled()) {
                            requestLocation(mLocationRequest, mLocationFetchMode);
                            droidLocationListener.onLocationProviderEnabled();
                        } else
                            droidLocationListener.onLocationProviderDisabled();


                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        // Location settings are not satisfied. But could be fixed by showing the user
                        // a dialog.
                        try {
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().
                            status.startResolutionForResult(activity, finalRequestcode);
                        } catch (IntentSender.SendIntentException e) {
                            e.printStackTrace();
                            // Ignore the error.
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        //droidLocationListener.onLocationProviderDisabled();

                        // Location settings are not satisfied. However, we have no way to fix the
                        // settings so we won't show the dialog.
                        break;
                }
            }
        });
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_REQUEST);
    }


    private void requestLocation(LocationRequest locationRequest, int locationMode) {
        if (isGoogleServiceAvailable()) {
            mLocationFetchMode = locationMode;
            mLocationRequest = locationRequest;
            checkForPermissionAndRequestLocation(locationRequest);
        } else
            showGooglePlayServicesErrorDialog();
    }

    private void checkForPermissionAndRequestLocation(LocationRequest locationRequest) {
        if (!hasLocationPermission()) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity, android.Manifest.permission.ACCESS_FINE_LOCATION))
                showPermissionRequireDialog();
            else
                requestPermission();
        } else{
            if(droidLocationRequest == null) return;
            startLocationBGService(locationRequest, droidLocationRequest.fallBackToLastLocationTime);
        }
    }

    private void unregisterLocationBroadcastReceiver() {
        LocalBroadcastManager.getInstance(activity).unregisterReceiver(locationReceiver);
    }

    private void registerLocationBroadcastReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(AppConstants.INTENT_LOCATION_RECEIVED);
        LocalBroadcastManager.getInstance(activity).registerReceiver(locationReceiver, intentFilter);
    }

    private boolean isGoogleServiceAvailable() {
        return googleApiAvailability.isGooglePlayServicesAvailable(activity) == ConnectionResult.SUCCESS;
    }

    private void showGooglePlayServicesErrorDialog() {
        int errorCode = googleApiAvailability.isGooglePlayServicesAvailable(activity);
        if (googleApiAvailability.isUserResolvableError(errorCode))
            googleApiAvailability.getErrorDialog(activity, errorCode, GOOGLE_PLAY_SERVICES_ERROR_DIALOG).show();
    }


    void onCreate() {
        mLocationManager = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);
        googleApiAvailability = GoogleApiAvailability.getInstance();
        registerLocationBroadcastReceiver();
    }

  /*  void onActivityResult(int requestCode) {
        switch (requestCode) {
            case ENABLE_LOCATION_SERVICES_REQUEST:
                if (isLocationEnabled()) {
                    requestLocation(mLocationRequest, mLocationFetchMode);
                    droidLocationListener.onLocationProviderEnabled();
                } else
                    droidLocationListener.onLocationProviderDisabled();
                break;
        }
    }*/

    void onRequestPermissionsResult(int requestCode, int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    requestLocation(mLocationRequest, mLocationFetchMode);
                    droidLocationListener.onLocationPermissionGranted();
                } else
                    droidLocationListener.onLocationPermissionDenied();
                break;
        }
    }

    void onDestroy() {
        stopLocationUpdates();
        unregisterLocationBroadcastReceiver();
    }

    Location getLastKnownLocation() {
        return PreferenceUtil.getInstance(activity).getLastKnownLocation();
    }

    void requestLocationUpdates(DroidLocationRequest droidLocationRequest) {
        isProperRequest(droidLocationRequest);
        requestLocation(droidLocationRequest.locationRequest, AppConstants.CONTINUOUS_LOCATION_UPDATES);
    }

    void requestSingleLocationFix(DroidLocationRequest droidLocationRequest) {
        isProperRequest(droidLocationRequest);
        requestLocation(droidLocationRequest.locationRequest, AppConstants.SINGLE_FIX);
    }


    void startIntentService(Location lastLocation) {
        Intent intent = new Intent(activity, FetchAddressIntentService.class);
        intent.putExtra(DroidConstants.RECEIVER, resultReceiver);
        intent.putExtra(DroidConstants.LOCATION_DATA_EXTRA, lastLocation);
        activity.startService(intent);
    }

    class AddressResultReceiver extends ResultReceiver {
        public AddressResultReceiver(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {

            if (resultData == null) {
                return;
            }

            // Display the address string
            // or an error message sent from the intent service.
            String addressOutput = resultData.getString(DroidConstants.RESULT_DATA_KEY);
            if (addressOutput == null) {
                addressOutput = "";
            }
            //displayAddressOutput();

            // Show a toast message if an address was found.
            if (resultCode == DroidConstants.SUCCESS_RESULT) {
                Log.d("address",activity.getString(R.string.address_found));
                droidLocationListener.onLocationAddressReceived(addressOutput);
            }
        }
    }

}