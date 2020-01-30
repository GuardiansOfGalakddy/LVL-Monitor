package com.guardiansofgalakddy.lvlmonitor.superb;

import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.guardiansofgalakddy.lvlmonitor.R;
import com.guardiansofgalakddy.lvlmonitor.junhwa.Aes;

public class HistoryDialog extends Dialog {
    //String uuid = null;
    byte[] uuid = null;
    String strUUID = null;

    /*private Button button;
    private Button deleteBtn;*/
    private TextView txtNID, txtAID, txtSEV, txtVal1, txtVal2, txtTH1, txtTH2, txtALM, txtOCRTIME, txtRLSTIME = null;
    //private TextView txtSTS = null;

    public HistoryDialog(@NonNull Context context) {
        super(context);
        setContentView(R.layout.dialog_uuid);

        /*button = findViewById(R.id.button3);
        deleteBtn = findViewById(R.id.button4);*/
    }

    public void initializeHistory(byte[] uuid) {
        this.uuid = uuid;
        strUUID = Aes.byteArrayToHexString(uuid);
        txtNID = findViewById(R.id.txtNID);
        txtAID = findViewById(R.id.txtAID);
        txtSEV = findViewById(R.id.txtSEV);
        txtALM = findViewById(R.id.txtALM);
        txtVal1 = findViewById(R.id.txtVal1);
        txtVal2 = findViewById(R.id.txtVal2);
        txtTH1 = findViewById(R.id.txtTH1);
        txtTH2 = findViewById(R.id.txtTH2);
        //txtSTS = findViewById(R.id.txt5);
        txtOCRTIME = findViewById(R.id.txtOCRTIME);
        txtRLSTIME = findViewById(R.id.txtRLSTIME);
        //showData();
        showHistory();
    }

    public void showHistory() {
        String NID = null;
        String AID, SEV, ALM, VALUE1, VALUE2, TH1, TH2, OCR_TIME, RLS_TIME = null;

        NID = "BS-" + String.format("%02X", uuid[1]&0xff) + String.format("%02X", uuid[0]&0xff);

        switch (strUUID.substring(5, 6)) {
            case "0":
                AID = "CH1(0)_Rogowski coil channel 1";
                break;
            case "1":
                AID = "CH2(1)_Rogowski coil channel 2";
                break;
            case "2":
                AID = "CH3(2)_Rogowski coil channel 3";
                break;
            case "3":
                AID = "WL(3)_침수 센서";
                break;
            default:
                AID = "AID_Undefined code";
                break;
        }

        switch (strUUID.substring(6, 7)) {
            case "0":
                SEV = "CR(0)_Critical Alarm";
                break;
            case "1":
                SEV = "MJ(1)_Major Alarm";
                break;
            case "2":
                SEV = "MN(2)_Minor Alarm";
                break;
            default:
                SEV = "SEV_Undefined code";
                break;
        }

        switch (strUUID.substring(7, 8)) {
            case "0":
                ALM = "COM_FAIL(0)_TID(RS)";
                break;
            case "1":
                ALM = "ECUR_MAX_TH_OVERRUN(1)_VALUE/TH_VALUE";
                break;
            case "2":
                ALM = "ECUR_MIN_TH_UNDERRUN(2)_VALUE/TH_VALUE";
                break;
            default:
                ALM = "ALM_Undefined code";
                break;
        }

        VALUE1 = "value1 = " + Integer.parseInt(strUUID.substring(8, 10));
        VALUE2 = "value2 = " + Integer.parseInt(strUUID.substring(10, 12));
        TH1 = "th1 = " + Integer.parseInt(strUUID.substring(12, 14));
        TH2 = "th2 = " + Integer.parseInt(strUUID.substring(14, 16));

        txtNID.setText(NID);
        txtAID.setText(AID);
        txtSEV.setText(SEV);
        txtALM.setText(ALM);
        txtVal1.setText(VALUE1);
        txtVal2.setText(VALUE2);
        txtTH1.setText(TH1);
        txtTH2.setText(TH2);

        StringBuilder ocrTimeBuilder = new StringBuilder();
        ocrTimeBuilder.append(strUUID.substring(16, 18)).append("년")
                .append(strUUID.substring(18, 20)).append("월")
                .append(strUUID.substring(20, 22)).append("일")
                .append(strUUID.substring(22, 24)).append("시")
                .append(strUUID.substring(24, 26)).append("분")
                .append(strUUID.substring(26, 28)).append("초");

        txtOCRTIME.setText(ocrTimeBuilder.toString());

        StringBuilder rlsTimeBuilder = new StringBuilder();

        rlsTimeBuilder.append(strUUID.substring(32, 34)).append("년")
                .append(strUUID.substring(34, 36)).append("월")
                .append(strUUID.substring(36, 38)).append("일")
                .append(strUUID.substring(38, 40)).append("시")
                .append(strUUID.substring(40, 42)).append("분")
                .append(strUUID.substring(42, 44)).append("초");
        txtRLSTIME.setText(rlsTimeBuilder.toString());
    }

    /*public void showData() {
        txtNID.setText(strUUID.substring(0, 7));
        if (strUUID.substring(0, 2).equals("BS")) {
            txtSEV.setVisibility(View.GONE);
            txtAID.setVisibility(View.GONE);
            txtALM.setVisibility(View.GONE);
            //txtSTS.setVisibility(View.GONE);
        }
        String AID, SEV, ALM, STS = null;
        switch (strUUID.charAt(7) - '0') {
            case 0:
                AID = "CH1(0)_Rogowski coil channel 1";
                break;
            case 1:
                AID = "CH2(1)_Rogowski coil channel 2";
                break;
            case 2:
                AID = "CH3(2)_Rogowski coil channel 3";
                break;
            case 3:
                AID = "WL(3)_침수 센서";
                break;
            default:
                AID = "AID_Undefined code";
                break;
        }
        switch (uuid.charAt(8) - '0') {
            case 0:
                SEV = "CR(0)_Critical Alarm";
                break;
            case 1:
                SEV = "MJ(1)_Major Alarm";
                break;
            case 2:
                SEV = "MN(2)_Minor Alarm";
                break;
            default:
                SEV = "SEV_Undefined code";
                break;
        }
        switch (uuid.charAt(9) - '0') {
            case 0:
                ALM = "COM_FAIL(0)_TID(RS)";
                break;
            case 1:
                ALM = "ECUR_MAX_TH_OVERRUN(1)_VALUE/TH_VALUE";
                break;
            case 2:
                ALM = "ECUR_MIN_TH_UNDERRUN(2)_VALUE/TH_VALUE";
                break;
            default:
                ALM = "ALM_Undefined code";
                break;
        }
        *//*switch (uuid.charAt(10) - '0') {
            case 0:
                STS = "REL(0)_Alarm 해제";
                txtSEV.setVisibility(View.GONE);
                txtAID.setVisibility(View.GONE);
                txtALM.setVisibility(View.GONE);
                //txtTIME.setVisibility(View.GONE);
                break;
            case 1:
                STS = "ALM(1)_Alarm 발생";
                break;
            default:
                STS = "STS_Undefined code";
                break;
        }*//*
        txtAID.setText(AID);
        txtSEV.setText(SEV);
        txtALM.setText(ALM);
        //txtSTS.setText(STS);

        StringBuilder builder = new StringBuilder();
        builder.append(uuid.substring(11, 13)).append("년")
                .append(uuid.substring(13, 15)).append("월")
                .append(uuid.substring(15, 17)).append("일")
                .append(uuid.substring(17, 19)).append("시")
                .append(uuid.substring(19, 21)).append("분")
                .append(uuid.substring(21)).append("초");

        txtOCRTIME.setText(builder.toString());
    }*/

    /*public static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4) + Character.digit(s.charAt(i + 1), 16));
        }
        return data;
    }

    public static String byteArrayToString(byte[] bytes) {
        StringBuilder builder = new StringBuilder();
        if (bytes[1] == 'R')
            builder.append("RS-");
        else
            builder.append("BS-");

        if (bytes[4] < 10)
            builder.append(String.format("%02X", bytes[4]&0xff));

        if (bytes[5] < 10)
            builder.append(String.format("%02X", bytes[5]&0xff));

        builder.append(bytes[6]).append(bytes[7])
                .append(bytes[8]).append(bytes[9]);

        for (int i = 10; i < 16; i++)
            builder.append(byteTo2DigitsString(bytes[i]));
        return builder.toString();
    }

    public static String byteTo2DigitsString(byte digit) {
        if (digit < 10)
            return "0" + digit;
        return digit + "";
    }*/

    /*public void setButtonOnClickListener(View.OnClickListener listener, Boolean bool) {
        this.button.setOnClickListener(listener);
        if (bool)
            button.setText("Update");
    }

    public void setDeleteButtonOnClickListener(View.OnClickListener listener) {
        this.deleteBtn.setOnClickListener(listener);
        deleteBtn.setVisibility(View.VISIBLE);
    }*/

    /*public String getSystemID() {
        return txtNID.getText().toString();
    }*/


}
