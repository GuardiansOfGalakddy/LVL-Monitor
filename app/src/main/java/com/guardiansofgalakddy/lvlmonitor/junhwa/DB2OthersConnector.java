package com.guardiansofgalakddy.lvlmonitor.junhwa;

import android.database.Cursor;
import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.guardiansofgalakddy.lvlmonitor.superb.HexToByte;

public class DB2OthersConnector {
    public static Marker[] addMarkersFromDB(GoogleMap map, Cursor cursor) {
        cursor.moveToFirst();
        Marker[] markers = new Marker[cursor.getCount()];
        int i = 0;

        if (cursor != null) {
            markers[i++] = map.addMarker(new MarkerOptions()
                    .position(new LatLng(cursor.getDouble(1), cursor.getDouble(2)))
                    .title(cursor.getString(0))
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));

            while (cursor.moveToNext()) {
                markers[i++] = map.addMarker(new MarkerOptions()
                        .position(new LatLng(cursor.getDouble(1), cursor.getDouble(2)))
                        .title(cursor.getString(0))
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
            }
            //cursor.close();
        }
        return markers;
    }

    public static Boolean isAlreadyExistInDB(String title, Cursor cursor) {
        cursor.moveToFirst();
        if (cursor != null) {
            String tmpTitle = cursor.getString(0);
            Log.d("isAlreadyExistInDB()", tmpTitle);
            if (tmpTitle.equals(title))
                return true;

            while (cursor.moveToNext()) {
                String tmpTitle2 = cursor.getString(0);
                Log.d("isAlreadyExistInDB()", tmpTitle2);
                if (tmpTitle2.equals(title))
                    return true;
            }
        }
        return false;
    }
}
