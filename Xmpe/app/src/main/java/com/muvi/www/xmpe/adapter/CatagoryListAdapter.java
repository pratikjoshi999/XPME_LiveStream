package com.muvi.www.xmpe.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.muvi.www.xmpe.R;
import com.muvi.www.xmpe.categorylist.CategoryActivity;
import com.muvi.www.xmpe.categorylist.ImageLoader;
import com.muvi.www.xmpe.categorylist.Model;
import com.muvi.www.xmpe.content.ContentListActivity;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by Tanya on 06-11-2017.
 */

public class CatagoryListAdapter extends RecyclerView.Adapter<CatagoryListAdapter.ViewHolder> {

    Context context;
    LayoutInflater inflater;
    ArrayList<Model> data;
    ImageLoader imageLoader;
    Model model;
    SharedPreferences sp;
    public CatagoryListAdapter(Context context,
                     ArrayList<Model> arraylist) {
        this.context = context;
        data = arraylist;
        imageLoader = new ImageLoader(context);

        sp = context.getSharedPreferences("XPME",MODE_PRIVATE);
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//        View view = inflater.inflate(R.layout.category_item, parent, false);
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.category_item, null);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        model = data.get(position);
        holder.categoryTitleTextView.setText(model.getCategory_name());

        holder.img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent contentActivity = new Intent(context, ContentListActivity.class);
                contentActivity.putExtra("permalink", data.get(position).getCategoryPermalink());
                contentActivity.putExtra("category_name", data.get(position).getCategoryPermalink());
                contentActivity.putExtra("category_id", data.get(position).getCategoryPermalink());
                contentActivity.putExtra("category_name_array", data.get(position).getCategory_name());
//                contentActivity.putExtra("id", id);
                context.startActivity(contentActivity);

                SharedPreferences.Editor editor = sp.edit();
                editor.putString("category_id", data.get(position).category_id);
                editor.commit();
            }
        });


    }



    @Override
    public int getItemCount() {
        return data.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView categoryTitleTextView;
        ImageView img;
        public ViewHolder(View itemView) {

            super(itemView);
            // Locate the TextViews in listview_item.xml
            this.categoryTitleTextView = (TextView) itemView.findViewById(R.id.categoryTitleTextView);

            // Locate the ImageView in listview_item.xml
            this.img = (ImageView) itemView.findViewById(R.id.categoryImageView);



        }
    }

}



