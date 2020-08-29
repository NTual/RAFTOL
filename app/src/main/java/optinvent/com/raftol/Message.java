package optinvent.com.raftol;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;

import java.util.Arrays;
import java.util.List;

/** SAME CLASS AS THE ERROR ONE BUT WITH DIFFERENT COLOR */
class Message
{
    private static int FRAME_HEIGHT;
    private static int WARNING_WIDTH;
    private static int GAP_WIDTH;
    private static int BORDER;
    private static final int WARNING_HEIGHT_COEF = 10;
    private static int MESSAGE_WIDTH;

    private static float getLongerLength(List<String> lines, Paint paint) {
        float max = paint.measureText(lines.get(0));
        for (String line : lines) {
            if (paint.measureText(line) > max)
                max = paint.measureText(line);
        }
        return max;
    }

    private static void drawFrame(Canvas canvas, Point pos, Paint redPaint) {
        int FRAME_WIDTH = MESSAGE_WIDTH + WARNING_WIDTH + GAP_WIDTH;
        redPaint.setColor(Color.argb(200, 40, 40, 40));
        canvas.drawRect(pos.x, pos.y, pos.x + FRAME_WIDTH, pos.y + FRAME_HEIGHT, redPaint);
        redPaint.setColor(Color.GREEN);
        redPaint.setStrokeWidth(2);
        redPaint.setStyle(Paint.Style.STROKE);
        canvas.drawRect(pos.x, pos.y, pos.x + FRAME_WIDTH, pos.y + FRAME_HEIGHT, redPaint);
    }

    private static void drawText(Canvas canvas, List<String> lines, Point pos, int textSize, boolean dispIcon, boolean centerText, Paint redPaint) {
        int count = 1;
        for (String line : lines) {
            Point newPos = new Point(pos.x + BORDER + WARNING_WIDTH + GAP_WIDTH, pos.y + textSize * count - BORDER);
            if (lines.size() == 1 && dispIcon)
                newPos.y = newPos.y + textSize / (WARNING_HEIGHT_COEF / 2);
            if (centerText)
                newPos = new Point(pos.x + (MESSAGE_WIDTH / 2) - (int)redPaint.measureText(line) / 2 + WARNING_WIDTH + GAP_WIDTH, newPos.y + BORDER);
            canvas.drawText(line, newPos.x, newPos.y, redPaint);
            count += 1;
        }
    }

    private static void drawWarning(Canvas canvas, int textSize, Point pos, Paint paint) {
        if (FRAME_HEIGHT < 1.5f * textSize)
            FRAME_HEIGHT = (int)(1.5f * textSize);
        paint.setStyle(Paint.Style.STROKE);
        paint.setTextSize(textSize);
        paint.setStrokeWidth(5);
        WARNING_WIDTH = textSize + (int)paint.measureText("!");
        Path path = new Path();
        path.moveTo(WARNING_WIDTH / 2 + BORDER + GAP_WIDTH / 2, -textSize);
        path.lineTo(WARNING_WIDTH + BORDER + GAP_WIDTH / 2, textSize / WARNING_HEIGHT_COEF);
        path.lineTo(BORDER + GAP_WIDTH / 2, textSize / WARNING_HEIGHT_COEF);
        path.close();
        path.offset(pos.x, pos.y + FRAME_HEIGHT / 2 + textSize / 2);
        canvas.drawPath(path, paint);
        paint.setStyle(Paint.Style.FILL);
        canvas.drawText("!", pos.x + WARNING_WIDTH / 2 + GAP_WIDTH / 2, pos.y + FRAME_HEIGHT / 2 + textSize / 2, paint);
    }

    private static Point initPos(List<String> lines, Point pos, int textSize, boolean centerPos, boolean dispIcon) {
        if (lines.size() == 1 && dispIcon)
            pos.y = pos.y - textSize / (WARNING_HEIGHT_COEF / 2);
        if (centerPos)
            pos = new Point(pos.x - MESSAGE_WIDTH / 2, pos.y - FRAME_HEIGHT / 2);
        if (dispIcon) {
            if (centerPos)
                pos.x = pos.x - (WARNING_WIDTH + GAP_WIDTH) / 2;
        }
        else {
            GAP_WIDTH = 0;
            WARNING_WIDTH = 0;
        }
        return pos;
    }

    static void display(Canvas canvas, String message, Point pos, int textSize, boolean centerPos, boolean centerText, boolean dispIcon) {
        List<String> lines = Arrays.asList(message.split("\\s*\n\\s*"));
        Paint redPaint = new Paint();
        redPaint.setTextSize(textSize);
        BORDER = (int)(canvas.getWidth() * (1f / 100f));
        FRAME_HEIGHT = textSize * Math.round(lines.size()) + BORDER * 2;
        MESSAGE_WIDTH = (int) getLongerLength(lines, redPaint) + BORDER * 2;

        redPaint.setStrokeWidth(5);
        pos = initPos(lines, pos, textSize, centerPos, dispIcon);
        drawFrame(canvas, pos, redPaint);
        redPaint.setColor(Color.GREEN);
        redPaint.setStyle(Paint.Style.FILL);
        if (dispIcon)
            drawWarning(canvas, textSize, pos, redPaint);
        redPaint.setStyle(Paint.Style.FILL);
        drawText(canvas, lines, pos, textSize, dispIcon, centerText, redPaint);
    }
}