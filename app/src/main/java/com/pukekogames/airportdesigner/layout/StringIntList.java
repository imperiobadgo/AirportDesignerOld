package com.pukekogames.airportdesigner.layout;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.pukekogames.airportdesigner.R;

import java.util.ArrayList;

/**
 * Created by Marko Rapka on 27.11.2016.
 */
public class StringIntList extends ArrayAdapter<String> {

    private final Activity context;
    private final ArrayList<String> name;
    private final ArrayList<Integer> numbers;

    public StringIntList(Activity context,
                         ArrayList<String> name, ArrayList<Integer> number) {
        super(context, R.layout.list_stringint, name);
        this.context = context;
        this.name = name;
        this.numbers = number;

    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView= inflater.inflate(R.layout.list_stringint, null, true);

        TextView txtTitle = (TextView) rowView.findViewById(R.id.txt_string);
        TextView numberTitle = (TextView) rowView.findViewById(R.id.txt_number);
        txtTitle.setText(name.get(position));
        numberTitle.setText(numbers.get(position).toString());

        return rowView;
    }

}
