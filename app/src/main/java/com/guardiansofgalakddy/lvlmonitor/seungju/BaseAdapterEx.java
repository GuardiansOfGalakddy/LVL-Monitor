package com.guardiansofgalakddy.lvlmonitor.seungju;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.guardiansofgalakddy.lvlmonitor.R;

import java.util.ArrayList;

public class BaseAdapterEx extends BaseAdapter {
    Context             mContext        = null;
    ArrayList<dbData>  mData           = null;
    LayoutInflater      mLayoutInflater = null;

    public BaseAdapterEx(Context context, ArrayList<dbData> data )
    {
        mContext        = context;
        mData           = data;
        mLayoutInflater = LayoutInflater.from(mContext);
    }

    public int getCount()
    {
        return mData.size();
    }

    public long getItemId( int position )
    {
        return position;
    }
    public void add( int index, dbData addData )
    {
        mData.add( index, addData );
        notifyDataSetChanged();
    }

    public void delete( int index )
    {
        mData.remove( index );
        notifyDataSetChanged();
    }

    public void clear( )
    {
        mData.clear();
        notifyDataSetChanged();
    }



    public dbData getItem(int position)
    {
        return mData.get(position);
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View itemLayout = mLayoutInflater.inflate(R.layout.list_view_item_layout,null);

        TextView nameTv = (TextView) itemLayout.findViewById(R.id.name_text);
        TextView numberTv = (TextView)itemLayout.findViewById(R.id.number_text);
        TextView departmentTv = (TextView) itemLayout.findViewById(R.id.department_text);

        nameTv.setText(mData.get(position).getTitle());
        numberTv.setText(String.valueOf(mData.get(position).getResId()));
        departmentTv.setText(String.valueOf(mData.get(position).getContent()));

        return itemLayout;
    }




}
