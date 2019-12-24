package com.guardiansofgalakddy.lvlmonitor.seungju;

import android.view.View;

import com.guardiansofgalakddy.lvlmonitor.seungju.RecyclerAdapter;

public interface OnItemClickListener {
    void onItemClick(RecyclerAdapter.ItemViewHolder holder, View view, int position);
}
