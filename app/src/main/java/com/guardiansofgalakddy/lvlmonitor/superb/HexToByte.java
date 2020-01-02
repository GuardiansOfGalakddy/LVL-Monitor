package com.guardiansofgalakddy.lvlmonitor.superb;

import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.guardiansofgalakddy.lvlmonitor.R;

public class HexToByte extends Dialog {
    String uuid = null;

    private Button button;
    private Button deleteBtn;
    private TextView txtSystemID, txtAID, txtSEV, txtALM, txtSTS, txtTIME = null;

    public HexToByte(@NonNull Context context) {
        super(context);
        setContentView(R.layout.dialog_uuid);

        button = findViewById(R.id.button3);
        deleteBtn = findViewById(R.id.button4);
    }

    public void initializeHexToByte(String uuid) {
        this.uuid = uuid;

        txtSystemID = findViewById(R.id.txt1);
        txtAID = findViewById(R.id.txt2);
        txtSEV = findViewById(R.id.txt3);
        txtALM = findViewById(R.id.txt4);
        txtSTS = findViewById(R.id.txt5);
        txtTIME = findViewById(R.id.txt6);
        showData();
    }

    public void showData() {
        txtSystemID.setText(uuid.substring(0, 5));

        String AID, SEV, ALM, STS = null;
        switch (uuid.charAt(5) - '0') {
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
        switch (uuid.charAt(6) - '0') {
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
        switch (uuid.charAt(7) - '0') {
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
        switch (uuid.charAt(8) - '0') {
            case 0:
                STS = "REL(0)_Alarm 해제";
                break;
            case 1:
                STS = "ALM(1)_Alarm 발생";
                break;
            default:
                STS = "STS_Undefined code";
                break;
        }
        txtAID.setText(AID);
        txtSEV.setText(SEV);
        txtALM.setText(ALM);
        txtSTS.setText(STS);

        StringBuilder builder = new StringBuilder();
        builder.append(uuid.substring(9, 11)).append("년")
                .append(uuid.substring(11, 13)).append("월")
                .append(uuid.substring(13, 15)).append("일")
                .append(uuid.substring(15, 17)).append("시")
                .append(uuid.substring(17, 19)).append("분")
                .append(uuid.substring(13, 15)).append("초");

        txtTIME.setText(builder.toString());
    }

    public static byte[] hexStringToByteArray(String s) {
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

        builder.append(bytes[4]).append(bytes[5]).append(bytes[6]).
                append(bytes[7]).append(bytes[8]).append(bytes[9]);

        for (int i = 10; i < 16; i++)
            builder.append(byteTo2DigitsString(bytes[i]));
        return builder.toString();
    }

    public static String byteTo2DigitsString(byte digit) {
        if (digit < 10)
            return "0" + digit;
        return digit + "";
    }

    public void setButtonOnClickListener(View.OnClickListener listener, Boolean bool) {
        this.button.setOnClickListener(listener);
        if (bool)
            button.setText("Update");
    }

    public void setDeleteButtonOnClickListener(View.OnClickListener listener) {
        this.deleteBtn.setOnClickListener(listener);
        deleteBtn.setVisibility(View.VISIBLE);
    }

    public String getSystemID() {
        return txtSystemID.getText().toString();
    }


}
