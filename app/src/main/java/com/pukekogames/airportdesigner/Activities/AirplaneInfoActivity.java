package com.pukekogames.airportdesigner.Activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import com.pukekogames.airportdesigner.GameInstance.GameInstance;
import com.pukekogames.airportdesigner.Objects.Airlines.Airline;
import com.pukekogames.airportdesigner.Objects.ClickableGameObject;
import com.pukekogames.airportdesigner.Objects.Vehicles.Airplane;
import com.pukekogames.airportdesigner.R;
import com.pukekogames.airportdesigner.Rendering.BitmapLoader;

/**
 * Created by Marko Rapka on 15.03.2017.
 */
public class AirplaneInfoActivity extends Activity {

    private boolean clearSelectedObject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ClickableGameObject selected = GameInstance.Settings().selectedObject;
        if (selected == null) {
            finish();
            return;
        }
        if (!(selected instanceof Airplane)){
            finish();
            return;
        }
        Airplane airplane = (Airplane) selected;

        Intent intent = getIntent();
        clearSelectedObject = intent.getBooleanExtra("clearSelectedObject", false);


        GameInstance.Airport().setPauseSimulation(true);
        System.gc();
        //turn title off
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        //set to full screen
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.airplane_info);
        ImageView imageView = (ImageView) findViewById(R.id.airplaneImage);

        imageView.setImageBitmap(BitmapLoader.Instance().getBitmap(airplane.getImageID()));

        TextView airplaneName = (TextView) findViewById(R.id.airplaneName);
        airplaneName.setText(airplane.getCallSign());

        TextView airlineName = (TextView) findViewById(R.id.airlineName);
        Airline airline = airplane.getAirline();
        if (airline != null){
            airlineName.setText(airline.getAirlineName());
        }else{
            airlineName.setText(getString(R.string.Airplane_noAirlineText));
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (clearSelectedObject){
            GameInstance.Settings().selectedObject = null;
        }
        GameInstance.Airport().setPauseSimulation(false);
    }
}
