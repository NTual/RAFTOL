package optinvent.com.raftol;

import android.graphics.Canvas;
import android.graphics.Color;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class MyView extends View {
    Core core;
    List<WorldElement> worldElements;
    List<UIElement> uiElements;

    public MyView(Core _core) {
        super(_core.context);
        core = _core;
        setBackgroundColor(Color.BLACK);
        uiElements = new ArrayList<>();
        worldElements = new ArrayList<>();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        for (UIElement element : uiElements) {
            element.update(canvas);
        }
        for (WorldElement element : worldElements) {
            element.update();
        }
        try {
            Thread.sleep((long)(1000f / (float)core.FPS));
        } catch (InterruptedException ignored) { }
        invalidate();
    }


}
