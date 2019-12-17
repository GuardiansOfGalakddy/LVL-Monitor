package com.guardiansofgalakddy.lvlmonitor.Onegold.adapter;

public class RecycleAdapter {
    private AdapterState state;

    public RecycleAdapter(){
        state = CollectorAdapter.getInstance();
    }

    public void setState(AdapterState state){
        this.state = state;
    }
    public AdapterState getAdapter(){
        return this.state;
    }
    public void changeMonitor(){ state.changeMonitor(this); }
    public void changeCollector(){
        state.changeCollector(this);
    }
}
