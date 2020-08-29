package optinvent.com.raftol;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

public class EventManager implements WorldElement {
    private Core core;
    private List<Event> events;

    EventManager(@NonNull Core _core) {
        core = _core;
        events = new ArrayList<>();
    }

    @Override
    public void update() {
        for (Event event : events) {
            event.tryToRun(core);
        }
    }
}
