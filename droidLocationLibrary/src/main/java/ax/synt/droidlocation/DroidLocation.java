package ax.synt.droidlocation;

import android.location.Location;

class DroidLocation {
    private final Location location;

    public DroidLocation(Location location) {
        this.location = location;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DroidLocation that = (DroidLocation) o;
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
}