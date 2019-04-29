package ax.synt.droidlocation;

import android.app.IntentService;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.ResultReceiver;
import androidx.annotation.WorkerThread;
import android.text.TextUtils;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class FetchAddressIntentService extends IntentService {

    private String TAG = "AddressService";

    protected ResultReceiver receiver;

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * //@param name Used to name the worker thread, important only for debugging.
     */
    public FetchAddressIntentService() {
        super("FetchAddressIntentService");
    }

    // ...
    private void deliverResultToReceiver(int resultCode, String message) {
        Bundle bundle = new Bundle();
        bundle.putString(DroidConstants.RESULT_DATA_KEY, message);
        receiver.send(resultCode, bundle);
    }

    @WorkerThread
    @Override
    protected void onHandleIntent(Intent intent) {

        if (intent == null) {
            return;
        }
        String errorMessage = "";

        receiver = intent.getParcelableExtra(DroidConstants.RECEIVER);

        // Check if receiver was properly registered.
        if (receiver == null) {
            Log.wtf(TAG, "No receiver received. There is nowhere to send the results.");
            return;
        }


        // Get the location passed to this service through an extra.
        Location location = intent.getParcelableExtra(
                DroidConstants.LOCATION_DATA_EXTRA);

        // ...

        // Determine whether a Geocoder is available.
        if (!Geocoder.isPresent()) {
            Log.wtf(TAG,getString(R.string.no_geocoder_available));
            return;
        }


        Geocoder geocoder = new Geocoder(this, Locale.getDefault());

        List<Address> addresses = null;

        try {
            Log.d(TAG,"lat"+ location.getLatitude()+"--------- lon"+
                    location.getLongitude());

            addresses = geocoder.getFromLocation(
                    location.getLatitude(),
                    location.getLongitude(),
                    // In this sample, get just a single address.
                    1);
        } catch (IOException ioException) {
            // Catch network or other I/O problems.
            errorMessage = getString(R.string.service_not_available);
            Log.e(TAG, errorMessage, ioException);
        } catch (IllegalArgumentException illegalArgumentException) {
            // Catch invalid latitude or longitude values.
            errorMessage = getString(R.string.invalid_lat_long_used);
            Log.e(TAG, errorMessage + ". " +
                    "Latitude = " + location.getLatitude() +
                    ", Longitude = " +
                    location.getLongitude(), illegalArgumentException);
        }

        // Handle case where no address was found.
        if (addresses == null || addresses.size()  == 0) {
            if (errorMessage.isEmpty()) {
                errorMessage = getString(R.string.no_address_found);
                Log.e(TAG, errorMessage);
            }
            deliverResultToReceiver(DroidConstants.FAILURE_RESULT, errorMessage);
        } else {
            Address address = addresses.get(0);
            ArrayList<String> addressFragments = new ArrayList<String>();

            // Fetch the address lines using getAddressLine,
            // join them, and send them to the thread.
            // Fetch the address lines using {@code getAddressLine},
            // join them, and send them to the thread. The {@link android.location.address}
            // class provides other options for fetching address details that you may prefer

            // to use. Here are some examples:
            // getLocality() ("Mountain View", for example)
            // getAdminArea() ("CA", for example)
            // getPostalCode() ("94043", for example)
            // getCountryCode() ("US", for example)
            // getCountryName() ("United States", for example)

            for(int i = 0; i <= address.getMaxAddressLineIndex(); i++) {
                addressFragments.add(address.getAddressLine(i));
            }
            Log.i(TAG, getString(R.string.address_found));
            deliverResultToReceiver(DroidConstants.SUCCESS_RESULT,
                    TextUtils.join(System.getProperty("line.separator"),
                            addressFragments));
        }
    }
}
