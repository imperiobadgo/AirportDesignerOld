package com.pukekogames.airportdesigner.Activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import com.pukekogames.airportdesigner.GameInstance.GameInstance;
import com.pukekogames.airportdesigner.Helper.ClassTranslation.RoadType;
import com.pukekogames.airportdesigner.Objects.Prices;
import com.pukekogames.airportdesigner.R;
import com.pukekogames.airportdesigner.layout.CustomList;

/**
 * Created by Marko Rapka on 13.07.2016.
 */
public class RoadListActivity extends Activity {

    ListView list;

    String[] textList = {
            "Google Plus",
            "Twitter",
            "Windows",
            "Bing",
            "Itunes",
            "Wordpress",
            "Drupal"
    };
    Integer[] imageId = {
            R.drawable.airplane_a320,
            R.drawable.bus,
            R.drawable.runway_middle,
            R.drawable.runway_end,
            R.drawable.street,
            R.drawable.taxiway,
            R.drawable.depot_bus
    };

    Integer[] priceList = {
            10,
            15,
            5,
            25,
            20,
            10,
            15
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        textList = intent.getStringArrayExtra("ClassNames");
        setImageArray();

        setListView(imageId, textList, priceList);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    private void setListView(Integer[] imageId, String[] text, Integer[] priceList) {
        textList = text;
        setContentView(R.layout.list_view);

        CustomList adapter = new
                CustomList(RoadListActivity.this, text, imageId, priceList);
        list = (ListView) findViewById(R.id.list);
        list.setAdapter(adapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                setResult(textList[position]);
                String msgFormat = getString(R.string.SelectTextToast);
                String msg = String.format(msgFormat, textList[position]);
                Toast toast = Toast.makeText(RoadListActivity.this, msg, Toast.LENGTH_SHORT);
                toast.show();
                finish();
            }
        });
    }

    private void setImageArray() {
        imageId = new Integer[textList.length];
        priceList = new Integer[textList.length];
        for (int i = 0; i < textList.length; i++) {
            switch (textList[i]) {
                case "Taxiway":
                    imageId[i] = R.drawable.taxiway;
                    priceList[i] = (int) Prices.TaxiwayCostPerHundred;
                    break;
                case "Runway":
                    imageId[i] = R.drawable.runway_end;
                    priceList[i] = (int) Prices.RunwayCostPerHundred ;
                    break;
                case "Street":
                    imageId[i] = R.drawable.street;
                    priceList[i] = (int) Prices.StreetCostPerHundred ;
                    break;
                case "ParkGate":
                    imageId[i] = R.drawable.parkgate;
                    priceList[i] = (int) Prices.ParkGateBuildPrice ;
                    break;

            }
        }
    }

    private void setResult(String result) {
        if (GameInstance.Settings().buildMode == 1) {
            RoadType roadType = null;
            long price = 0;
            switch (result) {
                case "Taxiway":
                    roadType = RoadType.taxiway;
                    price = Prices.TaxiwayCostPerHundred;
                    break;
                case "Runway":
                    roadType = RoadType.runway;
                    price = Prices.RunwayCostPerHundred;
                    break;
                case "Street":
                    roadType = RoadType.street;
                    price = Prices.StreetCostPerHundred;
                    break;
                case "ParkGate":
                    roadType = RoadType.parkGate;
                    price = Prices.ParkGateBuildPrice;
                    break;

            }
            if (roadType != null) {
                GameInstance.Settings().buildRoad = roadType;
                GameInstance.Settings().buildPrice = price;
                GameInstance.Settings().buildRoadSelected = true;
            }
        }
    }
}
