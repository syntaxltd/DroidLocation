package ax.synt.droidlocation;

import android.app.Activity;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

class EasyLocation {
    private final Location location;

    public EasyLocation(Location location) {
        this.location = location;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EasyLocation that = (EasyLocation) o;
        return location != null ? location.equals(that.location) : that.location == null;

    }

    @Override
    public int hashCode() {
        return location != null ? location.hashCode() : 0;
    }

    @Override
    public String toString() {
         return  location.getLatitude() +","+location.getLongitude();
    }

    public static String getAddress(Context context, Double latitude, Double longitude, boolean country, boolean fullAddress) {
        String add = "";
        Geocoder geoCoder = new Geocoder(((Activity) context).getBaseContext(), Locale.getDefault());
        try {
            List<Address> addresses = geoCoder.getFromLocation(latitude, longitude, 1);

            if (addresses.size() > 0) {
                if (country) {
                    add = addresses.get(0).getCountryName();
                } else if (fullAddress) {
                    add = addresses.get(0).getFeatureName() + "," + addresses.get(0).getSubLocality() + "," + addresses.get(0).getSubAdminArea() + "," + addresses.get(0).getPostalCode() + "," + addresses.get(0).getCountryName();
                } else {
                    add = addresses.get(0).getLocality();
                }
            }


        } catch (IOException e) {
            e.printStackTrace();
        }
        return add.replaceAll(",null", "");
    }
}