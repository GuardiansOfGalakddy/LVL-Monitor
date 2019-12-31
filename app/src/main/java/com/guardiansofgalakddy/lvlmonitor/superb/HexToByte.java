package com.guardiansofgalakddy.lvlmonitor.superb;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.guardiansofgalakddy.lvlmonitor.R;

public class HexToByte extends Dialog {
    private byte[] byteArray1;
    private byte[] byteArray2;

    private Button button;
    private TextView txtSystemID, txtAID, txtSEV, txtALM, txtSTS, txtTIME = null;

    public HexToByte(@NonNull Context context) {
        super(context);
        setContentView(R.layout.dialog_uuid);

        button = findViewById(R.id.button3);
    }

    public void initializeHexToByte (String str1, String str2){
        byteArray1 = hexStringToByteArray(str1);
        byteArray2 = hexStringToByteArray(str2);

        txtSystemID = findViewById(R.id.txt1);
        txtAID = findViewById(R.id.txt2);
        txtSEV = findViewById(R.id.txt3);
        txtALM = findViewById(R.id.txt4);
        txtSTS = findViewById(R.id.txt5);
        txtTIME = findViewById(R.id.txt6);
        showData();
    }

    public void showData() {
        if(byteArray1[1]=='R')
            txtSystemID.setText(""+"RS-"+""+byteArray1[4]+""+byteArray1[5]);
        else if(byteArray1[1]=='B')
            txtSystemID.setText("" + "BS-" + "" + byteArray1[4] + "" + byteArray1[5]);

        if(byteArray1[6]==1)
            txtAID.setText("CH1(0)_Rogowski coil channel 1");
        else if(byteArray1[6]==2)
            txtAID.setText("CH2(1)_Rogowski coil channel 2");
        else if(byteArray1[6]==3)
            txtAID.setText("CH3(2)_Rogowski coil channel 3");
        else if(byteArray1[6]==4)
            txtAID.setText("WL(3)_침수 센서");

        if(byteArray1[7]==1)
            txtSEV.setText("CR(0)_Critical Alarm");
        else if(byteArray1[7]==2)
            txtSEV.setText("MJ(1)_Major Alarm");
        else if(byteArray1[7]==3)
            txtSEV.setText("MN(2)_Minor Alarm");

        if(byteArray2[0]==1)
            txtALM.setText("REL(0)_Alarm 해제");
        else if(byteArray2[0]==2)
            txtALM.setText("ALM(1)_Alarm 발생");

        if (byteArray2[1]==1)
            txtSTS.setText("COM_FAIL(0)_TID(RS)");
        else if(byteArray2[1]==2)
            txtSTS.setText("ECUR_MAX_TH_OVERRUN(1)_VALUE/TH_VALUE");
        else if(byteArray2[1]==3)
            txtSTS.setText("ECUR_MIN_TH_UNDERRUN(2)_VALUE/TH_VALUE");

        txtTIME.setText(""+byteArray2[2]+"년"+byteArray2[3]+"월"+byteArray2[4]+"일"+byteArray2[5]+"시"+byteArray2[6]+"분"+byteArray2[7]+"초");
    }

    public static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i+=2) {
            data[i/2] = (byte) ((Character.digit(s.charAt(i), 16) << 4) + Character.digit(s.charAt(i+1), 16));
        }
        return data;
    }

    public void setButtonOnClickListener(View.OnClickListener listener) {
        this.button.setOnClickListener(listener);
    }

    public String getSystemID() {
        return txtSystemID.getText().toString();
    }
}
