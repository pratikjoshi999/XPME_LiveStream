package com.muvi.www.xmpe.categorylist;

import android.app.ListActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.muvi.www.xmpe.R;

/**
 * Created by MUVI on 5/24/2017.
 */

public class NotificationActivity extends ListActivity {

    /** Called when the activity is first created. */


    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.notification);

        ListAdapter adapter = createAdapter();
        setListAdapter(adapter);
    }

    /**
     * Creates and returns a list adapter for the current list activity
     * @return
     */
    protected ListAdapter createAdapter()
    {
        // List with strings of contacts name
       String[] notification={"sasasa","adsdsacacasc","asdasdasdas"};

        // Create a simple array adapter (of type string) with the test values


        ListAdapter adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,notification);


        return adapter;
    }
}