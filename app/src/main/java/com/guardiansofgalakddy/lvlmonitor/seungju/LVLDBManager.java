package com.guardiansofgalakddy.lvlmonitor.seungju;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class LVLDBManager {

    static final String DB_STUDENTS = "LVL.db";
    static final String TABLE_STUDENTS = "LVL";
    static final int DB_VERSION = 1;

    Context mContext = null;

    private static LVLDBManager mDbManager = null;
    private SQLiteDatabase mDatabase = null;

    private LVLDBManager(Context context) {
        mContext = context;

        mDatabase = context.openOrCreateDatabase(DB_STUDENTS,Context.MODE_PRIVATE,null);

        mDatabase.execSQL(
                "CREATE TABLE IF NOT EXISTS " + TABLE_STUDENTS +
                "(" + "_id  INTEGER PRIMARY KEY AUTOINCREMENT, "+
                "systemid  TEXT,"+
                "latitude  TEXT, "+
                "longitude TEXT); ");
    }

    public static LVLDBManager getInstance(Context context)
    {
        if(mDbManager ==null)
        {
            mDbManager = new LVLDBManager(context);
        }
        return mDbManager;
    }

    public long insert (ContentValues addRowValue)
    {
        return mDatabase.insert(TABLE_STUDENTS,null,addRowValue);
    }

    //////////////////////////////////////////////////////////////////////////////////////////////
    public Cursor query(String[] columns,String selection,String[] selectionArgs,String groupBy,String having,String orderBy)
    {
        return mDatabase.query(TABLE_STUDENTS,columns,selection,selectionArgs,groupBy,having,orderBy);
    }
    public int update (ContentValues updateRowValue,String whereClause,String[] whereArgs)
    {
        return mDatabase.update(TABLE_STUDENTS,updateRowValue,whereClause,whereArgs);
    }
    public int delete(String whereClause,String[] whereArgs){
        return mDatabase.delete(TABLE_STUDENTS,whereClause,whereArgs);
    }


}
