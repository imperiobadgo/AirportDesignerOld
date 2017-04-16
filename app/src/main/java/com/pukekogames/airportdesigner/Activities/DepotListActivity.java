package com.pukekogames.airportdesigner.Activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import com.pukekogames.airportdesigner.GameInstance.GameInstance;
import com.pukekogames.airportdesigner.Helper.ClassTranslation.BuildingType;
import com.pukekogames.airportdesigner.Objects.Prices;
import com.pukekogames.airportdesigner.R;
import com.pukekogames.airportdesigner.layout.CustomList;


/**
 * Created by Marko Rapka on 28.09.2016.
 */
public class DepotListActivity extends Activity {

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
                CustomList(DepotListActivity.this, text, imageId, priceList);
        list = (ListView) findViewById(R.id.list);
        list.setAdapter(adapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                setResult(textList[position]);
                String msgFormat = getString(R.string.SelectTextToast);
                String msg = String.format(msgFormat, textList[position]);
                Toast toast = Toast.makeText(DepotListActivity.this, msg, Toast.LENGTH_SHORT);
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
                case "BusDepot":
                    imageId[i] = R.drawable.depot_bus;
                    priceList[i] = (int) Prices.BuildBusDepot;
                    break;
                case "CateringDepot":
                    imageId[i] = R.drawable.depot_catering;
                    priceList[i] = (int) Prices.BuildCateringDepot;
                    break;
                case "CrewBusDepot":
                    imageId[i] = R.drawable.depot_crewbus;
                    priceList[i] = (int) Prices.BuildCrewbusDepot;
                    break;
                case "TankDepot":
                    imageId[i] = R.drawable.depot_tank;
                    priceList[i] = (int) Prices.BuildTankDepot;
                    break;
                case "BaggageDepot":
                    imageId[i] = R.drawable.depot_baggage;
                    priceList[i] = (int) Prices.BuildBaggageDepot;
                    break;
                case "Tower":
                    imageId[i] = R.drawable.tower;
                    priceList[i] = (int) Prices.BuildTower;
                    break;
                case "Terminal":
                    imageId[i] = R.drawable.terminal;
                    priceList[i] = (int) Prices.BuildTerminal;
                    break;
                default:
                    imageId[i] = R.drawable.depot_bus;
                    priceList[i] = 9999999;
                    break;
            }
        }
    }

    private void setResult(String result) {
        if (GameInstance.Settings().buildMode == 3) {
            BuildingType buildingType = null;
            long price = 0;
            switch (result) {
                case "BusDepot":
                    buildingType = BuildingType.busDepot;
                    price = Prices.BuildBusDepot;
                    break;
                case "CateringDepot":
                    buildingType = BuildingType.cateringDepot;
                    price = Prices.BuildCateringDepot;
                    break;
                case "CrewBusDepot":
                    buildingType = BuildingType.crewBusDepot;
                    price = Prices.BuildCrewbusDepot;
                    break;
                case "TankDepot":
                    buildingType = BuildingType.tankDepot;
                    price = Prices.BuildTankDepot;
                    break;
                case "BaggageDepot":
                    buildingType = BuildingType.baggageDepot;
                    price = Prices.BuildBaggageDepot;
                    break;
                case "Tower":
                    buildingType = BuildingType.tower;
                    price = Prices.BuildTower;
                    break;
                case "Terminal":
                    buildingType = BuildingType.terminal;
                    price = Prices.BuildTerminal;
                    break;
                default:
                    buildingType = BuildingType.busDepot;
                    price = 9999999L;
                    break;

            }

            GameInstance.Settings().buildDepot = buildingType;
            GameInstance.Settings().buildPrice = price;
            GameInstance.Settings().selectionCompleted = true;
        }
    }

}
