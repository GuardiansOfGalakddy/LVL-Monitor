package com.guardiansofgalakddy.lvlmonitor.superb;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.NumberPicker;

import androidx.annotation.NonNull;

import com.guardiansofgalakddy.lvlmonitor.R;

public class MarkerDialog extends Dialog {
    private NumberPicker.OnValueChangeListener valueChangeListener;

    private Button saveBtn;
    private Button deleteBtn;
    private NumberPicker picker1;
    private NumberPicker picker2;
    private NumberPicker picker3;
    private NumberPicker picker4;
    private NumberPicker picker5;

    public MarkerDialog(@NonNull Context context) {
        super(context);
        setContentView(R.layout.dialog_marker);

        saveBtn = findViewById(R.id.saveBtn);
        deleteBtn = findViewById(R.id.deleteBtn);

        picker1 = findViewById(R.id.numberPicker1);
        picker1.setMinValue(0);
        picker1.setMaxValue(1);
        picker1.setDisplayedValues(new String[] {
                "감시장치", "수집기"});
        picker1.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker numberPicker, int oldVal, int newVal) {

            }
        });

        picker2 = findViewById(R.id.numberPicker2);
        picker2.setMinValue(0);
        picker2.setMaxValue(15);
        picker2.setDisplayedValues(new String[] {
                "0", "1" ,"2", "3", "4", "5", "6", "7", "8", "9", "A", "B", "C", "D", "E", "F"
        });

        picker3 = findViewById(R.id.numberPicker3);
        picker3.setMinValue(0);
        picker3.setMaxValue(15);
        picker3.setDisplayedValues(new String[] {
                "0", "1" ,"2", "3", "4", "5", "6", "7", "8", "9", "A", "B", "C", "D", "E", "F"
        });

        picker4 = findViewById(R.id.numberPicker4);
        picker4.setMinValue(0);
        picker4.setMaxValue(15);
        picker4.setDisplayedValues(new String[] {
                "0", "1" ,"2", "3", "4", "5", "6", "7", "8", "9", "A", "B", "C", "D", "E", "F"
        });

        picker5 = findViewById(R.id.numberPicker5);
        picker5.setMinValue(0);
        picker5.setMaxValue(15);
        picker5.setDisplayedValues(new String[] {
                "0", "1" ,"2", "3", "4", "5", "6", "7", "8", "9", "A", "B", "C", "D", "E", "F"
        });
    }

    public void setSaveButtonOnClickListener(View.OnClickListener listener) {
        this.saveBtn.setOnClickListener(listener);
    }


    public String getSystemID() {
        String hex1 = Integer.toHexString(picker2.getValue()).toUpperCase();
        String hex2 = Integer.toHexString(picker3.getValue()).toUpperCase();
        String hex3 = Integer.toHexString(picker4.getValue()).toUpperCase();
        String hex4 = Integer.toHexString(picker5.getValue()).toUpperCase();
        if (picker1.getValue()==0) {
            return "BS-" + hex1 + hex2 + hex3 + hex4;
        }
        else return "RS-" + hex1 + hex2 + hex3 + hex4;
    }
}
