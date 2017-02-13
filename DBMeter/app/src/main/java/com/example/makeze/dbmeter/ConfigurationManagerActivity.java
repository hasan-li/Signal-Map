package com.example.makeze.dbmeter;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class ConfigurationManagerActivity extends AppCompatActivity {
    EditText df,uf; // dowload, upload frequencies
    int [] freq;
    Intent i;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuration_manager);
        df = (EditText) findViewById(R.id.down_freq);
        uf = (EditText) findViewById(R.id.up_freq);
        i = new Intent(this, mainMapActivity.class);
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
        freq = new int[2];
        freq[0] = Integer.valueOf(df.getText().toString());
        freq[1] = Integer.valueOf(uf.getText().toString());
        i.putExtra("save", freq);
        startActivity(i);
    }
    private void defaultValues(View v){
        i.putExtra("default", -1);
        startActivity(i);
    }
}
