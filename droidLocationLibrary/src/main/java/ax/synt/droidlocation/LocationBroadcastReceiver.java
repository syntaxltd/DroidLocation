package ax.synt.droidlocation;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;

class LocationBroadcastReceiver extends BroadcastReceiver {
    private final DroidLocationListener droidLocationListener;

    public LocationBroadcastReceiver(DroidLocationListener droidLocationListener) {
        this.droidLocationListener = droidLocationListener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(AppConstants.INTENT_LOCATION_RECEIVED)) {
            Location location = intent.getParcelableExtra(IntentKey.LOCATION);
            droidLocationListener.onLocationReceived(location);

        }
    }
}