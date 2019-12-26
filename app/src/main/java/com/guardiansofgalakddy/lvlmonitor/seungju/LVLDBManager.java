package com.guardiansofgalakddy.lvlmonitor.seungju;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.util.Pair;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class LVLDBManager {
    static final String LVL_DB = "LVL.db";
    static final String LVL_TABLE = "LVL";
    static final int DB_VERSION = 1;

    Context mContext = null;

    private static LVLDBManager mDbManager = null;
    private SQLiteDatabase mDatabase = null;

    private LVLDBManager(Context context) {
        this.mContext = context;

        mDatabase = this.mContext.openOrCreateDatabase(LVL_DB, Context.MODE_PRIVATE, null);

        mDatabase.execSQL(
                "CREATE TABLE IF NOT EXISTS " + LVL_TABLE +
                        "(_id  INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "systemid  TEXT," +
                        "latitude  DOUBLE, " +
                        "longitude DOUBLE); ");
    }

    public static LVLDBManager getInstance(Context context) {
        if (mDbManager == null)
            mDbManager = new LVLDBManager(context);
        return mDbManager;
    }

    public long insert(ContentValues addRowValue) {
        return mDatabase.insert(LVL_TABLE, null, addRowValue);
    }

    //////////////////////////////////////////////////////////////////////////////////////////////
    public Cursor query(String[] columns, String selection, String[] selectionArgs, String groupBy, String having, String orderBy) {
        return mDatabase.query(LVL_TABLE, columns, selection, selectionArgs, groupBy, having, orderBy);
    }

    public int update(ContentValues updateRowValue, String whereClause, String[] whereArgs) {
        return mDatabase.update(LVL_TABLE, updateRowValue, whereClause, whereArgs);
    }

    public int delete(String whereClause, String[] whereArgs) {
        return mDatabase.delete(LVL_TABLE, whereClause, whereArgs);
    }

    public Cursor getIdNLatLng() {
        String[] columns = new String[]{"systemid", "latitude", "longitude"};
        Cursor cursor = mDbManager.query(columns, null, null, null, null, null);
        return cursor;
    }
}
