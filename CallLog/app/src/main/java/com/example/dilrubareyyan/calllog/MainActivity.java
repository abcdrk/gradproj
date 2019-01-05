package com.example.dilrubareyyan.calllog;

import android.Manifest;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.provider.CallLog;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if( ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_CALL_LOG)
                != PackageManager.PERMISSION_GRANTED ){
            if(ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,Manifest.permission.READ_CALL_LOG))
            {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_CALL_LOG},1);
            }
            else{
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_CALL_LOG},1);
            }
        }
        else{
            // do stuff
            TextView textView = (TextView) findViewById(R.id.textView);
            try {
                textView.setText(getCallDetails());
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case 1: {
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    if(ContextCompat.checkSelfPermission(MainActivity.this,
                            Manifest.permission.READ_CALL_LOG) == PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(this, "Permission granted!", Toast.LENGTH_SHORT).show();

                        // do stuff
                        TextView textView = (TextView) findViewById(R.id.textView);
                        try {
                            textView.setText(getCallDetails());
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                }
                else {
                    Toast.makeText(this, "No permission granted!", Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
    }

    private String getCallDetails() throws ParseException {
        StringBuffer sb = new StringBuffer();
        Cursor managedCursor = getContentResolver().query(CallLog.Calls.CONTENT_URI, null,null,null,null);
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


        while( managedCursor.moveToNext()){
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
            switch (dircode){
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
            sb.append("\nPhone NUmber: "+ phNumber + " \nCall Type: " + dir + " \nCall Date: "+dateString+" \nCall Duration: "+ callDuration);
            sb.append("\n-----------------------------------------------");
        }
        sb.append("\nNumber of Calls: "+call_number+"\nNumber of Missed Calls: "+missed_number);
        managedCursor.close();
        return sb.toString();
    }
}
