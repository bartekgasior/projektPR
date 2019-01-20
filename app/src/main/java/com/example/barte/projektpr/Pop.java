package com.example.barte.projektpr;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;


public class Pop extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.popwindow);

        final Button bZapisz = (Button) findViewById(R.id.bZapisz);
        final TextView tvZnak = (TextView) findViewById(R.id.tvZnak);
        final ImageView ivSymbol = (ImageView) findViewById(R.id.ivSymbol);
        /*przyciski*/
        final Button b0 = (Button) findViewById(R.id.b0);
        final Button b1 = (Button) findViewById(R.id.b1);
        final Button b2 = (Button) findViewById(R.id.b2);
        final Button b3 = (Button) findViewById(R.id.b3);
        final Button b4 = (Button) findViewById(R.id.b4);
        final Button b5 = (Button) findViewById(R.id.b5);
        final Button b6 = (Button) findViewById(R.id.b6);
        final Button b7 = (Button) findViewById(R.id.b7);
        final Button b8 = (Button) findViewById(R.id.b8);
        final Button b9 = (Button) findViewById(R.id.b9);
        final Button bPlus = (Button) findViewById(R.id.bPlus);
        final Button bMinus = (Button) findViewById(R.id.bMinus);
        final Button bMulti = (Button) findViewById(R.id.bMulti);
        final Button bDivide = (Button) findViewById(R.id.bDivide);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        Intent getIntent = getIntent();
        /*pozycja wypisywanego znaku*/
        final int x = getIntent.getIntExtra("x", -1);
        final int y = getIntent.getIntExtra("y", -1);
        final int rectangleNumber = getIntent.getIntExtra("rectangleNumber", -1);
        final Bitmap croppedSymbol = (Bitmap) getIntent.getParcelableExtra("croppedSymbol");

        ivSymbol.setImageBitmap(croppedSymbol);

        int width = dm.widthPixels;
        int height = dm.heightPixels;

        getWindow().setLayout((int) (width * .8), (int) (height * .9));

        b0.setOnClickListener((new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvZnak.setText("0");
            }
        }));

        b1.setOnClickListener((new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvZnak.setText("1");
            }
        }));

        b2.setOnClickListener((new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvZnak.setText("2");
            }
        }));

        b3.setOnClickListener((new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvZnak.setText("3");
            }
        }));

        b4.setOnClickListener((new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvZnak.setText("4");
            }
        }));

        b5.setOnClickListener((new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvZnak.setText("5");
            }
        }));

        b6.setOnClickListener((new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvZnak.setText("6");
            }
        }));

        b7.setOnClickListener((new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvZnak.setText("7");
            }
        }));

        b8.setOnClickListener((new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvZnak.setText("8");
            }
        }));

        b9.setOnClickListener((new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvZnak.setText("9");
            }
        }));

        bPlus.setOnClickListener((new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvZnak.setText("+");
            }
        }));

        bMinus.setOnClickListener((new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvZnak.setText("-");
            }
        }));

        bMulti.setOnClickListener((new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvZnak.setText("*");
            }
        }));

        bDivide.setOnClickListener((new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvZnak.setText("/");
            }
        }));

        bZapisz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean flag = false;
                String znak = tvZnak.getText().toString();
                Intent intent = new Intent();
                intent.putExtra("flag", flag);
                intent.putExtra("znak", znak);
                intent.putExtra("x", x);
                intent.putExtra("y", y);
                intent.putExtra("rectangleNumber", rectangleNumber);
                setResult(Activity.RESULT_OK, intent);
                finish();
            }
        });
    }
}
