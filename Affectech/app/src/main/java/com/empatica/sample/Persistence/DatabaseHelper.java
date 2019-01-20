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
    private static final int DATABASE_VERSION = 5;

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

    // Results Columns
    public static final String TABLE_NAME_RESULT = "results";
    private static final String COLUMN_RESULT_ID = "id";
    private static final String COLUMN_RESULT_TIME = "time";
    private static final String COLUMN_RESULT_MEAN_HR = "mean_hr";
    private static final String COLUMN_RESULT_STD_HR = "std_hr";
    private static final String COLUMN_RESULT_LF_HR = "lf_hr";
    private static final String COLUMN_RESULT_HF_HR = "hf_hr";
    private static final String COLUMN_RESULT_LF_HF_HR = "lf_hf_hr";
    private static final String COLUMN_RESULT_MEAN_X = "mean_x";
    private static final String COLUMN_RESULT_MEAN_Y = "mean_y";
    private static final String COLUMN_RESULT_MEAN_Z = "mean_z";
    private static final String COLUMN_RESULT_ENERGY_ACC = "energy_acc";
    private static final String COLUMN_RESULT_MEAN_EDA = "mean_eda";
    private static final String COLUMN_RESULT_STD_EDA = "std_eda";
    private static final String COLUMN_RESULT_PEAKS_PER = "peaks_per";
    private static final String COLUMN_RESULT_MEAN_LIGHT = "mean_light";
    private static final String COLUMN_RESULT_STD_LIGHT = "std_light";
    private static final String COLUMN_RESULT_STEP = "step";
    private static final String COLUMN_RESULT_CALL = "call";

    private static final String CREATE_TABLE_RESULT =
            "CREATE TABLE " + TABLE_NAME_RESULT + "("
                    + COLUMN_RESULT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + COLUMN_RESULT_TIME + " DATETIME DEFAULT CURRENT_TIMESTAMP,"
                    + COLUMN_RESULT_MEAN_HR + " DOUBLE,"
                    + COLUMN_RESULT_STD_HR + " DOUBLE,"
                    + COLUMN_RESULT_LF_HR + " DOUBLE,"
                    + COLUMN_RESULT_HF_HR + " DOUBLE,"
                    + COLUMN_RESULT_LF_HF_HR + " DOUBLE,"
                    + COLUMN_RESULT_MEAN_X + " DOUBLE,"
                    + COLUMN_RESULT_MEAN_Y + " DOUBLE,"
                    + COLUMN_RESULT_MEAN_Z + " DOUBLE,"
                    + COLUMN_RESULT_ENERGY_ACC + " DOUBLE,"
                    + COLUMN_RESULT_MEAN_EDA + " DOUBLE,"
                    + COLUMN_RESULT_STD_EDA + " DOUBLE,"
                    + COLUMN_RESULT_PEAKS_PER + " DOUBLE,"
                    + COLUMN_RESULT_MEAN_LIGHT + " DOUBLE,"
                    + COLUMN_RESULT_STD_LIGHT + " DOUBLE,"
                    + COLUMN_RESULT_STEP + " DOUBLE,"
                    + COLUMN_RESULT_CALL + " DOUBLE"
                    + ")";


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_SENSOR);
        db.execSQL(CREATE_TABLE_RESULT);
    }


    public long insertResult(Result result) {
        lock.lock();
        // get writable database as we want to write data
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        // `id` and `timestamp` will be inserted automatically.
        // no need to add them
        values.put(COLUMN_RESULT_MEAN_HR, result.getMeanHR());
        values.put(COLUMN_RESULT_STD_HR, result.getStdHR());
        values.put(COLUMN_RESULT_LF_HR, result.getLfHR());
        values.put(COLUMN_RESULT_HF_HR, result.getHfHR());
        values.put(COLUMN_RESULT_LF_HF_HR, result.getLfhfHR());
        values.put(COLUMN_RESULT_MEAN_X, result.getMeanX());
        values.put(COLUMN_RESULT_MEAN_Y, result.getMeanY());
        values.put(COLUMN_RESULT_MEAN_Z, result.getMeanZ());
        values.put(COLUMN_RESULT_ENERGY_ACC, result.getEnergyAcc());
        values.put(COLUMN_RESULT_MEAN_EDA, result.getMeanEDA());
        values.put(COLUMN_RESULT_STD_EDA, result.getStdEDA());
        values.put(COLUMN_RESULT_PEAKS_PER, result.getPeaksPer());

        // insert row
        long id = db.insert(TABLE_NAME_RESULT, null, values);

        // close db connection
        db.close();

        lock.unlock();
        // return newly inserted row id
        return id;
    }

    public ArrayList<Result> getResults() {
        lock.lock();
        ArrayList<Result> results = new ArrayList<Result>();

        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_NAME_RESULT + " ORDER BY " + COLUMN_RESULT_TIME + " DESC";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Result result = new Result();
                result.setTimestamp(cursor.getString(cursor.getColumnIndex(COLUMN_RESULT_TIME)));

                result.setMeanHR(cursor.getDouble(cursor.getColumnIndex(COLUMN_RESULT_MEAN_HR)));
                result.setStdHR(cursor.getDouble(cursor.getColumnIndex(COLUMN_RESULT_STD_HR)));
                result.setLfHR(cursor.getDouble(cursor.getColumnIndex(COLUMN_RESULT_LF_HR)));
                result.setHfHR(cursor.getDouble(cursor.getColumnIndex(COLUMN_RESULT_HF_HR)));
                result.setLfhfHR(cursor.getDouble(cursor.getColumnIndex(COLUMN_RESULT_LF_HF_HR)));
                result.setMeanX(cursor.getDouble(cursor.getColumnIndex(COLUMN_RESULT_MEAN_X)));
                result.setMeanY(cursor.getDouble(cursor.getColumnIndex(COLUMN_RESULT_MEAN_Y)));
                result.setMeanZ(cursor.getDouble(cursor.getColumnIndex(COLUMN_RESULT_MEAN_Z)));
                result.setEnergyAcc(cursor.getDouble(cursor.getColumnIndex(COLUMN_RESULT_ENERGY_ACC)));
                result.setMeanEDA(cursor.getDouble(cursor.getColumnIndex(COLUMN_RESULT_MEAN_EDA)));
                result.setStdEDA(cursor.getDouble(cursor.getColumnIndex(COLUMN_RESULT_STD_EDA)));
                result.setPeaksPer(cursor.getDouble(cursor.getColumnIndex(COLUMN_RESULT_PEAKS_PER)));
                results.add(result);
            } while (cursor.moveToNext());
        }

        // close db connection
        db.close();

        lock.unlock();
        // return datas list
        return results;
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

    public ArrayList<SensorData> getSensorDatas() {
        return this.getSensorDatas(null, null);
    }

    public ArrayList<SensorData> getSensorDatas(SensorData.Type type) {
        return getSensorDatas(type, null);
    }

    public ArrayList<SensorData> getSensorDatas(SensorData.Type type, Date date) {
        lock.lock();
        ArrayList<SensorData> datas = new ArrayList<SensorData>();

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
                    ? " WHERE " + COLUMN_SENSOR_TIME + " < '" + timestamp.toString() + "'"
                    : " AND " + COLUMN_SENSOR_TIME + " < '" + timestamp.toString() + "'";
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

    public int getResultsCount() {
        lock.lock();
        String countQuery = "SELECT  * FROM " + TABLE_NAME_RESULT;

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
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_RESULT);

        // Create tables again
        onCreate(db);
    }

}
