package optinvent.com.raftol;

import android.graphics.Bitmap;
import android.location.Location;
import android.support.annotation.NonNull;

public abstract class Entity {
    protected Core core;
    protected Location location;
    protected int id;
    private String name;

    Entity(Core _core, int _id, @NonNull Location _location) {
        core = _core;
        id = _id;
        location = _location;
    }

    Location getLocation() {
        return (location);
    }
    void setLocation(@NonNull Location _location) {
        location = _location;
    }
    int getId() {
        return (id);
    }

    public abstract Bitmap getIcon();
    public abstract float getSize();
    public abstract int getLevel();
    public abstract int getColor();

    void setName(String name)
    {
        this.name = name;
    }

    public String getName()
    {
        return name;
    }
}
