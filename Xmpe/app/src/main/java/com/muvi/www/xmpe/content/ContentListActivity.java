package com.muvi.www.xmpe.content;

/**
 * Created by MUVI on 5/19/2017.
 */

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.home.apisdk.apiController.LogoutAsynctask;
import com.home.apisdk.apiModel.LogoutInput;
import com.muvi.player.activity.ExoPlayerActivity;
import com.muvi.player.activity.Player;
import com.muvi.www.xmpe.ProgressBarHandler;
import com.muvi.www.xmpe.activity.BroadcasterActivity;
import com.muvi.www.xmpe.activity.LoginActivity;
import com.muvi.www.xmpe.activity.MainActivity;
import com.muvi.www.xmpe.R;
import com.muvi.www.xmpe.Util;
import com.muvi.www.xmpe.activity.SplashActivity;

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
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


public class ContentListActivity extends AppCompatActivity implements LogoutAsynctask.LogoutListener{
    Player playerModel;
    // Declare Variables
    JSONObject jsonobject;
    JSONArray jsonarray;
    ListView listview;
    int statusCode;
    ContentListAdapter adapter;
    String responseStr;
    ProgressDialog mProgressDialog;
    ArrayList<ContentListModel> arraylist2;
    ArrayList<ContentListModel> liveArrayList;
    ArrayList<ContentListModel> offlineArrayList;
    String name = "name";
    String category_name = "category_name";
    String poster_url = "poster_url";
    String movie_stream_uniq_id, movie_id, feed_url, permalink, is_online, muvi_uniq_id, content_types_id;
    String permalinkStr = "";
    Toolbar mActionBarToolbar;
    String category_name1="";
    final Context context = this;
    private Button mbutton;
    ArrayList<String>CatagoryName = new ArrayList<String>();
    String category_id;
    String id;
    TextView all,live,offline;

    int corePoolSize = 60;
    int maximumPoolSize = 80;
    int keepAliveTime = 10;
    BlockingQueue<Runnable> workQueue = new LinkedBlockingQueue<Runnable>(maximumPoolSize);
    Executor threadPoolExecutor = new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, TimeUnit.SECONDS, workQueue);

    private SharedPreferences sp,pref;
    private ProgressBarHandler pDialog = null;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Get the view from listview_main.xml
        setContentView(R.layout.listview_main);

        mActionBarToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mActionBarToolbar);
        mActionBarToolbar.setTitle("");
        TextView boardcast= (TextView) findViewById(R.id.boardcast);
        TextView mTitle = (TextView) mActionBarToolbar.findViewById(R.id.toolbar_title);
        ImageView iv = (ImageView) findViewById(R.id.tv_header_title);
        ImageView iv1 = (ImageView) findViewById(R.id.tv_header_title2);

        all=(TextView)findViewById(R.id.all);
        live=(TextView)findViewById(R.id.live);
        offline=(TextView)findViewById(R.id.offline);

        pref = getSharedPreferences(Util.LOGIN_PREF, 0);

        CatagoryName = getIntent().getStringArrayListExtra("category_name_array");


        if (getIntent().getStringExtra("category_name") != null) {
            category_name1 = getIntent().getStringExtra("category_name");
            String name= category_name1;
            name = name.substring(0,1).toUpperCase() + name.substring(1).toLowerCase();
            mTitle.setText(name);
        }

        iv.setImageResource(R.drawable.logout);
        iv1.setImageResource(R.drawable.backbutton);
        if (getIntent().getStringExtra("permalink") != null) {
            permalinkStr = getIntent().getStringExtra("permalink");
        }
        Log.v("SUBHALAXMIPANDA","Contentlist"+getIntent().getStringExtra("permalink"));
        listview = (ListView) findViewById(R.id.listview);
        arraylist2 = new ArrayList<>();
        liveArrayList = new ArrayList<>();
        offlineArrayList = new ArrayList<>();

        ImageView img= (ImageView) findViewById(R.id.tv_header_title2);

        iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {

                // custom dialog
                final Dialog dialog = new Dialog(context);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.custom_dialog);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                // Custom Android Allert Dialog Title

                Button dialogButtonCancel = (Button) dialog.findViewById(R.id.customDialogCancel);
                Button dialogButtonOk = (Button) dialog.findViewById(
                        R.id.customDialogOk);
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
                        LogoutInput logoutInput = new LogoutInput();
                        logoutInput.setAuthToken(Util.authTokenStr);
                        logoutInput.setLogin_history_id( pref.getString("PREFS_LOGIN_HISTORYID_KEY", null));
                        String loginHistoryIdStr = pref.getString("PREFS_LOGIN_HISTORYID_KEY", null);
                        Log.v("pratik","loginHistoryIdStr=="+loginHistoryIdStr);
                        logoutInput.setLang_code(Util.getTextofLanguage(ContentListActivity.this,Util.SELECTED_LANGUAGE_CODE, Util.DEFAULT_SELECTED_LANGUAGE_CODE));
                        Log.v("pratik","lang_code=="+Util.getTextofLanguage(ContentListActivity.this,Util.SELECTED_LANGUAGE_CODE, Util.DEFAULT_SELECTED_LANGUAGE_CODE));

                        LogoutAsynctask asynLogoutDetails = new LogoutAsynctask(logoutInput, ContentListActivity.this, ContentListActivity.this);
                        asynLogoutDetails.executeOnExecutor(threadPoolExecutor);

                        dialog.dismiss();
                    }
                });

                dialog.show();
            }
        });

        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        boardcast.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Intent detailsIntent = new Intent(ContentListActivity.this, BroadcasterActivity.class);
                detailsIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                detailsIntent.putExtra("category_name_array",CatagoryName);
                detailsIntent.putExtra("category_id",category_id);
                startActivity(detailsIntent);
                Toast.makeText(context, "broadcast", Toast.LENGTH_SHORT).show();

            }
        });

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.v("SUBHA","CLICKED"+arraylist2.get(position));
                playerModel=new Player();

                playerModel.setStreamUniqueId(arraylist2.get(position).getMovie_stream_uniq_id());
                playerModel.setMovieUniqueId(arraylist2.get(position).getMuvi_uniq_id());
                playerModel.setUserId(getSharedPreferences(Util.LOGIN_PREF, 0).getString("PREFS_LOGGEDIN_ID_KEY",""));
                playerModel.setEmailId(getSharedPreferences(Util.LOGIN_PREF, 0).getString("PREFS_LOGIN_EMAIL_ID_KEY",""));
                playerModel.setAuthTokenStr(Util.authTokenStr.trim());
                playerModel.setRootUrl(Util.rootUrl().trim());
                playerModel.setEpisode_id("0");
                playerModel.setLiveStream(true);
                playerModel.setLandScape(false);
                playerModel.setVideoTitle("");
                playerModel.setVideoStory("");
                playerModel.setVideoGenre("");
                playerModel.setVideoDuration("");
                playerModel.setVideoReleaseDate("");
                playerModel.setCensorRating("");

                LoadVideoUrl loadVideoUrl = new LoadVideoUrl();
                loadVideoUrl.execute();


            }
        });
        // Execute DownloadJSON AsyncTask
        new GetContentListAsynTask().execute();

        live.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(liveArrayList.size()!=0){
                    adapter = new ContentListAdapter(ContentListActivity.this, liveArrayList);
                    // Set the adapter to the ListView
                    listview.setAdapter(adapter);
                }else{
                    adapter = new ContentListAdapter(ContentListActivity.this, liveArrayList);
                    // Set the adapter to the ListView
                    listview.setAdapter(adapter);
                    Toast.makeText(context, "No content found", Toast.LENGTH_SHORT).show();
                }
                live.setTextColor(getResources().getColor(R.color.colorPrimary));
                all.setTextColor(getResources().getColor(R.color.buttomtextColor));
                offline.setTextColor(getResources().getColor(R.color.buttomtextColor));
            }
        });

        offline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adapter = new ContentListAdapter(ContentListActivity.this, offlineArrayList);
                // Set the adapter to the ListView
                listview.setAdapter(adapter);
                offline.setTextColor(getResources().getColor(R.color.colorPrimary));
                all.setTextColor(getResources().getColor(R.color.buttomtextColor));
                live.setTextColor(getResources().getColor(R.color.buttomtextColor));
            }
        });

        all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adapter = new ContentListAdapter(ContentListActivity.this, arraylist2);
                // Set the adapter to the ListView
                listview.setAdapter(adapter);
                all.setTextColor(getResources().getColor(R.color.colorPrimary));
                offline.setTextColor(getResources().getColor(R.color.buttomtextColor));
                live.setTextColor(getResources().getColor(R.color.buttomtextColor));
            }
        });


    }

    @Override
    public void onLogoutPreExecuteStarted() {
        Log.v("pratik","logout preexe");

        pDialog = new ProgressBarHandler(ContentListActivity.this);
        pDialog.show();
    }

    @Override
    public void onLogoutPostExecuteCompleted(int code, String status, String message) {

        if (pDialog != null && pDialog.isShowing()) {
            pDialog.hide();
            pDialog = null;

        }

        Log.v("pratik","logout code=="+code);

        if (code != 200) {
            Toast.makeText(ContentListActivity.this, Util.getTextofLanguage(ContentListActivity.this,Util.SIGN_OUT_ERROR, Util.DEFAULT_SIGN_OUT_ERROR), Toast.LENGTH_LONG).show();

        }
        if (code == 0) {
            Toast.makeText(ContentListActivity.this, Util.getTextofLanguage(ContentListActivity.this,Util.SIGN_OUT_ERROR, Util.DEFAULT_SIGN_OUT_ERROR), Toast.LENGTH_LONG).show();

        }

        if (code > 0) {
            if (code == 200) {

                SharedPreferences.Editor editor = pref.edit();
                editor.clear();
                editor.commit();

                Log.v("pratik","is_one_step==="+Util.getTextofLanguage(ContentListActivity.this,Util.IS_ONE_STEP_REGISTRATION, Util.DEFAULT_IS_ONE_STEP_REGISTRATION));
                if ((Util.getTextofLanguage(ContentListActivity.this,Util.IS_ONE_STEP_REGISTRATION, Util.DEFAULT_IS_ONE_STEP_REGISTRATION)
                        .trim()).equals("1")) {
                    final Intent startIntent = new Intent(ContentListActivity.this, SplashActivity.class);
                    runOnUiThread(new Runnable() {
                        public void run() {
                            startIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                            startActivity(startIntent);
                            Toast.makeText(ContentListActivity.this, Util.getTextofLanguage(ContentListActivity.this,Util.LOGOUT_SUCCESS, Util.DEFAULT_LOGOUT_SUCCESS), Toast.LENGTH_LONG).show();

                            finish();

                        }
                    });
                } else {
                    final Intent startIntent = new Intent(ContentListActivity.this, MainActivity.class);
                    runOnUiThread(new Runnable() {
                        public void run() {
                            startIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                            startActivity(startIntent);
                            Toast.makeText(ContentListActivity.this, Util.getTextofLanguage(ContentListActivity.this,Util.LOGOUT_SUCCESS, Util.DEFAULT_LOGOUT_SUCCESS), Toast.LENGTH_LONG).show();

                            finish();

                        }
                    });
                }


            } else {
                Toast.makeText(ContentListActivity.this, Util.getTextofLanguage(ContentListActivity.this,Util.SIGN_OUT_ERROR, Util.DEFAULT_SIGN_OUT_ERROR), Toast.LENGTH_LONG).show();

            }
        }
    }

    // DownloadJSON AsyncTask
    private class GetContentListAsynTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Create a progressdialog
            mProgressDialog = new ProgressDialog(ContentListActivity.this);
            // Set progressdialog title
            mProgressDialog.setTitle("Xpme");
            // Set progressdialog message
            mProgressDialog.setMessage("Please Wait...");
            mProgressDialog.setIndeterminate(false);
            // Show progressdialog
            mProgressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            // Create an array
            JSONObject myJson = null;
            // Retrieve JSON Objects from the given URL address
            String urlRouteList = Util.rootUrl().trim() + Util.listUrl.trim();
            try {
                HttpClient httpclient = new DefaultHttpClient();
                HttpPost httppost = new HttpPost(urlRouteList);
                httppost.setHeader(HTTP.CONTENT_TYPE, "application/x-www-form-urlencoded;charset=UTF-8");

                httppost.addHeader("authToken", Util.authTokenStr.trim());
                httppost.addHeader("permalink", permalinkStr);
                Log.v("pratik","permal="+permalinkStr);

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


                } catch (org.apache.http.conn.ConnectTimeoutException e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            statusCode = 0;
                            //Crouton.showText(ShowWithEpisodesListActivity.this, "Slow Internet Connection", Style.INFO);
                            Toast.makeText(ContentListActivity.this, Util.getTextofLanguage(ContentListActivity.this, Util.SLOW_INTERNET_CONNECTION, Util.DEFAULT_SLOW_INTERNET_CONNECTION), Toast.LENGTH_LONG).show();

                        }

                    });

                } catch (IOException e) {
                    statusCode = 0;

                    e.printStackTrace();
                }

                Log.v("SUBHA", "response = " + responseStr);
                if (responseStr != null) {
                    myJson = new JSONObject(responseStr);
//                    statusCode = Integer.parseInt(myJson.optString("code"));
                    JSONArray obj = myJson.getJSONArray("movieList");

                    for (int i = 0; i < obj.length(); i++) {


                        JSONObject object = obj.getJSONObject(i);
                        name = object.optString("name");
                        category_name = object.optString("category_name");
                        poster_url = object.optString("poster_url");
                        movie_stream_uniq_id = object.optString("movie_stream_uniq_id");
                        movie_id = object.optString("movie_id");
                        feed_url = object.optString("feed_url");
                        permalink = object.optString("permalink");
                        is_online = object.optString("is_online");
                        muvi_uniq_id = object.optString("muvi_uniq_id");
                        content_types_id = object.optString("content_types_id");
                        if (is_online.equals("1")){
                            ContentListModel liveModel = new ContentListModel(movie_stream_uniq_id, movie_id, feed_url, permalink, name, poster_url, is_online, muvi_uniq_id, content_types_id);
                            liveArrayList.add(liveModel);
                        }else if(is_online.equals("0")){
                            ContentListModel offlineModel = new ContentListModel(movie_stream_uniq_id, movie_id, feed_url, permalink, name, poster_url, is_online, muvi_uniq_id, content_types_id);

                            offlineArrayList.add(offlineModel);
                        }
                        ContentListModel model = new ContentListModel(movie_stream_uniq_id, movie_id, feed_url, permalink, name, poster_url, is_online, muvi_uniq_id, content_types_id);
                        arraylist2.add(model);


                    }


                }

            } catch (Exception e) {
                statusCode = 0;

            }

            return null;
        }

        @Override
        protected void onPostExecute(Void args) {
            // Locate the listview in listview_main.xml
            // Pass the results into ListViewAdapter.java
            adapter = new ContentListAdapter(ContentListActivity.this, arraylist2);
            // Set the adapter to the ListView
            listview.setAdapter(adapter);
            // Close the progressdialog
            mProgressDialog.dismiss();
            all.setTextColor(getResources().getColor(R.color.colorPrimary));
        }
    }

    private class LoadVideoUrl extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Create a progressdialog
            mProgressDialog = new ProgressDialog(ContentListActivity.this);
            // Set progressdialog title
            mProgressDialog.setTitle("Xpme");
            // Set progressdialog message
            mProgressDialog.setMessage("Please Wait...");
            mProgressDialog.setIndeterminate(false);
            // Show progressdialog
            mProgressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            // Create an array
            JSONObject myJson = null;
            // Retrieve JSON Objects from the given URL address
            String urlRouteList = Util.rootUrl().trim() + Util.videoUrl.trim();
            try {
                HttpClient httpclient = new DefaultHttpClient();
                HttpPost httppost = new HttpPost(urlRouteList);
                httppost.setHeader(HTTP.CONTENT_TYPE, "application/x-www-form-urlencoded;charset=UTF-8");

                httppost.addHeader("authToken", Util.authTokenStr.trim());
                httppost.addHeader("content_uniq_id", playerModel.getMovieUniqueId());
                httppost.addHeader("stream_uniq_id", playerModel.getStreamUniqueId());
                httppost.addHeader("internet_speed","");
                httppost.addHeader("user_id",playerModel.getUserId());


                Log.v("SUBHA","CONtnte"+playerModel.getMovieUniqueId());
                Log.v("SUBHA","getStreamUniqueId"+playerModel.getStreamUniqueId());
                Log.v("SUBHA","getStreamUniqueId"+playerModel.getUserId());

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


                } catch (org.apache.http.conn.ConnectTimeoutException e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            statusCode = 0;
                            //Crouton.showText(ShowWithEpisodesListActivity.this, "Slow Internet Connection", Style.INFO);
                            Toast.makeText(ContentListActivity.this, Util.getTextofLanguage(ContentListActivity.this, Util.SLOW_INTERNET_CONNECTION, Util.DEFAULT_SLOW_INTERNET_CONNECTION), Toast.LENGTH_LONG).show();

                        }

                    });

                } catch (IOException e) {
                    statusCode = 0;

                    e.printStackTrace();
                }

                Log.v("SUBHAlaxmi", "response = " + responseStr);
                if (responseStr != null) {
                    myJson = new JSONObject(responseStr);
                    statusCode = Integer.parseInt(myJson.optString("code"));

                    if (statusCode == 200) {
                        if ((myJson.has("videoUrl")) && myJson.getString("videoUrl").trim() != null && !myJson.getString("videoUrl").trim().isEmpty() && !myJson.getString("videoUrl").trim().equals("null") && !myJson.getString("videoUrl").trim().matches("")) {
                            playerModel.setVideoUrl(myJson.getString("videoUrl"));

                        } else {
                            playerModel.setVideoUrl("No Data");
                        }
                    }

                }

            } catch (Exception e) {
                statusCode = 0;

            }

            return null;
        }

        @Override
        protected void onPostExecute(Void args) {

            mProgressDialog.dismiss();
            if (!playerModel.getVideoUrl().matches("No Data")){
                Intent playerIntent=new Intent(ContentListActivity.this,ExoPlayerActivity.class);
                playerIntent.putExtra("PlayerModel",playerModel);
                Log.v("pratik","go to exo");
                Log.v("pratik","video ="+playerModel.getVideoUrl());
                startActivity(playerIntent);
            }
        }
    }
}
