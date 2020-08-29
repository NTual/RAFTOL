package optinvent.com.raftol;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;

import java.util.ArrayList;
import java.util.List;

class UISizeIndicator implements UIElement
{

    private Core core;

    private Paint strokePaint;
    private int STROKE_SIZE = 2;
    private final int TEXT_SIZE = 30;

    private int BAR_CENTER_POS_X;
    private final int GAP = 75;

    private List<Integer> getDistList(final int FROM, final int GAP, final int nb)
    {
        List<Integer> res = new ArrayList<>();
        int dist = FROM;

        for (int i = 0; i < nb; i++)
        {
            res.add(dist);
            dist += GAP;
        }

        return res;
    }

    private int calcBarSize(List<Integer> distList)
    {
        int res = 0;

        for (int i = 0; i < distList.size(); i++)
            res += calcHeight(distList.get(i), 5) + GAP;

        res -= calcHeight(distList.get(0), 5) / 2;
        res -= calcHeight(distList.get(distList.size() - 1), 5) / 2;
        res -= GAP;

        return res;
    }

    private void drawDistBars(Canvas canvas, List<Integer> distList, final int BAR_SIZE)
    {
        int offset = 0;
        int BAR_POS_Y = (int) (core.HEIGHT / 1.5f);

        if (core.sizeFrom == 25)
        {
            BAR_POS_Y = (int) (core.HEIGHT / 1.25f);
        }

        final int DIST_BAR_SIZE = 10;
        for (int i = 0; i < distList.size(); i++)
        {
            final int POS_X = BAR_CENTER_POS_X - BAR_SIZE / 2;
            if (i != 0)
                offset += calcHeight(distList.get(i), 5) / 2;

            strokePaint.setStrokeWidth(0);
            String distText = String.valueOf(distList.get(i));
            final int TEXT_POS_X = (int) (POS_X + offset - strokePaint.measureText(distText) / 2);
            final int TEXT_POS_Y = BAR_POS_Y + TEXT_SIZE + DIST_BAR_SIZE;
            canvas.drawText(distText, TEXT_POS_X, TEXT_POS_Y, strokePaint);
            strokePaint.setStrokeWidth(STROKE_SIZE);

            canvas.drawLine(POS_X + offset, BAR_POS_Y + DIST_BAR_SIZE, POS_X + offset, BAR_POS_Y, strokePaint);

            offset += calcHeight(distList.get(i), 5) / 2 + GAP;
        }
    }

    private int calcHeight(int dist, float height)
    {
        final float SCALE = 1;

        final float tan = height / dist;
        final float angle = (float) Math.toDegrees(Math.atan(tan));
        final float res = 42.6f * angle;

        return Math.round(res * SCALE);
    }

    private void drawSizeBars(Canvas canvas, List<Integer> distList, final int BAR_SIZE)
    {
        int offset = 0;
        for (int i = 0; i < distList.size(); i++)
        {
            final int POS_X = BAR_CENTER_POS_X - BAR_SIZE / 2;
            int BAR_POS_Y = (int) (core.HEIGHT / 1.5f) - calcHeight(distList.get(i), 1.8f) - STROKE_SIZE;

            if (core.sizeFrom == 25)
            {
                BAR_POS_Y = (int) (core.HEIGHT / 1.25f) - calcHeight(distList.get(i), 1.8f) - STROKE_SIZE;
            }

            if (i != 0)
                offset += calcHeight(distList.get(i), 5) / 2;

            int SIZE_BAR_WIDTH = 25;
            if (SIZE_BAR_WIDTH < calcHeight(distList.get(i), 5) / 2)
                canvas.drawLine(POS_X + offset - SIZE_BAR_WIDTH, BAR_POS_Y, POS_X + offset + SIZE_BAR_WIDTH, BAR_POS_Y, strokePaint);
            else
                canvas.drawLine(POS_X + offset - calcHeight(distList.get(i), 5) / 2, BAR_POS_Y, POS_X + offset + calcHeight(distList.get(i), 5) / 2, BAR_POS_Y, strokePaint);

            offset += calcHeight(distList.get(i), 5) / 2 + GAP;
        }
    }

    private void drawVehicleSizeBox(Canvas canvas, List<Integer> distList, final int BAR_SIZE)
    {
        int offset = 0;
        for (int i = 0; i < distList.size(); i++)
        {
            final int POS_X = BAR_CENTER_POS_X - BAR_SIZE / 2;
            int BAR_POS_Y = (int) (core.HEIGHT / 1.5f) - calcHeight(distList.get(i), 2.5f) - STROKE_SIZE;

            if (core.sizeFrom == 25)
            {
                BAR_POS_Y = (int) (core.HEIGHT / 1.25f) - calcHeight(distList.get(i), 2.5f) - STROKE_SIZE;
            }

            if (i != 0)
                offset += calcHeight(distList.get(i), 5) / 2;

            strokePaint.setColor(Color.RED);
            canvas.drawLine(POS_X + offset - calcHeight(distList.get(i), 5) / 2, BAR_POS_Y, POS_X + offset + calcHeight(distList.get(i), 5) / 2, BAR_POS_Y, strokePaint);
            canvas.drawLine(POS_X + offset - calcHeight(distList.get(i), 5) / 2, BAR_POS_Y, POS_X + offset - calcHeight(distList.get(i), 5) / 2, BAR_POS_Y + calcHeight(distList.get(i), 2.5f), strokePaint);
            canvas.drawLine(POS_X + offset + calcHeight(distList.get(i), 5) / 2, BAR_POS_Y, POS_X + offset + calcHeight(distList.get(i), 5) / 2, BAR_POS_Y + calcHeight(distList.get(i), 2.5f), strokePaint);
            canvas.drawLine(POS_X + offset - calcHeight(distList.get(i), 5) / 2, BAR_POS_Y + calcHeight(distList.get(i), 2.5f), POS_X + offset + calcHeight(distList.get(i), 5) / 2, BAR_POS_Y + calcHeight(distList.get(i), 2.5f), strokePaint);
            strokePaint.setColor(Color.GREEN);

            offset += calcHeight(distList.get(i), 5) / 2 + GAP;
        }
    }

    private void drawIndicators(Canvas canvas)
    {
        List<Integer> distList = getDistList((int) core.sizeFrom, (int) core.sizeGap, 4);
        int BAR_SIZE = calcBarSize(distList);

        while (BAR_SIZE + calcHeight(distList.get(0), 5) / 2 + calcHeight(distList.get(distList.size() - 1), 5) / 2 > core.WIDTH)
        {
            distList = getDistList((int) core.sizeFrom, (int) core.sizeGap, distList.size() - 1);
            BAR_SIZE = calcBarSize(distList);
        }

        BAR_CENTER_POS_X = core.WIDTH / 2;

        //Draw base line
        Point BAR_START = new Point();
        BAR_START.x = BAR_CENTER_POS_X - BAR_SIZE / 2;
        BAR_START.y = (int) (core.HEIGHT / 1.5f);

        Point BAR_END = new Point();
        BAR_END.x = BAR_START.x + BAR_SIZE;
        BAR_END.y = (int) (core.HEIGHT / 1.5f);

        if (core.sizeFrom == 25)
        {
            BAR_START.y = (int) (core.HEIGHT / 1.25);
            BAR_END.y = (int) (core.HEIGHT / 1.25);
        }

        if (BAR_CENTER_POS_X - BAR_SIZE / 2 - calcHeight(distList.get(0), 5) / 2 < 0)
        {
            int over = -(BAR_CENTER_POS_X - BAR_SIZE / 2 - calcHeight(distList.get(0), 5) / 2);
            BAR_CENTER_POS_X += over;
            BAR_START.x += over;
            BAR_END.x += over;
        }

        canvas.drawLine(BAR_START.x, BAR_START.y, BAR_END.x, BAR_END.y, strokePaint);

        //Draw distance indicator
        drawDistBars(canvas, distList, BAR_SIZE);

        //Draw size indicator
        drawSizeBars(canvas, distList, BAR_SIZE);
        drawVehicleSizeBox(canvas, distList, BAR_SIZE);
    }

    UISizeIndicator(Core core)
    {
        this.core = core;
        strokePaint = new Paint();
        strokePaint.setColor(Color.GREEN);
        strokePaint.setTextSize(TEXT_SIZE);
        strokePaint.setStyle(Paint.Style.STROKE);
        strokePaint.setStrokeWidth(STROKE_SIZE);
    }

    @Override
    public void update(Canvas canvas)
    {
        drawIndicators(canvas);
    }
}
