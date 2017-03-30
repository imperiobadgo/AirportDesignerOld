package com.pukekogames.airportdesigner.Activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import com.pukekogames.airportdesigner.GameInstance.GameInstance;
import com.pukekogames.airportdesigner.Helper.CommonMethods;
import com.pukekogames.airportdesigner.Helper.GameSave;
import com.pukekogames.airportdesigner.R;
import com.pukekogames.airportdesigner.Settings;
import com.pukekogames.airportdesigner.layout.StringList;

import java.util.ArrayList;

/**
 * Created by Marko Rapka on 06.10.2016.
 */
public class LoadAndSaveActivity extends Activity {

    int selectedSlot = 0;
    ListView saveListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        GameInstance.Airport().setPauseSimulation(true);
        System.gc();
        //turn title off
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        //set to full screen
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.load_save);

        setCurrentSaveText();

        saveListView = (ListView) findViewById(R.id.saveListView);

        ArrayList<String> myStringArray = new ArrayList<String>();
        for (int i = 0; i < GameSave.SAVECOUNT; i++) {
            myStringArray.add("Save" + i);
        }

//        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.list_stringrow, myStringArray);
        StringList adapter = new StringList(this, myStringArray);
        saveListView.setAdapter(adapter);

        saveListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                selectedSlot = position;

                updateLayout();


//                String msgFormat = getString(R.string.LoadSaveScreen_SelectTextToast);
//                String msg = String.format(msgFormat, position);
//                airlineListView.setSelection(position);
//
//
//                showToast(msg);
            }
        });
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM,
                WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        final Context currentContext = this;
        Button loadButton = (Button) findViewById(R.id.button_Load);
        Button saveButton = (Button) findViewById(R.id.button_Save);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (GameSave.Instance().gameInstances[selectedSlot] == null) {
                    saveAirport();
                } else {
                    if (!GameSave.Instance().gameInstances[selectedSlot].equals(GameInstance.Instance())) {

                        //to prevent dialog showing multiple times
                        if (!Settings.Instance().isShowingDialog) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(currentContext);

                            builder.setPositiveButton(R.string.OverwriteAlertDialog_YesButton_Text, new DialogInterface.OnClickListener() {

                                public void onClick(DialogInterface dialog, int which) {
                                    saveAirport();
                                    Settings.Instance().isShowingDialog = false;
                                    dialog.dismiss();
                                }
                            });

                            builder.setNegativeButton(R.string.OverwriteAlertDialog_NoButton_Text, new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    Settings.Instance().isShowingDialog = false;
                                    // Do nothing
                                    dialog.dismiss();
                                }
                            });

                            Settings.Instance().isShowingDialog = true;
                            builder.create().show();

                        }
                    } else {
                        saveAirport();
                    }
                }

            }
        });
        loadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (GameSave.Instance().gameInstances[selectedSlot] == null) return;
                loadAirport();
                updateLayout();
            }
        });

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE );

    }

    private void updateLayout() {
        for (int i = 0; i < saveListView.getAdapter().getCount(); i++) {
            if (saveListView.getChildAt(i) == null){
                continue;
            }

            if (i == selectedSlot) {
                saveListView.getChildAt(i).setBackgroundColor(Color.CYAN);
            } else {
                if (GameSave.Instance().gameInstances[i] != null) {
                    if (GameSave.Instance().gameInstances[i].equals(GameInstance.Instance())) {
                        saveListView.getChildAt(i).setBackgroundColor(Color.YELLOW);
                    } else {
                        saveListView.getChildAt(i).setBackgroundColor(Color.TRANSPARENT);
                    }
                }else {
                    saveListView.getChildAt(i).setBackgroundColor(Color.TRANSPARENT);
                }
            }
        }
    }

    void saveAirport() {
        int currentSlot = selectedSlot;
        GameSave.Instance().currentSlot = currentSlot;
        setCurrentSaveText();
        GameSave.Instance().gameInstances[currentSlot] = GameInstance.Instance();

        String msgFormat = getString(R.string.LoadSaveScreen_SaveTextToast);
        String msg = String.format(msgFormat, currentSlot);
        showToast(msg);
    }

    void loadAirport() {
        int currentSlot = selectedSlot;
        GameSave.Instance().currentSlot = currentSlot;
        setCurrentSaveText();
        GameInstance.setGameInstance(GameSave.Instance().gameInstances[currentSlot]);
        CommonMethods.loadAllObjectReferences(GameInstance.Airport());
        String msgFormat = getString(R.string.LoadSaveScreen_LoadTextToast);
        String msg = String.format(msgFormat, currentSlot);
        showToast(msg);
    }

    void setCurrentSaveText() {
//        TextView currentSaveText = (TextView) findViewById(R.id.currentSaveText);
//        String msgFormat = getString(R.string.LoadSaveScreen_CurrentSaveText);
//        String msg = String.format(msgFormat, GameSave.Instance().currentSlot);
//        currentSaveText.setText(msg);
    }

    void showToast(String msg) {
        Toast toast = Toast.makeText(LoadAndSaveActivity.this, msg, Toast.LENGTH_SHORT);
        toast.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        GameInstance.Airport().setPauseSimulation(false);
    }

}
