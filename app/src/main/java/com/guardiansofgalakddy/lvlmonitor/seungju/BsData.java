package com.guardiansofgalakddy.lvlmonitor.seungju;

import android.bluetooth.BluetoothDevice;

public class BsData {

    private String title;
    private String content;
    private BluetoothDevice device;

    public BsData(String title, String content, BluetoothDevice device) {
        this.title = title;
        this.content = content;
        this.device = device;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public BluetoothDevice getDevice() {
        return device;
    }

    public void setDevice(BluetoothDevice device) {
        this.device = device;
    }
}
