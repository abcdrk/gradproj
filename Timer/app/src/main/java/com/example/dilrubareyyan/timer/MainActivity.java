package com.example.dilrubareyyan.timer;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements SensorEventListener {

    Timer timer;
    TimerTask timerTask;

    Timer timer1;
    TimerTask timerTask1;

    //we are going to use a handler to be able to run in our TimerTask
    final Handler handler = new Handler();
    final Handler handler1 = new Handler();

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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_alternative);

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

        printSensors();
        startTimer();
    }

    @Override
    protected void onResume() {
        super.onResume();

        //onResume we start our timer so it can start when the app comes from the background
        startTimer();
    }

    public void startTimer() {
        //set a new Timer
        timer = new Timer();
        timer1 = new Timer();

        //initialize the TimerTask's job
        initializeTimerTask();
        initializeTimerTask1();

        //schedule the timer, after the first 5000ms the TimerTask will run every 10000ms
        timer.schedule(timerTask, 0, 120000); //

        timer1.schedule(timerTask1, 100000, 120000); //
    }

    public void stoptimertask(View v) {
        //stop the timer, if it's not already null
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    public void initializeTimerTask() {

        timerTask = new TimerTask() {
            public void run() {

                //use a handler to run a toast that shows the current timestamp
                handler.post(new Runnable() {
                    public void run() {
                        clearFiles();
                        registerListeners();
                        System.out.println("START");
                    }
                });

            }
        };
    }

    public void initializeTimerTask1() {

        timerTask1 = new TimerTask() {
            public void run() {

                //use a handler to run a toast that shows the current timestamp
                handler1.post(new Runnable() {
                    public void run() {
                        unregisterListeners();
                        System.out.println("STOP");
                    }
                });

            }
        };
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

//    @Override
//    protected void onStart() {
//        super.onStart();
//
//        acc_manager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
//        light_manager.registerListener(this, light_sensor, SensorManager.SENSOR_DELAY_NORMAL);
//
//        // Let's See What Sensors The Phone Has
//        printSensors();
//    }

    public void registerListeners(){
        acc_manager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        light_manager.registerListener(this, light_sensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        acc_manager.unregisterListener(this);
        light_manager.unregisterListener(this);
        mSensorManager.unregisterListener(this);
    }

    protected void unregisterListeners() {
        acc_manager.unregisterListener(this);
        light_manager.unregisterListener(this);
        mSensorManager.unregisterListener(this);
    }

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

    void clearFiles(){
        clear(acc_filename, acc_file_textView); // Clears the Acc file
        clear(light_filename, light_file_textView); // Clears the Light file
    }

    void printSensors(){
        List<Sensor> deviceSensors = mSensorManager.getSensorList(Sensor.TYPE_ALL);

        for(int i=0; i<deviceSensors.size(); i++) {
            System.out.println(deviceSensors.get(i).getName());
        }
    }
}