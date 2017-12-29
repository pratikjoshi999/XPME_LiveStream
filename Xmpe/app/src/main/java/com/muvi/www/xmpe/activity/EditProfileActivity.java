package com.muvi.www.xmpe.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.home.apisdk.APIUrlConstant;
import com.home.apisdk.apiController.UpadteUserProfileAsynctask;
import com.home.apisdk.apiModel.Update_UserProfile_Input;
import com.home.apisdk.apiModel.Update_UserProfile_Output;
import com.muvi.www.xmpe.ProgressBarHandler;
import com.muvi.www.xmpe.R;
import com.muvi.www.xmpe.Util;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static android.provider.MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE;

public class EditProfileActivity extends AppCompatActivity implements UpadteUserProfileAsynctask.Update_UserProfileListener{

    ImageView profile_icon,editprofile;
    EditText editChatName;
    Button profileSubmitButton;
    Uri photoURI;
    String SelectedPath = "";
    public static final String IMAGE_DIRECTORY_NAME = "Profile_Image";
    private static final int REQUEST_CAMERA = 1;
    private static final int SELECT_FILE = 2;
    File profile_image_file;
    Bitmap bm;
    SharedPreferences pref;

    int editProfileCode;
    int CAMERA_REQUEST_CODE=1111;

    int corePoolSize = 60;
    int maximumPoolSize = 80;
    int keepAliveTime = 10;
    ArrayList permissions=new ArrayList<>();
    BlockingQueue<Runnable> workQueue = new LinkedBlockingQueue<Runnable>(maximumPoolSize);
    Executor threadPoolExecutor = new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, TimeUnit.SECONDS, workQueue);

    private ProgressBarHandler pDialog = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        APIUrlConstant.BASE_URl=Util.rootUrl();

        profile_icon=(ImageView)findViewById(R.id.profile_icon);
        editprofile=(ImageView)findViewById(R.id.editprofile);
        editChatName=(EditText)findViewById(R.id.editChatName);
        profileSubmitButton=(Button)findViewById(R.id.profileSubmitButton);

        permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);

        pref = getSharedPreferences(Util.LOGIN_PREF, 0);
        editprofile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final CharSequence[] items = {"Take Photo", "Choose from Library",
                        "Cancel"};

                AlertDialog.Builder builder = new AlertDialog.Builder(EditProfileActivity.this);
                builder.setTitle("Add Photo!");
                builder.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int item) {
                        if (items[item].equals("Take Photo")) {


                            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            photoURI = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);
                            SelectedPath = photoURI.getPath();
                            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                            // start the image capture Intent
                            if(Build.VERSION.SDK_INT>=23){
                                if (checkSelfPermission(Manifest.permission.CAMERA)
                                        != PackageManager.PERMISSION_GRANTED  || checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) !=PackageManager.PERMISSION_GRANTED) {

                                    requestPermissions(new String[]{Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                            CAMERA_REQUEST_CODE);
                                }else{
                                    startActivityForResult(intent, REQUEST_CAMERA);
                                }
                            }else{
                                startActivityForResult(intent, REQUEST_CAMERA);
                            }

                        } else if (items[item].equals("Choose from Library")) {
                            Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                            intent.setType("image/*");
                            startActivityForResult(Intent.createChooser(intent, "Select File"), SELECT_FILE);
                        } else if (items[item].equals("Cancel")) {
                            dialog.dismiss();
                        }
                    }
                });
                builder.show();
            }
        });

        profileSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (editChatName.getText().toString().trim().matches("")) {
                    Toast.makeText(EditProfileActivity.this, "enter chat name", Toast.LENGTH_SHORT).show();

                }else if(SelectedPath.matches("")){
                    Toast.makeText(EditProfileActivity.this, "choose profile image", Toast.LENGTH_SHORT).show();
                }else{
                    Update_UserProfile_Input updateUserProfileInput=new Update_UserProfile_Input();
                    updateUserProfileInput.setAuthToken(Util.authTokenStr);
                    String User_Id = pref.getString("PREFS_LOGGEDIN_ID_KEY", null);
                    Log.v("pratik","user id=="+User_Id);
                    updateUserProfileInput.setImagefile(SelectedPath);
                    Log.v("pratik","user id=="+SelectedPath);
                    updateUserProfileInput.setUser_id(User_Id);
                    updateUserProfileInput.setName(editChatName.getText().toString().trim());

                    AsynUpdateProfile asyncLoadVideos = new AsynUpdateProfile();
                    asyncLoadVideos.executeOnExecutor(threadPoolExecutor);

                }
            }
        });


    }

    public Uri getOutputMediaFileUri(int type) {
        return Uri.fromFile(getOutputMediaFile(type));
    }

    private static File getOutputMediaFile(int type) {

        // External sdcard location
        File mediaStorageDir = new File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), IMAGE_DIRECTORY_NAME);

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d("BIBHU2", "Oops! Failed create " + IMAGE_DIRECTORY_NAME + " directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = ""+System.currentTimeMillis();
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator+ "IMG_" + timeStamp + ".jpg");

        } else {
            return null;
        }

        return mediaFile;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_CAMERA) {
                profile_image_file = new File(photoURI.getPath());
                Log.v("pratikException", "photoURIPath" +photoURI.getPath());
                Log.v("pratikException", "photoURI" +photoURI);
                String demoImageUrl="http://convertimage.net/frontframe/images/cute_ball_info.png";

               /* BitmapFactory.Options btmapOptions = new BitmapFactory.Options();
                bm = BitmapFactory.decodeFile(photoURI.getPath());
                Log.v("pratikException", "bitmapPath" +bm);*/

                try {
                    profile_icon.setImageURI(photoURI);
//                    profile_icon.setImageBitmap(bm);
                } catch (Exception e) {
                    Log.v("pratikException", "" + e.toString());

                    return;
                }
            } else if (requestCode == SELECT_FILE) {
                Uri selectedImageUri = data.getData();
                SelectedPath = getRealPathFromURI(selectedImageUri);


                String tempPath = getPath(selectedImageUri, EditProfileActivity.this);
                BitmapFactory.Options btmapOptions = new BitmapFactory.Options();
                bm = BitmapFactory.decodeFile(tempPath);

                Log.v("temporaryPath", tempPath);
                Log.v("pratikException", "bitmapPathLIb" +bm);
                profile_icon.setImageBitmap(bm);
            }
        }
    }

    private String getRealPathFromURI(Uri contentURI) {
        Uri contentUri = contentURI;
        Cursor cursor = this.getContentResolver().query(contentUri, null, null, null, null);
        if (cursor == null) {
            return contentUri.getPath();
        } else {
            cursor.moveToFirst();
            int index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            return cursor.getString(index);
        }
    }

    public String getPath(Uri uri, Activity activity) {
        String[] projection = {MediaStore.MediaColumns.DATA};
        Cursor cursor = activity
                .managedQuery(uri, projection, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }


    @Override
    public void onUpdateUserProfilePreExecuteStarted() {

        Log.v("pratik","updateUpro preexe");

        pDialog = new ProgressBarHandler(EditProfileActivity.this);
        pDialog.show();
    }

    @Override
    public void onUpdateUserProfilePostExecuteCompleted(Update_UserProfile_Output update_userProfile_output, int code, String message) {

        if (pDialog != null && pDialog.isShowing()) {
            pDialog.hide();
            pDialog = null;

        }

        Log.v("pratik","updateUpro code=="+code);
        Log.v("pratik","updateUpro msg=="+message);
    }

    private class AsynUpdateProfile extends AsyncTask<Void, Void, Void>{

        int statusCode;
        String loggedInIdStr;
        String chatNameStr = editChatName.getText().toString().trim();

        String responseStr;
        JSONObject myJson = null;


        int serverResponseCode=0;
        @Override
        protected Void doInBackground(Void... params) {
            String selectedFilePath = SelectedPath;
            String urlRouteList = Util.rootUrl().trim() + Util.updateProfileUrl.trim();
            if (pref != null) {
                loggedInIdStr = pref.getString("PREFS_LOGGEDIN_ID_KEY", null);
            }

            int serverResponseCode = 0;

            final HttpURLConnection connection;
            DataOutputStream dataOutputStream;
            String lineEnd = "\r\n";
            String twoHyphens = "--";
            String boundary = "*****";


            int bytesRead, bytesAvailable, bufferSize;
            byte[] buffer;
            int maxBufferSize = 5 * 1024 * 1024;
            File selectedFile = new File(selectedFilePath);

            String[] parts = selectedFilePath.split("/");
            final String fileName = parts[parts.length - 1];

            try {
                FileInputStream fileInputStream = new FileInputStream(selectedFile);
                URL url = new URL(urlRouteList);
                connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);//Allow Inputs
                connection.setDoOutput(true);//Allow Outputs
                connection.setUseCaches(false);//Don't use a cached Copy
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Connection", "Keep-Alive");
                connection.setRequestProperty("ENCTYPE", "multipart/form-data");
                connection.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
                connection.setRequestProperty("file", selectedFilePath);
                connection.setRequestProperty("authToken", Util.authTokenStr.trim());
                connection.setRequestProperty("user_id", loggedInIdStr.trim());
                connection.setRequestProperty("nick_name", chatNameStr.trim());

                //creating new dataoutputstream
                dataOutputStream = new DataOutputStream(connection.getOutputStream());


                //writing bytes to data outputstream
//                    dataOutputStream.writeBytes(jsonParam.toString());
                dataOutputStream.writeBytes(twoHyphens + boundary + lineEnd);
                dataOutputStream.writeBytes("Content-Disposition: form-data ; name=\"file\";filename=\""
                        + selectedFilePath + "\"" + lineEnd);

                dataOutputStream.writeBytes(lineEnd);

                //returns no. of bytes present in fileInputStream
                bytesAvailable = fileInputStream.available();
                //selecting the buffer size as minimum of available bytes or 1 MB
                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                //setting the buffer as byte array of size of bufferSize
                buffer = new byte[bufferSize];

                //reads bytes from FileInputStream(from 0th index of buffer to buffersize)
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                //loop repeats till bytesRead = -1, i.e., no bytes are left to read
                while (bytesRead > 0) {
                    //write the bytes read from inputstream
                    dataOutputStream.write(buffer, 0, bufferSize);
                    bytesAvailable = fileInputStream.available();
                    bufferSize = Math.min(bytesAvailable, maxBufferSize);
                    bytesRead = fileInputStream.read(buffer, 0, bufferSize);
                }

                dataOutputStream.writeBytes(lineEnd);
                dataOutputStream.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

                serverResponseCode = connection.getResponseCode();
                editProfileCode=connection.getResponseCode();
                String serverResponseMessage = connection.getResponseMessage();

                Log.v("BIBHU3", "Server Response is: " + serverResponseMessage + ": " + serverResponseCode);

                //response code of 200 indicates the server status OK
                if (serverResponseCode == 200) {


                            try {
                                InputStream ins = connection.getInputStream();
                                responseStr = readStream(ins);


                                if (!responseStr.equals(null)) {
                                    myJson = new JSONObject(responseStr);
                                    statusCode = Integer.parseInt(myJson.optString("code"));

                                    Log.v("BIBHU3", "statusCode 1==" + statusCode);

                                }
                                Log.v("BIBHU3", "Server Response is: " + responseStr);


                            } catch (Exception e) {
                                Log.v("BIBHU3", "Exception is: " + e.toString());
                            }
                }

                //closing the input and output streams
                fileInputStream.close();
                dataOutputStream.flush();
                dataOutputStream.close();
            }catch (Exception e){
                statusCode = 0;
                Log.v("BIBHU3", "Exception 1" + e.toString());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            if (pDialog != null && pDialog.isShowing()) {
                pDialog.hide();
                pDialog = null;
            }


            if (statusCode > 0) {

                if (statusCode == 200) {


                    try {
                        if (pDialog != null && pDialog.isShowing()) {
                            pDialog.hide();
                            pDialog = null;
                        }
                    } catch (IllegalArgumentException ex) {
                        SharedPreferences.Editor editor = pref.edit();
                        if (myJson.has("nick_name")) {
                            String chatNameStr = myJson.optString("nick_name");
                            editor.putString("chat_namePref", chatNameStr);
                        }
                        if (myJson.has("profile_image")) {
                            String profile_imageStr = myJson.optString("profile_image");
                            Log.v("pratik", "profile_image1=" + profile_imageStr);
                            editor.putString("display_imagePref", profile_imageStr);
                        }
                        if (myJson.has("name")) {
                            String profileName = myJson.optString("name");
                            Log.v("pratik", "nick_name=" + profileName);
                            editor.putString("display_namePref", profileName);
                        }

                        editor.commit();
                    }

                    SharedPreferences.Editor editor = pref.edit();
                    if (myJson.has("nick_name")) {
                        String chatNameStr = myJson.optString("nick_name");
                        Log.v("pratik", "nick_name=" + chatNameStr);
                        editor.putString("chat_namePref", chatNameStr);
                    }
                    if (myJson.has("profile_image")) {
                        String profile_imageStr = myJson.optString("profile_image");
                        Log.v("pratik", "profile_image=" + profile_imageStr);
                        editor.putString("display_imagePref", profile_imageStr);
                    }
                    if (myJson.has("name")) {
                        String profileName = myJson.optString("name");
                        Log.v("pratik", "nick_name=" + profileName);
                        editor.putString("display_namePref", profileName);
                    }
                    editor.commit();

                    Log.v("pratik", "update profile to main ==**");

                    Intent profileintent = new Intent(EditProfileActivity.this, MainActivity.class);
                    startActivity(profileintent);
                }

            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressBarHandler(EditProfileActivity.this);
            pDialog.show();
        }
    }


    private String readStream(InputStream in) {
        BufferedReader reader = null;
        StringBuffer response = new StringBuffer();
        try {
            reader = new BufferedReader(new InputStreamReader(in));
            String line = "";
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return response.toString();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode==CAMERA_REQUEST_CODE){
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                photoURI = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);
                SelectedPath = photoURI.getPath();
                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(intent, REQUEST_CAMERA);
            }

        }
    }
}
