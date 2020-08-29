package optinvent.com.raftol;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.support.annotation.NonNull;
import android.util.DisplayMetrics;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Core
{
    /** VARIABLES CONCERNING DISPLAY */
    private DisplayMetrics displayMetrics = Resources.getSystem().getDisplayMetrics();
    int WIDTH = displayMetrics.widthPixels;
    int HEIGHT = displayMetrics.heightPixels;
    int FPS = 30;
    float FOV = 10f;
    private float SCREEN_RATIO = 16f / 9f;
    float CURSOR_SIZE = 3f;
    int TEXT_SIZE = HEIGHT / 10;
    int SMALL_TEXT_SIZE = HEIGHT / 15;

    /** VARIABLES CONCERNING MINIMAP */
    float range = 600;
    float rangeCorrection = 0;

    /** VARIABLES CONCERNING STADIMETRIC VIEW */
    float sizeGap = 50;
    float sizeFrom = 25;

    Context context;
    Boolean loaded = false;

    List<Entity> entities;
    List<Waypoint> waypoints;

    private OrientationTracker orientationTracker;
    private LocationTracker locationTracker;

    /** VARIABLES USED TO AVERAGE THE BEARING */
    private final float NB_VALUES = 250f;
    private float[] trackerValues = new float[(int) NB_VALUES];

    Core(@NonNull Context _context)
    {
        context = _context;
        orientationTracker = new OrientationTracker(context);
        locationTracker = new LocationTracker(context);
        startUpdates();
        entities = new ArrayList<>();
        waypoints = new ArrayList<>();
    }

    /** METHOD USED TO CONVERT BEARING TO SCREEN POSITION */
    int getBearingToScreen(int screenWidth, float bearing)
    {
        float currBearing = getBearing();
        float relativeBearing = currBearing - bearing;
        float xPosA;
        float xPosB;

        xPosA = Misc.map(relativeBearing, FOV, -FOV, 0, screenWidth);
        xPosB = Misc.map(relativeBearing - 360, FOV, -FOV, 0, screenWidth);
        if (Math.abs(xPosA) < Math.abs(xPosB))
            return ((int) xPosA);
        return ((int) xPosB);
    }

    /** METHODS USED TO RESIZE A BITMAP */
    public Bitmap loadResizedBitmap(int id, float ratioX, float ratioY)
    {
        int width;
        int height;
        Bitmap originalBitmap;
        Bitmap res;

        originalBitmap = BitmapFactory.decodeResource(context.getResources(), id);
        width = (int) ((float) WIDTH * ratioX / 100f);
        height = (int) (((float) HEIGHT * ratioY / 100f) * SCREEN_RATIO);
        res = Bitmap.createScaledBitmap(originalBitmap, width, height, false);
        return (res);
    }

    public Bitmap loadResizedBitmap(int id, float ratioX)
    {
        int width;
        int height;
        float diff;
        Bitmap originalBitmap;
        Bitmap res;

        originalBitmap = BitmapFactory.decodeResource(context.getResources(), id);
        width = (int) (WIDTH * ratioX / 100f);
        diff = ((float) originalBitmap.getWidth()) / ((float) originalBitmap.getHeight());
        height = (int) (((float) HEIGHT * (ratioX * diff) / 100f) * SCREEN_RATIO);
        res = Bitmap.createScaledBitmap(originalBitmap, width, height, false);
        return (res);
    }

    /** METHODS USED TO AVERAGE THE BEARING */
    private float sumValues(float[] tab)
    {
        float sum = 0;
        for (float value : tab)
            sum += value;
        return sum;
    }

    private float gap(float[] tab)
    {
        float min = tab[0];
        float max = tab[0];
        for (float value : tab)
        {
            if (value <= min)
                min = value;
            else if (value >= max)
                max = value;
        }
        return (max - min);
    }

    private void fillValues(float value)
    {
        float[] newValues = new float[(int) NB_VALUES];
        System.arraycopy(trackerValues, 1, newValues, 0, (int) NB_VALUES - 1);
        trackerValues = newValues;
        trackerValues[(int) NB_VALUES - 1] = value;
    }

    float getBearing()
    {
        fillValues(orientationTracker.getOrientation()[0]);
        if (gap(trackerValues) > 350)
            Arrays.fill(trackerValues, orientationTracker.getOrientation()[0]);
        return (sumValues(trackerValues) / NB_VALUES);
    }

    public Location getLocation()
    {
        return (locationTracker.getLocation());
    }

    void startUpdates()
    {
        orientationTracker.startUpdates();
        locationTracker.startUpdates();
    }

    void stopUpdates()
    {
        orientationTracker.stopUpdates();
        locationTracker.stopUpdates();
    }

    Entity getEntityByName(String name)
    {
        Entity res = null;
        for (Entity entity : entities)
        {
            if (entity.getName().matches(name))
                res = entity;
        }
        return res;
    }
}
