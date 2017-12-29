package com.muvi.www.xmpe.fragment;


import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.DefaultSliderView;
import com.muvi.www.xmpe.R;
import com.muvi.www.xmpe.Util;
import com.muvi.www.xmpe.adapter.CatagoryListAdapter;
import com.muvi.www.xmpe.categorylist.CategoryActivity;
import com.muvi.www.xmpe.categorylist.Model;

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

import static android.R.attr.data;
import static android.content.res.Configuration.SCREENLAYOUT_SIZE_LARGE;
import static android.content.res.Configuration.SCREENLAYOUT_SIZE_MASK;
import static android.content.res.Configuration.SCREENLAYOUT_SIZE_XLARGE;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {

    RecyclerView featureContent;
    View rootView;

    Context context;
    RelativeLayout sliderRelativeLayout;
    private SliderLayout mDemoSlider;
    private RelativeLayout noInternetLayout;
    RelativeLayout noDataLayout;
    TextView noDataTextView;
    TextView noInternetTextView;
    String responseStr;
    int statusCode;
    String category_id = "category_id";
    String category_name = "category_name";
    String category_img_url = "category_img_url";
    String permalink="permalink";
    ArrayList<Model> arraylist1;
    ArrayList<String>CatagoryName = new ArrayList<>();

    CatagoryListAdapter adapter;

    ArrayList<String> url_maps;

    int banner[] = {R.drawable.xpme_banner,R.drawable.xpme_banner,R.drawable.xpme_banner};

    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v= inflater.inflate(R.layout.fragment_home, container, false);
        rootView=v;
        context = getActivity();
        sliderRelativeLayout = (RelativeLayout) v.findViewById(R.id.sliderRelativeLayout);
        mDemoSlider = (SliderLayout) v.findViewById(R.id.sliderLayout);

        featureContent=(RecyclerView)v.findViewById(R.id.featureContent);
        noInternetLayout = (RelativeLayout)rootView.findViewById(R.id.noInternet);
        noDataLayout = (RelativeLayout)rootView.findViewById(R.id.noData);
        noInternetTextView =(TextView)rootView.findViewById(R.id.noInternetTextView);
        noDataTextView =(TextView)rootView.findViewById(R.id.noDataTextView);
        noInternetTextView.setText(Util.getTextofLanguage(context, Util.NO_INTERNET_CONNECTION, Util.DEFAULT_NO_INTERNET_CONNECTION));
        noDataTextView.setText(Util.getTextofLanguage(context, Util.NO_CONTENT, Util.DEFAULT_NO_CONTENT));

        arraylist1 = new ArrayList<>();
        new AsynGetCategoryList().execute();


        if (((getActivity().getResources().getConfiguration().screenLayout & SCREENLAYOUT_SIZE_MASK) == SCREENLAYOUT_SIZE_LARGE)
                || ((getActivity().getResources().getConfiguration().screenLayout & SCREENLAYOUT_SIZE_MASK) == SCREENLAYOUT_SIZE_XLARGE)) {
            for (int j = 0; j < banner.length; j++) {
                DefaultSliderView textSliderView = new DefaultSliderView(getActivity());
                textSliderView
                        .description("")
                        .image(banner[j])
                        .setScaleType(BaseSliderView.ScaleType.Fit);

                textSliderView.bundle(new Bundle());
                textSliderView.getBundle()
                        .putString("extra", "");

                mDemoSlider.addSlider(textSliderView);
            }
        }else{
            for (int j = 0; j < banner.length; j++) {
                DefaultSliderView textSliderView = new DefaultSliderView(getActivity().getApplicationContext());
                textSliderView
                        .description("")
                        .image(banner[j])
                        .setScaleType(BaseSliderView.ScaleType.Fit);
//                        .setOnSliderClickListener(this);
                textSliderView.bundle(new Bundle());
                textSliderView.getBundle()
                        .putString("extra", "");

                mDemoSlider.addSlider(textSliderView);
            }
        }

        return v;


    }

    private class AsynGetCategoryList extends AsyncTask<Void,Void,Void>{

        @Override
        protected Void doInBackground(Void... params) {
            // Create an array
            JSONObject myJson = null;
            // Retrieve JSON Objects from the given URL address
            String urlRouteList = Util.rootUrl().trim() + Util.getCategoryList.trim();

            try{
                HttpClient httpclient = new DefaultHttpClient();
                HttpPost httppost = new HttpPost(urlRouteList);
                httppost.setHeader(HTTP.CONTENT_TYPE, "application/x-www-form-urlencoded;charset=UTF-8");

                httppost.addHeader("authToken", Util.authTokenStr.trim());
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
                            Toast.makeText(getContext(), Util.getTextofLanguage(getContext(), Util.SLOW_INTERNET_CONNECTION, Util.DEFAULT_SLOW_INTERNET_CONNECTION), Toast.LENGTH_LONG).show();

                        }

                    });
                    Log.v("SUBHA", "ConnectTimeoutException" + e.toString());


                } catch (IOException e) {
                    statusCode = 0;
                    Log.v("SUBHA", "IOException" + e.toString());

                    e.printStackTrace();
                }

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
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            featureContent.setLayoutManager(new GridLayoutManager(getContext(), 2));
            adapter = new CatagoryListAdapter(getContext(), arraylist1);

            featureContent.setAdapter(adapter);


        }
    }

}
