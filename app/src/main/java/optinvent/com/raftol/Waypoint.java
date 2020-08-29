package optinvent.com.raftol;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.Location;
import android.support.annotation.NonNull;

public class Waypoint
{
    protected Core core;
    protected Location location;
    protected int id;
    private Waypoint next;
    private Bitmap icon;
    private int validationDist;

    Waypoint(Core core, int id, @NonNull Location location, Waypoint next)
    {
        this(core, id, location, next, 2);
    }

    Waypoint(Core core, int id, @NonNull Location location, Waypoint next, int validationDist)
    {
        this.core = core;
        this.location = location;
        this.next = next;
        this.id = id;
        this.icon = core.loadResizedBitmap(R.drawable.waypoint, core.CURSOR_SIZE * 1.25f, core.CURSOR_SIZE * 1.25f);
        this.validationDist = validationDist;
    }

    Waypoint(Core core, int id, @NonNull Location location)
    {
        this(core, id, location, null);
    }

    Waypoint(Core core, int id, @NonNull Location location, int validationDist)
    {
        this(core, id, location, null, validationDist);
    }

    public int getId()
    {
        return id;
    }

    Location getLocation()
    {
        return this.location;
    }

    Waypoint getNext()
    {
        return next;
    }

    public Bitmap getIcon()
    {
        return icon;
    }

    public int getColor()
    {
        return Color.parseColor("#f57300");
    }

    int getValidationDist()
    {
        return validationDist;
    }
}
