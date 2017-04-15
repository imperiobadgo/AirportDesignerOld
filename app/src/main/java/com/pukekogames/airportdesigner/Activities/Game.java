package com.pukekogames.airportdesigner.Activities;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ConfigurationInfo;
import android.opengl.GLSurfaceView;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.*;
import android.widget.Button;
import android.widget.Toast;
import com.pukekogames.airportdesigner.GameInstance.GameInstance;
import com.pukekogames.airportdesigner.Helper.CommonMethods;
import com.pukekogames.airportdesigner.Helper.GameSave;
import com.pukekogames.airportdesigner.*;
import com.pukekogames.airportdesigner.Main.ExceptionHandler;
import com.pukekogames.airportdesigner.Main.GamePanel;
import com.pukekogames.airportdesigner.Main.MainThread;
import com.pukekogames.airportdesigner.OpenGL.OpenGLRenderer;
import com.pukekogames.airportdesigner.Rendering.BitmapLoader;

import java.io.*;

public class Game extends Activity {
    public static final String TAG = "AirLog";
    private static final String SAVE_EXTENSION = ".ADSave";
    private static final String SAVE_BACKUP_EXTENSION = ".ADBackupSave";
    private GamePanel gamePanel;
    private MainThread thread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));
        System.gc();

        //turn title off
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        //set to full screen
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        Settings.Instance().game = this;

        String nextLayout = "";
        Intent intent = getIntent();
        if (intent.getStringExtra("Layout") != null) {
            nextLayout = intent.getStringExtra("Layout");
        }
        if (nextLayout.equals("game")) {
            CommonMethods.loadAllObjectReferences(GameInstance.Airport());
            setGame();
        } else {
            setMainMenu();

        }
    }


    @Override
    protected void onStop() {
        super.onStop();


//        save();
    }

    private void loadGame() {
        setContentView(R.layout.activity_loading);
        Loading loading = new Loading();
        loading.execute(null, null);
    }

    public void setGame() {
        Handler handler = new Handler(this);


//        GraphView graphView = new GraphView(this);
//
//        ArrayList<float[]> dataList = new ArrayList<>();
//        Random r = new Random(4);
//        for (int listIndex = 0; listIndex < 5; listIndex++) {
//            float[] data = new float[128];
//            System.out.println();
//            for (int i = 0; i < data.length; i++) {
//                int index = (data.length - 1) - i;
//                data[index] = (float) i/ 200f + (listIndex) / 20.0f - 0.02f;
////                data[index] = (float) (Math.sin((i + r.nextInt(127)) / 5f) + 1 )/ 2f;
//                System.out.print(data[index] + " ");
//            }
//            System.out.println();
//            dataList.add(data);
//        }
//
//        graphView.setDataSource(dataList);
//
//        setContentView(graphView);

        GLSurfaceView glView = new GLSurfaceView(this);

        final ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        final ConfigurationInfo configurationInfo = activityManager.getDeviceConfigurationInfo();
        final boolean supportsEs2 = configurationInfo.reqGlEsVersion >= 0x20000;
        if (false){

            glView.setEGLContextClientVersion(2);
            glView.setEGLConfigChooser(8,8,8,8,16,0);
            glView.setRenderer(new OpenGLRenderer(this));
            setContentView(glView);

        }else{

            gamePanel = new GamePanel(this, handler);
            setContentView(gamePanel);
        }



    }

    public void setDepotScreen() {
        Intent depotIntent = new Intent("com.pukekogames.airportdesigner.DEPOTACTIVITY");
        startActivity(depotIntent);
    }

    public void setAirplaneBoardingScreen() {
        Intent depotIntent = new Intent("com.pukekogames.airportdesigner.AIRPLANEBOARDINGACTIVITY");
        startActivity(depotIntent);
    }


    public void setAirplaneInfoScreen() {
        Intent depotIntent = new Intent("com.pukekogames.airportdesigner.AIRPLANEINFOACTIVITY");
        startActivity(depotIntent);
    }

    public void setLoadSaveScreen() {
        Intent saveIntent = new Intent("com.pukekogames.airportdesigner.LOADSAVEACTIVITY");
        startActivity(saveIntent);
    }

    public void setMainMenu() {
        setContentView(R.layout.activity_main_menu);

        Button button = (Button) findViewById(R.id.button_NewAirport);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GameInstance.Settings().gameType = 0;
                loadGame();
            }
        });
        button = (Button) findViewById(R.id.button_LoadAirport);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setContentView(R.layout.activity_loading);
                LoadAirport loading = new LoadAirport();
                loading.execute(null, null);

            }
        });

        button = (Button) findViewById(R.id.button_DebugAirport);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GameInstance.Settings().gameType = 1;
                loadGame();

            }
        });
        Log.i(Game.TAG, "Set contentView");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_game, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    void loadSaves() {

        boolean loadSuccessful = false;

        try {

            loadWithExtension(SAVE_EXTENSION);

            loadSuccessful = true;
            Log.i(Game.TAG, "Loading completed.");
        } catch (Exception e) {
            loadSuccessful = false;
            Log.e(Game.TAG, "error: ", e);
        }

        if (!loadSuccessful) {
            Log.e(Game.TAG, "Loading failed. Trying to load backup...");
            try {
                loadWithExtension(SAVE_BACKUP_EXTENSION);

                File directory = this.getFilesDir();
                File[] files = directory.listFiles();
                for (File file : files) {
                    String fileName = file.getName();
                    String[] splittedFileName = fileName.split("\\.");//escape dot as wildcard
                    if (splittedFileName.length != 2) {
                        continue;
                    }
                    if (SAVE_EXTENSION.equals("." + splittedFileName[1])) {
                        file.delete();//delete corrupted files
                    }

                }

            } catch (Exception e) {
                Log.e(Game.TAG, "error: ", e);
            }
        }

    }

    private void loadWithExtension(String extension) throws IOException, ClassNotFoundException {
        //first load just gamesave
        FileInputStream fis = openFileInput("game" + extension);
        ObjectInputStream is = new ObjectInputStream(fis);
        GameSave save = (GameSave) is.readObject();
        GameSave.setGameSave(save);
        GameSave.Instance().gameInstances = new GameInstance[GameSave.SAVECOUNT];
        is.close();
        fis.close();

        for (int i = 0; i < GameSave.SAVECOUNT; i++) {
            //than load every airport

            fis = openFileInput("airport" + i + extension);
            is = new ObjectInputStream(fis);
            Log.i(Game.TAG, "Loading instance number " + i + " with " + extension);
            GameInstance instance = (GameInstance) is.readObject();
            if (instance != null) {
                GameSave.Instance().gameInstances[i] = instance;
            } else {
                GameSave.Instance().gameInstances[i] = null;
            }

            is.close();
            fis.close();

        }
    }

    public void save() {

        //make last save the backupsave
        File directory = this.getFilesDir();
        File[] files = directory.listFiles();
        for (File file : files) {
            String fileName = file.getName();
            String[] splittedFileName = fileName.split("\\.");//escape dot as wildcard
            if (splittedFileName.length != 2) {
                continue;
            }
            if (SAVE_EXTENSION.equals("." + splittedFileName[1])) {
                File newFile = new File(directory, splittedFileName[0] + SAVE_BACKUP_EXTENSION);
                file.renameTo(newFile);
                Log.i(TAG, "renamed " + file.getName() + " to " + newFile.getName());
            }

        }


        try {

            //first save just GameSave
            FileOutputStream fos = openFileOutput("game" + SAVE_EXTENSION, Context.MODE_PRIVATE);
            ObjectOutputStream os = new ObjectOutputStream(fos);
            os.writeObject(GameSave.Instance());
            os.close();
            fos.close();

            for (int i = 0; i < GameSave.SAVECOUNT; i++) {
                //than save every Airport
                fos = openFileOutput("airport" + i + SAVE_EXTENSION, Context.MODE_PRIVATE);
                os = new ObjectOutputStream(fos);
                Log.i(Game.TAG, "Saving instance number " + i);
                os.writeObject(GameSave.Instance().gameInstances[i]);
                os.close();
                fos.close();
            }

            Log.i(Game.TAG, "Saving completed.");
        } catch (Exception e) {
            Toast toast = Toast.makeText(Game.this, "error: " + e.getMessage(), Toast.LENGTH_SHORT);
            toast.show();
            Log.e(Game.TAG, "error: ", e);
        }
    }

    private class Loading extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                BitmapLoader.Instance().loadBitmaps(getResources());

                loadSaves();
            } catch (Exception e) {
                Log.e(Game.TAG, "error: ", e);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {

            setGame();
        }
    }

    private class LoadAirport extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                BitmapLoader.Instance().loadBitmaps(getResources());

                loadSaves();


                int currentSlot = GameSave.Instance().currentSlot;
                if (GameSave.Instance().gameInstances[currentSlot] == null) {
                    return null;
                }
                GameInstance.setGameInstance(GameSave.Instance().gameInstances[currentSlot]);

                CommonMethods.loadAllObjectReferences(GameInstance.Airport());
                GameInstance.Settings().gameType = 2; //important to show, that this airport was loaded!
            } catch (Exception e) {
                Log.e(Game.TAG, "error: ", e);
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {

            setGame();
        }
    }
}
