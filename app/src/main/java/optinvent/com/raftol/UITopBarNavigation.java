package optinvent.com.raftol;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.location.Location;
import android.os.SystemClock;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/** SAME CLASS AS UITOPBAR BUT DISPLAY WAYPOINTS INSTEAD OF ENTITIES */
public class UITopBarNavigation implements UIElement
{
    private Core core;
    private Paint paint;
    private List<Waypoint> displayOnRight = new ArrayList<>();
    private List<Waypoint> displayOnLeft = new ArrayList<>();
    private List<Waypoint> displayOnCenter = new ArrayList<>();
    private final float BORDERS = 9.5f;
    private int HALF_BAR_SIZE;
    private int TOP_BAR_HEIGHT;
    private final float TEXT_SIZE_PERCENTAGE = 7.5f;
    private final float TOP_OFFSET_PERCENTAGE = 5f;
    private final float MARGIN_PERCENTAGE = 1.25f;

    private Bitmap arrow;
    private Bitmap arrowNoAccess;

    UITopBarNavigation(Core _core)
    {
        core = _core;
        paint = new Paint();
        paint.setTextSize(core.HEIGHT * (TEXT_SIZE_PERCENTAGE / 100f));
        paint.setColor(Color.GREEN);


        float ARROW_SIZE_X = 15f;
        float ARROW_SIZE_Y = 15f;
        arrow = core.loadResizedBitmap(R.drawable.nav_arrow, ARROW_SIZE_X, ARROW_SIZE_Y);
        arrowNoAccess = core.loadResizedBitmap(R.drawable.nav_arrow_no_access, ARROW_SIZE_X, ARROW_SIZE_Y);
    }

    private void drawTextAtBearing(Canvas canvas, String text, float bearing, int y, int color)
    {
        paint.setColor(color);
        int x = core.getBearingToScreen(canvas.getWidth(), bearing);
        canvas.drawText(text, x - paint.measureText(text) / 2, y, paint);
        paint.setColor(Color.GREEN);
    }

    private void drawBitmapAtBearing(Canvas canvas, Bitmap bitmap, float bearing, int y)
    {
        int x = core.getBearingToScreen(canvas.getWidth(), bearing);
        canvas.drawBitmap(bitmap, x - bitmap.getWidth() / 2, y, paint);
    }

    private boolean listContainsBitmap(Bitmap bitmap, List<Waypoint> waypoints)
    {
        for (Waypoint check : waypoints)
        {
            if (check.getIcon().sameAs(bitmap))
                return (true);
        }
        return (false);
    }

    private void sortWaypoints(Waypoint waypoint, Location location)
    {
        float bearing = location.bearingTo(waypoint.getLocation());
        double currAngle = Math.toRadians(core.getBearing());
        double angle = Math.toRadians(bearing);
        double delta = Math.toDegrees(Math.atan2(Math.sin(currAngle - angle), Math.cos(currAngle - angle)));

        //Put entities' bitmap in corresponding List
        if (Math.abs(delta) < BORDERS) //Center List
            displayOnCenter.add(waypoint);
        else if (delta > BORDERS && !listContainsBitmap(waypoint.getIcon(), displayOnLeft)) //Left List
            displayOnLeft.add(waypoint);
        else if (!listContainsBitmap(waypoint.getIcon(), displayOnRight)) //Right List
            displayOnRight.add(waypoint);
        paint.setColor(Color.GREEN);
    }

    private int getUpperDecadeAngle() {
        int intBearing = (int)core.getBearing();

        return (Misc.roundToDecade(intBearing) + 10);
    }

    private int getUpperFiveAngle() {
        int intBearing = (int)core.getBearing();
        int angle = ((intBearing / 5) + 1) * 5;
        if (angle % 10 == 0)
            angle += 5;

        return (angle);
    }

    private int getLowerDecadeAngle() {
        int intBearing = (int)core.getBearing();

        return (Misc.roundToDecade(intBearing));
    }

    private int getLowerFiveAngle() {
        int intBearing = (int)core.getBearing();
        int angle = ((intBearing / 5) - 1) * 5;
        if (angle % 10 == 0)
            angle += 5;

        return (angle);
    }

    private void drawBar(Canvas canvas, float bearing)
    {
        int offset = (int) (canvas.getHeight() * (TOP_OFFSET_PERCENTAGE / 100f));
        double currAngle = Math.toRadians(core.getBearing());
        double angle = Math.toRadians(bearing);
        double delta = Math.toDegrees(Math.atan2(Math.sin(currAngle - angle), Math.cos(currAngle - angle)));

        //Disp bar if posAtScreen is in top bar
        if (Math.abs(delta) < BORDERS)
        {
            int x = core.getBearingToScreen(canvas.getWidth(), bearing);
            canvas.drawRect(x - HALF_BAR_SIZE, core.TEXT_SIZE + offset, x + HALF_BAR_SIZE, core.TEXT_SIZE + offset + TOP_BAR_HEIGHT, paint);
        }
    }

    private void drawFatBar(Canvas canvas, float bearing) {
        int offset = (int)(canvas.getHeight() * (TOP_OFFSET_PERCENTAGE / 100f));
        double currAngle = Math.toRadians(core.getBearing());
        double angle = Math.toRadians(bearing);
        double delta = Math.toDegrees(Math.atan2(Math.sin(currAngle - angle), Math.cos(currAngle - angle)));

        //Disp bar if posAtScreen is in top bar
        if (Math.abs(delta) < BORDERS) {
            int x = core.getBearingToScreen(canvas.getWidth(), bearing);
            canvas.drawRect(x - HALF_BAR_SIZE * 2, core.TEXT_SIZE + offset - TOP_BAR_HEIGHT / 2, x + HALF_BAR_SIZE * 2, core.TEXT_SIZE + offset + TOP_BAR_HEIGHT, paint);
        }
    }

    private void dispCardinal(Canvas canvas, String cardinal, float bearing)
    {
        final float CARD_SIZE_PERCENTAGE = 8f;
        paint.setTextSize(canvas.getHeight() * (CARD_SIZE_PERCENTAGE / 100f));
        double currAngle = Math.toRadians(core.getBearing());
        double angle = Math.toRadians(bearing);
        double delta = Math.toDegrees(Math.atan2(Math.sin(currAngle - angle), Math.cos(currAngle - angle)));

        //Disp cardinal point if posAtScreen is in top bar
        if (Math.abs(delta) < BORDERS)
        {
            drawTextAtBearing(canvas, cardinal, bearing, (int) (core.TEXT_SIZE * 2.5f), Color.GREEN);
            drawBar(canvas, bearing);
        }
        paint.setTextSize(canvas.getHeight() * (TEXT_SIZE_PERCENTAGE / 100f));
    }

    private void drawFixedElements(Canvas canvas)
    {
        final int MARGIN = (int) (canvas.getWidth() * (MARGIN_PERCENTAGE / 100f));
        final float BAR_SIZE_PERCENTAGE = 0.5f;
        final int BAR_SIZE = (int) (canvas.getHeight() * (BAR_SIZE_PERCENTAGE / 100f));

        //display cardinal points
        for (float x = 0; x < Compass.POINTS; x += Compass.STEP)
            dispCardinal(canvas, Compass.getDirection(x), x);

        //display the top bar frame
        int offset = (int) (canvas.getHeight() * (TOP_OFFSET_PERCENTAGE / 100f));
        canvas.drawRect(MARGIN, core.TEXT_SIZE + offset, MARGIN + BAR_SIZE, core.TEXT_SIZE + offset + TOP_BAR_HEIGHT, paint);
        canvas.drawRect(MARGIN + BAR_SIZE, core.TEXT_SIZE + offset + TOP_BAR_HEIGHT - BAR_SIZE, canvas.getWidth() - MARGIN, core.TEXT_SIZE + offset + TOP_BAR_HEIGHT, paint);

        Paint triangle = new Paint();
        triangle.setColor(Color.GREEN);
        triangle.setStyle(Paint.Style.FILL);

        Point point1 = new Point((canvas.getWidth() / 2) - 10, core.TEXT_SIZE + offset + TOP_BAR_HEIGHT);
        Point point2 = new Point(canvas.getWidth() / 2, core.TEXT_SIZE + offset - 5);
        Point point3 = new Point((canvas.getWidth() / 2) + 10, core.TEXT_SIZE + offset + TOP_BAR_HEIGHT);

        Path path = new Path();
        path.moveTo(point1.x, point1.y);
        path.lineTo(point2.x, point2.y);
        path.lineTo(point3.x, point3.y);
        path.close();

        canvas.drawPath(path, triangle);

        canvas.drawRect(canvas.getWidth() - MARGIN - BAR_SIZE, core.TEXT_SIZE + offset, canvas.getWidth() - MARGIN, core.TEXT_SIZE + offset + TOP_BAR_HEIGHT, paint);

        //display upper and lower decade
        drawFatBar(canvas, getLowerDecadeAngle());
        drawBar(canvas, getLowerFiveAngle());
        drawFatBar(canvas, getUpperDecadeAngle());
        drawBar(canvas, getUpperFiveAngle());
    }

    private void displayArrow(Canvas canvas)
    {
        Matrix matrix = new Matrix();

        float bearing = core.getLocation().bearingTo(core.waypoints.get(0).getLocation()) - core.getBearing();

        if (core.waypoints.get(0).getValidationDist() == 2)
        {
            Point position = new Point(canvas.getWidth() / 2 - arrow.getWidth() / 2,
                    canvas.getHeight() / 2 + arrow.getHeight() / 2);
            String dist = String.valueOf((int) core.getLocation().distanceTo(core.waypoints.get(0).getLocation())) + "m";
            canvas.drawText(dist, position.x - paint.measureText(dist), position.y, paint);
            matrix.postRotate(bearing, arrow.getWidth() / 2, arrow.getHeight() / 2);
            matrix.postTranslate(position.x, position.y);
            canvas.drawBitmap(arrow, matrix, paint);
            String id = String.valueOf(core.waypoints.get(0).getId());
            paint.setTextSize(20);
            canvas.drawText(id, position.x + arrow.getWidth() / 2 - paint.measureText(id) / 2, position.y + arrow.getHeight() / 2 + 7, paint);
        }
        else
        {
            Point position = new Point(canvas.getWidth() / 2 - arrowNoAccess.getWidth() / 2,
                    canvas.getHeight() / 2 + arrowNoAccess.getHeight() / 2);
            String dist = String.valueOf((int) core.getLocation().distanceTo(core.waypoints.get(0).getLocation())) + "m";
            canvas.drawText(dist, position.x - paint.measureText(dist), position.y, paint);
            matrix.postRotate(bearing, arrowNoAccess.getWidth() / 2, arrowNoAccess.getHeight() / 2);
            matrix.postTranslate(position.x, position.y);
            canvas.drawBitmap(arrowNoAccess, matrix, paint);
            String id = String.valueOf(core.waypoints.get(0).getId());
            paint.setTextSize(20);
            canvas.drawText(id, position.x + arrowNoAccess.getWidth() / 2 - paint.measureText(id) / 2, position.y + arrow.getHeight() / 2 + 7, paint);
        }
    }

    private long validWaypointTime = 0;

    @Override
    public void update(Canvas canvas)
    {
        int offset;
        final int BAR_WIDTH = (int) (canvas.getWidth() * 0.25f / 100f);
        TOP_BAR_HEIGHT = (int) (canvas.getHeight() * 2f / 100f);
        HALF_BAR_SIZE = BAR_WIDTH / 2;

        drawFixedElements(canvas);

        Location location = core.getLocation();

        if (SystemClock.currentThreadTimeMillis() - validWaypointTime < 10000)
        {
            Point mid = new Point(core.WIDTH / 2, core.HEIGHT / 2 - 50);
            Message.display(canvas, "Checkpoint atteint", new Point(mid.x, mid.y + core.TEXT_SIZE), core.TEXT_SIZE, true, true, false);
        }



        if (core.waypoints.size() != 0 )
        {
            displayArrow(canvas);
        }

        if (location != null)
        {
            final int MARGIN = (int) (canvas.getWidth() * (MARGIN_PERCENTAGE / 100f)) + HALF_BAR_SIZE;
            final float SUPERPOSITION_PERCENTAGE = 0.5f;
            final int SUPERPOSITION_OFFSET = (int) (canvas.getWidth() * (SUPERPOSITION_PERCENTAGE / 100f));
            displayOnLeft.clear();
            displayOnRight.clear();
            displayOnCenter.clear();
            for (Waypoint waypoint : core.waypoints)
            {
                if (waypoint.getLocation() != null)
                {
                    if (waypoint.getLocation().distanceTo(location) <= waypoint.getValidationDist() + (location.getAccuracy() * 1.5f))
                    {
                        validWaypointTime = SystemClock.currentThreadTimeMillis();
                        if (waypoint.getNext() != null)
                        {
                            core.waypoints.add(waypoint.getNext());
                        }
                        core.waypoints.remove(waypoint);
                    }
                    else
                        sortWaypoints(waypoint, location);
                }
            }
            for (Waypoint dispLeft : displayOnLeft)
            {
                int y = core.TEXT_SIZE + (int) (canvas.getHeight() * (TOP_OFFSET_PERCENTAGE / 100f)) - dispLeft.getIcon().getHeight();
                offset = (displayOnLeft.size() - displayOnLeft.indexOf(dispLeft) - 1) * SUPERPOSITION_OFFSET;
                canvas.drawBitmap(dispLeft.getIcon(), MARGIN - dispLeft.getIcon().getWidth() / 2 + offset, y - offset, paint);
            }
            for (Waypoint dispRight : displayOnRight)
            {
                int y = core.TEXT_SIZE + (int) (canvas.getHeight() * (TOP_OFFSET_PERCENTAGE / 100f)) - dispRight.getIcon().getHeight();
                offset = (displayOnRight.size() - displayOnRight.indexOf(dispRight) - 1) * SUPERPOSITION_OFFSET;
                canvas.drawBitmap(dispRight.getIcon(), canvas.getWidth() - MARGIN - (dispRight.getIcon().getWidth() / 2) - offset, y - offset, paint);
            }
            for (Waypoint dispCenter : displayOnCenter)
            {
                float bearing = location.bearingTo(dispCenter.getLocation());
                String dist;
                dist = String.valueOf((int)location.distanceTo(dispCenter.getLocation()));
                offset = core.TEXT_SIZE + (int) (canvas.getHeight() * (TOP_OFFSET_PERCENTAGE / 100f)) - dispCenter.getIcon().getHeight();
                drawBitmapAtBearing(canvas, dispCenter.getIcon(), bearing, offset);
                paint.setTextSize(offset * (75f / 100f));
                drawTextAtBearing(canvas, dist, bearing, offset - SUPERPOSITION_OFFSET * 2, dispCenter.getColor());
                paint.setTextSize(core.TEXT_SIZE);
            }
        }
    }
}
