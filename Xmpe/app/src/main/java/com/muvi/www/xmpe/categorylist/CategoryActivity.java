package com.muvi.www.xmpe.categorylist;

/**
 * Created by MUVI on 5/19/2017.
 */

import android.app.Dialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.muvi.www.xmpe.activity.LoginActivity;
import com.muvi.www.xmpe.R;
import com.muvi.www.xmpe.Util;
import com.muvi.www.xmpe.activity.MainActivity;
import com.muvi.www.xmpe.content.ContentListActivity;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;

public class CategoryActivity extends android.support.v4.app.Fragment {
   
    ListView listview;
    int statusCode;
    RVAdapter adapter;
    String responseStr;
    ProgressDialog mProgressDialog;
    ArrayList<Model> arraylist1;
    ArrayList<String>CatagoryName = new ArrayList<>();
    SharedPreferences sp;

    String category_id = "category_id";
    String category_name = "category_name";
    String category_img_url = "category_img_url";
    String permalink="permalink";
    Toolbar mActionBarToolbar;
     Context context;
    private Button mbutton;
    TextView featuredCategoryTitle;
   // String id;



    public CategoryActivity(){

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.listview_category, container, false);

        context=getActivity();

        sp = context.getSharedPreferences("XPME",MODE_PRIVATE);

        //////Added for test


       /* mActionBarToolbar = (Toolbar) rootView.findViewById(R.id.toolbar);
        ((AppCompatActivity)getActivity()).setSupportActionBar(mActionBarToolbar);*/
        /*mActionBarToolbar.setTitle("");
        TextView mTitle = (TextView) mActionBarToolbar.findViewById(R.id.toolbar_title);
        ImageView iv= (ImageView)rootView.findViewById(R.id.tv_header_title);
        ImageView iv1= (ImageView)rootView.findViewById(R.id.tv_header_title1);
        mTitle.setText("Categories");
        iv.setImageResource(R.drawable.bell_white);
        iv1.setImageResource(R.drawable.logout);*/
        listview = (ListView) rootView.findViewById(R.id.listview);
        featuredCategoryTitle=(TextView)rootView.findViewById(R.id.featuredCategoryTitle);
        arraylist1 = new ArrayList<>();

        String title=getArguments().getString("title");
        Log.v("pratik","title="+title);
        featuredCategoryTitle.setText(getArguments().getString("title"));

//        Intent startingIntent = context.getIntent();;
        //id = startingIntent.getStringExtra("id");

       /* iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, NotificationActivity.class);
                startActivity(intent);
            }
        });
        iv1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {

                // custom dialog
                final Dialog dialog = new Dialog(context);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.custom_dialog);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                // Custom Android Allert Dialog Title

                Button dialogButtonCancel = (Button) dialog.findViewById(R.id.customDialogCancel);
                Button dialogButtonOk = (Button) dialog.findViewById(R.id.customDialogOk);
                // Click cancel to dismiss android custom dialog box
                dialogButtonCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                // Your android custom dialog ok action
                // Action for custom dialog ok button click
                dialogButtonOk.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(context, LoginActivity.class);
                        startActivity(intent);
                        getActivity().finish();
                    }
                });

                dialog.show();
            }
        });*/



        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.v("SUBHA", "FHH" + arraylist1.get(position).getCategoryPermalink());
                Intent contentActivity = new Intent(context, ContentListActivity.class);
                contentActivity.putExtra("permalink", arraylist1.get(position).getCategoryPermalink());
                contentActivity.putExtra("category_name", arraylist1.get(position).getCategoryPermalink());
                contentActivity.putExtra("category_id", arraylist1.get(position).getCategoryPermalink());
                contentActivity.putExtra("category_name_array", CatagoryName);
                contentActivity.putExtra("id", id);
                startActivity(contentActivity);

                SharedPreferences.Editor editor = sp.edit();
                editor.putString("category_id", arraylist1.get(position).category_id);
                editor.commit();
            }
        });
        Log.v("SUBHA", "called");

        // Execute DownloadJSON AsyncTask
        new DownloadJSON().execute();

        rootView.setFocusableInTouchMode(true);
        rootView.requestFocus();
        rootView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {

                if(event.getAction()==KeyEvent.ACTION_DOWN){
                    if(keyCode==KeyEvent.KEYCODE_BACK){
                        Intent backIntent=new Intent(context, MainActivity.class);
                        backIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        startActivity(backIntent);
                        getActivity().finish();
                    }
                }
                return false;
            }
        });

        return rootView;
    }

 /*   @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Get the view from listview_main.xml
        setContentView(R.layout.listview_category);
        sp = context.getSharedPreferences("XPME",MODE_PRIVATE);

        mActionBarToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mActionBarToolbar);
        mActionBarToolbar.setTitle("");
        TextView mTitle = (TextView) mActionBarToolbar.findViewById(R.id.toolbar_title);
        ImageView iv= (ImageView)findViewById(R.id.tv_header_title);
        ImageView iv1= (ImageView)findViewById(R.id.tv_header_title1);
        mTitle.setText("Categories");
        iv.setImageResource(R.drawable.bell_white);
        iv1.setImageResource(R.drawable.logout);
        listview = (ListView) findViewById(R.id.listview);
        arraylist1 = new ArrayList<>();

        Intent startingIntent = getIntent();;
        //id = startingIntent.getStringExtra("id");

        iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CategoryActivity.this, NotificationActivity.class);
                startActivity(intent);
            }
        });
        iv1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {

                // custom dialog
                final Dialog dialog = new Dialog(context);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.custom_dialog);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                // Custom Android Allert Dialog Title

                Button dialogButtonCancel = (Button) dialog.findViewById(R.id.customDialogCancel);
                Button dialogButtonOk = (Button) dialog.findViewById(R.id.customDialogOk);
                // Click cancel to dismiss android custom dialog box
                dialogButtonCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                // Your android custom dialog ok action
                // Action for custom dialog ok button click
                dialogButtonOk.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(CategoryActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish();
                    }
                });

                dialog.show();
            }
        });

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.v("SUBHA", "FHH" + arraylist1.get(position).getCategoryPermalink());
                Intent contentActivity = new Intent(CategoryActivity.this, ContentListActivity.class);
                contentActivity.putExtra("permalink", arraylist1.get(position).getCategoryPermalink());
                contentActivity.putExtra("category_name", arraylist1.get(position).getCategoryPermalink());
                contentActivity.putExtra("category_id", arraylist1.get(position).getCategoryPermalink());
                contentActivity.putExtra("category_name_array", CatagoryName);
                contentActivity.putExtra("id", id);
                startActivity(contentActivity);

                SharedPreferences.Editor editor = sp.edit();
                editor.putString("category_id", arraylist1.get(position).category_id);
                editor.commit();
            }
        });
        Log.v("SUBHA", "called");

        // Execute DownloadJSON AsyncTask
        new DownloadJSON().execute();
    }*/

    // DownloadJSON AsyncTask
    private class DownloadJSON extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Create a progressdialog
            mProgressDialog = new ProgressDialog(context);
            // Set progressdialog title
            mProgressDialog.setTitle("Xpme");
            // Set progressdialog message
            mProgressDialog.setMessage("Loading...");
            mProgressDialog.setIndeterminate(false);
            // Show progressdialog
            mProgressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            // Create an array
            JSONObject myJson = null;
            // Retrieve JSON Objects from the given URL address
            String urlRouteList = Util.rootUrl().trim() + Util.getCategoryList.trim();
            try {
                HttpClient httpclient = new DefaultHttpClient();
                HttpPost httppost = new HttpPost(urlRouteList);
                httppost.setHeader(HTTP.CONTENT_TYPE, "application/x-www-form-urlencoded;charset=UTF-8");

                httppost.addHeader("authToken", Util.authTokenStr.trim());


                // httppost.addHeader("lang_code",Util.getTextofLanguage(LoginActivity.this,Util.SELECTED_LANGUAGE_CODE,Util.DEFAULT_SELECTED_LANGUAGE_CODE));

           /*     try {
                    httppost.setEntity(new UrlEncodedFormEntity(cred, "UTF-8"));
                }

                catch (UnsupportedEncodingException e) {
                    statusCode = 0;
                    e.printStackTrace();
                }*/

                // Execute HTTP Post Request
                try {
                    HttpResponse response = httpclient.execute(httppost);
                    responseStr = EntityUtils.toString(response.getEntity());
                    Log.v("SUBHA","RES"+responseStr);


                } catch (org.apache.http.conn.ConnectTimeoutException e) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            statusCode = 0;
                            //Crouton.showText(ShowWithEpisodesListActivity.this, "Slow Internet Connection", Style.INFO);
                            Toast.makeText(context, Util.getTextofLanguage(context, Util.SLOW_INTERNET_CONNECTION, Util.DEFAULT_SLOW_INTERNET_CONNECTION), Toast.LENGTH_LONG).show();

                        }

                    });
                    Log.v("SUBHA", "ConnectTimeoutException" + e.toString());


                } catch (IOException e) {
                    statusCode = 0;
                    Log.v("SUBHA", "IOException" + e.toString());

                    e.printStackTrace();
                }

                //Log.v("SUBHA", "response = " + responseStr);
                if (responseStr != null) {
                    myJson = new JSONObject(responseStr);
//                    statusCode = Integer.parseInt(myJson.optString("code"));
                    JSONArray obj = myJson.getJSONArray("category_list");

                    for (int i = 0; i < obj.length(); i++) {

                        JSONObject object = obj.getJSONObject(i);
                        category_id = object.optString("category_id");
                        category_name = object.optString("category_name");
                        category_img_url = object.optString("category_img_url");
                        permalink=object.optString("permalink");
                        Model model = new Model(category_id,category_name,category_img_url,permalink);
                        arraylist1.add(model);
                        CatagoryName.add(category_name);
                        Log.v("BIBHU","category_name="+category_name);

                    }


                }

            } catch (Exception e) {
                Log.v("SUBHA", "Exception" + e.toString());

                statusCode = 0;

            }

            return null;
        }

        @Override
        protected void onPostExecute(Void args) {
            // Locate the listview in listview_main.xml
            // Pass the results into ListViewAdapter.java
            adapter = new RVAdapter(context, arraylist1);
            // Set the adapter to the ListView
            listview.setAdapter(adapter);

            // Close the progressdialog
            mProgressDialog.dismiss();
        }

        public void onClick() {
            getActivity().onBackPressed();
        }
    }


}
