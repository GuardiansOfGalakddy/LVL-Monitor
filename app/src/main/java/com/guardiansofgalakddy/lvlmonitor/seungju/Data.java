package com.guardiansofgalakddy.lvlmonitor.seungju;

public class Data {
    private String title;
    private byte[] content;
    private int resId;

    public Data(String title, byte[] content, int resId) {
        this.title = title;
        this.content = content;
        this.resId = resId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public byte[] getContent() {
        return content;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }

    public int getResId() {
        return resId;
    }

    public void setResId(int resId) {
        this.resId = resId;
    }
}
