package optinvent.com.raftol;

import android.graphics.Canvas;
import android.graphics.Point;

public class UIWarnings implements UIElement {
    private Core core;

    UIWarnings(Core _core) {
        core = _core;
    }

    @Override
    public void update(Canvas canvas) {
        Point mid = new Point(core.WIDTH / 2, core.HEIGHT / 2);
        if (core.getLocation() == null) {
            String noGps = core.context.getResources().getString(R.string.no_gps);
            new Error2(canvas).write(noGps, mid.x, mid.y, core.TEXT_SIZE);
        }
        else if (!core.loaded)
        {
            String noGps = core.context.getResources().getString(R.string.no_config);
            Error.display(canvas, noGps, new Point(mid.x, mid.y + core.TEXT_SIZE), core.TEXT_SIZE, true, true, false);
        }
    }
}
