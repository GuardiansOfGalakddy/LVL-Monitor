package com.guardiansofgalakddy.lvlmonitor.superb;

import android.app.Dialog;
import android.content.Context;
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
        setContentView(R.layout.dialog_history);
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
        String AID, SEV, ALM, VALUE1, VALUE2, TH1, TH2 = null;

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

        VALUE1 = "value1 = " + Integer.parseInt(strUUID.substring(8, 10), 16);
        VALUE2 = "value2 = " + Integer.parseInt(strUUID.substring(10, 12), 16);
        TH1 = "th1 = " + Integer.parseInt(strUUID.substring(12, 14), 16);
        TH2 = "th2 = " + Integer.parseInt(strUUID.substring(14, 16), 16);

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
}
