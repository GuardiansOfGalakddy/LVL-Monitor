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
            while (cursor.moveToNext()) {
                markers[i++] = map.addMarker(new MarkerOptions()
                        .position(new LatLng(cursor.getDouble(1), cursor.getDouble(2)))
                        .title(cursor.getString(0))
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
            }
            //cursor.close();
        }
        return markers;
    }

    public static Boolean isAlreadyExistInDB(String title, Cursor cursor) {
        cursor.moveToFirst();
        String tmpTitle = null;

        try {
            tmpTitle = new String(HexToByte.hexStringToByteArray(title.substring(0, 6)), "UTF-8") +
                    title.substring(6);
            Log.d("isAlreadyExistInDB", tmpTitle);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (cursor != null) {
            while (cursor.moveToNext()) {
                String tmpTitle2 = cursor.getString(0);
                Log.d("isAlreadyExistInDB()", tmpTitle2);
                if (tmpTitle2.equals(tmpTitle))
                    return true;
            }
            //cursor.close();
        }
        return false;
    }
}
