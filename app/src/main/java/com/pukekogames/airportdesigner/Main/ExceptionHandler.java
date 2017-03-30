package com.pukekogames.airportdesigner.Main;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import com.pukekogames.airportdesigner.Activities.ErrorActivity;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * Created by Marko Rapka on 12.11.2016.
 */
public class ExceptionHandler implements Thread.UncaughtExceptionHandler {

    private final Activity myContext;
    private final String LINE_SEPARATOR = "\n";

    public ExceptionHandler(Activity context) {
        myContext = context;
    }

    @Override
    public void uncaughtException(Thread thread, Throwable exception) {

        String deviceReport = "Oh, something went wrong...\n\n" +
                "\n***** Error *****\n" +
                exception.getMessage() +
                LINE_SEPARATOR +
                "\n***** DEVICE *****\n" +
                "Brand: " +
                Build.BRAND +
                LINE_SEPARATOR +
                "Device: " +
                Build.DEVICE +
                LINE_SEPARATOR +
                "Model: " +
                Build.MODEL +
                LINE_SEPARATOR +
                "Id: " +
                Build.ID +
                LINE_SEPARATOR +
                "Product: " +
                Build.PRODUCT +
                LINE_SEPARATOR +
                "\n***** FIRMWARE *****\n" +
                "SDK: " +
                Build.VERSION.SDK_INT +
                LINE_SEPARATOR +
                "Release: " +
                Build.VERSION.RELEASE +
                LINE_SEPARATOR +
                "Incremental: " +
                Build.VERSION.INCREMENTAL +
                LINE_SEPARATOR;


        StringWriter stackTrace = new StringWriter();
        exception.printStackTrace(new PrintWriter(stackTrace));
        String errorReport = "\n***** DETAILS OF ERROR *****\n" +
                LINE_SEPARATOR +
                stackTrace.toString();


        Intent intent = new Intent(myContext, ErrorActivity.class);
        intent.putExtra("device", deviceReport);
        intent.putExtra("error", errorReport);

        //for removing app from recent apps list
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);

        myContext.startActivity(intent);


        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(10);

    }
}
