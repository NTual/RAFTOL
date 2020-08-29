package optinvent.com.raftol;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

public class OrientationTracker implements SensorEventListener {
    private float[] orientation = new float[3];
    private SensorManager sensorManager;
    private Sensor rotationSensor;
    private boolean running = false;

    OrientationTracker(Context _context) {
        sensorManager = (SensorManager) _context.getSystemService(Context.SENSOR_SERVICE);
        rotationSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
    }

    void startUpdates() {
        if (!running)
            running = sensorManager.registerListener(this, rotationSensor, SensorManager.SENSOR_DELAY_UI);
    }

    void stopUpdates(){
        if (running)
            sensorManager.unregisterListener(this);
        running = false;
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if (sensorEvent.sensor.getType() == Sensor.TYPE_ROTATION_VECTOR) {
            float[] rotationMatrix = new float[9];
            SensorManager.getRotationMatrixFromVector(rotationMatrix, sensorEvent.values);
            SensorManager.getOrientation(rotationMatrix, orientation);
            float BEARING_OFFSET = 270f;
            orientation[0] = (Misc.map(orientation[0], -(float)Math.PI, (float)Math.PI, 0f, Compass.POINTS) + BEARING_OFFSET) % Compass.POINTS;
            orientation[1] = (float) Math.toDegrees(orientation[1]);
            orientation[2] = (float) Math.toDegrees(orientation[2]);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
        if (sensor.getType() == Sensor.TYPE_ROTATION_VECTOR) {
            String TAG = "TrackingListener";
            Log.d(TAG, "Accuracy: " + i);
        }
    }

    float[] getOrientation() {
        return (orientation);
    }
}
