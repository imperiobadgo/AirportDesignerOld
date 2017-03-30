package com.pukekogames.airportdesigner.Activities;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import com.pukekogames.airportdesigner.GameInstance.GameInstance;
import com.pukekogames.airportdesigner.Helper.GameLogic.AirplaneServices;
import com.pukekogames.airportdesigner.Objects.ClickableGameObject;
import com.pukekogames.airportdesigner.Objects.Vehicles.Airplane;
import com.pukekogames.airportdesigner.R;
import com.pukekogames.airportdesigner.Rendering.BitmapLoader;

import java.util.ArrayList;

/**
 * Created by Marko Rapka on 02.10.2016.
 */
public class AirplaneBoardingActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ClickableGameObject selected = GameInstance.Settings().selectedObject;
        if (selected == null) {
            finish();
            return;
        }
        if (!(selected instanceof Airplane)) {
            finish();
            return;
        }
        Airplane airplane = (Airplane) selected;

        GameInstance.Airport().setPauseSimulation(true);
        System.gc();
        //turn title off
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        //set to full screen
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.airplane_boarding_info);
        ImageView imageView = (ImageView) findViewById(R.id.airplaneImage);

        imageView.setImageBitmap(BitmapLoader.Instance().getBitmap(airplane.getImageID()));

        TextView airlineName = (TextView) findViewById(R.id.airplaneName);
        airlineName.setText(airplane.getCallSign());


        ListView servicesListView = (ListView) findViewById(R.id.serviceListView);
        ArrayList<String> myStringArray = new ArrayList<String>();
        for (int i = 0; i < airplane.needsService().length; i++) {
            AirplaneServices service = airplane.needsService()[i];
            myStringArray.add(service.name());
        }


        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.list_stringrow, myStringArray);
        servicesListView.setAdapter(adapter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        GameInstance.Airport().setPauseSimulation(false);
    }
}
