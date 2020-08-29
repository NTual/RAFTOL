package optinvent.com.raftol;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

public class UITextCompass implements UIElement {
    private Core core;
    private Paint paint;

    UITextCompass(Core _core) {
        core = _core;
        paint = new Paint();
        paint.setTextSize(core.TEXT_SIZE);
        paint.setColor(Color.GREEN);
    }

    @Override
    public void update(Canvas canvas) {
        float currBearing = core.getBearing();
        String bearing = Integer.toString((int)currBearing) + "°";
        canvas.drawText(bearing, canvas.getWidth() / 2 - paint.measureText(bearing) / 2, core.TEXT_SIZE * 3.5f, paint);
    }
}
