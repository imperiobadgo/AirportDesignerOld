package com.pukekogames.airportdesigner.layout;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.pukekogames.airportdesigner.GameInstance.GameInstance;
import com.pukekogames.airportdesigner.Helper.GameSave;
import com.pukekogames.airportdesigner.R;

import java.util.ArrayList;

/**
 * Created by Marko Rapka on 02.10.2016.
 */
public class StringList extends ArrayAdapter<String> {

    Context context;

    ArrayList<String> objects;

    public StringList(Context context, ArrayList<String> objects) {
        super(context, R.layout.list_stringrow, objects);
        this.context = context;
        this.objects = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final View view = View.inflate(context, R.layout.list_stringrow, null);

        if (view instanceof TextView) {
            ((TextView) view).setText(objects.get(position));
        }

        if (GameSave.Instance().gameInstances[position] == null) return view;

        if (GameSave.Instance().gameInstances[position].equals(GameInstance.Instance())) {
            view.setBackgroundColor(Color.YELLOW);
        } else {
            view.setBackgroundColor(Color.TRANSPARENT);
        }

        return view;
    }

}
