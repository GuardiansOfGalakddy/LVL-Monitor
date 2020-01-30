package com.guardiansofgalakddy.lvlmonitor.junhwa;

import android.database.Cursor;
import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class DB2OthersConnector {
    public static Marker[] addMarkersFromDB(GoogleMap map, Cursor cursor) {
        if (cursor == null || cursor.getCount() == 0)
            return null;

        cursor.moveToFirst();
        Marker[] markers = new Marker[cursor.getCount()];
        int i = 0;
        while (true) {
            markers[i++] = map.addMarker(new MarkerOptions()
                    .position(new LatLng(cursor.getDouble(1), cursor.getDouble(2)))
                    .title(cursor.getString(0))
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
            if (!cursor.moveToNext())
                break;
        }
        return markers;
    }

    public static Boolean isAlreadyExistInDB(String title, Cursor cursor) {
        if (cursor == null || cursor.getCount() == 0)
            return false;

        cursor.moveToFirst();
        while (true) {
            String tmpTitle = cursor.getString(0);
            Log.d("isAlreadyExistInDB()", tmpTitle);
            if (tmpTitle.equals(title))
                return true;

            if (!cursor.moveToNext())
                break;
        }
        return false;
    }
}
