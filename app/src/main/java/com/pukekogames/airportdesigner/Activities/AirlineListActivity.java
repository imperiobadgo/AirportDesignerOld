package com.pukekogames.airportdesigner.Activities;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import com.pukekogames.airportdesigner.GameInstance.GameInstance;
import com.pukekogames.airportdesigner.Objects.Airlines.Airline;
import com.pukekogames.airportdesigner.Objects.Airlines.PlannedArrival;
import com.pukekogames.airportdesigner.R;
import com.pukekogames.airportdesigner.UIManager;
import com.pukekogames.airportdesigner.layout.StringIntList;

import java.util.ArrayList;

/**
 * Created by Marko Rapka on 23.07.2016.
 */
public class AirlineListActivity extends Activity {

    private int selectedSlot = 0;
    private ListView airlineListView;
    private ArrayList<Airline> sortedAirlines;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        System.gc();
        //turn title off
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        //set to full screen
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.airline_selection);

        airlineListView = (ListView) findViewById(R.id.airlinelist);
        sortedAirlines = new ArrayList<>();


        for (int i = 0; i < GameInstance.AirlineManager().AirlinesCount(); i++) {
            Airline airline = GameInstance.AirlineManager().getAirline(i);

            if (sortedAirlines.size() == 0){
                sortedAirlines.add(airline);
                continue;
            }
            boolean added = false;
            for (int j = 0; j < sortedAirlines.size(); j++) {
                Airline currentAirline = sortedAirlines.get(j);
                int currentPlannedArrivalsCount = currentAirline.PlannedArivalsCount();
                int plannedArrivalsCount = airline.PlannedArivalsCount();
                if (plannedArrivalsCount >= currentPlannedArrivalsCount){
                    sortedAirlines.add(j, airline);
                    added = true;
                    break;
                }
            }
            if (!added){
                sortedAirlines.add(airline);
            }
        }



        ArrayList<String> myStringArray = new ArrayList<>();
        ArrayList<Integer> numberArray = new ArrayList<>();

        for (Airline airline:sortedAirlines){
            myStringArray.add(airline.getAirlineName());

            int count = 0;
            for (int i = 0; i < airline.PlannedArivalsCount(); i++) {
                PlannedArrival arrival = airline.getPlannedArival(i);
                if (!arrival.isAccepted()){
                    count++;
                }
            }

            numberArray.add(count);
        }

//        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.list_stringrow, myStringArray);
        StringIntList adapter = new StringIntList(this, myStringArray, numberArray);
        airlineListView.setAdapter(adapter);

        airlineListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                selectedSlot = position;
                GameInstance.AirlineManager().setSelectedAirline(sortedAirlines.get(position));
                showToast(sortedAirlines.get(position).getAirlineName());
                UIManager.UpdateScreens();
                finish();
            }
        });
    }

    void showToast(String msg) {
        Toast toast = Toast.makeText(AirlineListActivity.this, msg, Toast.LENGTH_SHORT);
        toast.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}
