package com.guardiansofgalakddy.lvlmonitor.seungju;

public class DbData {
    private String title;
    private double content;
    private double resId;

    public DbData(String title, double content, double resId) {
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

    public double getContent() {
        return content;
    }

    public void setContent(double content) {
        this.content = content;
    }

    public double getResId() {
        return resId;
    }

    public void setResId(double resId) {
        this.resId = resId;
    }
}
