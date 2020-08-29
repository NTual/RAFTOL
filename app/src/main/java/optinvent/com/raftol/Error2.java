package optinvent.com.raftol;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.NonNull;

class Error2 {
    private Canvas canvas;
    private int BACKGROUND_COLOR = Color.argb(200, 40, 40, 40);

    public enum Type {
        CENTER,
        BOTTOM_RIGHT,
        BOTTOM_LEFT,
        TOP_RIGHT,
        TOP_LEFT
    }

    Error2(@NonNull Canvas _canvas) {
        canvas = _canvas;
    }

    private void expandRect(Rect rect, int expansion) {
        rect.left -= expansion;
        rect.right += expansion;
        rect.top -= expansion;
        rect.bottom += expansion;
    }

    private void drawBackground(Rect rect, float size) {
        Paint backgroundPaint = new Paint();
        Paint borderPaint = new Paint();
        backgroundPaint.setColor(BACKGROUND_COLOR);
        int BORDER_COLOR = Color.RED;
        borderPaint.setColor(BORDER_COLOR);
        borderPaint.setStyle(Paint.Style.STROKE);
        float BORDER_WIDTH = 0.04f;
        borderPaint.setStrokeWidth(BORDER_WIDTH * size);

        canvas.drawRect(rect, backgroundPaint);
        canvas.drawRect(rect, borderPaint);
    }

    private void write(String text, int x, int y, float size, Type type) {
        Paint paint = new Paint();
        Rect textBounds = new Rect();
        paint.setTextSize(size);
        int TEXT_COLOR = Color.RED;
        paint.setColor(TEXT_COLOR);
        paint.getTextBounds(text, 0, text.length(), textBounds);
        float BORDER_OFFSET = 0.18f;
        expandRect(textBounds, (int)(BORDER_OFFSET * size));

        switch (type) {
            case CENTER:
                x -= textBounds.width() / 2;
                y += textBounds.height() / 2;
                break;
            case BOTTOM_RIGHT:
                y += textBounds.height();
                break;
            case BOTTOM_LEFT:
                x -= textBounds.width();
                y += textBounds.height();
                break;
            case TOP_LEFT:
                x -= textBounds.width();
                break;
            case TOP_RIGHT:
                break;
            default:
        }
        Rect backgroundRect = new Rect(textBounds);
        backgroundRect.offset(x, y);
        int yDiff = y - backgroundRect.bottom;
        int xDiff = x - backgroundRect.left;
        y += yDiff;
        x += xDiff;
        backgroundRect.offset(xDiff, yDiff);
        drawBackground(backgroundRect, size);
        canvas.drawText(text, x, y, paint);
    }

    void write(String text, int x, int y, float size) {
        write(text, x, y, size, Type.CENTER);
    }
}
