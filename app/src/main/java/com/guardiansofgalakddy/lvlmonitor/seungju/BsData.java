package com.guardiansofgalakddy.lvlmonitor.seungju;

import android.bluetooth.BluetoothDevice;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BsData {
    private String title;
    private String content;
    private BluetoothDevice device;
}
