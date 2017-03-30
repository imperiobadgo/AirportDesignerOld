package com.pukekogames.airportdesigner.Helper;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;

/**
 * Created by Marko Rapka on 09.03.2016.
 */
public class LoggerFormatter extends Formatter {
    @Override
    public String format(LogRecord record) {
        String log = "";

        long millis = record.getMillis();

        SimpleDateFormat sdf = new SimpleDateFormat("dd.mm.yyyy HH:mm:ss");
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(millis);

        log += sdf.format(calendar.getTime());

        log += " ThreadID : " + record.getThreadID() + "\n" + record.getSourceClassName() + " " + record.getSourceMethodName() + "\n";

        log += record.getLevel() + " : " + record.getMessage() + "\n";
        if (record.getLevel() == Level.SEVERE) {
            Throwable throwable = record.getThrown();
            if (throwable != null)
                log += getStackTrace(throwable);
//            System.out.println(record.getSourceClassName() + " " + record.getSourceMethodName());
//            throwable.printStackTrace();
        }
        log += "\n";
        return log;
    }

    public static String getStackTrace(final Throwable throwable) {
        final StringWriter sw = new StringWriter();
        final PrintWriter pw = new PrintWriter(sw, true);
        throwable.printStackTrace(pw);
        return sw.getBuffer().toString();
    }
}
