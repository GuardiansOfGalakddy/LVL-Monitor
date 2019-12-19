package com.guardiansofgalakddy.lvlmonitor;

import android.app.Dialog;
import android.content.Context;
import android.widget.TextView;

import androidx.annotation.NonNull;

public class HexToByte extends Dialog {
    private byte[] byteArray1;
    private byte[] byteArray2;

    TextView txt1 = null;
    TextView txt2 = null;
    TextView txt3 = null;
    TextView txt4 = null;
    TextView txt5 = null;
    TextView txt6 = null;

    public HexToByte(@NonNull Context context) {
        super(context);
        setContentView(R.layout.uuid_dialog);
    }

    public void initializeHexToByte (String str1, String str2){
        byteArray1 = hexStringToByteArray(str1);
        byteArray2 = hexStringToByteArray(str2);

        txt1 = findViewById(R.id.txt1);
        txt2 = findViewById(R.id.txt2);
        txt3 = findViewById(R.id.txt3);
        txt4 = findViewById(R.id.txt4);
        txt5 = findViewById(R.id.txt5);
        txt6 = findViewById(R.id.txt6);
        showData();
    }

    public void showData() {
        if(byteArray1[1]=='R') {
            txt1.setText(""+"RS-"+""+byteArray1[4]+""+byteArray1[5]);
        }

        else if(byteArray1[1]=='B') {
            txt1.setText("" + "BS-" + "" + byteArray1[4] + "" + byteArray1[5]);
        }

        if(byteArray1[6]==1) {
            txt2.setText("CH1(0)_Rogowski coil channel 1");
        }
        else if(byteArray1[6]==2) {
            txt2.setText("CH2(1)_Rogowski coil channel 2");
        }
        else if(byteArray1[6]==3) {
            txt2.setText("CH3(2)_Rogowski coil channel 3");
        }
        else if(byteArray1[6]==4) {
            txt2.setText("WL(3)_침수 센서");
        }

        if(byteArray1[7]==1) {
            txt3.setText("CR(0)_Critical Alarm");
        }
        else if(byteArray1[7]==2) {
            txt3.setText("MJ(1)_Major Alarm");
        }
        else if(byteArray1[7]==3) {
            txt3.setText("MN(2)_Minor Alarm");
        }

        if(byteArray2[0]==1) {
            txt4.setText("REL(0)_Alarm 해제");
        }
        else if(byteArray2[0]==2) {
            txt4.setText("ALM(1)_Alarm 발생");
        }

        if (byteArray2[1]==1) {
            txt5.setText("COM_FAIL(0)_TID(RS)");
        }
        else if(byteArray2[1]==2) {
            txt5.setText("ECUR_MAX_TH_OVERRUN(1)_VALUE/TH_VALUE");
        }
        else if(byteArray2[1]==3) {
            txt5.setText("ECUR_MIN_TH_UNDERRUN(2)_VALUE/TH_VALUE");
        }

        txt6.setText(""+byteArray2[2]+"년"+byteArray2[3]+"월"+byteArray2[4]+"일"+byteArray2[5]+"시"+byteArray2[6]+"분"+byteArray2[7]+"초");
    }

    public static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i+=2) {
            data[i/2] = (byte) ((Character.digit(s.charAt(i), 16) << 4) + Character.digit(s.charAt(i+1), 16));
        }
        return data;
    }
}
