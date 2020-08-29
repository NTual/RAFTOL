package optinvent.com.raftol;

import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.BatteryManager;

/**
 * Created by $IAR000-27EIH13HOEDO on 06/03/2018.
 */

public class UIBattery implements UIElement
{
    private Core core;
    private Paint paint;

    UIBattery(Core _core) {
        core = _core;
        paint = new Paint();
        paint.setTextSize(core.TEXT_SIZE);
        paint.setColor(Color.GREEN);
    }

    @Override
    public void update(Canvas canvas) {
        String levelTxt;

        // DRAW BATTERY FRAME
        canvas.drawRect(25, 250, 27, 350, paint);
        canvas.drawRect(75, 250, 77, 350, paint);
        canvas.drawRect(28, 250, 74, 252, paint);
        canvas.drawRect(28, 350, 74, 352, paint);
        canvas.drawRect(46, 246, 56, 250, paint);

        IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = core.context.registerReceiver(null, ifilter);
        int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);

        canvas.drawRect(25, 350 - level, 77, 350, paint);
        paint.setColor(Color.WHITE);
        paint.setTextSize(18);
        levelTxt = String.valueOf(level) + "%";
        canvas.drawText(levelTxt, 50 - paint.measureText(levelTxt) / 2, 312, paint);
        paint.setColor(Color.GREEN);
    }
}
