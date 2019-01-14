package com.empatica.sample.Persistence;

import android.os.Handler;
import android.os.HandlerThread;

public class DataThread {

    private static Handler handler;
    private static HandlerThread thread;
    private static DataThread dataThread;
    private DatabaseHelper db;

    private DataThread(DatabaseHelper db_) {
        thread = new HandlerThread("DataThread");
        thread.start();
        handler = new Handler(thread.getLooper());
        this.db = db_;
    }

    public static DataThread getInstance(DatabaseHelper db) {
        if (dataThread == null) {
            dataThread = new DataThread(db);
        }
        return dataThread;
    }

    public void insertSensorData(final SensorData.Type type, final float value) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                insertData(type, value);
            }
        });
    }

    private void insertData(SensorData.Type type, float value) {
        this.db.insertSensorData(new SensorData(type, (double) value));
    }


}
