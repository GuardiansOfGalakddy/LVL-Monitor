package com.guardiansofgalakddy.lvlmonitor.Onegold.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.guardiansofgalakddy.lvlmonitor.R;

import java.util.ArrayList;

public class CollectorAdapter extends RecyclerView.Adapter<CollectorAdapter.ViewHolder> implements AdapterState {
    private static CollectorAdapter collector = null;
    ArrayList<Collector> items = new ArrayList<>();

    private CollectorAdapter(){}

    public static CollectorAdapter getInstance(){
        if(collector == null){
            collector = new CollectorAdapter();
        }
        return collector;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        //view
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            // find view id
        }
        public void setItem(Collector item){
            // set view
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView = inflater.inflate(R.layout.collector_item, parent, false);

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Collector item = items.get(position);
        holder.setItem(item);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }
    public void addItem(Collector item){
        items.add(item);
    }
    public void setItems(ArrayList<Collector> items){
        this.items = items;
    }
    public Collector getItem(int position){
        return items.get(position);
    }
    public void setItem(int position, Collector item){
        items.set(position, item);
    }

    @Override
    public void changeMonitor(RecycleAdapter adapter) {
        adapter.setState(MonitorAdapter.getInstance());
    }

    @Override
    public void changeCollector(RecycleAdapter adapter) { }
}
