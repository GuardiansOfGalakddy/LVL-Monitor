package com.guardiansofgalakddy.lvlmonitor.seungju;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.guardiansofgalakddy.lvlmonitor.R;

import java.util.ArrayList;

public class BsItemAdapter extends RecyclerView.Adapter<BsItemAdapter.ItemViewHolder> implements OnBSItemClickListener{
    private ArrayList<BsData> listData = new ArrayList<>();
    private OnBSItemClickListener listener = null;


    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // LayoutInflater를 이용하여 전 단계에서 만들었던 item.xml을 inflate 시킵니다.
        // return 인자는 ViewHolder 입니다.
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item3, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        // Item을 하나, 하나 보여주는(bind 되는) 함수입니다.
        holder.onBind(listData.get(position));
    }

    @Override
    public int getItemCount() {
        // RecyclerView의 총 개수 입니다.
        return listData.size();
    }

    public BsData getBsData(int position) {
        return listData.get(position);
    }

    private boolean isAlreadyExist(BsData data) {
        for (BsData d : listData)
            if (d.getContent().equals(data.getContent()))
                return true;
        return false;
    }

    public void addItem(BsData data) {
        if (isAlreadyExist(data))
            return;

        // 외부에서 item을 추가시킬 함수입니다.
        listData.add(data);
        this.notifyDataSetChanged();
    }

    // RecyclerView의 핵심인 ViewHolder 입니다.
    // 여기서 subView를 setting 해줍니다.
    public class ItemViewHolder extends RecyclerView.ViewHolder {

        private TextView textView1;
        private TextView textView2;

        ItemViewHolder(View itemView) {
            super(itemView);

            textView1 = itemView.findViewById(R.id.textView1);
            textView2 = itemView.findViewById(R.id.textView2);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    if (listener != null)
                        listener.onItemClick(BsItemAdapter.ItemViewHolder.this, view, position);
                }
            });
        }

        void onBind(BsData data) {
            textView1.setText(data.getTitle());
            textView2.setText(data.getContent());
        }
    }

    public void setOnItemClickListener(OnBSItemClickListener listener) {
        this.listener = listener;
    }

    @Override
    public void onItemClick(ItemViewHolder holder, View view, int position) {
        if (listener != null)
            listener.onItemClick(holder, view, position);
    }

    public void clearData() {
        this.listData.clear();
        this.notifyDataSetChanged();
    }
}