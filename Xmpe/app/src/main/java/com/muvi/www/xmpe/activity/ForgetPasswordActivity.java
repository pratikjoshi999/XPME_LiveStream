package com.muvi.www.xmpe.activity;

/**
 * Created by MUVI on 5/17/2017.
 */

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.muvi.www.xmpe.ProgressBarHandler;
import com.muvi.www.xmpe.R;
import com.muvi.www.xmpe.Util;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


public class ForgetPasswordActivity extends AppCompatActivity {
   // Toolbar mActionBarToolbar;
    ImageView logoImageView;
    EditText editEmailStr;
    TextView logintextView;
    Button submitButton;
    String loginEmailStr = "";
    boolean navigation = false;
    AsynForgotPasswordDetails asyncPasswordForgot;
    // load asynctask
    int corePoolSize = 60;
    int maximumPoolSize = 80;
    int keepAliveTime = 10;
    BlockingQueue<Runnable> workQueue = new LinkedBlockingQueue<Runnable>(maximumPoolSize);
    Executor threadPoolExecutor = new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, TimeUnit.SECONDS, workQueue);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_forgot_password);
      //  mActionBarToolbar = (Toolbar) findViewById(R.id.toolbar);
       // setSupportActionBar(mActionBarToolbar);
      //  mActionBarToolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_arrow_back));
       // mActionBarToolbar.setNavigationOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                onBackPressed();
//            }
//        });

        logoImageView = (ImageView) findViewById(R.id.logoImageView);
        editEmailStr = (EditText) findViewById(R.id.editEmailStr);
        logintextView = (TextView) findViewById(R.id.loginTextView);
        submitButton = (Button) findViewById(R.id.submitButton);


        editEmailStr.setHint(Util.getTextofLanguage(ForgetPasswordActivity.this, Util.TEXT_EMIAL, Util.DEFAULT_TEXT_EMIAL));
        submitButton.setText(Util.getTextofLanguage(ForgetPasswordActivity.this, Util.BTN_SUBMIT, Util.DEFAULT_BTN_SUBMIT));
        logintextView.setText(Util.getTextofLanguage(ForgetPasswordActivity.this, Util.LOGIN, Util.DEFAULT_LOGIN));

        logintextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Intent detailsIntent = new Intent(ForgetPasswordActivity.this, LoginActivity.class);
                detailsIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(detailsIntent);
                finish();
            }
        });

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                forgotPasswordButtonClicked();
            }
        });
    }


    public void forgotPasswordButtonClicked() {

        loginEmailStr = editEmailStr.getText().toString().trim();
        boolean isNetwork = Util.checkNetwork(ForgetPasswordActivity.this);

        if (isNetwork) {
            boolean isValidEmail = Util.isValidMail(loginEmailStr);
            if (isValidEmail == true) {
                asyncPasswordForgot = new AsynForgotPasswordDetails();
                asyncPasswordForgot.executeOnExecutor(threadPoolExecutor);
            } else {

                ShowDialog(Util.getTextofLanguage(ForgetPasswordActivity.this, Util.FAILURE, Util.DEFAULT_FAILURE), Util.getTextofLanguage(ForgetPasswordActivity.this, Util.OOPS_INVALID_EMAIL, Util.DEFAULT_OOPS_INVALID_EMAIL));

            }
        } else {
            ShowDialog(Util.getTextofLanguage(ForgetPasswordActivity.this, Util.SORRY, Util.DEFAULT_SORRY), Util.getTextofLanguage(ForgetPasswordActivity.this, Util.NO_INTERNET_CONNECTION, Util.DEFAULT_NO_INTERNET_CONNECTION));

        }
    }

    private class AsynForgotPasswordDetails extends AsyncTask<Void, Void, Void> {
        ProgressBarHandler pDialog;
        String loginEmailIdStr = editEmailStr.getText().toString();
        int responseCode;
        String responseStr;

        @Override
        protected Void doInBackground(Void... params) {

            ArrayList<NameValuePair> cred = new ArrayList<NameValuePair>();
            cred.add(new BasicNameValuePair("email", loginEmailIdStr));
            cred.add(new BasicNameValuePair("authToken", Util.authTokenStr.trim()));

            String urlRouteList = Util.rootUrl().trim() + Util.forgotpasswordUrl.trim();
            try {
                HttpClient httpclient = new DefaultHttpClient();
                HttpPost httppost = new HttpPost(urlRouteList);
                httppost.setHeader(HTTP.CONTENT_TYPE, "application/x-www-form-urlencoded;charset=UTF-8");
                httppost.addHeader("email", loginEmailIdStr);
                httppost.addHeader("authToken", Util.authTokenStr.trim());
                httppost.addHeader("lang_code", Util.getTextofLanguage(ForgetPasswordActivity.this, Util.SELECTED_LANGUAGE_CODE, Util.DEFAULT_SELECTED_LANGUAGE_CODE));


                try {
                    httppost.setEntity(new UrlEncodedFormEntity(cred, "UTF-8"));
                } catch (UnsupportedEncodingException e) {
                    if (pDialog != null && pDialog.isShowing()) {
                        pDialog.hide();
                        pDialog = null;
                    }
                    responseCode = 0;
                    e.printStackTrace();
                }
                try {
                    HttpResponse response = httpclient.execute(httppost);
                    responseStr = EntityUtils.toString(response.getEntity());


                } catch (org.apache.http.conn.ConnectTimeoutException e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (pDialog != null && pDialog.isShowing()) {
                                pDialog.hide();
                                pDialog = null;
                            }
                            responseCode = 0;
                            Toast.makeText(ForgetPasswordActivity.this, Util.getTextofLanguage(ForgetPasswordActivity.this, Util.SLOW_INTERNET_CONNECTION, Util.DEFAULT_SLOW_INTERNET_CONNECTION), Toast.LENGTH_LONG).show();


                        }

                    });

                } catch (IOException e) {
                    if (pDialog != null && pDialog.isShowing()) {
                        pDialog.hide();
                        pDialog = null;
                    }
                    responseCode = 0;
                    e.printStackTrace();
                }
                if (responseStr != null) {
                    JSONObject myJson = new JSONObject(responseStr);
                    responseCode = Integer.parseInt(myJson.optString("code"));
                }

            } catch (Exception e) {
                if (pDialog != null && pDialog.isShowing()) {
                    pDialog.hide();
                    pDialog = null;
                }
                responseCode = 0;

            }

            return null;
        }


        protected void onPostExecute(Void result) {
            try {
                if (pDialog != null && pDialog.isShowing()) {
                    pDialog.hide();
                    pDialog = null;
                }
            } catch (IllegalArgumentException ex) {
                ShowDialog(Util.getTextofLanguage(ForgetPasswordActivity.this, Util.FAILURE, Util.DEFAULT_FAILURE), Util.getTextofLanguage(ForgetPasswordActivity.this, Util.EMAIL_DOESNOT_EXISTS, Util.DEFAULT_EMAIL_DOESNOT_EXISTS));

            }
            if (responseStr == null) {
                ShowDialog(Util.getTextofLanguage(ForgetPasswordActivity.this, Util.FAILURE, Util.DEFAULT_FAILURE), Util.getTextofLanguage(ForgetPasswordActivity.this, Util.EMAIL_DOESNOT_EXISTS, Util.DEFAULT_EMAIL_DOESNOT_EXISTS));

            }
            if (responseCode == 0) {
                ShowDialog(Util.getTextofLanguage(ForgetPasswordActivity.this, Util.FAILURE, Util.DEFAULT_FAILURE), Util.getTextofLanguage(ForgetPasswordActivity.this, Util.EMAIL_DOESNOT_EXISTS, Util.DEFAULT_EMAIL_DOESNOT_EXISTS));
            }
            if (responseCode > 0) {
                if (responseCode == 200) {
                    if (pDialog != null && pDialog.isShowing()) {
                        pDialog.hide();
                        pDialog = null;
                    }
                    navigation = true;
                    ShowDialog(Util.getTextofLanguage(ForgetPasswordActivity.this, Util.FAILURE, Util.DEFAULT_FAILURE), Util.getTextofLanguage(ForgetPasswordActivity.this, Util.PASSWORD_RESET_LINK, Util.DEFAULT_PASSWORD_RESET_LINK));


                } else {
                    ShowDialog(Util.getTextofLanguage(ForgetPasswordActivity.this, Util.FAILURE, Util.DEFAULT_FAILURE), Util.getTextofLanguage(ForgetPasswordActivity.this, Util.EMAIL_DOESNOT_EXISTS, Util.DEFAULT_EMAIL_DOESNOT_EXISTS));
                }
            }

        }

        @Override
        protected void onPreExecute() {
            pDialog = new ProgressBarHandler(ForgetPasswordActivity.this);
            pDialog.show();
        }
    }

    @Override
    public void onBackPressed() {
        if (asyncPasswordForgot != null) {
            asyncPasswordForgot.cancel(true);
        }
        finish();
        overridePendingTransition(0, 0);
        super.onBackPressed();
    }

    public void ShowDialog(String Title, String msg) {
        AlertDialog.Builder dlgAlert = new AlertDialog.Builder(ForgetPasswordActivity.this, R.style.MyAlertDialogStyle);
        dlgAlert.setMessage(msg);
        dlgAlert.setTitle(Title);
        dlgAlert.setPositiveButton(Util.getTextofLanguage(ForgetPasswordActivity.this, Util.BUTTON_OK, Util.DEFAULT_BUTTON_OK), null);
        dlgAlert.setCancelable(false);
        dlgAlert.setPositiveButton(Util.getTextofLanguage(ForgetPasswordActivity.this, Util.BUTTON_OK, Util.DEFAULT_BUTTON_OK),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if (navigation) {
                            Intent in = new Intent(ForgetPasswordActivity.this, LoginActivity.class);
                            startActivity(in);
                            dialog.cancel();
                        } else {
                            dialog.cancel();
                        }
                    }
                });
        dlgAlert.create().show();
    }
}


