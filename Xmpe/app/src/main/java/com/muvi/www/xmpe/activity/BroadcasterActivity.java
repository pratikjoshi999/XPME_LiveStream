package com.muvi.www.xmpe.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
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
import com.muvi.www.xmpe.R;
import com.muvi.www.xmpe.Util;
import com.muvi.www.xmpe.content.ContentListAdapter;
import com.muvi.www.xmpe.content.ContentListModel;
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
import org.json.JSONObject;

import java.io.IOException;
import java.net.SocketException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Random;

import static android.Manifest.permission.CAMERA;

public class BroadcasterActivity extends AppCompatActivity implements RtmpHandler.RtmpListener, SrsRecordHandler.SrsRecordListener, SrsEncodeHandler.SrsEncodeListener {

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

    private SharedPreferences sp;
    private String rtmpUrl;
    private String recPath = Environment.getExternalStorageDirectory().getPath() + "/test.mp4";

    private SrsPublisher mPublisher;

    //**Chat**//
    LinearLayout chatLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_broadcaster);

        // restore data.
        sp = getSharedPreferences("XPME", MODE_PRIVATE);
        rtmpUrl = sp.getString("rtmpUrl", rtmpUrl);
        idd = getSharedPreferences(Util.LOGIN_PREF, MODE_PRIVATE).getString("PREFS_LOGGEDIN_ID_KEY", null);

        category_idd=sp.getString("category_id",null);

        createStreamButton = (Button)findViewById(R.id.createStreamButton);
        descriptionInput = (EditText)findViewById(R.id.descriptionInput);
        titleInput = (EditText)findViewById(R.id.titleInput);
        ImageView iv1 = (ImageView) findViewById(R.id.tv_header_title2);
        iv1.setImageResource(R.drawable.backbutton);
        btnPublish = (ImageView) findViewById(R.id.publish);
        btnPublish1 = (ImageView) findViewById(R.id.publish1);
        btnSwitchCamera = (ImageView) findViewById(R.id.swCam);
        final TextInputLayout usernameWrapper = (TextInputLayout) findViewById(R.id.titleWrapper);
        btnRecord = (ImageView) findViewById(R.id.record);
//        chatLayout = (LinearLayout) findViewById(R.id.chatLayout);
        mActionBarToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mActionBarToolbar);
        mActionBarToolbar.setTitle("");

        TextView mTitle = (TextView) mActionBarToolbar.findViewById(R.id.toolbar_title);
        mTitle.setText("Live Stream");

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

        CatagoryName = getIntent().getStringArrayListExtra("category_name_array");
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

        createStreamButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

                GetEditText = titleInput.getText().toString();

                if (GetEditText.isEmpty() || GetEditText.length() == 0 || GetEditText.equals("") || GetEditText == null) {

                    Toast.makeText(BroadcasterActivity.this, "Please Enter Title", Toast.LENGTH_LONG).show();

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
                /*SharedPreferences.Editor editor = sp.edit();
                editor.putString("rtmpUrl", rtmpUrl);
                editor.apply();*/
//                    chatLayout.setVisibility(View.VISIBLE);
                Log.v("pratik","rtmpUrl=="+rtmpUrl);
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
        });


    }

    private class DownloadJSON extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Create a progressdialog
            mProgressDialog = new ProgressDialog(BroadcasterActivity.this);
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

                Log.v("pratik","idd category_idd asyn=="+category_idd);
                Log.v("pratik"," user_id asyn=="+idd);

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
                            Toast.makeText(BroadcasterActivity.this, Util.getTextofLanguage(BroadcasterActivity.this, Util.SLOW_INTERNET_CONNECTION, Util.DEFAULT_SLOW_INTERNET_CONNECTION), Toast.LENGTH_LONG).show();

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
            adapter = new ContentListAdapter(BroadcasterActivity.this, arraylist2);
            mProgressDialog.dismiss();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
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
        }
        setTitle(item.getTitle());

        return super.onOptionsItemSelected(item);
    }

    @Override
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
    }

    private boolean checkPermissions() {
        int result1 = ContextCompat.checkSelfPermission(getApplicationContext(), CAMERA);

        return result1 == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (checkPermissions()) {
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
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mPublisher!=null) {
            mPublisher.pauseRecord();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPublisher.stopPublish();
        mPublisher.stopRecord();
    }

    private static String getRandomAlphaString(int length) {
        String base = "abcdefghijklmnopqrstuvwxyz";
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            int number = random.nextInt(base.length());
            sb.append(base.charAt(number));
        }
        return sb.toString();
    }

    private static String getRandomAlphaDigitString(int length) {
        String base = "abcdefghijklmnopqrstuvwxyz0123456789";
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            int number = random.nextInt(base.length());
            sb.append(base.charAt(number));
        }
        return sb.toString();
    }

    private void handleException(Exception e) {
        try {
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            mPublisher.stopPublish();
            mPublisher.stopRecord();
            // Icon should be changed
        } catch (Exception e1) {
        }
    }

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
    public void onRecordIllegalArgumentException(IllegalArgumentException e) {
        handleException(e);
    }

    @Override
    public void onRecordIOException(IOException e) {
        handleException(e);
    }
}
