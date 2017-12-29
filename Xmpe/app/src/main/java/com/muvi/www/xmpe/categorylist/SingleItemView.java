package com.muvi.www.xmpe.categorylist;

/**
 * Created by MUVI on 5/19/2017.
 */

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.muvi.www.xmpe.R;

public class SingleItemView extends Activity {
    // Declare Variables
    String category_name;
    String category_id;
    String category_img_url;
    String position;
    ImageLoader imageLoader = new ImageLoader(this);

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Get the view from singleitemview.xml
        setContentView(R.layout.singleitemview);

        Intent i = getIntent();
        // Get the result of rank
        category_name = i.getStringExtra("category_name");
        // Get the result of country
        category_id = i.getStringExtra("category_id");
        // Get the result of population
        // Get the result of flag
        category_img_url = i.getStringExtra("category_img_url");

        // Locate the TextViews in singleitemview.xml
        TextView txtrank = (TextView) findViewById(R.id.tv1);
       // TextView txtcountry = (TextView) findViewById(R.id.tv2);

        // Locate the ImageView in singleitemview.xml
        ImageView imgflag = (ImageView) findViewById(R.id.img);

        // Set results to the TextViews
        txtrank.setText(category_name);
        //txtcountry.setText(category_id);

        // Capture position and set results to the ImageView
        // Passes flag images URL into ImageLoader.class
        imageLoader.DisplayImage(category_img_url, imgflag);
    }
}

