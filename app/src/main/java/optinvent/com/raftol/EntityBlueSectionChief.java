package optinvent.com.raftol;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.Location;
import android.support.annotation.NonNull;

public class EntityBlueSectionChief extends Entity {
    Bitmap icon;
    int color;
    EntityBlueSectionChief(@NonNull Core _core, int _id, @NonNull Location _location) {
        super(_core, _id, _location);
        icon = core.loadResizedBitmap(R.drawable.blue_section_chief_cursor, core.CURSOR_SIZE, (int)(core.CURSOR_SIZE * 1.7));
        color = Color.rgb(0, 186, 255);
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
    public int getColor() {
        return color;
    }

}
