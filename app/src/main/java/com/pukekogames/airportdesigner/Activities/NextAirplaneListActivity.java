package com.pukekogames.airportdesigner.Activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import com.pukekogames.airportdesigner.GameInstance.GameInstance;
import com.pukekogames.airportdesigner.Objects.Airlines.Airline;
import com.pukekogames.airportdesigner.Objects.Vehicles.Airplane;
import com.pukekogames.airportdesigner.R;
import com.pukekogames.airportdesigner.Settings;
import com.pukekogames.airportdesigner.layout.StringStringIntList;

import java.util.ArrayList;

/**
 * Created by Marko Rapka on 13.03.2017.
 */
public class NextAirplaneListActivity extends Activity {

    ListView list;

    Airplane[] nextAirplanes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        GameInstance.Airport().setPauseSimulation(true);

        setContentView(R.layout.list_view);

        ArrayList<Airplane> nextAirplanesList = GameInstance.Airport().getNextAirplanes();

        ArrayList<String> nextAirplanesName = new ArrayList<String>();
        ArrayList<String> nextAirplaneAirline = new ArrayList<>();
        ArrayList<Integer> hoursOfWaiting = new ArrayList<>();
        for (Airplane airplane: nextAirplanesList) {
            nextAirplanesName.add(airplane.getCallSign());
            Airline airline = airplane.getAirline();
            if (airline != null){
                nextAirplaneAirline.add(airline.getAirlineName());
            }else{
                nextAirplaneAirline.add(getString(R.string.Airplane_noAirlineText));
            }
            hoursOfWaiting.add(GameInstance.Instance().getMinuteDifferenceFromCurrentTime(airplane.getPlannedTime()));
        }

        nextAirplanes = nextAirplanesList.toArray(new Airplane[nextAirplanesList.size()]);

        StringStringIntList adapter = new StringStringIntList(this, nextAirplanesName,nextAirplaneAirline, hoursOfWaiting);

        list = (ListView) findViewById(R.id.list);
        list.setAdapter(adapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                setResult(nextAirplanes[position]);
                finish();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        GameInstance.Airport().setPauseSimulation(false);
    }

    private void setResult(Airplane selectedAirplane) {
        GameInstance.Settings().selectedObject = selectedAirplane;
        Intent intent = new Intent("com.pukekogames.airportdesigner.AIRPLANEINFOACTIVITY");
        intent.putExtra("clearSelectedObject", true);

        Settings.Instance().game.startActivity(intent);
    }
}
