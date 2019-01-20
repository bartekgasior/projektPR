package com.example.barte.projektpr;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import org.opencv.android.OpenCVLoader;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener{

    private static final String TAG = "MainActivity";
    String classifier;

    static {
        if(!OpenCVLoader.initDebug()){
            Log.d(TAG, "OpenCV not loaded");
        }else {
            Log.d(TAG, "OpenCV loaded");
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        final Button bKamera = (Button) findViewById(R.id.bKamera);
        final Spinner sClassifier = (Spinner) findViewById(R.id.sClassifier);
        sClassifier.setOnItemSelectedListener(this);

        final List<String> classifiers = new ArrayList<String>();
        classifiers.add("TESSERACT");
        classifiers.add("PERCEPTRON WIELOWARSTWOWY");

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, classifiers);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sClassifier.setAdapter(adapter);

        bKamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent kameraIntent = new Intent(MainActivity.this, KameraActivity.class);
                kameraIntent.putExtra("classifier", classifier);
                MainActivity.this.startActivity(kameraIntent);
            }
        });
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        classifier = parent.getItemAtPosition(position).toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
