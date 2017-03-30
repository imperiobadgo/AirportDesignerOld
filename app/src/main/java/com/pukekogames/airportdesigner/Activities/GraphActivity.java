package com.pukekogames.airportdesigner.Activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import com.pukekogames.airportdesigner.layout.GraphView;

/**
 * Created by Marko Rapka on 03.12.2016.
 */
public class GraphActivity extends Activity {

    GraphView graphView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        System.gc();
        //turn title off
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        //set to full screen
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);


        Intent intent = getIntent();
        float[] data = intent.getFloatArrayExtra("data");
        float[] price = intent.getFloatArrayExtra("price");
        String xAxis = intent.getStringExtra("xAxis");
        String yAxis = intent.getStringExtra("yAxis");
        if (data == null) {
            finish();
            return;
        }
        if (data.length == 0){
            data = new float[1];
        }
        if (xAxis == null){
            xAxis = "";
        }
        if (yAxis == null){
            yAxis = "";
        }
        graphView = new GraphView(this);
        graphView.setupGraph("", xAxis, 0, data.length, yAxis, 0, 1);
        graphView.setDataSource(data);
        if (price != null) {
            graphView.setPriceSource(price);
        }

        setContentView(graphView);

        getWindow().setLayout(700, WindowManager.LayoutParams.WRAP_CONTENT);
    }

}
