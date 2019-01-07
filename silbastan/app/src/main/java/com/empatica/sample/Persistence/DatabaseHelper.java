package com.empatica.sample.Persistence;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


public class DatabaseHelper extends SQLiteOpenHelper {
    public static Lock lock = new ReentrantLock();
    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "empaticas_db";


    // Sensor Data Columns
    public static final String TABLE_NAME_SENSOR = "sensor_datas";

    private static final String COLUMN_SENSOR_ID = "id";
    private static final String COLUMN_SENSOR_TYPE = "type";
    private static final String COLUMN_SENSOR_VALUE = "value";
    private static final String COLUMN_SENSOR_TIME = "time";
    private static final String CREATE_TABLE_SENSOR =
            "CREATE TABLE " + TABLE_NAME_SENSOR + "("
                    + COLUMN_SENSOR_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + COLUMN_SENSOR_TYPE + " INTEGER,"
                    + COLUMN_SENSOR_VALUE + " DOUBLE,"
                    + COLUMN_SENSOR_TIME + " DATETIME DEFAULT CURRENT_TIMESTAMP"
                    + ")";


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        // create notes table
        db.execSQL(CREATE_TABLE_SENSOR);
    }


    public long insertSensorData(SensorData data) {
        lock.lock();
        // get writable database as we want to write data
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        // `id` and `timestamp` will be inserted automatically.
        // no need to add them
        values.put(COLUMN_SENSOR_TYPE, data.getType().data);
        values.put(COLUMN_SENSOR_VALUE, data.getValue());

        // insert row
        long id = db.insert(TABLE_NAME_SENSOR, null, values);

        // close db connection
        db.close();

        lock.unlock();
        // return newly inserted row id
        return id;
    }

    public List<SensorData> getSensorDatas() {
        return this.getSensorDatas(null, null);
    }

    public List<SensorData> getSensorDatas(SensorData.Type type) {
        return getSensorDatas(type, null);
    }

    public List<SensorData> getSensorDatas(SensorData.Type type, Date date) {
        lock.lock();
        List<SensorData> datas = new ArrayList<SensorData>();

        Timestamp timestamp = null;
        if (date != null) {
            timestamp = new Timestamp(date.getTime());
        }

        String typeWhere = "";
        String timestampWhere = "";

        if (type != null) {
            typeWhere = " WHERE " + COLUMN_SENSOR_TYPE + " = " + type.data;
        }

        if (timestamp != null) {
            timestampWhere = (type == null)
                    ? " WHERE " + COLUMN_SENSOR_TIME + " < " + timestamp.toString()
                    : " AND " + COLUMN_SENSOR_TIME + " < " + timestamp.toString();
        }
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_NAME_SENSOR + typeWhere + timestampWhere + " ORDER BY " + COLUMN_SENSOR_TIME + " DESC";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                SensorData data = new SensorData();
                data.setType(cursor.getInt(cursor.getColumnIndex(COLUMN_SENSOR_TYPE)));
                data.setValue(cursor.getDouble(cursor.getColumnIndex(COLUMN_SENSOR_VALUE)));
                data.setTimestamp(cursor.getString(cursor.getColumnIndex(COLUMN_SENSOR_TIME)));

                datas.add(data);
            } while (cursor.moveToNext());
        }

        // close db connection
        db.close();

        lock.unlock();
        // return datas list
        return datas;
    }

    public int getSensorDatasCount() {
        return this.getSensorDatasCount(null);
    }

    public int getSensorDatasCount(SensorData.Type type) {
        lock.lock();
        String countQuery = (type == null)
                ? "SELECT  * FROM " + TABLE_NAME_SENSOR
                : "SELECT  * FROM " + TABLE_NAME_SENSOR + " WHERE " + COLUMN_SENSOR_TYPE + " = " + type.data;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);

        int count = cursor.getCount();
        cursor.close();

        lock.unlock();
        // return count
        return count;
    }

    public void deleteSensorDatas() {
        deleteSensorDatas(null, null);
    }

    public void deleteSensorDatas(SensorData.Type type) {
        deleteSensorDatas(type, null);
    }

    public void deleteSensorDatas(SensorData.Type type, Date date) {
        lock.lock();
        Timestamp timestamp = null;
        if (date != null) {
            timestamp = new Timestamp(date.getTime());
        }

        SQLiteDatabase db = this.getWritableDatabase();
        if (type == null && timestamp == null) {
            db.delete(TABLE_NAME_SENSOR, null, null);
        } else if (type != null && timestamp == null) {
            db.delete(TABLE_NAME_SENSOR, COLUMN_SENSOR_TYPE + " = ?", new String[]{String.valueOf(type.data)});
        } else if (type == null && timestamp != null) {
            db.delete(TABLE_NAME_SENSOR, COLUMN_SENSOR_TIME + " < ?", new String[]{timestamp.toString()});
        } else {
            db.delete(TABLE_NAME_SENSOR, COLUMN_SENSOR_TYPE + " = ? AND " + COLUMN_SENSOR_TIME + " < ?", new String[]{String.valueOf(type.data), timestamp.toString()});
        }
        db.close();
        lock.unlock();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_SENSOR);

        // Create tables again
        onCreate(db);
    }

}
