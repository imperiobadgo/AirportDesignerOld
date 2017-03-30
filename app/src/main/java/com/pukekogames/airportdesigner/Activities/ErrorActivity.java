package com.pukekogames.airportdesigner.Activities;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.pukekogames.airportdesigner.Main.ExceptionHandler;
import com.pukekogames.airportdesigner.R;

/**
 * Created by Marko Rapka on 12.11.2016.
 */
public class ErrorActivity extends Activity {

    TextView error;
    TextView device;
    Button errorButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));
        setContentView(R.layout.activity_error);
        device = (TextView) findViewById(R.id.deviceTextView);
        error = (TextView) findViewById(R.id.errorTextView);

        device.setText(getIntent().getStringExtra("device"));
        error.setText(getIntent().getStringExtra("error"));

        errorButton = (Button) findViewById(R.id.errorButton);
        errorButton.setText("Exit Application");
        errorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onDestroy();
            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(android.os.Build.VERSION.SDK_INT >= 21)
        {
            finishAndRemoveTask();
        }
        else
        {
            finish();
        }
    }
}
