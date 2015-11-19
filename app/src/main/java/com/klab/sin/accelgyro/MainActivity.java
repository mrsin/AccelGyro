package com.klab.sin.accelgyro;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class MainActivity extends Activity implements SensorEventListener {

    private float lastX, lastY, lastZ, calibrateX, calibrateY, calibrateZ;
    private long lastTime = 0;

    private SensorManager sensorManager;
    private Sensor accelerometer;

    private float deltaXMax = 0;
    private float deltaYMax = 0;
    private float deltaZMax = 0;

    private float deltaX = 0;
    private float deltaY = 0;
    private float deltaZ = 0;

   /* private float minX = Float.MAX_VALUE, minY = Float.MAX_VALUE, minZ = Float.MAX_VALUE;
    private float maxxX = Float.MIN_VALUE, maxxY = Float.MIN_VALUE, maxxZ = Float.MIN_VALUE;*/

    private float speedX = 0;
    private float speedY = 0;
    private float speedZ = 0;

    private float coordX = 0;
    private float coordY = 0;
    private float coordZ = 0;

    private float vibrateThreshold = 0;

    private TextView currentX, currentY, currentZ, maxX, maxY, maxZ, spdX, spdY, spdZ, crdX, crdY, crdZ;

    public Vibrator v;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initializeViews();

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null) {
            // success! we have an accelerometer

            accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_FASTEST);
            vibrateThreshold = accelerometer.getMaximumRange() / 2;
        } else {
            // fai! we dont have an accelerometer!
        }

        //initialize vibration
        v = (Vibrator) this.getSystemService(Context.VIBRATOR_SERVICE);

    }

    public void initializeViews() {
        currentX = (TextView) findViewById(R.id.currentX);
        currentY = (TextView) findViewById(R.id.currentY);
        currentZ = (TextView) findViewById(R.id.currentZ);

        maxX = (TextView) findViewById(R.id.maxX);
        maxY = (TextView) findViewById(R.id.maxY);
        maxZ = (TextView) findViewById(R.id.maxZ);

        crdX = (TextView) findViewById(R.id.coordX);
        crdY = (TextView) findViewById(R.id.coordY);
        crdZ = (TextView) findViewById(R.id.coordZ);

        spdX = (TextView) findViewById(R.id.speedX);
        spdY = (TextView) findViewById(R.id.speedY);
        spdZ = (TextView) findViewById(R.id.speedZ);
    }

    //onResume() register the accelerometer for listening the events
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_FASTEST);
    }

    //onPause() unregister the accelerometer for stop listening the events
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        // clean current values
        displayCleanValues();
        // display the current x,y,z accelerometer values
        displayCurrentValues();
        // display the max x,y,z accelerometer values
        displayMaxValues();

        // get the change of the x,y,z values of the accelerometer
        float x, y, z;
        x = event.values[0];
        y = event.values[1];
        z = event.values[2];
        deltaX = Math.abs(lastX - x);
        deltaY = Math.abs(lastY - y);
        deltaZ = Math.abs(lastZ - z);



        // if the change is below 2, it is just plain noise
        if (deltaX < 2)
            deltaX = 0;
        if (deltaY < 2)
            deltaY = 0;
        if ((deltaZ > vibrateThreshold) || (deltaY > vibrateThreshold) || (deltaZ > vibrateThreshold)) {
            v.vibrate(50);
        }

        // compare to previous and calculate speed and coordinates
        long time = event.timestamp;
        if (lastTime >= 0) {
            speedX += (time - lastTime) / 10000000 * (x + calibrateX + lastX) / 2;
            speedY += (time - lastTime) / 10000000 * (y + calibrateY + lastY) / 2;
            speedZ += (time - lastTime) / 10000000 * (z + calibrateZ + lastZ) / 2;
        }
        else {
            calibrateX = x;
            calibrateY = y;
            calibrateY = z;
        }
        lastTime = time;
        lastX = x;
        lastY = y;
        lastZ = z;
/*
        minX = Math.min(x, minX);
        minY = Math.min(y, minY);
        minZ = Math.min(z, minY);

        maxxX = Math.max(x, maxxX);
        maxxY = Math.max(y, maxxY);
        maxxZ = Math.max(z, maxxZ);*/
    }

    public void displayCleanValues() {
        currentX.setText("0.0");
        currentY.setText("0.0");
        currentZ.setText("0.0");
    }

    // display the current x,y,z accelerometer values
    public void displayCurrentValues() {
        currentX.setText(Float.toString(lastX));
        currentY.setText(Float.toString(lastY));
        currentZ.setText(Float.toString(lastZ));
        spdX.setText(Float.toString(speedX));
        spdY.setText(Float.toString(speedY));
        spdZ.setText(Float.toString(speedZ));
        crdX.setText(Float.toString(coordX));
        crdY.setText(Float.toString(coordY));
        crdZ.setText(Float.toString(coordZ));
        /*spdX.setText(Float.toString(minX));
        spdY.setText(Float.toString(minY));
        spdZ.setText(Float.toString(minZ));
        crdX.setText(Float.toString(maxxX));
        crdY.setText(Float.toString(maxxY));
        crdZ.setText(Float.toString(maxxZ));*/
    }

    // display the max x,y,z accelerometer values
    public void displayMaxValues() {
        if (deltaX > deltaXMax) {
            deltaXMax = deltaX;
            maxX.setText(Float.toString(deltaXMax));
        }
        if (deltaY > deltaYMax) {
            deltaYMax = deltaY;
            maxY.setText(Float.toString(deltaYMax));
        }
        if (deltaZ > deltaZMax) {
            deltaZMax = deltaZ;
            maxZ.setText(Float.toString(deltaZMax));
        }
    }
}

