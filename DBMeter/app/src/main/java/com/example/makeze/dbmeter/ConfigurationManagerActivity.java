package com.example.makeze.dbmeter;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class ConfigurationManagerActivity extends AppCompatActivity {
    EditText df,uf;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuration_manager);
        df = (EditText) findViewById(R.id.down_freq);
        uf = (EditText) findViewById(R.id.up_freq);
        Button saveButton = (Button) findViewById(R.id.saveBtn);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveValues(v);
            }
        });
        Button defaultButton = (Button) findViewById(R.id.defaultBtn);
        defaultButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                defaultValues(v);
            }

        });
    }
    private void saveValues(View v){
        String input = df.getText().toString();
    }
    private void defaultValues(View v){

    }
}
