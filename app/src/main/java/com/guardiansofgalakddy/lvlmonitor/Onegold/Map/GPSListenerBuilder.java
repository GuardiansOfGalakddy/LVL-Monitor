package com.guardiansofgalakddy.lvlmonitor.Onegold.Map;

import android.content.Context;

import com.google.android.gms.maps.GoogleMap;

public class GPSListenerBuilder {
    private GPSListener gpsListener;

    public GPSListenerBuilder() {
        gpsListener = GPSListener.getInstance();
        gpsListener.setInit();
    }

    public GPSListenerBuilder setMap(GoogleMap map) {
        gpsListener.setMap(map);
        return this;
    }

    public GPSListenerBuilder setContext(Context context){
        gpsListener.setContext(context);
        return this;
    }

    public GPSListener build() {
        if(gpsListener.startLocationUpdate())
            return gpsListener;
        return null;
    }
}
