package optinvent.com.raftol;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.Location;
import android.support.annotation.NonNull;

public class EntityRedVeh extends Entity {
    Bitmap icon;
    int color;
    EntityRedVeh(@NonNull Core _core, int _id, @NonNull Location _location) {
        super(_core, _id, _location);
        icon = core.loadResizedBitmap(R.drawable.red_vehicule_cursor, core.CURSOR_SIZE);
        color = Color.rgb(255, 73, 0);
    }

    @Override
    public Bitmap getIcon() {
        return icon;
    }

    @Override
    public float getSize() {
        return 1.8f;
    }

    @Override
    public int getLevel() {
        return 2;
    }

    @Override
    public int getColor() { return color; }

}
