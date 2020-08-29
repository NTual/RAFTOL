package optinvent.com.raftol;

import android.app.Activity;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends Activity
{
    private final int TOP_BAR_VIEW = 0;
    private final int MINIMAP_VIEW = 1;
    private final int STADIMETRIC_VIEW = 2;
    private final int NAVIGATION_VIEW = 3;

    private int currentView = TOP_BAR_VIEW;

    Core core;
    EventManager eventManager;

    UITextCompass compass;
    UIMinimap minimap;
    UITopBar topBar;
    UIWarnings warnings;
    UISizeIndicator sizeIndicator;
    UITopBarNavigation topBarNavigation;
    UIBattery battery;

    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    final DatabaseReference ref = database.getReference();
    final ValueEventListener listener = new ValueEventListener()
    {
        final ValueEventListener latitudeListener = new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                Entity entity = core.getEntityByName(dataSnapshot.getRef().getParent().getKey());
                Location location = entity.getLocation();
                location.setLatitude(dataSnapshot.getValue(Float.class));
                Log.d("UpdateData", location.getLatitude() + "");
                entity.setLocation(location);
            }

            @Override
            public void onCancelled(DatabaseError databaseError)
            {

            }
        };

        final ValueEventListener longitudeListener = new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                Entity entity = core.getEntityByName(dataSnapshot.getRef().getParent().getKey());
                Location location = entity.getLocation();
                location.setLongitude(dataSnapshot.getValue(Float.class));
                entity.setLocation(location);
            }

            @Override
            public void onCancelled(DatabaseError databaseError)
            {

            }
        };

        private void parseEntity(DataSnapshot entity)
        {
            int id;
            String type;
            float lat;
            float lon;
            float speed;
            float heading;
            Location location = new Location("");
            Entity newEntity;

            id = entity.child("id").getValue(Integer.class);
            type = entity.child("type").getValue(String.class);
            lat = entity.child("latitude").getValue(Float.class);
            lon = entity.child("longitude").getValue(Float.class);
            speed = entity.child("speed").getValue(Float.class);
            heading = entity.child("heading").getValue(Float.class);
            location.setLatitude(lat);
            location.setLongitude(lon);
            if (heading != 0 && speed != 0)
            {
                location.setBearing(heading);
                location.setSpeed(speed);
            }
            switch (type)
            {
                case "BF":
                    newEntity = new EntityBlueInf(core, id, location);
                    break;
                case "BGC":
                    newEntity = new EntityBlueGroupChief(core, id, location);
                    break;
                case "BSC":
                    newEntity = new EntityBlueSectionChief(core, id, location);
                    break;
                case "BV":
                    newEntity = new EntityBlueVeh(core, id, location);
                    break;
                case "RF":
                    newEntity = new EntityRedInf(core, id, location);
                    break;
                case "RV":
                    newEntity = new EntityRedVeh(core, id, location);
                    break;
                case "U":
                    newEntity = new EntityUnknown(core, id, location);
                    break;
                case "N":
                    newEntity = new EntityNeutral(core, id, location);
                    break;
                case "NV":
                    newEntity = new EntityNeutralVeh(core, id, location);
                    break;
                default:
                    return;
            }
            newEntity.setName(entity.getKey());
            entity.child("latitude").getRef().addValueEventListener(latitudeListener);
            entity.child("longitude").getRef().addValueEventListener(longitudeListener);
            core.entities.add(newEntity);
        }

        /** METHOD USED TO CREATE ENTITIES AND WAYPOINTS FROM THE DATABASE */
        private Waypoint parseWaypoint(DataSnapshot waypoint)
        {
            int id;
            float lat;
            float lon;
            Location location = new Location("");

            lat = waypoint.child("latitude").getValue(Float.class);
            lon = waypoint.child("longitude").getValue(Float.class);
            id = waypoint.child("id").getValue(Integer.class);
            location.setLatitude(lat);
            location.setLongitude(lon);
            Waypoint newWaypoint;
            if (waypoint.hasChild("next"))
            {
                if (waypoint.child("access").getValue(Boolean.class))
                    newWaypoint = new Waypoint(core, id, location, parseWaypoint(waypoint.child("next")));
                else
                    newWaypoint = new Waypoint(core, id, location, parseWaypoint(waypoint.child("next")),30);
                if (waypoint.hasChild("distance"))
                    newWaypoint = new Waypoint(core, id, location, parseWaypoint(waypoint.child("next")), waypoint.child("distance").getValue(Integer.class));
            }
            else
            {
                if (waypoint.child("access").getValue(Boolean.class))
                    newWaypoint = new Waypoint(core, id, location);
                else
                    newWaypoint = new Waypoint(core, id, location, 30);
                if (waypoint.hasChild("distance"))
                    newWaypoint = new Waypoint(core, id, location, waypoint.child("distance").getValue(Integer.class));
            }
            return newWaypoint;
        }

        @Override
        public void onDataChange(DataSnapshot dataSnapshot)
        {
            if (dataSnapshot.getValue() != null)
            {
                while (core.waypoints.size() != 0)
                    core.waypoints.remove(0);
                for (DataSnapshot item : dataSnapshot.getChildren())
                {
                    if (item.getKey().startsWith("Entity"))
                    {
                        if (core.getEntityByName(item.getKey()) == null)
                            parseEntity(item);
                    }
                    if (item.getKey().startsWith("Waypoint"))
                        core.waypoints.add(parseWaypoint(item));
                }
            }
        }

        @Override
        public void onCancelled(DatabaseError databaseError)
        {

        }
    };

    MyView myView;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
        {
            uiOptions |= View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        }
        decorView.setSystemUiVisibility(uiOptions);

        core = new Core(this);
        myView = new MyView(core);

        compass = new UITextCompass(core);
        minimap = new UIMinimap(core);
        topBar = new UITopBar(core);
        warnings = new UIWarnings(core);
        sizeIndicator = new UISizeIndicator(core);
        topBarNavigation = new UITopBarNavigation(core);
        battery = new UIBattery(core);

        changeView();
        myView.uiElements.add(warnings);

        eventManager = new EventManager(core);
        myView.worldElements.add(eventManager);

        setContentView(myView);
    }

    @Override
    protected void onPause()
    {
        core.stopUpdates();
        super.onPause();
    }

    @Override
    protected void onResume()
    {
        core.startUpdates();
        super.onResume();
    }

    public void clearUi()
    {
        while (myView.uiElements.size() != 0)
            myView.uiElements.remove(0);
    }

    /** METHOD USED TO SET THE VIEW ELEMENTS */
    private void changeView()
    {
        if (currentView == TOP_BAR_VIEW)
        {
            myView.uiElements.add(topBar);
            myView.uiElements.add(compass);
            myView.uiElements.add(battery);
        }
        else if (currentView == MINIMAP_VIEW)
        {
            myView.uiElements.add(minimap);
            myView.uiElements.add(battery);
        }
        else if (currentView == STADIMETRIC_VIEW)
        {
            //myView.uiElements.add(topBar);
            //myView.uiElements.add(compass);
            myView.uiElements.add(sizeIndicator);
            myView.uiElements.add(battery);
        }
        else if (currentView == NAVIGATION_VIEW)
        {
            myView.uiElements.add(topBarNavigation);
            myView.uiElements.add(compass);
            myView.uiElements.add(battery);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {

        Log.d("Key", "k: " + keyCode);
        if (eventManager != null && !core.loaded)
        {
            if (core.getLocation() != null)
            {
                ref.addValueEventListener(listener);
                core.loaded = true;
            }
            else
            {
                Toast.makeText(core.context, "Wait for GPS signal.", Toast.LENGTH_LONG).show();
            }
        }
        else if (core.loaded)
        {
            clearUi();
            currentView++;
            if (currentView > NAVIGATION_VIEW)
                currentView = TOP_BAR_VIEW;
            changeView();
        }
        return (true);
    }

    long time;
    int lastY;
    int lastX;

    /** METHOD USED TO CHANGE FROM ONE VIEW TO ANOTHER */
    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        final int minSpeed = 2;
        int action = event.getAction();

        switch (action)
        {
            case (MotionEvent.ACTION_DOWN):
                time = SystemClock.currentThreadTimeMillis();
                lastY = (int) event.getY();
                lastX = (int) event.getX();
                return true;
            case (MotionEvent.ACTION_UP):
                float deltaTime = SystemClock.currentThreadTimeMillis() - time;
                if (deltaTime > 25 && currentView == MINIMAP_VIEW)
                {
                    int deltaY = lastY - (int) event.getY();
                    float velocity = deltaY / deltaTime;
                    if (velocity >= minSpeed && core.range + core.rangeCorrection < 500)
                        core.rangeCorrection += 25;
                    else if (velocity <= -minSpeed && core.range + core.rangeCorrection > 25)
                        core.rangeCorrection -= 25;
                }
                else if (deltaTime > 25 && currentView == STADIMETRIC_VIEW)
                {
                    int deltaY = lastY - (int) event.getY();
                    int deltaX = lastX - (int) event.getX();
                    float yVelocity = deltaY / deltaTime;
                    float xVelocity = deltaX / deltaTime;
                    if (yVelocity >= minSpeed && core.sizeGap < 100)
                    {
                        core.sizeGap += 25;
                        if (core.sizeFrom < core.sizeGap)
                            core.sizeFrom = core.sizeGap;
                        if (core.sizeFrom % core.sizeGap != 0)
                            core.sizeFrom -= core.sizeFrom % core.sizeGap;
                        while (core.sizeFrom + 3 * core.sizeGap > 400)
                            core.sizeFrom -= core.sizeGap;
                    }
                    else if (yVelocity <= -minSpeed && core.sizeGap > 25)
                    {
                        core.sizeGap -= 25;
                        if (core.sizeFrom % core.sizeGap != 0)
                            core.sizeFrom -= core.sizeFrom % core.sizeGap;
                        while (core.sizeFrom + 3 * core.sizeGap > 400)
                            core.sizeFrom -= core.sizeGap;
                    }
                    else if (xVelocity >= minSpeed && core.sizeFrom > core.sizeGap && core.sizeFrom - core.sizeGap >= 25)
                        core.sizeFrom -= core.sizeGap;
                    else if (xVelocity <= -minSpeed && core.sizeFrom + 4 * core.sizeGap <= 400)
                        core.sizeFrom += core.sizeGap;
                }
                return true;
            default:
                return super.onTouchEvent(event);
        }
    }
}
