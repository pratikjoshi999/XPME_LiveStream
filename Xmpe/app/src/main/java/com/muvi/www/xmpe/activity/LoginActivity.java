package com.muvi.www.xmpe.activity;

/**
 * Created by MUVI on 5/12/2017.
 */

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.muvi.www.xmpe.ProgressBarHandler;
import com.muvi.www.xmpe.R;
import com.muvi.www.xmpe.Util;
import com.muvi.www.xmpe.categorylist.CategoryActivity;
import com.muvi.www.xmpe.chat.ChatApplication;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import io.socket.client.Socket;


public class LoginActivity extends AppCompatActivity {


    AsynLogInDetails asyncReg;
    EditText editEmailStr, editPasswordStr;
    TextView forgotPassword, loginNewUser, signUpTextView;
    Button loginButton;
    int corePoolSize = 60;
    int maximumPoolSize = 80;
    int keepAliveTime = 10;
    SharedPreferences sp,pref;
    private Socket mSocket;
    String regEmailStr, regPasswordStr, display_name;
    Toolbar mActionBarToolbar;
    BlockingQueue<Runnable> workQueue = new LinkedBlockingQueue<Runnable>(maximumPoolSize);
    Executor threadPoolExecutor = new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, TimeUnit.SECONDS, workQueue);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_login);

        ChatApplication app = (ChatApplication) getApplication();
        mSocket = app.getSocket();

        mActionBarToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mActionBarToolbar);


//        setSupportActionBar(mActionBarToolbar);
//        mActionBarToolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_arrow_back));
//        mActionBarToolbar.setNavigationOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                onBackPressed();
//            }
//        });

        editEmailStr = (EditText) findViewById(R.id.editEmailStr);

        editEmailStr.setHint(Util.getTextofLanguage(LoginActivity.this, Util.TEXT_EMIAL, Util.DEFAULT_TEXT_EMIAL));


        editPasswordStr = (EditText) findViewById(R.id.editPasswordStr);
        editPasswordStr.setHint(Util.getTextofLanguage(LoginActivity.this, Util.TEXT_PASSWORD, Util.DEFAULT_TEXT_PASSWORD));


        forgotPassword = (TextView) findViewById(R.id.forgotPasswordTextView);

        forgotPassword.setText(Util.getTextofLanguage(LoginActivity.this, Util.FORGOT_PASSWORD, Util.DEFAULT_FORGOT_PASSWORD));


       // loginNewUser = (TextView) findViewById(R.id.loginNewUser);

       // signUpTextView = (TextView) findViewById(R.id.signUpTextView);

//        signUpTextView.setText(Util.getTextofLanguage(LoginActivity.this, Util.SIGN_UP_TITLE, Util.DEFAULT_SIGN_UP_TITLE));


        loginButton = (Button) findViewById(R.id.loginButton);


        loginButton.setText(Util.getTextofLanguage(LoginActivity.this, Util.LOGIN, Util.DEFAULT_LOGIN));
//        mActionBarToolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_arrow_back));
//        mActionBarToolbar.setNavigationOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                onBackPressed();
//            }
//        });
        sp = getSharedPreferences("XPME",MODE_PRIVATE);
        pref = getSharedPreferences(Util.LOGIN_PREF,MODE_PRIVATE);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                loginButtonClicked();
            }
        });

        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Intent detailsIntent = new Intent(LoginActivity.this, ForgetPasswordActivity.class);
                detailsIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(detailsIntent);
                finish();
            }
        });

        ImageView img= (ImageView) findViewById(R.id.back);
        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

//        signUpTextView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                final Intent detailsIntent = new Intent(LoginActivity.this, RegisterActivity.class);
//                detailsIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
//                startActivity(detailsIntent);
//                finish();
            }
       // });

      /*  loginNewUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Intent detailsIntent = new Intent(LoginActivity.this, PurchaseHistoryActivity.class);
                detailsIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(detailsIntent);
            }
        });*/
 //   }

    public void loginButtonClicked() {

        regEmailStr = editEmailStr.getText().toString().trim();
        regPasswordStr = editPasswordStr.getText().toString().trim();

        boolean isNetwork = Util.checkNetwork(LoginActivity.this);
        if (isNetwork) {
            if ((!regEmailStr.equals("")) && (!regPasswordStr.equals(""))) {
                boolean isValidEmail = Util.isValidMail(regEmailStr);
                if (isValidEmail == true) {
                    asyncReg = new AsynLogInDetails();
                    asyncReg.executeOnExecutor(threadPoolExecutor);
                } else {
                    Toast.makeText(LoginActivity.this, Util.getTextofLanguage(LoginActivity.this, Util.OOPS_INVALID_EMAIL, Util.DEFAULT_OOPS_INVALID_EMAIL), Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(LoginActivity.this, Util.getTextofLanguage(LoginActivity.this, Util.ENTER_REGISTER_FIELDS_DATA, Util.DEFAULT_ENTER_REGISTER_FIELDS_DATA), Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(LoginActivity.this, Util.getTextofLanguage(LoginActivity.this, Util.NO_INTERNET_CONNECTION, Util.DEFAULT_NO_INTERNET_CONNECTION), Toast.LENGTH_LONG).show();
        }

    }

    private class AsynLogInDetails extends AsyncTask<Void, Void, Void> {
        ProgressBarHandler pDialog;
        int statusCode;
        String loggedInIdStr;
        String responseStr;
        String isSubscribedStr;
        String loginHistoryIdStr,id;

        JSONObject myJson = null;

        @Override
        protected Void doInBackground(Void... params) {


//            ArrayList<NameValuePair> cred = new ArrayList<NameValuePair>();
//            cred.add(new BasicNameValuePair("email", regEmailStr));
//            cred.add(new BasicNameValuePair("password", regPasswordStr));
//            cred.add(new BasicNameValuePair("authToken", Util.authTokenStr.trim()));

            String urlRouteList = Util.rootUrl().trim() + Util.loginUrl.trim();
            try {
                HttpClient httpclient = new DefaultHttpClient();
                HttpPost httppost = new HttpPost(urlRouteList);
                httppost.setHeader(HTTP.CONTENT_TYPE, "application/x-www-form-urlencoded;charset=UTF-8");
                httppost.addHeader("password", regPasswordStr);
                httppost.addHeader("display_name", display_name);
                httppost.addHeader("email", regEmailStr);
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


                } catch (org.apache.http.conn.ConnectTimeoutException e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            statusCode = 0;
                            //Crouton.showText(ShowWithEpisodesListActivity.this, "Slow Internet Connection", Style.INFO);
                            Toast.makeText(LoginActivity.this, Util.getTextofLanguage(LoginActivity.this, Util.SLOW_INTERNET_CONNECTION, Util.DEFAULT_SLOW_INTERNET_CONNECTION), Toast.LENGTH_LONG).show();

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

                    //userIdStr = myJson.optString("status");
                    loggedInIdStr = myJson.optString("id");
                    isSubscribedStr = myJson.optString("isSubscribed");
                    loginHistoryIdStr = myJson.optString("login_history_id");


                }

            } catch (Exception e) {
                statusCode = 0;

            }

            return null;
        }


        protected void onPostExecute(Void result) {

            if (responseStr == null) {
                try {
                    if (pDialog != null && pDialog.isShowing()) {
                        pDialog.hide();
                        pDialog = null;
                    }
                } catch (IllegalArgumentException ex) {
                    statusCode = 0;

                }
                AlertDialog.Builder dlgAlert = new AlertDialog.Builder(LoginActivity.this, R.style.MyAlertDialogStyle);
                dlgAlert.setMessage(Util.getTextofLanguage(LoginActivity.this, Util.NO_RECORD, Util.DEFAULT_NO_RECORD));
                dlgAlert.setTitle(Util.getTextofLanguage(LoginActivity.this, Util.SORRY, Util.DEFAULT_SORRY));
                dlgAlert.setMessage(Util.getTextofLanguage(LoginActivity.this, Util.BUTTON_OK, Util.DEFAULT_BUTTON_OK));
                dlgAlert.setCancelable(false);
                dlgAlert.setPositiveButton(Util.getTextofLanguage(LoginActivity.this, Util.BUTTON_OK, Util.DEFAULT_BUTTON_OK),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
                dlgAlert.create().show();
            }

            if (statusCode > 0) {

//                SharedPreferences.Editor editor = sp.edit();
                SharedPreferences.Editor editor = pref.edit();

                if (statusCode == 200) {
                    String displayNameStr = myJson.optString("display_name");
                    String emailFromApiStr = myJson.optString("email");
                    String profileImageStr = myJson.optString("profile_image");
                    String idd=myJson.optString("id");

                    editor.putString("PREFS_LOGGEDIN_KEY", "1");
                    editor.putString("PREFS_LOGGEDIN_ID_KEY", loggedInIdStr);
                    editor.putString("PREFS_LOGGEDIN_PASSWORD_KEY", "");
                    editor.putString("user_id",idd);
                    editor.putString("display_name",display_name);
                    editor.putString("PREFS_LOGIN_EMAIL_ID_KEY", emailFromApiStr);
                    editor.putString("PREFS_LOGIN_DISPLAY_NAME_KEY", displayNameStr);
                    editor.putString("PREFS_LOGIN_PROFILE_IMAGE_KEY", profileImageStr);
                    // editor.putString("PREFS_LOGGEDIN_PASSWORD_KEY",loginPasswordEditText.getText().toString().trim());
                    editor.putString("PREFS_LOGIN_ISSUBSCRIBED_KEY", isSubscribedStr);
                    editor.putString("PREFS_LOGIN_HISTORYID_KEY", loginHistoryIdStr);


                    Date todayDate = new Date();
                    String todayStr = new SimpleDateFormat("yyyy-MM-dd").format(todayDate);
                    editor.putString("date", todayStr.trim());
                    editor.commit();


                    if (Util.checkNetwork(LoginActivity.this) == true) {
                        if (pDialog != null && pDialog.isShowing()) {
                            pDialog.hide();
                            pDialog = null;
                        }
                        //load video urls according to resolution

                        Intent in = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(in);
                        in.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        onBackPressed();


                    } else {
                        Toast.makeText(LoginActivity.this, Util.getTextofLanguage(LoginActivity.this, Util.NO_INTERNET_CONNECTION, Util.DEFAULT_NO_INTERNET_CONNECTION), Toast.LENGTH_LONG).show();
                    }


                } else {
                    try {
                        if (pDialog != null && pDialog.isShowing()) {
                            pDialog.hide();
                            pDialog = null;
                        }
                    } catch (IllegalArgumentException ex) {
                        statusCode = 0;

                    }
                    AlertDialog.Builder dlgAlert = new AlertDialog.Builder(LoginActivity.this, R.style.MyAlertDialogStyle);
                    dlgAlert.setMessage(Util.getTextofLanguage(LoginActivity.this, Util.EMAIL_PASSWORD_INVALID, Util.DEFAULT_EMAIL_PASSWORD_INVALID));
                    dlgAlert.setTitle(Util.getTextofLanguage(LoginActivity.this, Util.SORRY, Util.DEFAULT_SORRY));
                    dlgAlert.setPositiveButton(Util.getTextofLanguage(LoginActivity.this, Util.BUTTON_OK, Util.DEFAULT_BUTTON_OK), null);
                    dlgAlert.setCancelable(false);
                    dlgAlert.setPositiveButton(Util.getTextofLanguage(LoginActivity.this, Util.BUTTON_OK, Util.DEFAULT_BUTTON_OK),
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });
                    dlgAlert.create().show();
                }
            } else {
                try {
                    if (pDialog != null && pDialog.isShowing()) {
                        pDialog.hide();
                        pDialog = null;
                    }
                } catch (IllegalArgumentException ex) {

                }
                AlertDialog.Builder dlgAlert = new AlertDialog.Builder(LoginActivity.this, R.style.MyAlertDialogStyle);
                dlgAlert.setMessage(Util.getTextofLanguage(LoginActivity.this, Util.NO_RECORD, Util.DEFAULT_NO_RECORD));
                dlgAlert.setTitle(Util.getTextofLanguage(LoginActivity.this, Util.SORRY, Util.DEFAULT_SORRY));
                dlgAlert.setPositiveButton(Util.getTextofLanguage(LoginActivity.this, Util.BUTTON_OK, Util.DEFAULT_BUTTON_OK), null);
                dlgAlert.setCancelable(false);
                dlgAlert.setPositiveButton(Util.getTextofLanguage(LoginActivity.this, Util.BUTTON_OK, Util.DEFAULT_BUTTON_OK),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
                dlgAlert.create().show();
            }

        }

        @Override
        protected void onPreExecute() {
            pDialog = new ProgressBarHandler(LoginActivity.this);
            pDialog.show();
        }
    }

    @Override
    public void onBackPressed() {
        if (getFragmentManager().getBackStackEntryCount() > 0) {
            getFragmentManager().popBackStack();
        } else {
            super.onBackPressed();
        }
    }

    public void removeFocusFromViews() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}


