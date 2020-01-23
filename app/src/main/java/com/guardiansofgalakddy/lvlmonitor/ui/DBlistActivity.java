package com.guardiansofgalakddy.lvlmonitor.ui;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.guardiansofgalakddy.lvlmonitor.R;
import com.guardiansofgalakddy.lvlmonitor.seungju.BaseAdapterEx;
import com.guardiansofgalakddy.lvlmonitor.seungju.LVLDBManager;
import com.guardiansofgalakddy.lvlmonitor.seungju.dbData;

import java.util.ArrayList;

public class DBlistActivity extends AppCompatActivity {

    ListView mListView = null;
    BaseAdapterEx mAdapter = null;
    ArrayList<dbData> mData = null; // mData 는 임의로 넣어준 데이터 원래는 db값을 저장해야됨
    public LVLDBManager mDbManager = null;
    Context Thiscontext = this;
    int po=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dblist);

        mDbManager = LVLDBManager.getInstance(this);

        mData = new ArrayList<dbData>();


        for(int i=0;i<mDbManager.count();i++)
        {
            dbData data = new dbData("Title"+String.valueOf(i),i,i+100);
            mData.add(data);
        }




        mAdapter = new BaseAdapterEx(this,mData); //어댑터 생성하고 데이터설정

        //리스트뷰에 어댑터 설정
        mListView = (ListView) findViewById(R.id.list_view);
        mListView.setAdapter(mAdapter);

        String[] columns = new String[]{"systemid","latitude","longitude"};
        Cursor c = mDbManager.query(columns,null,null,null,null,null);

        if(c !=null)
        {
            while(c.moveToNext())
            {
                String systemid = c.getString(0);
                double latitude = c.getDouble(1);
                double longtude = c.getDouble(2);

                dbData data = new dbData(systemid,latitude,longtude);
                mData.add(data);

                Log.e("dbdb",systemid + "  "+String.valueOf(latitude)+"  "+String.valueOf(longtude));
            }
            c.close();
        }




        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                po = position;
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(Thiscontext);
                alertDialogBuilder.setTitle("DBdataitem 삭제");
                alertDialogBuilder
                        .setMessage("삭제 하시겠습니까?")
                        .setCancelable(false)
                        .setPositiveButton("취소",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                    }

                                })

                        .setNegativeButton("삭제", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {

                                        mAdapter.delete(po);

                                        mDbManager.deleteAll();

                                        mDbManager = LVLDBManager.getInstance(Thiscontext);
                                        //Log.e("dbdb",String.valueOf(deleteRecordCnt));//디비 다 지워진거 확인
                                        for(int i=0;i<mData.size();i++)
                                        {

                                            ContentValues addRowValue = new ContentValues();
                                            addRowValue.put("systemid",mData.get(i).getTitle());
                                            addRowValue.put("latitude",mData.get(i).getContent());
                                            addRowValue.put("longitude",mData.get(i).getResId());
                                            long insertRecordId = mDbManager.insert(addRowValue);

                                        }

                                        String[] columns = new String[]{"systemid","latitude","longitude"};
                                        Cursor c = mDbManager.query(columns,null,null,null,null,null);

                                        if(c !=null)
                                        {
                                            while(c.moveToNext())
                                            {
                                                String systemid = c.getString(0);
                                                double latitude = c.getDouble(1);
                                                double longtude = c.getDouble(2);

                                                Log.e("dbdb",systemid + "  "+String.valueOf(latitude)+"  "+String.valueOf(longtude));
                                            }
                                            c.close();
                                        }
                                    }
                                });

                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();

                /*
                mAdapter.delete(position);

                mDbManager.deleteAll(); //데이블 자체를 drop

                mDbManager = LVLDBManager.getInstance(Thiscontext);
                //Log.e("dbdb",String.valueOf(deleteRecordCnt));//디비 다 지워진거 확인
                for(int i=0;i<mData.size();i++)
                {

                    ContentValues addRowValue = new ContentValues();
                    addRowValue.put("systemid",mData.get(i).getTitle());
                    addRowValue.put("latitude",mData.get(i).getContent());
                    addRowValue.put("longitude",mData.get(i).getResId());
                    long insertRecordId = mDbManager.insert(addRowValue);

                }

                String[] columns = new String[]{"systemid","latitude","longitude"};
                Cursor c = mDbManager.query(columns,null,null,null,null,null);

                if(c !=null)
                {
                    while(c.moveToNext())
                    {
                        String systemid = c.getString(0);
                        double latitude = c.getDouble(1);
                        double longtude = c.getDouble(2);

                        Log.e("dbdb",systemid + "  "+String.valueOf(latitude)+"  "+String.valueOf(longtude));
                    }
                    c.close();
                }

                 */
            }

        });

    }
}
