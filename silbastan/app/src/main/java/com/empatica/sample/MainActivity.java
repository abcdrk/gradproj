package com.empatica.sample;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.CallLog;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.empatica.empalink.ConnectionNotAllowedException;
import com.empatica.empalink.EmpaDeviceManager;
import com.empatica.empalink.EmpaticaDevice;
import com.empatica.empalink.config.EmpaSensorStatus;
import com.empatica.empalink.config.EmpaSensorType;
import com.empatica.empalink.config.EmpaStatus;
import com.empatica.empalink.delegate.EmpaDataDelegate;
import com.empatica.empalink.delegate.EmpaStatusDelegate;
import com.empatica.sample.Persistence.DataThread;
import com.empatica.sample.Persistence.DatabaseHelper;
import com.empatica.sample.Persistence.Result;
import com.empatica.sample.Persistence.SensorData;

import org.apache.commons.math3.stat.StatUtils;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


public class MainActivity extends AppCompatActivity implements EmpaDataDelegate, EmpaStatusDelegate, SensorEventListener {

    private static final int REQUEST_ENABLE_BT = 1;

    private static final int REQUEST_PERMISSION_ACCESS_COARSE_LOCATION = 1;

    private static final String EMPATICA_API_KEY = "566dd8431c934faba8a480d3930c111c"; // TODO insert your API Key here


    private EmpaDeviceManager deviceManager = null;

    private TextView accel_xLabel;

    private TextView accel_yLabel;

    private TextView accel_zLabel;

    private TextView bvpLabel;

    private TextView edaLabel;

    private TextView ibiLabel;

    private TextView temperatureLabel;

    private TextView batteryLabel;

    private TextView statusLabel;

    private TextView deviceNameLabel;

    private TextView dataCountLabel;

    private TextView resultCountLabel;

    private LinearLayout dataCnt;

    private TextView file_view;

    public static Lock insertLock = new ReentrantLock();

    //ACCELOMETER
    private SensorManager acc_manager;
    private Sensor accelerometer;
    private TextView acc_textView;
    private float xAcceleration, yAcceleration, zAcceleration;

    //LIGHT SENSOR
    private SensorManager light_manager;
    private Sensor light_sensor;
    private TextView light_textView;
    private float light;

    //STEP COUNTER
    private SensorManager step_manager;
    private Sensor step_sensor;
    private TextView step_textView;
    private float step;

    //TEMPERATURE
    private SensorManager temp_manager;
    private Sensor temp_sensor;
    private TextView temp_textView;
    private float temp;

    // CALL LOG
    private TextView call_textView;

    //SENSOR LIST
    private SensorManager mSensorManager;
    private Sensor mSensor;

    FileOutputStream outputStream;

    // DB Helper
    public static DatabaseHelper db;

    private Set bluetoothDevices = new HashSet();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        this.db = new DatabaseHelper(getApplicationContext());

        setContentView(R.layout.activity_main_alternative);

        // Initialize vars that reference UI components
        statusLabel = (TextView) findViewById(R.id.status);

        dataCnt = (LinearLayout) findViewById(R.id.dataArea);

        accel_xLabel = (TextView) findViewById(R.id.accel_x);

        accel_yLabel = (TextView) findViewById(R.id.accel_y);

        accel_zLabel = (TextView) findViewById(R.id.accel_z);

        bvpLabel = (TextView) findViewById(R.id.bvp);

        edaLabel = (TextView) findViewById(R.id.eda);

        ibiLabel = (TextView) findViewById(R.id.ibi);

        temperatureLabel = (TextView) findViewById(R.id.temperature);

        batteryLabel = (TextView) findViewById(R.id.battery);

        deviceNameLabel = (TextView) findViewById(R.id.deviceName);

        dataCountLabel = (TextView) findViewById(R.id.dataCount);

        resultCountLabel = (TextView) findViewById(R.id.resultCount);

        file_view = (TextView) findViewById(R.id.file_view);

        call_textView = (TextView) findViewById(R.id.callLogData);

        acc_textView = (TextView) findViewById(R.id.sensorAccData);
        light_textView = (TextView) findViewById(R.id.sensorLightData);
        temp_textView = (TextView) findViewById(R.id.sensorTempData);
        step_textView = (TextView) findViewById(R.id.sensorStepData);

        // Set The Sensor Managers
        acc_manager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        light_manager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        step_manager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        temp_manager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        // Set The Sensors
        accelerometer = acc_manager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        light_sensor = light_manager.getDefaultSensor(Sensor.TYPE_LIGHT);
        //step_sensor = light_manager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        //temp_sensor = light_manager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE);

        acc_manager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        light_manager.registerListener(this, light_sensor, SensorManager.SENSOR_DELAY_NORMAL);
//        step_manager.registerListener(this, step_sensor, SensorManager.SENSOR_DELAY_NORMAL);
//        temp_manager.registerListener(this, temp_sensor, SensorManager.SENSOR_DELAY_NORMAL);


        final Button disconnectButton = findViewById(R.id.disconnectButton);

        disconnectButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if (deviceManager != null) {

                    deviceManager.disconnect();
                }
            }
        });

        this.getDatabaseHelper().deleteSensorDatas();
        initCallLogPermission();
        initEmpaticaDeviceManager();
        initCalculateTimer();

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_PERMISSION_ACCESS_COARSE_LOCATION:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission was granted, yay!
                    initEmpaticaDeviceManager();
                } else {
                    // Permission denied, boo!
                    final boolean needRationale = ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_COARSE_LOCATION);
                    new AlertDialog.Builder(this)
                            .setTitle("Permission required")
                            .setMessage("Without this permission bluetooth low energy devices cannot be found, allow it in order to connect to the device.")
                            .setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // try again
                                    if (needRationale) {
                                        // the "never ask again" flash is not set, try again with permission request
                                        initEmpaticaDeviceManager();
                                    } else {
                                        // the "never ask again" flag is set so the permission requests is disabled, try open app settings to enable the permission
                                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                        Uri uri = Uri.fromParts("package", getPackageName(), null);
                                        intent.setData(uri);
                                        startActivity(intent);
                                    }
                                }
                            })
                            .setNegativeButton("Exit application", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // without permission exit is the only way
                                    finish();
                                }
                            })
                            .show();
                }
                break;

            case 2:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(MainActivity.this,
                            Manifest.permission.READ_CALL_LOG) == PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(this, "Permission granted!", Toast.LENGTH_SHORT).show();

                        // do stuff
                        call_textView = (TextView) findViewById(R.id.callLogData);
                        call_textView.setText(Integer.toString(getCallDetails()));
                    }
                } else {
                    Toast.makeText(this, "No permission granted!", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    private void initCalculateTimer() {
        Timer timer = new Timer();
        TimerTask calculateTask = new TimerTask() {
            @Override
            public void run() {
                calculateResult();
            }
        };

//        timer.schedule(calculateTask, 0l, 1000 * 60 * 2);   // every 2 minutes
        timer.schedule(calculateTask, 0l, 1000 * 20);   // every 20 seconds

    }

    private void initEmpaticaDeviceManager() {
        // Android 6 (API level 23) now require ACCESS_COARSE_LOCATION permission to use BLE
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_PERMISSION_ACCESS_COARSE_LOCATION);
        } else {

            if (TextUtils.isEmpty(EMPATICA_API_KEY)) {
                new AlertDialog.Builder(this)
                        .setTitle("Warning")
                        .setMessage("Please insert your API KEY")
                        .setNegativeButton("Close", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // without permission exit is the only way
                                finish();
                            }
                        })
                        .show();
                return;
            }

            // Create a new EmpaDeviceManager. MainActivity is both its data and status delegate.
            deviceManager = new EmpaDeviceManager(getApplicationContext(), this, this);

            // Initialize the Device Manager using your API key. You need to have Internet access at this point.
            deviceManager.authenticateWithAPIKey(EMPATICA_API_KEY);
        }
    }

    private void initCallLogPermission() {

        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.READ_CALL_LOG)) {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_CALL_LOG}, 2);
            } else {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_CALL_LOG}, 2);
            }

        } else {
            // do stuff
            TextView textView = (TextView) findViewById(R.id.callLogData);
            textView.setText(Integer.toString(getCallDetails()));
        }
    }

    private void calculateResult() {
        Date date = new Date();
        Result result = new Result();
        ArrayList<SensorData> xs = this.getDatabaseHelper().getSensorDatas(SensorData.Type.ACC_X, date);
        ArrayList<SensorData> ys = this.getDatabaseHelper().getSensorDatas(SensorData.Type.ACC_Y, date);
        ArrayList<SensorData> zs = this.getDatabaseHelper().getSensorDatas(SensorData.Type.ACC_Z, date);

        result.calculateAcc(xs, ys, zs);

        ArrayList<SensorData> edas = this.getDatabaseHelper().getSensorDatas(SensorData.Type.EDA, date);
        result.calculateEDA(edas);

        ArrayList<SensorData> lights = this.getDatabaseHelper().getSensorDatas(SensorData.Type.LIGHT, date);
        result.calculateLight(lights);

        ArrayList<SensorData> ibis = this.getDatabaseHelper().getSensorDatas(SensorData.Type.IBI, date);
        result.calculateHR(ibis);


        result.setCall((double) getCallDetails());


        this.getDatabaseHelper().insertResult(result);

        this.getDatabaseHelper().deleteSensorDatas();

        runOnUiThread(new Runnable() {

            @Override
            public void run() {

                Toast.makeText(MainActivity.this, "Results are calculated", Toast.LENGTH_SHORT).show();
            }
        });


    }

    @Override
    protected void onPause() {
        super.onPause();
        if (deviceManager != null) {
            deviceManager.stopScanning();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (deviceManager != null) {
            deviceManager.cleanUp();
        }
    }

    @Override
    public void didDiscoverDevice(EmpaticaDevice bluetoothDevice, String deviceName, int rssi,
                                  boolean allowed) {
        // Check if the discovered device can be used with your API key. If allowed is always false,
        // the device is not linked with your API key. Please check your developer area at
        // https://www.empatica.com/connect/developer.php
        if (allowed) {
            // Stop scanning. The first allowed device will do.
            deviceManager.stopScanning();
            System.out.println("[CAKIR]: stop scanning ");
            try {
                // Connect to the device
                deviceManager.connectDevice(bluetoothDevice);
                updateLabel(deviceNameLabel, deviceName);
            } catch (ConnectionNotAllowedException e) {
                // This should happen only if you try to connect when allowed == false.
                Toast.makeText(MainActivity.this, "Sorry, you can't connect to this device", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void didRequestEnableBluetooth() {
        // Request the user to enable Bluetooth
        Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // The user chose not to enable Bluetooth
        if (requestCode == REQUEST_ENABLE_BT && resultCode == Activity.RESULT_CANCELED) {
            // You should deal with this
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void didUpdateSensorStatus(@EmpaSensorStatus int status, EmpaSensorType type) {

        didUpdateOnWristStatus(status);
    }

    @Override
    public void didUpdateStatus(EmpaStatus status) {
        // Update the UI
        updateLabel(statusLabel, status.name());

        // The device manager is ready for use
        if (status == EmpaStatus.READY) {
            updateLabel(statusLabel, status.name() + " - Turn on your device");
            // Start scanning
            deviceManager.startScanning();
            // The device manager has established a connection

            hide();

        } else if (status == EmpaStatus.CONNECTED) {

            show();
            // The device manager disconnected from a device
        } else if (status == EmpaStatus.DISCONNECTED) {

            updateLabel(deviceNameLabel, "");

            hide();
        }
    }

    @Override
    public void didReceiveAcceleration(int x, int y, int z, double timestamp) {
        updateLabel(accel_xLabel, "" + x);
        updateLabel(accel_yLabel, "" + y);
        updateLabel(accel_zLabel, "" + z);

        this.insertSensorData(SensorData.Type.ACC_X, x);
        this.insertSensorData(SensorData.Type.ACC_Y, y);
        this.insertSensorData(SensorData.Type.ACC_Z, z);
    }

    @Override
    public void didReceiveBVP(float bvp, double timestamp) {
        updateLabel(bvpLabel, "" + bvp);
    }

    @Override
    public void didReceiveBatteryLevel(float battery, double timestamp) {
        updateLabel(batteryLabel, String.format("%.0f %%", battery * 100));
    }

    @Override
    public void didReceiveGSR(float gsr, double timestamp) {
        updateLabel(edaLabel, "" + gsr);
        this.insertSensorData(SensorData.Type.EDA, gsr);
    }

    @Override
    public void didReceiveIBI(float ibi, double timestamp) {
        updateLabel(ibiLabel, "" + ibi);
        this.insertSensorData(SensorData.Type.IBI, ibi);
    }

    @Override
    public void didReceiveTemperature(float temp, double timestamp) {
        updateLabel(temperatureLabel, "" + temp);
    }

    // Update a label with some text, making sure this is run in the UI thread
    private void updateLabel(final TextView label, final String text) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                label.setText(text);
            }
        });
    }

    @Override
    public void didReceiveTag(double timestamp) {

    }

    @Override
    public void didEstablishConnection() {

        show();
    }

    @Override
    public void didUpdateOnWristStatus(@EmpaSensorStatus final int status) {

        runOnUiThread(new Runnable() {

            @Override
            public void run() {

                if (status == EmpaSensorStatus.ON_WRIST) {

                    ((TextView) findViewById(R.id.wrist_status_label)).setText("ON WRIST");
                } else {

                    ((TextView) findViewById(R.id.wrist_status_label)).setText("NOT ON WRIST");
                }
            }
        });
    }

    void show() {

        runOnUiThread(new Runnable() {

            @Override
            public void run() {

                dataCnt.setVisibility(View.VISIBLE);
            }
        });
    }

    void hide() {

        runOnUiThread(new Runnable() {

            @Override
            public void run() {

                dataCnt.setVisibility(View.INVISIBLE);
            }
        });
    }

    private String readFromFile(Context context, String filename) {

        String ret = "";

        try {
            InputStream inputStream = context.openFileInput(filename);

            if (inputStream != null) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ((receiveString = bufferedReader.readLine()) != null) {
                    stringBuilder.append(receiveString);
                    stringBuilder.append("\n");
                }

                inputStream.close();
                ret = stringBuilder.toString();
            }
        } catch (FileNotFoundException e) {
            Log.e("login activity", "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e("login activity", "Can not read file: " + e.toString());
        }

        return ret;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            xAcceleration = event.values[0];
            yAcceleration = event.values[1];
            zAcceleration = event.values[2];

            String content = "X: " + xAcceleration + "\nY: " + yAcceleration + "\nZ: " + zAcceleration + "\n";
            acc_textView.setText(content);
//            this.insertSensorData(SensorData.Type.ACC_PX, event.values[0]);
//            this.insertSensorData(SensorData.Type.ACC_PY, event.values[1]);
//            this.insertSensorData(SensorData.Type.ACC_PZ, event.values[2]);

        }

        if (event.sensor.getType() == Sensor.TYPE_LIGHT) {
            light_textView.setText(Double.toString(event.values[0]));
            this.insertSensorData(SensorData.Type.LIGHT, event.values[0]);
        }

        if (event.sensor.getType() == Sensor.TYPE_AMBIENT_TEMPERATURE) {
            temp_textView.setText(Double.toString(event.values[0]));
            this.insertSensorData(SensorData.Type.TEMP, event.values[0]);

        }

        if (event.sensor.getType() == Sensor.TYPE_STEP_COUNTER) {
            step_textView.setText(Double.toString(event.values[0]));
            this.insertSensorData(SensorData.Type.STEP, event.values[0]);
        }

        dataCountLabel.setText(Integer.toString(this.getDatabaseHelper().getSensorDatasCount()));
        resultCountLabel.setText(Integer.toString(this.getDatabaseHelper().getResultsCount()));
    }

    public DatabaseHelper getDatabaseHelper() {
        return this.db;
    }

    public void insertSensorData(SensorData.Type type, float value) {
        DataThread.getInstance(this.getDatabaseHelper()).insertSensorData(type, value);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    private int getCallDetails() {
        StringBuffer sb = new StringBuffer();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return 0;
        }
        Cursor managedCursor = getContentResolver().query(CallLog.Calls.CONTENT_URI, null, null, null, null);
        int number = managedCursor.getColumnIndex(CallLog.Calls.NUMBER);
        int type = managedCursor.getColumnIndex(CallLog.Calls.TYPE);
        int date = managedCursor.getColumnIndex(CallLog.Calls.DATE);
        int duration = managedCursor.getColumnIndex(CallLog.Calls.DURATION);
        int call_number = 0;
        int missed_number = 0;
        //String valid_until = "30-12-18 18:45";

        sb.append("Call Details:\n\n");

        //SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yy HH:mm");
        //Date strDate = sdf.parse(valid_until);


        while (managedCursor.moveToNext()) {
            String phNumber = managedCursor.getString(number);
            String callType = managedCursor.getString(type);
            String callDate = managedCursor.getString(date);


            Date callDayTime = new Date(Long.valueOf(callDate));
            Date current = Calendar.getInstance().getTime();

            // (29 + 15 + 60) * 60 * 1000 = 6240000L
            // One Day Before: 1 * 24 * 60 * 60 * 1000 = 86400000L

            Date one_day_before = new Date(current.getTime() - 86400000L);

            SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yy HH:mm");
            String dateString = formatter.format(callDayTime);

            String callDuration = managedCursor.getString(duration);

            if (callDayTime.before(one_day_before)) {
                continue;
            }

            String dir = null;
            int dircode = Integer.parseInt(callType);
            switch (dircode) {
                case CallLog.Calls.OUTGOING_TYPE:
                    dir = "OUTGOING";
                    call_number++;
                    break;

                case CallLog.Calls.INCOMING_TYPE:
                    dir = "INCOMING";
                    call_number++;
                    break;

                case CallLog.Calls.MISSED_TYPE:
                    dir = "MISSED";
                    missed_number++;
                    break;
            }
            //sb.append("\nPhone NUmber: " + phNumber + " \nCall Type: " + dir + " \nCall Date: " + dateString + " \nCall Duration: " + callDuration);
            //sb.append("\n-----------------------------------------------");
        }
        sb.append("\nNumber of Calls: " + call_number + "\nNumber of Missed Calls: " + missed_number);
        managedCursor.close();
        return call_number + missed_number;
    }

}
