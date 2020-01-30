package com.guardiansofgalakddy.lvlmonitor.junhwa;

import android.content.Context;

public class BLEScannerBuilder {
    public static BLEScanner bleScanner= null;

    public static BLEScanner getInstance(Context context) {
        if (bleScanner != null)
            return bleScanner;
        bleScanner = new BLEScanner();
        if (bleScanner.initialize(context))
            return bleScanner;
        return null;
    }
}
