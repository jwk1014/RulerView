package com.devwon.rulerexample;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.jwk.rulerlibrary.RulerView;

public class MainActivity extends AppCompatActivity {
    RulerView rulerView;
    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rulerView = (RulerView)findViewById(R.id.a_main_rulerView);
        textView = (TextView)findViewById(R.id.a_main_textview);
        Button button = (Button)findViewById(R.id.a_main_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                textView.setText(rulerView.toString());
            }
        });
    }
}
