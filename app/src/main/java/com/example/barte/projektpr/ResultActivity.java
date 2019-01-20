package com.example.barte.projektpr;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class ResultActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        final TextView tvWynik = (TextView) findViewById(R.id.tvWynik);
        final TextView tvRownanie = (TextView) findViewById(R.id.tvRownanie);
        final Button bZamknij = (Button) findViewById(R.id.bZamknij);

        Intent getIntent = getIntent();
        final String wynik = getIntent.getStringExtra("wynik");
        java.math.BigDecimal res = Rownanie.rown(wynik);

        tvRownanie.setText(wynik + "=");
        tvWynik.setText(res.toString());

        bZamknij.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mainIntent = new Intent(ResultActivity.this, MainActivity.class);
                ResultActivity.this.startActivity(mainIntent);
            }
        });
    }
}
