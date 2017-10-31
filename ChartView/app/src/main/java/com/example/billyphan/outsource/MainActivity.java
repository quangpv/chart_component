package com.example.billyphan.outsource;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import com.example.billyphan.chart_components.views.HorizontalProgressBar;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final HorizontalProgressBar progressBar = (HorizontalProgressBar) findViewById(R.id.pro);
        EditText edit = (EditText) findViewById(R.id.editPro);
        edit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                int num = 0;
                try {
                    num = Integer.parseInt(charSequence.toString());
                } catch (Exception e) {

                }
                progressBar.setProgress(num);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }
}
