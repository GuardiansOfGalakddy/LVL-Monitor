package com.guardiansofgalakddy.lvlmonitor.Onegold.Map;

import com.google.android.gms.maps.GoogleMap;

public class GPSListenerBuilder {
    private GPSListener gpsListener;

    private GPSListenerBuilder() {
        gpsListener = GPSListener.getInstance();
    }

    private static class GPSListenerBuilderHolder {
        private static final GPSListenerBuilder instance = new GPSListenerBuilder();
    }

    public static GPSListenerBuilder getInstance() {
        GPSListenerBuilder builder = GPSListenerBuilderHolder.instance;
        builder.getGpsListener().setInit();

        return builder;
    }

    public GPSListener getGpsListener() {
        return gpsListener;
    }

    public GPSListenerBuilder setMap(GoogleMap map) {
        gpsListener.setMap(map);
        return this;
    }
}
