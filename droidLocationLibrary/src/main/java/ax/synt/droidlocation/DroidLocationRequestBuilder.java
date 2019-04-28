package ax.synt.droidlocation;

import com.google.android.gms.location.LocationRequest;

public class DroidLocationRequestBuilder {
    private LocationRequest locationRequest;
    private String locationSettingsDialogTitle;
    private String locationSettingsDialogMessage;
    private String locationSettingsDialogPositiveButtonText;
    private String locationSettingsDialogNegativeButtonText;
    private String locationPermissionDialogTitle;
    private String locationPermissionDialogMessage;
    private String locationPermissionDialogPositiveButtonText;
    private String locationPermissionDialogNegativeButtonText;
    private long fallBackToLastLocationTime;

    public DroidLocationRequestBuilder setLocationRequest(LocationRequest locationRequest) {
        this.locationRequest = locationRequest;
        return this;
    }

    public DroidLocationRequestBuilder setLocationSettingsDialogTitle(String locationSettingsDialogTitle) {
        this.locationSettingsDialogTitle = locationSettingsDialogTitle;
        return this;
    }

    public DroidLocationRequestBuilder setLocationSettingsDialogMessage(String locationSettingsDialogMessage) {
        this.locationSettingsDialogMessage = locationSettingsDialogMessage;
        return this;
    }

    public DroidLocationRequestBuilder setLocationSettingsDialogPositiveButtonText(String locationSettingsDialogPositiveButtonText) {
        this.locationSettingsDialogPositiveButtonText = locationSettingsDialogPositiveButtonText;
        return this;
    }

    public DroidLocationRequestBuilder setLocationSettingsDialogNegativeButtonText(String locationSettingsDialogNegativeButtonText) {
        this.locationSettingsDialogNegativeButtonText = locationSettingsDialogNegativeButtonText;
        return this;
    }

    public DroidLocationRequestBuilder setLocationPermissionDialogTitle(String locationPermissionDialogTitle) {
        this.locationPermissionDialogTitle = locationPermissionDialogTitle;
        return this;
    }

    public DroidLocationRequestBuilder setLocationPermissionDialogMessage(String locationPermissionDialogMessage) {
        this.locationPermissionDialogMessage = locationPermissionDialogMessage;
        return this;
    }

    public DroidLocationRequestBuilder setLocationPermissionDialogPositiveButtonText(String locationPermissionDialogPositiveButtonText) {
        this.locationPermissionDialogPositiveButtonText = locationPermissionDialogPositiveButtonText;
        return this;
    }

    public DroidLocationRequestBuilder setLocationPermissionDialogNegativeButtonText(String locationPermissionDialogNegativeButtonText) {
        this.locationPermissionDialogNegativeButtonText = locationPermissionDialogNegativeButtonText;
        return this;
    }

    public DroidLocationRequestBuilder setFallBackToLastLocationTime(long fallBackToLastLocationTime) {
        this.fallBackToLastLocationTime = fallBackToLastLocationTime;
        return this;
    }

    public DroidLocationRequest build() {
        return new DroidLocationRequest(locationRequest, locationSettingsDialogTitle, locationSettingsDialogMessage, locationSettingsDialogPositiveButtonText, locationSettingsDialogNegativeButtonText, locationPermissionDialogTitle, locationPermissionDialogMessage, locationPermissionDialogPositiveButtonText, locationPermissionDialogNegativeButtonText, fallBackToLastLocationTime);
    }
}