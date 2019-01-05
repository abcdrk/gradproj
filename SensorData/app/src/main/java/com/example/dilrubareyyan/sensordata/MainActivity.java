package com.example.dilrubareyyan.sensordata;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    // ACCELOMETER
    private SensorManager acc_manager;
    private Sensor accelerometer;
    private TextView acc_textView;
    private TextView acc_file_textView;
    private float xAcceleration,yAcceleration,zAcceleration;
    String acc_filename = "acc_file";


    // LIGHT SENSOR
    private SensorManager light_manager;
    private Sensor light_sensor;
    private TextView light_textView;
    private TextView light_file_textView;
    private float light;
    String light_filename = "light_file";


    // SENSOR LIST
    private SensorManager mSensorManager;
    private Sensor mSensor;

    // READ WRITE
    FileOutputStream outputStream;


    @Override
    protected void onStart() {
        super.onStart();

//        Timer timer = new Timer();
//
//        timer.schedule(new TimerTask() {
//            @Override
//            public void run() {
//                while(true){
//                    System.out.println("a");
//                }
//
//            }
//        }, 2*1*1000);

        acc_manager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        light_manager.registerListener(this, light_sensor, SensorManager.SENSOR_DELAY_NORMAL);

        // Let's See What Sensors The Phone Has
        printSensors();
    }

    @Override
    protected void onPause() {
        super.onPause();
        acc_manager.unregisterListener(this);
        light_manager.unregisterListener(this);
        mSensorManager.unregisterListener(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_alternative);

        // Set Text Views
        acc_textView = (TextView)findViewById(R.id.sensorAccData);
        light_textView = (TextView)findViewById(R.id.sensorLightData);
        acc_file_textView = (TextView)findViewById(R.id.sensorAccFile);
        light_file_textView = (TextView)findViewById(R.id.sensorLightFile);

        // Set The Sensor Managers
        acc_manager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        light_manager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        // Set The Sensors
        accelerometer = acc_manager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        light_sensor = light_manager.getDefaultSensor(Sensor.TYPE_LIGHT);

        clear(acc_filename, acc_file_textView); // Clears the Acc file
        clear(light_filename, light_file_textView); // Clears the Light file

    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        if(event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            xAcceleration = event.values[0];
            yAcceleration = event.values[1];
            zAcceleration = event.values[2];

            String content = "x:"+xAcceleration+"\nY:"+yAcceleration+"\nZ:"+zAcceleration+"\n";

            acc_textView.setText(content);

            try {
                outputStream = openFileOutput(acc_filename, Context.MODE_APPEND | Context.MODE_PRIVATE);
                outputStream.write(content.getBytes());
                outputStream.close();
            }
            catch (Exception e) {
                e.printStackTrace();
            }

            acc_file_textView.setText(readFromFile(this, acc_filename));

        }

        if(event.sensor.getType() == Sensor.TYPE_LIGHT) {
            String content = "intensity: "+event.values[0]+"\n";
            light_textView.setText(content);

            try {
                outputStream = openFileOutput(light_filename, Context.MODE_APPEND | Context.MODE_PRIVATE);
                outputStream.write(content.getBytes());
                outputStream.close();
            }
            catch (Exception e) {
                e.printStackTrace();
            }

            light_file_textView.setText(readFromFile(this, light_filename));
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }


    /**
     * EKSTRA METHODS ARE IMPLEMENTED HERE
     */


    private String readFromFile(Context context, String filename) {

        String ret = "";

        try {
            InputStream inputStream = context.openFileInput(filename);

            if ( inputStream != null ) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ( (receiveString = bufferedReader.readLine()) != null ) {
                    stringBuilder.append(receiveString);
                    stringBuilder.append("\n");
                }

                inputStream.close();
                ret = stringBuilder.toString();
            }
        }
        catch (FileNotFoundException e) {
            Log.e("login activity", "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e("login activity", "Can not read file: " + e.toString());
        }

        return ret;
    }

    void clear(String filename, TextView data_textView){
        String empty = "";

        try {
            outputStream = openFileOutput(filename, Context.MODE_PRIVATE);
            outputStream.write(empty.getBytes());
            System.out.println("Clear Everything.");
            outputStream.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        data_textView.setText(readFromFile(this, filename));
    }

    void printSensors(){
        List<Sensor> deviceSensors = mSensorManager.getSensorList(Sensor.TYPE_ALL);

        for(int i=0; i<deviceSensors.size(); i++) {
            System.out.println(deviceSensors.get(i).getName());
        }
    }


}
