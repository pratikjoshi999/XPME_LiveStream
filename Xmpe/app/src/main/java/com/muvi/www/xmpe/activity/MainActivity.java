package com.muvi.www.xmpe.activity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.hardware.Camera;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.faucamp.simplertmp.RtmpHandler;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import com.home.apisdk.APIUrlConstant;
import com.home.apisdk.apiController.LogoutAsynctask;
import com.home.apisdk.apiModel.LogoutInput;
import com.muvi.www.xmpe.ProgressBarHandler;
import com.muvi.www.xmpe.R;
import com.muvi.www.xmpe.Util;
import com.muvi.www.xmpe.categorylist.CategoryActivity;
import com.muvi.www.xmpe.categorylist.NotificationActivity;
import com.muvi.www.xmpe.content.ContentListAdapter;
import com.muvi.www.xmpe.content.ContentListModel;
import com.muvi.www.xmpe.fragment.FragmentDrawer;
import com.muvi.www.xmpe.fragment.HomeFragment;
import com.muvi.www.xmpe.model.NavDrawerItem;
import com.seu.magicfilter.utils.MagicFilterType;

import net.ossrs.yasea.SrsCameraView;
import net.ossrs.yasea.SrsEncodeHandler;
import net.ossrs.yasea.SrsPublisher;
import net.ossrs.yasea.SrsRecordHandler;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.SocketException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static android.Manifest.permission.CAMERA;

public class MainActivity extends AppCompatActivity implements FragmentDrawer.FragmentDrawerListener, LogoutAsynctask.LogoutListener {
    private static final int PERMISSION_REQUEST_CODE = 200;
    private static final String TAG = "pratik";

    private ImageView btnPublish,btnPublish1;
    private ImageView btnSwitchCamera;
    private ImageView btnRecord;
    private Button btnSwitchEncoder;
    private EditText titleInput;
    private EditText descriptionInput;
    String GetEditText;
    Button createStreamButton;
    JSONObject jsonobject;
    JSONArray jsonarray;
    ListView listview;
    int statusCode;
    ContentListAdapter adapter;
    String responseStr;
    ProgressDialog mProgressDialog;
    ArrayList<ContentListModel> arraylist2;
    private EditText mInputMessageView;
    String feed_url;
    String idd;
    String category_idd, feed_url1,mUsername;
    final Context context = this;
    private Button mbutton;
    ImageView image;

    Boolean isStreamStarted=false;


    Toolbar mActionBarToolbar;
    ArrayList<String>CatagoryName = new ArrayList<String>();

    private SharedPreferences sp,pref;
    private String rtmpUrl;
    private String recPath = Environment.getExternalStorageDirectory().getPath() + "/test.mp4";

    private SrsPublisher mPublisher;

    public static ProgressBarHandler internetSpeedDialog;
    private RelativeLayout noInternetLayout;
    TextView noInternetTextView;
    private FragmentDrawer drawerFragment;
    public static String internetSpeed ;

    public static ArrayList<NavDrawerItem> menuList;
    private static ArrayList<NavDrawerItem> titles = null;
    public static int isNavigated = 0;
    private ProgressBarHandler pDialog = null;
    Fragment fragment = null;
    private String imageUrlStr;
    AsynLoadMenuItems asynLoadMenuItems = null;

    int corePoolSize = 60;
    int maximumPoolSize = 80;
    int keepAliveTime = 10;
    BlockingQueue<Runnable> workQueue = new LinkedBlockingQueue<Runnable>(maximumPoolSize);
    Executor threadPoolExecutor = new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, TimeUnit.SECONDS, workQueue);

    ImageView notificationIcon,logoutIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        APIUrlConstant.BASE_URl=Util.rootUrl();

//        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_main);


        // restore data.
       /* sp = getSharedPreferences("XPME", MODE_PRIVATE);
        rtmpUrl = sp.getString("rtmpUrl", rtmpUrl);
        idd = getSharedPreferences(Util.LOGIN_PREF, MODE_PRIVATE).getString("PREFS_LOGGEDIN_ID_KEY", null);

        category_idd=sp.getString("category_id",null);*/
//        category_id=sp.getString("category_id",null);

        // initialize url.



        /*createStreamButton = (Button)findViewById(R.id.createStreamButton);
        descriptionInput = (EditText)findViewById(R.id.descriptionInput);
        titleInput = (EditText)findViewById(R.id.titleInput);
        ImageView iv1 = (ImageView) findViewById(R.id.tv_header_title2);
        iv1.setImageResource(R.drawable.backbutton);
        btnPublish = (ImageView) findViewById(R.id.publish);
        btnPublish1 = (ImageView) findViewById(R.id.publish1);
        btnSwitchCamera = (ImageView) findViewById(R.id.swCam);
        final TextInputLayout usernameWrapper = (TextInputLayout) findViewById(R.id.titleWrapper);
        btnRecord = (ImageView) findViewById(R.id.record);
        mActionBarToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mActionBarToolbar);
        mActionBarToolbar.setTitle("");

        TextView mTitle = (TextView) mActionBarToolbar.findViewById(R.id.toolbar_title);
        mTitle.setText("Live Stream");*/
        //btnSwitchEncoder = (Button) findViewById(R.id.swEnc);

       /* mPublisher = new SrsPublisher((SrsCameraView) findViewById(R.id.glsurfaceview_camera));
        mPublisher.setEncodeHandler(new SrsEncodeHandler(this));
        mPublisher.setRtmpHandler(new RtmpHandler(this));
        mPublisher.setRecordHandler(new SrsRecordHandler(this));
        mPublisher.setPreviewResolution(screenHeight, screenWidth);
        mPublisher.setOutputResolution(720, 1280);
        mPublisher.setVideoHDMode();
        mPublisher.startCamera();
*/

//        CatagoryName = getIntent().getStringArrayListExtra("category_name_array");
//        category_id=getIntent().getStringExtra("category_id");
        // Toast.makeText(getApplicationContext(),""+CatagoryName.size(),Toast.LENGTH_SHORT).show();


        //String[] name = {category_name1};
////        String[] name = {"sass","sasadaaa", "asadada"};
//        ArrayAdapter stringArrayAdapter = new ArrayAdapter(this, R.layout.spinner_item, CatagoryName);
//
//        // create a spinner
//        Spinner spinner = (Spinner) findViewById(R.id.spinner);
//        // add adapter to spinner
//        spinner.setAdapter(stringArrayAdapter);
//        // create listener and add to spinner
//        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                // put code which recognize a selected element
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//
//            }
//        });

        /*createStreamButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

                GetEditText = titleInput.getText().toString();

                if (GetEditText.isEmpty() || GetEditText.length() == 0 || GetEditText.equals("") || GetEditText == null) {

                    Toast.makeText(MainActivity.this, "Please Enter Title", Toast.LENGTH_LONG).show();

                } else {

                    isStreamStarted=true;
                    new DownloadJSON().execute();
                    LinearLayout linearLayout = (LinearLayout) findViewById(R.id.stream);
                    linearLayout.setVisibility(View.GONE);
                }
            }
                });

        iv1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        btnPublish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v("SUBHAlaxmi","RTPM"+rtmpUrl);
                //rtmpUrl = efu.getText().toString();
                if(isStreamStarted) {
                    btnPublish1.setVisibility(View.VISIBLE);
                    btnPublish.setVisibility(View.GONE);
                *//*SharedPreferences.Editor editor = sp.edit();
                editor.putString("rtmpUrl", rtmpUrl);
                editor.apply();*//*
                    mPublisher.startPublish(rtmpUrl);
                    mPublisher.startCamera();
                }
                else{
                    Toast.makeText(context, "Stream is not started", Toast.LENGTH_SHORT).show();
                }


            }
        });



        btnPublish1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //rtmpUrl = efu.getText().toString();
                btnPublish.setVisibility(View.VISIBLE);
                btnPublish1.setVisibility(View.GONE);
                mPublisher.stopPublish();
                mPublisher.stopRecord();
                mPublisher.stopCamera();
            }
        });

        btnSwitchCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPublisher.switchCameraFace((mPublisher.getCamraId() + 1) % Camera.getNumberOfCameras());
            }
        });

        btnRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });*/


        mActionBarToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mActionBarToolbar);
        mActionBarToolbar.setTitle("");
         notificationIcon= (ImageView)findViewById(R.id.tv_header_title);
         logoutIcon= (ImageView)findViewById(R.id.tv_header_title1);

        notificationIcon.setImageResource(R.drawable.bell_white);
        logoutIcon.setImageResource(R.drawable.logout);

        TextView mTitle = (TextView) mActionBarToolbar.findViewById(R.id.toolbar_title);
        noInternetLayout = (RelativeLayout) findViewById(R.id.noInternet);
        noInternetTextView = (TextView) findViewById(R.id.noInternetTextView);
        noInternetTextView.setText(Util.getTextofLanguage(MainActivity.this,Util.NO_INTERNET_NO_DATA,Util.DEFAULT_NO_INTERNET_NO_DATA));
        noInternetLayout.setVisibility(View.GONE);

        if (menuList != null && menuList.size() > 0){
            menuList.clear();
        }

        boolean isNetwork = Util.checkNetwork(MainActivity.this);
        if (isNetwork == true ) {
            if (asynLoadMenuItems != null){
                asynLoadMenuItems = null;
            }
            asynLoadMenuItems = new AsynLoadMenuItems();
            asynLoadMenuItems.executeOnExecutor(threadPoolExecutor);

        }else{
            noInternetLayout.setVisibility(View.VISIBLE);
            DrawerLayout dl =  (DrawerLayout) findViewById(R.id.drawer_layout);
            dl.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        }

        notificationIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, NotificationActivity.class);
                startActivity(intent);
            }
        });

        pref = getSharedPreferences(Util.LOGIN_PREF, 0);

        logoutIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(context);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.custom_dialog);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                // Custom Android Allert Dialog Title

                Button dialogButtonCancel = (Button) dialog.findViewById(R.id.customDialogCancel);
                Button dialogButtonOk = (Button) dialog.findViewById(R.id.customDialogOk);

                dialogButtonCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });


                dialogButtonOk.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        LogoutInput logoutInput = new LogoutInput();
                        logoutInput.setAuthToken(Util.authTokenStr);
                        logoutInput.setLogin_history_id( pref.getString("PREFS_LOGIN_HISTORYID_KEY", null));
                        String loginHistoryIdStr = pref.getString("PREFS_LOGIN_HISTORYID_KEY", null);
                        Log.v("pratik","loginHistoryIdStr=="+loginHistoryIdStr);
                        logoutInput.setLang_code(Util.getTextofLanguage(MainActivity.this,Util.SELECTED_LANGUAGE_CODE, Util.DEFAULT_SELECTED_LANGUAGE_CODE));
                        Log.v("pratik","lang_code=="+Util.getTextofLanguage(MainActivity.this,Util.SELECTED_LANGUAGE_CODE, Util.DEFAULT_SELECTED_LANGUAGE_CODE));

                        LogoutAsynctask asynLogoutDetails = new LogoutAsynctask(logoutInput, MainActivity.this, MainActivity.this);
                        asynLogoutDetails.executeOnExecutor(threadPoolExecutor);

                        dialog.dismiss();
                    }
                });

                dialog.show();
            }
        });





    }

    @Override
    public void onDrawerItemSelected(View view, int position) {
        displayView(position);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public void onBackPressed()
    {

        if (asynLoadMenuItems != null){
            asynLoadMenuItems.cancel(true);
        }
        // Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.container_body);
        super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        /*if (id == R.id.action_settings) {
            return true;
        } else {
            switch (id) {
                case R.id.cool_filter:
                    mPublisher.switchCameraFilter(MagicFilterType.COOL);
                    break;
                case R.id.beauty_filter:
                    mPublisher.switchCameraFilter(MagicFilterType.BEAUTY);
                    break;
                case R.id.early_bird_filter:
                    mPublisher.switchCameraFilter(MagicFilterType.EARLYBIRD);
                    break;
                case R.id.evergreen_filter:
                    mPublisher.switchCameraFilter(MagicFilterType.EVERGREEN);
                    break;
                case R.id.n1977_filter:
                    mPublisher.switchCameraFilter(MagicFilterType.N1977);
                    break;
                case R.id.nostalgia_filter:
                    mPublisher.switchCameraFilter(MagicFilterType.NOSTALGIA);
                    break;
                case R.id.romance_filter:
                    mPublisher.switchCameraFilter(MagicFilterType.ROMANCE);
                    break;
                case R.id.sunrise_filter:
                    mPublisher.switchCameraFilter(MagicFilterType.SUNRISE);
                    break;
                case R.id.sunset_filter:
                    mPublisher.switchCameraFilter(MagicFilterType.SUNSET);
                    break;
                case R.id.tender_filter:
                    mPublisher.switchCameraFilter(MagicFilterType.TENDER);
                    break;
                case R.id.toast_filter:
                    mPublisher.switchCameraFilter(MagicFilterType.TOASTER2);
                    break;
                case R.id.valencia_filter:
                    mPublisher.switchCameraFilter(MagicFilterType.VALENCIA);
                    break;
                case R.id.walden_filter:
                    mPublisher.switchCameraFilter(MagicFilterType.WALDEN);
                    break;
                case R.id.warm_filter:
                    mPublisher.switchCameraFilter(MagicFilterType.WARM);
                    break;
                case R.id.original_filter:
                default:
                    mPublisher.switchCameraFilter(MagicFilterType.NONE);
                    break;
            }
        }*/
        setTitle(item.getTitle());

        return super.onOptionsItemSelected(item);
    }

  /*  @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0) {

                    boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;

                    if (cameraAccepted) {
                        //Snackbar.make(view, "Permission Granted, Now you can access camera.", Snackbar.LENGTH_LONG).show();
                    }
                    else {

                        DisplayMetrics displayMetrics = new DisplayMetrics();
                        WindowManager wm = (WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE); // the results will be higher than using the activity context object or the getWindowManager() shortcut
                        wm.getDefaultDisplay().getMetrics(displayMetrics);
                        int screenWidth = displayMetrics.widthPixels;
                        int screenHeight = displayMetrics.heightPixels;
                        Log.v("Subhalaxmi","ongranted resume");
                        mPublisher = new SrsPublisher((SrsCameraView) findViewById(R.id.glsurfaceview_camera));
                        mPublisher.setEncodeHandler(new SrsEncodeHandler(this));
                        mPublisher.setRtmpHandler(new RtmpHandler(this));
                        mPublisher.setRecordHandler(new SrsRecordHandler(this));
                        mPublisher.setPreviewResolution(screenHeight,screenWidth);
//                        mPublisher.setOutputResolution(720, 1280);

                        mPublisher.setOutputResolution(screenWidth, screenHeight);
                        Log.v("pratik","screenHeight="+screenHeight);
                        Log.v("pratik","screenWidth="+screenWidth);
                        mPublisher.setVideoHDMode();

                        mPublisher.startCamera();
                        final ImageView btn = (ImageView) findViewById(R.id.publish);
                        btn.setEnabled(true);
                        mPublisher.resumeRecord();
                        finish();

                    }
                }

                break;
        }
    }*/
    private boolean checkPermissions() {
        int result1 = ContextCompat.checkSelfPermission(getApplicationContext(), CAMERA);

        return result1 == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    protected void onResume() {
        super.onResume();
       /* if (checkPermissions()) {
            DisplayMetrics displayMetrics = new DisplayMetrics();
            WindowManager wm = (WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE); // the results will be higher than using the activity context object or the getWindowManager() shortcut
            wm.getDefaultDisplay().getMetrics(displayMetrics);
            int screenWidth = displayMetrics.widthPixels;
            int screenHeight = displayMetrics.heightPixels;
            Log.v("Subhalaxmi","ongranted resume");
            mPublisher = new SrsPublisher((SrsCameraView) findViewById(R.id.glsurfaceview_camera));
            mPublisher.setEncodeHandler(new SrsEncodeHandler(this));
            mPublisher.setRtmpHandler(new RtmpHandler(this));
            mPublisher.setRecordHandler(new SrsRecordHandler(this));
            mPublisher.setPreviewResolution(screenHeight, screenWidth);
//            mPublisher.setOutputResolution(720, 1280);
            mPublisher.setOutputResolution(screenWidth, screenHeight);
            Log.v("pratik","screenHeight res="+screenHeight);
            Log.v("pratik","screenWidth res="+screenWidth);
            mPublisher.setVideoHDMode();
            mPublisher.startCamera();
            final ImageView btn = (ImageView) findViewById(R.id.publish);
            btn.setEnabled(true);

            mPublisher.resumeRecord();
        }else {
            requestPermissions(new String[]{CAMERA},
                    PERMISSION_REQUEST_CODE);
        }*/


    }
   /* @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case 111: {

                if (grantResults.length > 0) {
                    if ((grantResults.length > 0) && (grantResults[0]) == PackageManager.PERMISSION_GRANTED) {
                        //Call whatever you want

                        if (Util.checkNetwork(this)) {

                            DisplayMetrics displayMetrics = new DisplayMetrics();
                            WindowManager wm = (WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE); // the results will be higher than using the activity context object or the getWindowManager() shortcut
                            wm.getDefaultDisplay().getMetrics(displayMetrics);
                            int screenWidth = displayMetrics.widthPixels;
                            int screenHeight = displayMetrics.heightPixels;
                            Log.v("Subhalaxmi","ongranted");

                            mPublisher = new SrsPublisher((SrsCameraView) findViewById(R.id.glsurfaceview_camera));
                            mPublisher.setEncodeHandler(new SrsEncodeHandler(this));
                            mPublisher.setRtmpHandler(new RtmpHandler(this));
                            mPublisher.setRecordHandler(new SrsRecordHandler(this));
                            mPublisher.setPreviewResolution(screenHeight, screenWidth);
                            mPublisher.setOutputResolution(720, 1280);
                            mPublisher.setVideoHDMode();
                            mPublisher.startCamera();
                            final ImageView btn = (ImageView) findViewById(R.id.publish);
                            btn.setEnabled(true);

                            mPublisher.resumeRecord();
                        } else {
                            Toast.makeText(MainActivity.this,"You declined to allow the app to access your camera",Toast.LENGTH_LONG).show();

                            // Toast.makeText(getApplicationContext(), Util.getTextofLanguage(MovieDetailsActivity.this, Util.NO_INTERNET_CONNECTION, Util.DEFAULT_NO_INTERNET_CONNECTION), Toast.LENGTH_LONG).show();
                            finish();
                        }

                    } else {
                        finish();
                    }
                } else {
                    finish();
                }

                return;
            }
        }
    }*/
  //  @Override
    /*public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {


                    //Start your camera handling here
                } else {
                    Toast.makeText(MainActivity.this,"You declined to allow the app to access your camera",Toast.LENGTH_LONG).show();
                }
        }
    }*/
    @Override
    protected void onPause() {
        super.onPause();
        /*if (mPublisher!=null) {
            mPublisher.pauseRecord();
        }*/
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        /*mPublisher.stopPublish();
        mPublisher.stopRecord();*/
    }

    @Override
    public void onLogoutPreExecuteStarted() {

        Log.v("pratik","logout preexe");

        pDialog = new ProgressBarHandler(MainActivity.this);
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
            Toast.makeText(MainActivity.this, Util.getTextofLanguage(MainActivity.this,Util.SIGN_OUT_ERROR, Util.DEFAULT_SIGN_OUT_ERROR), Toast.LENGTH_LONG).show();

        }
        if (code == 0) {
            Toast.makeText(MainActivity.this, Util.getTextofLanguage(MainActivity.this,Util.SIGN_OUT_ERROR, Util.DEFAULT_SIGN_OUT_ERROR), Toast.LENGTH_LONG).show();

        }

        if (code > 0) {
            if (code == 200) {

                SharedPreferences.Editor editor = pref.edit();
                editor.clear();
                editor.commit();

                Log.v("pratik","is_one_step==="+Util.getTextofLanguage(MainActivity.this,Util.IS_ONE_STEP_REGISTRATION, Util.DEFAULT_IS_ONE_STEP_REGISTRATION));
                if ((Util.getTextofLanguage(MainActivity.this,Util.IS_ONE_STEP_REGISTRATION, Util.DEFAULT_IS_ONE_STEP_REGISTRATION)
                        .trim()).equals("1")) {
                    final Intent startIntent = new Intent(MainActivity.this, SplashActivity.class);
                    runOnUiThread(new Runnable() {
                        public void run() {
                            startIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                            startActivity(startIntent);
                            Toast.makeText(MainActivity.this, Util.getTextofLanguage(MainActivity.this,Util.LOGOUT_SUCCESS, Util.DEFAULT_LOGOUT_SUCCESS), Toast.LENGTH_LONG).show();

                            finish();

                        }
                    });
                } else {
                    final Intent startIntent = new Intent(MainActivity.this, MainActivity.class);
                    runOnUiThread(new Runnable() {
                        public void run() {
                            startIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                            startActivity(startIntent);
                            Toast.makeText(MainActivity.this, Util.getTextofLanguage(MainActivity.this,Util.LOGOUT_SUCCESS, Util.DEFAULT_LOGOUT_SUCCESS), Toast.LENGTH_LONG).show();

                            finish();

                        }
                    });
                }


            } else {
                Toast.makeText(MainActivity.this, Util.getTextofLanguage(MainActivity.this,Util.SIGN_OUT_ERROR, Util.DEFAULT_SIGN_OUT_ERROR), Toast.LENGTH_LONG).show();

            }
        }
    }


    //*****commented as BroadcasterActivity created separately**////

   /* private void handleException(Exception e) {
        try {
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            mPublisher.stopPublish();
            mPublisher.stopRecord();
            // Icon should be changed
        } catch (Exception e1) {
        }
    }

    // Implementation of SrsRtmpListener.

    @Override
    public void onRtmpConnecting(String msg) {
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRtmpConnected(String msg) {
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRtmpVideoStreaming() {
    }

    @Override
    public void onRtmpAudioStreaming() {
    }

    @Override
    public void onRtmpStopped() {
        Toast.makeText(getApplicationContext(), "Stopped", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRtmpDisconnected() {
        Toast.makeText(getApplicationContext(), "Disconnected", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRtmpVideoFpsChanged(double fps) {
        Log.i(TAG, String.format("Output Fps: %f", fps));
    }

    @Override
    public void onRtmpVideoBitrateChanged(double bitrate) {
        int rate = (int) bitrate;
        if (rate / 1000 > 0) {
            Log.i(TAG, String.format("Video bitrate: %f kbps", bitrate / 1000));
        } else {
            Log.i(TAG, String.format("Video bitrate: %d bps", rate));
        }
    }

    @Override
    public void onRtmpAudioBitrateChanged(double bitrate) {
        int rate = (int) bitrate;
        if (rate / 1000 > 0) {
            Log.i(TAG, String.format("Audio bitrate: %f kbps", bitrate / 1000));
        } else {
            Log.i(TAG, String.format("Audio bitrate: %d bps", rate));
        }
    }

    @Override
    public void onRtmpSocketException(SocketException e) {
        handleException(e);
    }

    @Override
    public void onRtmpIOException(IOException e) {
        handleException(e);
    }

    @Override
    public void onRtmpIllegalArgumentException(IllegalArgumentException e) {
        handleException(e);
    }

    @Override
    public void onRtmpIllegalStateException(IllegalStateException e) {
        handleException(e);
    }

    // Implementation of SrsRecordHandler.

    @Override
    public void onRecordPause() {
        Toast.makeText(getApplicationContext(), "Record paused", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRecordResume() {
        Toast.makeText(getApplicationContext(), "Record resumed", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRecordStarted(String msg) {
        Toast.makeText(getApplicationContext(), "Recording file: " + msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRecordFinished(String msg) {
        Toast.makeText(getApplicationContext(), "MP4 file saved: " + msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRecordIOException(IOException e) {
        handleException(e);
    }

    @Override
    public void onRecordIllegalArgumentException(IllegalArgumentException e) {
        handleException(e);
    }

    // Implementation of SrsEncodeHandler.

    @Override
    public void onNetworkWeak() {
        Toast.makeText(getApplicationContext(), "Network weak", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onNetworkResume() {
        Toast.makeText(getApplicationContext(), "Network resume", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onEncodeIllegalArgumentException(IllegalArgumentException e) {
        handleException(e);
    }*/


    /////AsynLoadmenuItems
    private class AsynLoadMenuItems extends AsyncTask<Void, Void, Void> {
        String responseStr;
        int statusCode;
        String permalink = null;
        String displayName = null;
        String menuLinkType= null;
        @Override
        protected Void doInBackground(Void... params) {
            try {
                HttpClient httpclient=new DefaultHttpClient();
                HttpPost httppost = new HttpPost(Util.rootUrl().trim()+Util.loadMenuUrl.trim());
                httppost.setHeader(HTTP.CONTENT_TYPE, "application/x-www-form-urlencoded;charset=UTF-8");
                httppost.addHeader("authToken", Util.authTokenStr.trim());
                SharedPreferences countryPref = getSharedPreferences(Util.COUNTRY_PREF, 0); // 0 - for private mode
                if (countryPref != null) {
                    String countryCodeStr = countryPref.getString("countryCode", null);
                    httppost.addHeader("country", countryCodeStr);
                }else{
                    httppost.addHeader("country", "IN");

                }
                httppost.addHeader("lang_code",Util.getTextofLanguage(MainActivity.this,Util.SELECTED_LANGUAGE_CODE,Util.DEFAULT_SELECTED_LANGUAGE_CODE));

                // Execute HTTP Post Request
                try {

                    HttpResponse response = httpclient.execute(httppost);
                    responseStr = EntityUtils.toString(response.getEntity());


                } catch (org.apache.http.conn.ConnectTimeoutException e){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (internetSpeedDialog != null && internetSpeedDialog.isShowing()) {
                                internetSpeedDialog.hide();
                                internetSpeedDialog = null;

                            }
                            responseStr = "0";
                            menuList = null;

                            noInternetLayout.setVisibility(View.VISIBLE);
                            DrawerLayout dl = (DrawerLayout) findViewById(R.id.drawer_layout);
                            dl.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
                        }

                    });

                }catch (IOException e) {
                    if (internetSpeedDialog != null && internetSpeedDialog.isShowing()) {
                        internetSpeedDialog.hide();
                        internetSpeedDialog = null;
                    }
                    responseStr = "0";
                    menuList = null;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            noInternetLayout.setVisibility(View.VISIBLE);
                            DrawerLayout dl = (DrawerLayout) findViewById(R.id.drawer_layout);
                            dl.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);

                        }
                    });
                    e.printStackTrace();
                }

                JSONObject myJson =null;
                if(responseStr!=null){
                    myJson = new JSONObject(responseStr);
                    statusCode = Integer.parseInt(myJson.optString("code"));
                }

                if (statusCode > 0) {
                    if (statusCode == 200) {
                        menuList = new ArrayList<NavDrawerItem>();
                        JSONArray jsonMainNode = myJson.getJSONArray("menu");
                        JSONArray jsonFooterNode = myJson.getJSONArray("footer_menu");
                        int jsonFooterNodeArr = jsonFooterNode.length();

                        int lengthJsonArr = jsonMainNode.length();
                        for(int i=0; i < lengthJsonArr; i++) {
                            JSONObject jsonChildNode;
                            try {
                                jsonChildNode = jsonMainNode.getJSONObject(i);

                                if ((jsonChildNode.has("display_name")) && jsonChildNode.getString("display_name").trim() != null && !jsonChildNode.getString("display_name").trim().isEmpty() && !jsonChildNode.getString("display_name").trim().equals("null") && !jsonChildNode.getString("display_name").trim().matches("")) {
                                    displayName = jsonChildNode.getString("display_name");

                                }


                                if ((jsonChildNode.has("link_type")) && jsonChildNode.getString("link_type").trim() != null && !jsonChildNode.getString("link_type").trim().isEmpty() && !jsonChildNode.getString("link_type").trim().equals("null") && !jsonChildNode.getString("link_type").trim().matches("")) {
                                    menuLinkType = jsonChildNode.getString("link_type");

                                }else{
                                    menuLinkType = "0";
                                }

                                if (menuLinkType!=null && !menuLinkType.equalsIgnoreCase("")) {
                                    if ((jsonChildNode.has("permalink")) && jsonChildNode.getString("permalink").trim() != null && !jsonChildNode.getString("permalink").trim().isEmpty() && !jsonChildNode.getString("permalink").trim().equals("null") && !jsonChildNode.getString("permalink").trim().matches("")) {
                                        permalink = jsonChildNode.getString("permalink");
                                    }
                                }
                                if (menuLinkType!=null && !menuLinkType.equalsIgnoreCase("") && menuLinkType.equalsIgnoreCase("0")) {

                                    ////////drawer item is not added
//                                    menuList.add(new NavDrawerItem(displayName, permalink,true,menuLinkType));
                                }




                            } catch (Exception e) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {

                                        noInternetLayout.setVisibility(View.VISIBLE);
                                        DrawerLayout dl = (DrawerLayout) findViewById(R.id.drawer_layout);
                                        dl.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);

                                    }
                                });
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                        }

                        menuList.add(new NavDrawerItem(Util.getTextofLanguage(MainActivity.this,Util.MY_LIBRARY,Util.DEFAULT_MY_LIBRARY),"102",true,"102"));


                        /*** footer menu******/
                        for(int i=0; i < jsonFooterNodeArr; i++) {
                            JSONObject jsonChildNode;
                            try {
                                jsonChildNode = jsonFooterNode.getJSONObject(i);

                                if ((jsonChildNode.has("display_name")) && jsonChildNode.getString("display_name").trim() != null && !jsonChildNode.getString("display_name").trim().isEmpty() && !jsonChildNode.getString("display_name").trim().equals("null") && !jsonChildNode.getString("display_name").trim().matches("")) {
                                    displayName = jsonChildNode.getString("display_name");

                                }
                                if ((jsonChildNode.has("url")) && jsonChildNode.getString("url").trim() != null && !jsonChildNode.getString("url").trim().isEmpty() && !jsonChildNode.getString("url").trim().equals("null") && !jsonChildNode.getString("url").trim().matches("")) {
                                    menuLinkType = jsonChildNode.getString("url");

                                }
                                if ((jsonChildNode.has("permalink")) && jsonChildNode.getString("permalink").trim() != null && !jsonChildNode.getString("permalink").trim().isEmpty() && !jsonChildNode.getString("permalink").trim().equals("null") && !jsonChildNode.getString("permalink").trim().matches("")) {
                                    permalink = jsonChildNode.getString("permalink");
                                }
                                if (menuLinkType!=null && !menuLinkType.equalsIgnoreCase("")) {

                                    if (permalink.equals("contactus")) {
//                                        menuList.add(new NavDrawerItem(displayName, permalink, false, menuLinkType));
                                    }
                                }
                            }
                            catch (Exception e) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {

                                        noInternetLayout.setVisibility(View.VISIBLE);
                                        DrawerLayout dl = (DrawerLayout) findViewById(R.id.drawer_layout);
                                        dl.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);

                                    }
                                });
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }

                        }

                        /**********footer**********/

                    }
                }
                else {

                    responseStr = "0";
                    menuList = null;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            noInternetLayout.setVisibility(View.VISIBLE);
                            DrawerLayout dl = (DrawerLayout) findViewById(R.id.drawer_layout);
                            dl.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);

                        }
                    });
                }
            } catch (JSONException e1) {

                if (internetSpeedDialog != null && internetSpeedDialog.isShowing()) {
                    internetSpeedDialog.hide();
                    internetSpeedDialog = null;
                }
                responseStr = "0";
                menuList = null;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        noInternetLayout.setVisibility(View.VISIBLE);
                        DrawerLayout dl = (DrawerLayout) findViewById(R.id.drawer_layout);
                        dl.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);

                    }
                });
                e1.printStackTrace();
            }

            catch (Exception e)

            {

                if (internetSpeedDialog != null && internetSpeedDialog.isShowing()) {
                    internetSpeedDialog.hide();
                    internetSpeedDialog = null;

                }
                responseStr = "0";
                menuList = null;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        noInternetLayout.setVisibility(View.VISIBLE);
                        DrawerLayout dl = (DrawerLayout) findViewById(R.id.drawer_layout);
                        dl.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);

                    }
                });
                e.printStackTrace();

            }
            return null;

        }

        protected void onPostExecute(Void result) {


            try{

            }
            catch(IllegalArgumentException ex)
            {

                responseStr = "0";
                menuList = null;

                noInternetLayout.setVisibility(View.VISIBLE);
                DrawerLayout dl = (DrawerLayout) findViewById(R.id.drawer_layout);
                dl.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);

            }

            if(responseStr == null) {

                responseStr = "0";
                menuList = null;

                noInternetLayout.setVisibility(View.VISIBLE);
                DrawerLayout dl = (DrawerLayout) findViewById(R.id.drawer_layout);
                dl.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);

            }

            if ((responseStr.trim().equalsIgnoreCase("0"))){
                menuList = null;

                noInternetLayout.setVisibility(View.VISIBLE);
                DrawerLayout dl = (DrawerLayout) findViewById(R.id.drawer_layout);
                dl.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);



            }else{
                if(menuList!=null && menuList.size()>0){


                    menuList.add(0,new NavDrawerItem(Util.getTextofLanguage(MainActivity.this, Util.HOME, Util.DEFAULT_HOME),"-101",true,"-101"));
                    //  menuList.add(menuList.size()-2,new NavDrawerItem(Util.getTextofLanguage(MainActivity.this,Util.MY_LIBRARY,Util.DEFAULT_MY_LIBRARY),"102",true,"102"));

                    // menuList.add(new NavDrawerItem("Home", "",true,"0"));
                   /* menuList.add(new NavDrawerItem("Terms", "",false,"-1"));
                    menuList.add(new NavDrawerItem("About", "",false,"-1"));



*/
                    /////menuitem added Statically
                    menuList.add(new NavDrawerItem("Subscribed", "Subscribed", true, "88"));
                    menuList.add(new NavDrawerItem("Following", "Following", true, "88"));
                    menuList.add(new NavDrawerItem("Recommended", "Recommended", true, "88"));
                    menuList.add(new NavDrawerItem("Trending", "Trending", true, "88"));


                    boolean isNetwork = Util.checkNetwork(MainActivity.this);
                    imageUrlStr = "https://dadc-muvi.s3-eu-west-1.amazonaws.com/check-download-speed.jpg";
                    if (isNetwork == true) {

                        new Thread(mWorker).start();
                    }else{
                        internetSpeed = "0";
                    }
                    if (internetSpeedDialog != null && internetSpeedDialog.isShowing()) {
                        internetSpeedDialog.hide();
                        internetSpeedDialog = null;

                    }
                    drawerFragment = (FragmentDrawer)
                            getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);
                    drawerFragment.setUp(R.id.fragment_navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout), mActionBarToolbar);
                    drawerFragment.setDrawerListener(MainActivity.this);
                    displayView(0);

                  /*  as = new AsynLoadImageUrls();
                    as.executeOnExecutor(threadPoolExecutor);*/
                }else{

                    noInternetLayout.setVisibility(View.VISIBLE);
                    DrawerLayout dl = (DrawerLayout) findViewById(R.id.drawer_layout);
                    dl.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
                }
            }
        }

        @Override
        protected void onPreExecute() {
            try {
             /*   internetSpeedDialog = new ProgressDialog(MainActivity.this);
                internetSpeedDialog.setMessage(getResources().getString(R.string.loading_str));
                internetSpeedDialog.setIndeterminate(false);
                internetSpeedDialog.setCancelable(false);
                internetSpeedDialog.show();*/

                internetSpeedDialog = new ProgressBarHandler(MainActivity.this);
                internetSpeedDialog.show();


            }catch (IllegalArgumentException ex){

                noInternetLayout.setVisibility(View.VISIBLE);
                DrawerLayout dl =  (DrawerLayout) findViewById(R.id.drawer_layout);
                dl.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
            }
         /*  if (internetSpeedDialog == null || !internetSpeedDialog.isShowing()){
               internetSpeedDialog = new ProgressDialog(MainActivity.this);
               internetSpeedDialog.setMessage(getResources().getString(R.string.loading_str));
               internetSpeedDialog.setIndeterminate(false);
               internetSpeedDialog.setCancelable(false);
               internetSpeedDialog.show();
           }*/
        }


    }

    private void displayView(int position) {

        isNavigated = 1;

        String title = getString(R.string.app_name);
     /*   SharedPreferences.Editor dataEditor = dataPref.edit();*/
        Bundle bundle = new Bundle();
        String str = menuList.get(position).getPermalink();
        String titleStr = menuList.get(position).getTitle();
        Log.v("pratik","permalink=="+str);
        // state = position;

        if (internetSpeedDialog!=null && internetSpeedDialog.isShowing()){
            internetSpeedDialog.hide();
            internetSpeedDialog = null;

        }
        if (pDialog!=null && pDialog.isShowing()){
            pDialog.hide();
            pDialog = null;

        }
        if (str !=null && !str.equalsIgnoreCase("") && !str.isEmpty() && menuList.get(position).getLinkType().equalsIgnoreCase("-101")){

            Log.v("SUBHA","home fragment");
            fragment = new HomeFragment();
            bundle.putString("item", str);


        }
        else if (menuList.get(position).getLinkType().equalsIgnoreCase("102")){

//            fragment = new MyLibraryFragment();
            bundle.putString("title",titleStr);

        }
        else if (menuList.get(position).getIsEnabled() == false){

            if(str.equals("contactus"))
            {

//                fragment = new ContactUs();
                bundle.putString("title",titleStr);


            }
            else{


//                fragment = new AboutUs();
                bundle.putString("item",str);
                bundle.putString("title",titleStr);

            }


        }
        else if (menuList.get(position).getIsEnabled() == true) {
            Log.v("SUBHA","video fragment");
            fragment = new CategoryActivity();
            bundle.putString("item", str);
            Log.v("pratik","titleStr="+titleStr);
            bundle.putString("title",titleStr);


        }
       /* else if (menuList.get(position).getIsEnabled() == false) {

            fragment = new WebViewFragment();
            bundle.putString("item", getResources().getString(R.string.studio_site)+str);

        }*/
        fragment.setArguments(bundle);

      /*  dataEditor.putString("state", String.valueOf(state));
                dataEditor.commit();*/



        if (fragment != null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.container_body, fragment);
            View view = this.getCurrentFocus();
            if (view != null) {
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
            fragmentTransaction.commitAllowingStateLoss();


            // set the toolbar title
            getSupportActionBar().setTitle(title);
        }
    }

    private final Runnable mWorker=new Runnable(){
        @Override
        public void run() {
            InputStream stream = null;

            try {
                int bytesIn = 0;
                boolean isNetwork = Util.checkNetwork(MainActivity.this);
                String downloadFileUrl = imageUrlStr;
                long startCon = System.currentTimeMillis();
                URL url = new URL(downloadFileUrl);
                URLConnection con = url.openConnection();
                con.setUseCaches(false);
                long connectionLatency = System.currentTimeMillis() - startCon;
                stream = con.getInputStream();

                Message msgUpdateConnection = Message.obtain(mHandler, MSG_UPDATE_CONNECTION_TIME);
                msgUpdateConnection.arg1 = (int) connectionLatency;
                mHandler.sendMessage(msgUpdateConnection);

                long start = System.currentTimeMillis();
                int currentByte = 0;
                long updateStart = System.currentTimeMillis();
                long updateDelta = 0;
                int bytesInThreshold = 0;

                while ((currentByte = stream.read()) != -1) {
                    bytesIn++;
                    bytesInThreshold++;

                    if (updateDelta >= UPDATE_THRESHOLD) {
                        int progress = (int) ((bytesIn / (double) EXPECTED_SIZE_IN_BYTES) * 100);

                        Message msg = Message.obtain(mHandler, MSG_UPDATE_STATUS, calculate(updateDelta, bytesInThreshold));
                        msg.arg1 = progress;
                        msg.arg2 = bytesIn;
                        mHandler.sendMessage(msg);
                        //Reset
                        updateStart = System.currentTimeMillis();
                        bytesInThreshold = 0;
                    }
                    updateDelta = System.currentTimeMillis() - updateStart;
                }

                long downloadTime = (System.currentTimeMillis() - start);
                //Prevent AritchmeticException
                if (downloadTime == 0) {
                    downloadTime = 1;
                }

                Message msg = Message.obtain(mHandler, MSG_COMPLETE_STATUS, calculate(downloadTime, bytesIn));
                msg.arg1 = bytesIn;
                mHandler.sendMessage(msg);
            }
            catch(MalformedURLException e){

                internetSpeed = "0";
            }catch(IOException e){

                internetSpeed = "0";

            }finally{
                try {
                    if (stream != null) {
                        stream.close();

                    }
                } catch (IOException e) {
                    //Suppressed
                    internetSpeed = "0";
                }
            }



        }

    };

    private SpeedInfo calculate(final long downloadTime, final long bytesIn){
        SpeedInfo info=new SpeedInfo();
        //from mil to sec
        long bytespersecond   =(bytesIn / downloadTime) * 1000;
        double kilobits=bytespersecond * BYTE_TO_KILOBIT;
        double megabits=kilobits  * KILOBIT_TO_MEGABIT;
        info.downspeed=bytespersecond;
        info.kilobits=kilobits;
        info.megabits=megabits;

        return info;
    }

    private final Handler mHandler=new Handler(){
        @Override
        public void handleMessage(final Message msg) {
            boolean isNetwork = Util.checkNetwork(MainActivity.this);
            if (isNetwork == true) {
                switch (msg.what) {
                    case MSG_UPDATE_STATUS:
                        break;
                    case MSG_UPDATE_CONNECTION_TIME:

                        break;
                    case MSG_COMPLETE_STATUS:
                        final SpeedInfo info2 = (SpeedInfo) msg.obj;
                        String downloadedSpeed = String.format("%.1f", info2.megabits);
                        internetSpeed = downloadedSpeed;

                        break;
                    default:
                        internetSpeed = "0";

                        super.handleMessage(msg);
                }
            }else{
                internetSpeed = "0";

            }
        }
    };

    private static class SpeedInfo{
        public double kilobits=0;
        public double megabits=0;
        public double downspeed=0;
    }

    //Private fields
//    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int EXPECTED_SIZE_IN_BYTES = 1048576;//1MB 1024*1024

    private static final double EDGE_THRESHOLD = 176.0;
    private static final double BYTE_TO_KILOBIT = 0.0078125;
    private static final double KILOBIT_TO_MEGABIT = 0.0009765625;

    private final int MSG_UPDATE_STATUS=0;
    private final int MSG_UPDATE_CONNECTION_TIME=1;
    private final int MSG_COMPLETE_STATUS=2;

    private final static int UPDATE_THRESHOLD=300;


    private DecimalFormat mDecimalFormater;


    /////*********for Broadcast***/////////
    private class DownloadJSON extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Create a progressdialog
            mProgressDialog = new ProgressDialog(MainActivity.this);
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
            String urlRouteList = Util.rootUrl().trim() + Util.assignBroadCastToContent.trim();
            Log.v("SUBHA","JJ"+category_idd+"USWR"+idd+GetEditText);
            try {
                HttpClient httpclient = new DefaultHttpClient();
                HttpPost httppost = new HttpPost(urlRouteList);
                httppost.setHeader(HTTP.CONTENT_TYPE, "application/x-www-form-urlencoded;charset=UTF-8");

                httppost.addHeader("authToken", Util.authTokenStr.trim());
                httppost.addHeader("category_id", category_idd);
                httppost.addHeader("user_id", idd);
                httppost.addHeader("content_name",GetEditText);

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
                            Toast.makeText(MainActivity.this, Util.getTextofLanguage(MainActivity.this, Util.SLOW_INTERNET_CONNECTION, Util.DEFAULT_SLOW_INTERNET_CONNECTION), Toast.LENGTH_LONG).show();

                        }

                    });

                } catch (IOException e) {
                    statusCode = 0;

                    e.printStackTrace();
                }

                Log.v("SUBHA", "response = " + responseStr);
                if (responseStr != null) {
                    myJson = new JSONObject(responseStr);
                    statusCode = Integer.parseInt(myJson.optString("code"));

                    if (statusCode == 200) {
                        Log.v("SUBHA", "rtmpUrlSD" + myJson.optString("feed_url"));

                        Log.v("SUBHA", "rtmpUrl" + myJson.optString("feed_url").replace("\"\"", ""));

                        rtmpUrl = myJson.optString("feed_url").replace("\"\"", "");
                        Log.v("SUBHA", "rtmpUrldf" + rtmpUrl);

                    }

                }

            } catch (Exception e) {
                statusCode = 0;

            }

            return null;
        }

        private Socket mSocket;
        {
            try {
                mSocket = IO.socket("http://chat.socket.io");
            } catch (URISyntaxException e) {}
        }
        @Override
        protected void onPostExecute(Void args) {
            adapter = new ContentListAdapter(MainActivity.this, arraylist2);
            mProgressDialog.dismiss();
        }
    }

    @Override
    protected void onUserLeaveHint() {
        super.onUserLeaveHint();
        if (isNavigated == 0){
            if (internetSpeedDialog != null && internetSpeedDialog.isShowing()) {
                internetSpeedDialog.hide();
                internetSpeedDialog = null;

            }

            /*ActivityCompat.finishAffinity(this);
            finish();
            System.exit(0);*/
        }
    }



}