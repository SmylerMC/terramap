package net.smyler.terramap.util.geo;

import java.util.Locale;

import static java.lang.Math.abs;

abstract class GeoPointAbstract<T extends GeoPoint<?>> implements GeoPoint<T> {

    @Override
    public int hashCode() {
        // Cache hashCode result
        final int prime = 31;
        int result = 1;
        double latitude = this.latitude();
        double longitude = abs(latitude) == 90d ? 0d: this.longitude();
        if (longitude == -180d) longitude = 180d;
        long temp = Double.doubleToLongBits(longitude);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(latitude);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if(this == obj) return true;
        if(obj == null) return false;
        if(!(obj instanceof GeoPoint<?>)) return false;
        GeoPoint<?> other = (GeoPoint<?>) obj;
        double thisLat = this.latitude();
        double otherLat = other.latitude();
        if(thisLat != otherLat) return false;
        if(abs(thisLat) == 90d) return true; // We don't care about longitude at the poles
        double thisLong = this.longitude();
        double otherLong = other.longitude();
        if((thisLong == -180d || thisLong == 180d)
                && thisLong + otherLong == 0d)
            return true; // Antimeridian can be both 180 or -180
        return thisLong == otherLong;
    }

    @Override
    public String toString() {
        return String.format(Locale.US, "GeoPoint{lon=%s°, lat=%s°}", this.longitude(), this.latitude());
    }

}
