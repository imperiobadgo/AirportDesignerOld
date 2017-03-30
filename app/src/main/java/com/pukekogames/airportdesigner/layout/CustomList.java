package com.pukekogames.airportdesigner.layout;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.pukekogames.airportdesigner.R;

/**
 * Created by Marko Rapka on 22.06.2016.
 */
public class CustomList extends ArrayAdapter<String> {

    private final Activity context;
    private final String[] name;
    private final Integer[] price;
    private final Integer[] imageId;
    public CustomList(Activity context,
                      String[] name, Integer[] imageId, Integer[] price) {
        super(context, R.layout.list_single, name);
        this.context = context;
        this.name = name;
        this.imageId = imageId;
        this.price = price;

    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView= inflater.inflate(R.layout.list_single, null, true);

        ImageView imageView = (ImageView) rowView.findViewById(R.id.img);
        TextView txtTitle = (TextView) rowView.findViewById(R.id.txt);
        TextView priceTitle = (TextView) rowView.findViewById(R.id.txt_price);

        if (imageId[position] != null) {
            imageView.setImageResource(imageId[position]);
        }
        txtTitle.setText(name[position]);
        priceTitle.setText(price[position] + " €");

        return rowView;
    }
}
