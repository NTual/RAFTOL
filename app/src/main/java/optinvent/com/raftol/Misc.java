package optinvent.com.raftol;

import android.location.Location;
import android.util.Log;

import static java.lang.Math.asin;
import static java.lang.Math.atan2;
import static java.lang.Math.cos;
import static java.lang.Math.sin;
import static java.lang.Math.toDegrees;
import static java.lang.Math.toRadians;

class Misc {
    private static final double EARTH_RADIUS = 6371009;

    static float map(float x, float sa, float ea, float sb, float eb) {
        if (sa - ea == 0)
            return (0);
        return ((((sa - x) / (sa - ea)) * (eb - sb)) + sb);
    }

    static Location computeOffset(Location from, double distance, double heading) {
        Location res = new Location("");
        distance /= EARTH_RADIUS;
        heading = toRadians(heading);
        double fromLat = toRadians(from.getLatitude());
        double fromLng = toRadians(from.getLongitude());
        double cosDistance = cos(distance);
        double sinDistance = sin(distance);
        double sinFromLat = sin(fromLat);
        double cosFromLat = cos(fromLat);
        double sinLat = cosDistance * sinFromLat + sinDistance * cosFromLat * cos(heading);
        double dLng = atan2(
                sinDistance * cosFromLat * sin(heading),
                cosDistance - sinFromLat * sinLat);
        res.setLatitude(toDegrees(asin(sinLat)));
        res.setLongitude(toDegrees(fromLng + dLng));
        return res;
    }

    static int roundToDecade(float nb) {
        return ((int)(nb / 10f) * 10);
    }
}
