package com.guardiansofgalakddy.lvlmonitor.seungju;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RsData {
    private String title;
    private byte[] content;
    private int resId;

    public RsData(String title, byte[] content, int resId) {
        if (title == null)
            this.title = "null";
        else
            this.title = title;
        this.content = content;
        this.resId = resId;
    }
}
