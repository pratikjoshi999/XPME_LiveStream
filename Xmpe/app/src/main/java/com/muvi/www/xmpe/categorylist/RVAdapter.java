package com.muvi.www.xmpe.categorylist;

/**
 * Created by MUVI on 5/19/2017.
 */

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.muvi.www.xmpe.R;

public class RVAdapter extends BaseAdapter {

    // Declare Variables
    Context context;
    LayoutInflater inflater;
    ArrayList<Model> data;
    ImageLoader imageLoader;
    Model model;


    public RVAdapter(Context context,
                     ArrayList<Model> arraylist) {
        this.context = context;
        data = arraylist;
        imageLoader = new ImageLoader(context);
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    public View getView(final int position, View convertView, ViewGroup parent) {
        // Declare Variables
        TextView categoryTitleTextView;
        ImageView img;

        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View itemView = inflater.inflate(R.layout.featured_category_item, parent, false);

        // Get the position
//        Model model=new Model();
        model = data.get(position);

        // Locate the TextViews in listview_item.xml
        categoryTitleTextView = (TextView) itemView.findViewById(R.id.categoryTitleTextView);

        // Locate the ImageView in listview_item.xml
        img = (ImageView) itemView.findViewById(R.id.categoryImageView);

        // Capture position and set results to the TextViews
        categoryTitleTextView.setText(model.getCategory_name());



        //for image load uncomment this


       // imageLoader.DisplayImage(model.getCategory_img_url(),img);
        // Capture ListView item click

        return itemView;
    }
}
