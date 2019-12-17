package com.guardiansofgalakddy.lvlmonitor.Onegold.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.guardiansofgalakddy.lvlmonitor.R;

import java.util.ArrayList;

public class MonitorAdapter extends RecyclerView.Adapter<MonitorAdapter.ViewHolder> implements AdapterState {
    private static MonitorAdapter monitor = null;
    ArrayList<Monitor> items = new ArrayList<>();

    private MonitorAdapter(){}

    public static MonitorAdapter getInstance(){
        if(monitor == null){
            monitor = new MonitorAdapter();
        }
        return monitor;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        //view
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            // find view id
        }
        public void setItem(Monitor item){
            // set view
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView = inflater.inflate(R.layout.monitor_item, parent, false);

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Monitor item = items.get(position);
        holder.setItem(item);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }
    public void addItem(Monitor item){
        items.add(item);
    }
    public void setItems(ArrayList<Monitor> items){
        this.items = items;
    }
    public Monitor getItem(int position){
        return items.get(position);
    }
    public void setItem(int position, Monitor item){
        items.set(position, item);
    }

    @Override
    public void changeMonitor(RecycleAdapter adapter) {
    }

    @Override
    public void changeCollector(RecycleAdapter adapter) { adapter.setState(CollectorAdapter.getInstance());}
}
