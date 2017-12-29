package com.muvi.www.xmpe;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.muvi.www.xmpe.categorylist.ImageLoader;
import com.muvi.www.xmpe.content.ContentListModel;

import java.util.ArrayList;

/**
 * Created by MUVI on 5/25/2017.
 */

public class MainAdapter extends BaseAdapter {


    Context context;
    LayoutInflater inflater;
    ArrayList<ContentListModel> data;
    ContentListModel model;


    public MainAdapter (Context context,
                              ArrayList<ContentListModel> arraylist) {
        this.context = context;
        data = arraylist;
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
        TextView contentNameTextView;
        TextView byUserNameTextView;
        TextView userStatusTextView;

        // TextView tv2;

        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View itemView = inflater.inflate(R.layout.listview_item, parent, false);
        // Get the position
//        Model model=new Model();
        model = data.get(position);

        // Locate the TextViews in listview_item.xml
        contentNameTextView = (TextView) itemView.findViewById(R.id.contentNameTextView);
        byUserNameTextView = (TextView) itemView.findViewById(R.id.byTextView);
        userStatusTextView = (TextView) itemView.findViewById(R.id.userStatusTextView);


        // Capture position and set results to the TextViews
        contentNameTextView.setText(model.getName());
        byUserNameTextView.setText("by "+model.getName());

        if (model.getIs_online() == "1"){
            userStatusTextView.setVisibility(View.VISIBLE);
        }else{
            userStatusTextView.setVisibility(View.GONE);

        }

        // Capture position and set results to the ImageView
        // Passes flag images URL into ImageLoader.class
        // Capture ListView item click

        return itemView;
    }
}
