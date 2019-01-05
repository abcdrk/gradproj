package com.empatica.sample;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;

import com.empatica.empalink.EmpaDeviceManager;
import com.empatica.empalink.EmpaticaDevice;
import com.empatica.empalink.config.EmpaSensorType;
import com.empatica.empalink.config.EmpaStatus;
import com.empatica.empalink.delegate.EmpaDataDelegate;
import com.empatica.empalink.delegate.EmpaStatusDelegate;

import java.io.FileOutputStream;

public class EmpaticaListener implements EmpaDataDelegate, EmpaStatusDelegate {

    private static final int REQUEST_ENABLE_BT = 1;

    private static final int REQUEST_PERMISSION_ACCESS_COARSE_LOCATION = 1;

    private static final String EMPATICA_API_KEY = "566dd8431c934faba8a480d3930c111c"; // TODO insert your API Key here

    FileOutputStream outputStream;

    String ibi_filename = "ibi_file";
    String acc_filename = "acc_file";


    private EmpaDeviceManager deviceManager = null;

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
                    Log.d("INIT", "Couldn't connect to device");
                }
                break;
        }
    }

    private void initEmpaticaDeviceManager() {
        // Android 6 (API level 23) now require ACCESS_COARSE_LOCATION permission to use BLE
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[] { Manifest.permission.ACCESS_COARSE_LOCATION }, REQUEST_PERMISSION_ACCESS_COARSE_LOCATION);
        } else {

            if (TextUtils.isEmpty(EMPATICA_API_KEY)) {
                Log.d("INIT","Please Enter the API key");
                return;
            }

            // Create a new EmpaDeviceManager. MainActivity is both its data and status delegate.
            deviceManager = new EmpaDeviceManager(MainActivity., this, this);

            // Initialize the Device Manager using your API key. You need to have Internet access at this point.
            deviceManager.authenticateWithAPIKey(EMPATICA_API_KEY);
        }
    }

    @Override
    public void didReceiveGSR(float gsr, double timestamp) {

    }

    @Override
    public void didReceiveBVP(float bvp, double timestamp) {

    }

    @Override
    public void didReceiveIBI(float ibi, double timestamp) {

    }

    @Override
    public void didReceiveTemperature(float t, double timestamp) {

    }

    @Override
    public void didReceiveAcceleration(int x, int y, int z, double timestamp) {

    }

    @Override
    public void didReceiveBatteryLevel(float level, double timestamp) {

    }

    @Override
    public void didReceiveTag(double timestamp) {

    }

    @Override
    public void didUpdateStatus(EmpaStatus status) {

    }

    @Override
    public void didEstablishConnection() {

    }

    @Override
    public void didUpdateSensorStatus(int status, EmpaSensorType type) {

    }

    @Override
    public void didDiscoverDevice(EmpaticaDevice device, String deviceLabel, int rssi, boolean allowed) {

    }

    @Override
    public void didRequestEnableBluetooth() {

    }

    @Override
    public void didUpdateOnWristStatus(int status) {

    }

    /**
     * For the DISCONNECT Button
     */
    void disconnect(){
        if (deviceManager != null) {

            deviceManager.disconnect();
        }
    }
}
