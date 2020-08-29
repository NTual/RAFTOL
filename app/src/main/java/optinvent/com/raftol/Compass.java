package optinvent.com.raftol;

class Compass {
    static float POINTS = 360f;
    private static String[] POINTS_STR = new String[] {
            "N", "NE", "E", "SE",
            "S", "SW", "W", "NW"
    };
    static float STEP = POINTS / (float)POINTS_STR.length;


    /** RETURN THE CARDINAL POINT CORRESPONDING TO THE CURRENT BEARING */
    static String getDirection(float bearing) {
        return (POINTS_STR[((int)((bearing + (STEP / 2f)) / STEP) % POINTS_STR.length)]);
    }
}
