package com.muvi.www.xmpe.activity;


import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.method.LinkMovementMethod;
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
import com.muvi.www.xmpe.categorylist.CategoryActivity;

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

public class RegisterActivity extends AppCompatActivity {


    public static ProgressBarHandler progressBarHandler;

    AsynRegistrationDetails asyncReg;
    private ImageView registerImageView;
    private EditText editEmail, editName, editPassword;
    private Button registerButton;
    private TextView alreadyMemmberText, loginTextView;
    private TextView termsTextView, termsTextView1;
    String regNameStr, regEmailStr, regPasswordStr;
    int corePoolSize = 60;
    int maximumPoolSize = 80;
    int keepAliveTime = 10;
    SharedPreferences pref;
   // Toolbar mActionBarToolbar;

    BlockingQueue<Runnable> workQueue = new LinkedBlockingQueue<Runnable>(maximumPoolSize);
    Executor threadPoolExecutor = new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, TimeUnit.SECONDS, workQueue);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_register);

//        mActionBarToolbar = (Toolbar) findViewById(R.id.toolbar);
//        //setSupportActionBar(mActionBarToolbar);
//        mActionBarToolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_arrow_back));
//     //   mActionBarToolbar.setNavigationOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                onBackPressed();
//            }
//        });

        registerImageView = (ImageView) findViewById(R.id.registerImageView);
        editName = (EditText) findViewById(R.id.editNameStr);
        editEmail = (EditText) findViewById(R.id.editEmailStr);
        editPassword = (EditText) findViewById(R.id.editPasswordStr);
        registerButton = (Button) findViewById(R.id.registerButton);
        alreadyMemmberText = (TextView) findViewById(R.id.alreadyMemberText);
        loginTextView = (TextView) findViewById(R.id.alreadyHaveALoginButton);
        termsTextView = (TextView) findViewById(R.id.termsTextView);
        termsTextView1 = (TextView) findViewById(R.id.termsTextView1);


        termsTextView1.setText(Util.getTextofLanguage(RegisterActivity.this, Util.CHK_OVER_18, Util.DEFAULT_CHK_OVER_18));
        termsTextView.setText(Util.getTextofLanguage(RegisterActivity.this, Util.TERMS, Util.DEFAULT_TERMS));


        editName.setHint(Util.getTextofLanguage(RegisterActivity.this, Util.NAME_HINT, Util.DEFAULT_NAME_HINT));
        editEmail.setHint(Util.getTextofLanguage(RegisterActivity.this, Util.TEXT_EMIAL, Util.DEFAULT_TEXT_EMIAL));
        editPassword.setHint(Util.getTextofLanguage(RegisterActivity.this, Util.TEXT_PASSWORD, Util.DEFAULT_TEXT_PASSWORD));
        registerButton.setText(Util.getTextofLanguage(RegisterActivity.this, Util.BTN_REGISTER, Util.DEFAULT_BTN_REGISTER));
        alreadyMemmberText.setText(Util.getTextofLanguage(RegisterActivity.this, Util.ALREADY_MEMBER, Util.DEFAULT_ALREADY_MEMBER));
        loginTextView.setText(Util.getTextofLanguage(RegisterActivity.this, Util.LOGIN, Util.DEFAULT_LOGIN));


        termsTextView.setMovementMethod(LinkMovementMethod.getInstance());
        String text = "<a href='https://xpme.muvi.com/page/terms-privacy-policy'> terms </a>";
        termsTextView.setText(Html.fromHtml(text));



//        Toolbar mActionBarToolbar = (Toolbar) findViewById(R.id.toolbar);
//        mActionBarToolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_arrow_back));
//        mActionBarToolbar.setNavigationOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                onBackPressed();
//            }
//        });
        pref = getSharedPreferences(Util.LOGIN_PREF, 0);

        loginTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Intent detailsIntent = new Intent(RegisterActivity.this, LoginActivity.class);
                detailsIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(detailsIntent);
                finish();
                overridePendingTransition(0, 0);
            }
        });

       /* alreadyMemmberText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(RegisterActivity.this,"There's some error. Please try again !", Toast.LENGTH_SHORT).show();
            }
        });*/
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerButtonClicked();
            }
        });
        if (!Util.TERMS_PRIVACY_URL.matches("")) {
            termsTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(Util.TERMS_PRIVACY_URL));
                    browserIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    startActivity(browserIntent);
                }
            });
        }
    }

    public void registerButtonClicked() {

        regNameStr = editName.getText().toString().trim();
        regEmailStr = editEmail.getText().toString().trim().toLowerCase();
        regPasswordStr = editPassword.getText().toString();

        boolean isNetwork = Util.checkNetwork(RegisterActivity.this);
        if (isNetwork == true) {

            if (!regNameStr.matches("") && (!regEmailStr.matches("")) && (!regPasswordStr.matches("")) && !regNameStr.equals("")) {
                boolean isValidEmail = Util.isValidMail(regEmailStr);
                if (isValidEmail) {

                    asyncReg = new AsynRegistrationDetails();
                    asyncReg.executeOnExecutor(threadPoolExecutor);

                } else {
                    Toast.makeText(RegisterActivity.this, Util.getTextofLanguage(RegisterActivity.this, Util.OOPS_INVALID_EMAIL, Util.DEFAULT_OOPS_INVALID_EMAIL), Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(RegisterActivity.this, Util.getTextofLanguage(RegisterActivity.this, Util.ENTER_REGISTER_FIELDS_DATA, Util.DEFAULT_ENTER_REGISTER_FIELDS_DATA), Toast.LENGTH_LONG).show();

            }
        } else {
            Toast.makeText(RegisterActivity.this, Util.getTextofLanguage(RegisterActivity.this, Util.NO_INTERNET_CONNECTION, Util.DEFAULT_NO_INTERNET_CONNECTION), Toast.LENGTH_LONG).show();
        }
    }


    private class AsynRegistrationDetails extends AsyncTask<Void, Void, Void> {

        ProgressBarHandler pDialog;

        int status;
        String responseStr;
        String registrationIdStr;
        String isSubscribedStr;
        String loginHistoryIdStr;
        JSONObject myJson = null;


        @Override
        protected Void doInBackground(Void... params) {
            String urlRouteList = Util.rootUrl().trim() + Util.registrationUrl.trim();
            try {
                HttpClient httpclient = new DefaultHttpClient();
                HttpPost httppost = new HttpPost(urlRouteList);
                httppost.setHeader(HTTP.CONTENT_TYPE, "application/x-www-form-urlencoded;charset=UTF-8");
                httppost.addHeader("name", regNameStr);
                httppost.addHeader("email", regEmailStr);
                httppost.addHeader("password", regPasswordStr);
                httppost.addHeader("authToken", Util.authTokenStr.trim());
                httppost.addHeader("lang_code", Util.getTextofLanguage(RegisterActivity.this, Util.SELECTED_LANGUAGE_CODE, Util.DEFAULT_SELECTED_LANGUAGE_CODE));

                // Execute HTTP Post Request
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
                            status = 0;
                            Toast.makeText(RegisterActivity.this, Util.getTextofLanguage(RegisterActivity.this, Util.SLOW_INTERNET_CONNECTION, Util.DEFAULT_SLOW_INTERNET_CONNECTION), Toast.LENGTH_LONG).show();

                        }

                    });

                } catch (IOException e) {
                    if (pDialog != null && pDialog.isShowing()) {
                        pDialog.hide();
                        pDialog = null;
                    }
                    status = 0;
                    e.printStackTrace();
                }
                if (responseStr != null) {
                    myJson = new JSONObject(responseStr);
                    status = Integer.parseInt(myJson.optString("code"));
                    registrationIdStr = myJson.optString("id");
                    isSubscribedStr = myJson.optString("isSubscribed");
                    loginHistoryIdStr = myJson.optString("login_history_id");

                }

            } catch (Exception e) {
                if (pDialog != null && pDialog.isShowing()) {
                    pDialog.hide();
                    pDialog = null;
                }
                status = 0;

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
                status = 0;

            }
            if (responseStr == null) {
                status = 0;

            }
            if (status == 0) {
                AlertDialog.Builder dlgAlert = new AlertDialog.Builder(RegisterActivity.this, R.style.MyAlertDialogStyle);
                dlgAlert.setMessage(Util.getTextofLanguage(RegisterActivity.this, Util.ERROR_IN_REGISTRATION, Util.DEFAULT_ERROR_IN_REGISTRATION));
                dlgAlert.setTitle(Util.getTextofLanguage(RegisterActivity.this, Util.FAILURE, Util.DEFAULT_FAILURE));
                dlgAlert.setPositiveButton(Util.getTextofLanguage(RegisterActivity.this, Util.BUTTON_OK, Util.DEFAULT_BUTTON_OK), null);
                dlgAlert.setCancelable(false);
                dlgAlert.setPositiveButton(Util.getTextofLanguage(RegisterActivity.this, Util.BUTTON_OK, Util.DEFAULT_BUTTON_OK),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
                dlgAlert.create().show();
            }
            if (status > 0) {

                if (status == 422) {
                    AlertDialog.Builder dlgAlert = new AlertDialog.Builder(RegisterActivity.this, R.style.MyAlertDialogStyle);
                    dlgAlert.setMessage(Util.getTextofLanguage(RegisterActivity.this, Util.EMAIL_EXISTS, Util.DEFAULT_EMAIL_EXISTS));
                    dlgAlert.setTitle(Util.getTextofLanguage(RegisterActivity.this, Util.SORRY, Util.DEFAULT_SORRY));
                    dlgAlert.setPositiveButton(Util.getTextofLanguage(RegisterActivity.this, Util.BUTTON_OK, Util.DEFAULT_BUTTON_OK), null);
                    dlgAlert.setCancelable(false);
                    dlgAlert.setPositiveButton(Util.getTextofLanguage(RegisterActivity.this, Util.BUTTON_OK, Util.DEFAULT_BUTTON_OK),
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });
                    dlgAlert.create().show();

                } else if (status == 200) {

                    String displayNameStr = myJson.optString("display_name");
                    String emailFromApiStr = myJson.optString("email");
                    String profileImageStr = myJson.optString("profile_image");


                    // Take appropiate step here.


                    SharedPreferences.Editor editor = pref.edit();
                    editor.putString("PREFS_LOGGEDIN_KEY", "1");
                    editor.putString("PREFS_LOGGEDIN_ID_KEY", registrationIdStr);
                    editor.putString("PREFS_LOGGEDIN_PASSWORD_KEY", editPassword.getText().toString().trim());
                    editor.putString("PREFS_LOGIN_EMAIL_ID_KEY", emailFromApiStr);
                    editor.putString("PREFS_LOGIN_DISPLAY_NAME_KEY", displayNameStr);
                    editor.putString("PREFS_LOGIN_PROFILE_IMAGE_KEY", profileImageStr);
                    editor.putString("PREFS_LOGIN_ISSUBSCRIBED_KEY", isSubscribedStr);
                    editor.putString("PREFS_LOGIN_HISTORYID_KEY", loginHistoryIdStr);

                    Date todayDate = new Date();
                    String todayStr = new SimpleDateFormat("yyyy-MM-dd").format(todayDate);
                    editor.putString("date", todayStr.trim());
                    editor.commit();


                    Intent intent = new Intent(RegisterActivity.this, EditProfileActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                    overridePendingTransition(0, 0);
                }


                if (Util.checkNetwork(RegisterActivity.this) == true) {
                    if (pDialog != null && pDialog.isShowing()) {
                        pDialog.hide();
                        pDialog = null;
                    }
                } else {
                    Toast.makeText(RegisterActivity.this, Util.getTextofLanguage(RegisterActivity.this, Util.NO_INTERNET_CONNECTION, Util.DEFAULT_NO_INTERNET_CONNECTION), Toast.LENGTH_LONG).show();
                }

                if (Util.checkNetwork(RegisterActivity.this) == true) {
                    if (pDialog != null && pDialog.isShowing()) {
                        pDialog.hide();
                        pDialog = null;
                    }


                }
            }

        }

    }
}
