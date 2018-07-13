package in.arp.compasstest.ui;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.text.DecimalFormat;

import in.arp.compasstest.R;

public class TakePicActivity extends AppCompatActivity implements SensorEventListener {

    private float lastX, lastY, lastZ;
    private SensorManager sensorManager;
    private Sensor accelerometer, magnetometer;
    private float deltaXMax = 0;
    private float deltaYMax = 0;
    private float deltaZMax = 0;
    private float deltaX = 0;
    private float deltaY = 0;
    private float deltaZ = 0;
    private float vibrateThreshold = 0;
    private TextView currentX, currentY, currentZ, maxX, maxY, maxZ, cmpsangle;
    private ImageView ivTakePic, ivImage;
    private Button btnSave;
    public Vibrator v;
    private DecimalFormat df;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_take_pic);

        initializeViews();

        df = new DecimalFormat("00.0");

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
//        if (sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null) {
//            // success! we have an accelerometer
//
//            accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
//            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
//            vibrateThreshold = accelerometer.getMaximumRange() / 2;
//        } else {
//            // fai! we dont have an accelerometer!
//        }

        if(sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null &&
                sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD) != null) {
            accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);

            magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
            sensorManager.registerListener(this, magnetometer, SensorManager.SENSOR_DELAY_NORMAL);
        } else {
            // fai! we dont have an accelerometer!
        }

        //initialize vibration
        v = (Vibrator) this.getSystemService(Context.VIBRATOR_SERVICE);

        ivTakePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    public void initializeViews() {
        currentX = (TextView) findViewById(R.id.currentX);
        currentY = (TextView) findViewById(R.id.currentY);
        currentZ = (TextView) findViewById(R.id.currentZ);

        maxX = (TextView) findViewById(R.id.maxX);
        maxY = (TextView) findViewById(R.id.maxY);
        maxZ = (TextView) findViewById(R.id.maxZ);

        cmpsangle = (TextView) findViewById(R.id.cmpsangle);

        btnSave = (Button) findViewById(R.id.btnSave);

        ivTakePic = (ImageView) findViewById(R.id.ivTakePic);
        ivImage = (ImageView) findViewById(R.id.ivImage);
    }

    //onResume() register the accelerometer for listening the events
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, magnetometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    //onPause() unregister the accelerometer for stop listening the events
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    float[] mGravity, mGeomagnetic;
    float azimut;
    @Override
    public void onSensorChanged(SensorEvent event) {

//         clean current values
        displayCleanValues();
        // display the current x,y,z accelerometer values
        displayCurrentValues();
        // display the max x,y,z accelerometer values
        displayMaxValues();

        // get the change of the x,y,z values of the accelerometer
        deltaX = Math.abs(lastX - event.values[0]);
        deltaY = Math.abs(lastY - event.values[1]);
        deltaZ = Math.abs(lastZ - event.values[2]);

        // if the change is below 2, it is just plain noise
        if (deltaX < 2)
            deltaX = 0;
        if (deltaY < 2)
            deltaY = 0;
//        if ((deltaZ > vibrateThreshold) || (deltaY > vibrateThreshold) || (deltaZ > vibrateThreshold)) {
//            v.vibrate(50);
//        }


        //Compass
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER)
            mGravity = event.values;
        if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD)
            mGeomagnetic = event.values;

        if (mGravity != null && mGeomagnetic != null) {
            float R[] = new float[9];
            float outR[] = new float[9];
            float I[] = new float[9];

            boolean success = SensorManager.getRotationMatrix(R, I, mGravity, mGeomagnetic);
            if (success) {
                float orientation[] = new float[3];

                SensorManager.remapCoordinateSystem(R, SensorManager.AXIS_X, SensorManager.AXIS_Y, outR);

                SensorManager.getOrientation(outR, orientation);
                azimut = orientation[0];

                float degree = (float) (Math.toDegrees(azimut) + 360) % 360;

                System.out.println("degree " + degree);
                String direction = "";
                if(degree > 22 && degree <=67)
                    direction = "North-East";
                else if(degree > 67 && degree <=112)
                    direction = "East";
                else if(degree > 112 && degree <=157)
                    direction = "South-East";
                else if(degree > 157 && degree <=202)
                    direction = "South";
                else if(degree > 202 && degree <=247)
                    direction = "South-West";
                else if(degree > 247 && degree <=292)
                    direction = "West";
                else if(degree > 292 && degree <=337)
                    direction = "North-West";
                else if(degree > 337 && degree > 0 && degree <=22)
                    direction = "North";

                cmpsangle.setText(direction + df.format(degree));
//                cmpsangle.setText(Float.toString(degree));
            }
        }
    }

    public void displayCleanValues() {
        currentX.setText("0.0");
        currentY.setText("0.0");
        currentZ.setText("0.0");
    }

    // display the current x,y,z accelerometer values
    public void displayCurrentValues() {//df.format(
//        currentX.setText(Float.toString(deltaX));
//        currentY.setText(Float.toString(deltaY));
//        currentZ.setText(Float.toString(deltaZ));

        currentX.setText(df.format(deltaX));
        currentY.setText(df.format(deltaY));
        currentZ.setText(df.format(deltaZ));
    }

    // display the max x,y,z accelerometer values
    public void displayMaxValues() {
        if (deltaX > deltaXMax) {
            deltaXMax = deltaX;
            maxX.setText(df.format(deltaXMax));
        }
        if (deltaY > deltaYMax) {
            deltaYMax = deltaY;
            maxY.setText(df.format(deltaYMax));
        }
        if (deltaZ > deltaZMax) {
            deltaZMax = deltaZ;
            maxZ.setText(df.format(deltaZMax));
        }

//        if (deltaX > deltaXMax) {
//            deltaXMax = deltaX;
//            maxX.setText(Float.toString(deltaXMax));
//        }
//        if (deltaY > deltaYMax) {
//            deltaYMax = deltaY;
//            maxY.setText(Float.toString(deltaYMax));
//        }
//        if (deltaZ > deltaZMax) {
//            deltaZMax = deltaZ;
//            maxZ.setText(Float.toString(deltaZMax));
//        }
    }
}
