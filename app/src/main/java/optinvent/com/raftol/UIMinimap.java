package optinvent.com.raftol;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.RectF;
import android.location.Location;
import android.support.annotation.NonNull;

import static java.lang.Math.sin;
import static java.lang.Math.toRadians;

public class UIMinimap implements UIElement
{
    private float STROKE_SIZE = 3;
    private Core core;
    private Paint textPaint;
    private Paint strokePaint;
    private Point position;
    private Point size;

    /** METHOD USED TO GET SCREEN POSITION FROM POINTS RELATIVE TO THE MINIMAP (X AND Y BETWEEN -1 AND 1) */
    private Point mapToScreen(float x, float y)
    {
        Point res = new Point();
        res.x = (int) ((x * (float) size.x / 2f) + (float) position.x - 175);
        res.y = (int) ((y * (float) size.y / 2f) + (float) position.y);
        return (res);
    }

    /** METHOD USED TO GET ENTITY POSITION ON SCREEN */
    private Point getScreenPosition(float bearing, float distance, boolean corrected)
    {
        double radBearing;
        Point screenPosition;
        PointF mapPosition;

        radBearing = toRadians(bearing) - (Math.PI / 2);
        mapPosition = new PointF((float) Math.cos(radBearing), (float) sin(radBearing));
        if (corrected) {
            mapPosition.x = mapPosition.x * (distance / (core.range + core.rangeCorrection));
            mapPosition.y = mapPosition.y * (distance / (core.range + core.rangeCorrection));
        }
        else {
            mapPosition.x = mapPosition.x * (distance / core.range);
            mapPosition.y = mapPosition.y * (distance / core.range);
        }
        screenPosition = mapToScreen(mapPosition.x, mapPosition.y);
        return (screenPosition);
    }

    private void drawEntity(@NonNull Canvas canvas, @NonNull Entity entity)
    {
        Location location = core.getLocation();
        Location entityLocation = entity.getLocation();
        if (entityLocation == null || location == null)
            return;
        Float distance = location.distanceTo(entityLocation);
        Float bearing = -(core.getBearing() - location.bearingTo(entityLocation));
        Bitmap bitmap = entity.getIcon();
        if (distance <= core.range + core.rangeCorrection)
        {
            Point screenPos = getScreenPosition(bearing, distance, true);
            canvas.drawBitmap(bitmap, screenPos.x - (bitmap.getWidth() / 2f), screenPos.y - bitmap.getHeight(), strokePaint);
        }
        else
        {
            float OUTSIDE_RANGE_OFFSET = 0.10f;
            Point screenPos = getScreenPosition(bearing, core.range + core.range * OUTSIDE_RANGE_OFFSET, false);
            Point screenPosRange = getScreenPosition(bearing, core.range, false);
            canvas.drawLine(screenPos.x, screenPos.y, screenPosRange.x, screenPosRange.y, strokePaint);

            canvas.drawBitmap(bitmap, screenPos.x - (bitmap.getWidth() / 2f), screenPos.y - bitmap.getHeight() / 2, strokePaint);
        }
    }

    private void drawRangeCircle(@NonNull Canvas canvas, float circleRange, boolean drawRange)
    {
        float ratio;
        Point topLeft;
        Point bottomRight;

        ratio = circleRange / (core.range + core.rangeCorrection);
        topLeft = mapToScreen(-ratio, -ratio);
        bottomRight = mapToScreen(ratio, ratio);
        canvas.drawOval(new RectF(topLeft.x, topLeft.y, bottomRight.x, bottomRight.y),
                strokePaint);
        if (drawRange)
        {
            Point middleRight;
            middleRight = mapToScreen(ratio, 0);
            String rangeStr = Integer.toString((int) circleRange);
            textPaint.setTextSize(core.SMALL_TEXT_SIZE);
            canvas.drawText(rangeStr, middleRight.x + STROKE_SIZE, middleRight.y, textPaint);
        }
    }

    private void drawMinimapBase(@NonNull Canvas canvas)
    {
        // Draw map circles
        Point middle = mapToScreen(0f, 0f);
        drawRangeCircle(canvas, core.range + core.rangeCorrection, true);
        drawRangeCircle(canvas, (core.range + core.rangeCorrection) / 2, true);

        // Draw FOV
        Point fovLeft;
        Point fovRight;
        Point fovMiddle;

        fovLeft = getScreenPosition(-core.FOV, core.range, false);
        canvas.drawLine(middle.x, middle.y, fovLeft.x, fovLeft.y, strokePaint);
        fovRight = getScreenPosition(core.FOV, core.range, false);
        canvas.drawLine(middle.x, middle.y, fovRight.x, fovRight.y, strokePaint);
        fovMiddle = getScreenPosition(0, core.range, false);
        canvas.drawLine(middle.x, middle.y, fovMiddle.x, fovMiddle.y, strokePaint);

        // Draw North
        Point northTop;
        Point northLeft;
        Point northRight;
        float NORTH_SIZE = 0.20f;
        northTop = getScreenPosition(-core.getBearing(), core.range + core.range * NORTH_SIZE, false);
        float NORTH_FOV = 8f;
        northLeft = getScreenPosition(-core.getBearing() - NORTH_FOV, core.range, false);
        northRight = getScreenPosition(-core.getBearing() + NORTH_FOV, core.range, false);
        Path path = new Path();
        path.setFillType(Path.FillType.EVEN_ODD);
        path.moveTo(northLeft.x, northLeft.y);
        path.lineTo(northTop.x, northTop.y);
        path.lineTo(northRight.x, northRight.y);
        path.close();
        canvas.drawPath(path, textPaint);

        Point north = getScreenPosition(0, core.range + (NORTH_SIZE / 2 ) * core.range, false);
        Paint northPaint = new Paint();
        northPaint.setColor(Color.BLACK);
        northPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        northPaint.setStrokeWidth(2);
        northPaint.setTextSize(22);
        canvas.save();
        canvas.rotate(-core.getBearing(), middle.x, middle.y);
        canvas.drawText("N", north.x - northPaint.measureText("N") / 2, north.y + 15, northPaint);
        canvas.restore();
    }

    UIMinimap(@NonNull Core _core)
    {
        core = _core;
        strokePaint = new Paint();
        strokePaint.setColor(Color.GREEN);
        strokePaint.setStyle(Paint.Style.STROKE);
        textPaint = new Paint();
        textPaint.setColor(Color.GREEN);
    }

    private int getFarthestDist(boolean all)
    {
        int max = 0;
        Location location = core.getLocation();
        for (Entity entity : core.entities)
        {
            if (location.distanceTo(entity.getLocation()) > max)
            {
                if (all)
                    max = (int) location.distanceTo(entity.getLocation());
                else if (location.distanceTo(entity.getLocation()) <= 200)
                    max = (int) location.distanceTo(entity.getLocation());
            }
        }
        return max;
    }

    private int roundDist(int dist)
    {
        int res;
        res = (dist / 25 + 1) * 25;
        return res;
    }

    @Override
    public void update(Canvas canvas)
    {
        strokePaint.setStrokeWidth(STROKE_SIZE);

        Location location = core.getLocation();

        //hardcoded size and position
        size = new Point((int) ((float) canvas.getHeight() / 1.5f), (int) ((float) canvas.getHeight() / 1.5f));
        position = new Point((int) (canvas.getWidth() * 0.72f), (int) (canvas.getHeight() * 0.6f));
        if (roundDist(getFarthestDist(true)) <= 300)
            core.range = roundDist(getFarthestDist(true));
        else
            core.range = roundDist(getFarthestDist(false));

        core.range = 300;

        drawMinimapBase(canvas);
        String bearingText = (int)core.getBearing() + "°";
        textPaint.setTextSize(40);
        canvas.drawText(bearingText, 410 - textPaint.measureText(bearingText) / 2, 90, textPaint);

        if (location != null)
        {
            for (Entity entity : core.entities)
            {
                if (entity.getLocation() != null && core.getLocation().distanceTo(entity.getLocation()) <= 500)
                    drawEntity(canvas, entity);
            }
        }
    }
}

