package com.pukekogames.airportdesigner.Activities;

import android.app.Activity;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import com.pukekogames.airportdesigner.GameInstance.GameInstance;
import com.pukekogames.airportdesigner.Objects.Buildings.Depot;
import com.pukekogames.airportdesigner.Objects.ClickableGameObject;
import com.pukekogames.airportdesigner.R;
import com.pukekogames.airportdesigner.Rendering.BitmapLoader;

/**
 * Created by Marko Rapka on 26.09.2016.
 */
public class DepotActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ClickableGameObject selected = GameInstance.Settings().selectedObject;
        if (selected == null) {
            finish();
            return;
        }
        if (!(selected instanceof Depot)){
            finish();
            return;
        }
        Depot depot = (Depot) selected;

        GameInstance.Airport().setPauseSimulation(true);
        System.gc();
        //turn title off
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        //set to full screen
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.depot_info);
        ImageView imageView = (ImageView) findViewById(R.id.depotImage);
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;
        imageView.setImageBitmap(BitmapLoader.Instance().getBitmap(depot.getImageID()));


        TextView capacityView = (TextView) findViewById(R.id.capacity);
        String msgFormat = getString(R.string.DepotScreen_CapacityText);
        String msg = String.format(msgFormat, depot.getVehicleOnTheRoadCount(), depot.getCapacity());
        capacityView.setText(msg);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        GameInstance.Airport().setPauseSimulation(false);
    }
}
