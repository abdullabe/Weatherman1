package com.project.weatherman.fragment;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.Toolbar;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.youtube.player.YouTubeBaseActivity;
import com.project.weatherman.R;
import com.project.weatherman.activity.YouTubeDetailActivity;
import com.project.weatherman.activity.adapter.Adapter_Radar_Gif;
import com.project.weatherman.activity.adapter.VideoPostAdapter;
import com.project.weatherman.activity.global.AppConfig;
import com.project.weatherman.activity.model.OnItemClickListener;
import com.project.weatherman.activity.model.YoutubeDataModel;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import dmax.dialog.SpotsDialog;


public class VideoFragment extends Fragment   {

    private static String GOOGLE_YOUTUBE_API_KEY = "AIzaSyCfwpeRzUqSImQ2LdmXkh5c7DhoE803yt8";//here you should use your api key for testing purpose you can use this api also
    private static String CHANNEL_ID = "UCx8utcCuquV8iGLuzQA3obw"; //here you should use your channel id for testing purpose you can use this ID also
    private static String CHANNLE_GET_URL = "https://www.googleapis.com/youtube/v3/search?part=snippet&order=date&channelId=" + CHANNEL_ID + "&maxResults=20&key=" + GOOGLE_YOUTUBE_API_KEY + "";
    //https://www.googleapis.com/youtube/v3/search?part=snippet&order=date&channelId=UCx8utcCuquV8iGLuzQA3obw&maxResults=20&key=AIzaSyCfwpeRzUqSImQ2LdmXkh5c7DhoE803yt8

    private RecyclerView mList_videos = null;
    private VideoPostAdapter adapter = null;
    private ArrayList<YoutubeDataModel> mListData = new ArrayList<>();
    private RequestQueue queue;
    private SpotsDialog dialog;
    public VideoFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_video, container, false);
        mList_videos = (RecyclerView) view.findViewById(R.id.mList_videos);
        initList(mListData);
//        new RequestYoutubeAPI().execute();
        dialog = new SpotsDialog(getActivity());
        dialog.show();
        queue = Volley.newRequestQueue(getActivity());
        Action_Category_list();
        EditText editText = view.findViewById(R.id.edittext);
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                filter(s.toString());
            }
        });
       AdView adView = view.findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
        MobileAds.initialize(getContext(), new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });
        return view;
    }

    private void initList(ArrayList<YoutubeDataModel> mListData) {
//        mList_videos.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        mList_videos.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new VideoPostAdapter(getActivity(), mListData, new OnItemClickListener() {
            @Override
            public void onItemClick(YoutubeDataModel item) {

                YoutubeDataModel youtubeDataModel = item;
                Intent intent = new Intent(getActivity(), YouTubeDetailActivity.class);
                intent.putExtra(YoutubeDataModel.class.toString(), youtubeDataModel);
                startActivity(intent);
            }
        });
        mList_videos.setAdapter(adapter);

    }


    public void Action_Category_list() {

        System.out.println("### Appconfig.URL_POSTAL_ADDRESS " + CHANNLE_GET_URL);

        String tag_json_obj = "json_obj_req";
        System.out.println("CAME 1");
        final StringRequest request = new StringRequest(Request.Method.GET,
                CHANNLE_GET_URL, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                System.out.println("###  Appconfig.URL_POSTAL_ADDRESS onResposne " + response);

                Log.d("TAG", response.toString());

                    if (response != null) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            Log.e("response", jsonObject.toString());
                            mListData = parseVideoListFromResponse(jsonObject);
                            initList(mListData);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                dialog.dismiss();


            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                dialog.dismiss();
                System.out.println("###  Appconfig.URL_POSTAL_ADDRESS onError");
                if (error != null)
                    System.out.println("###  Appconfig.URL_POSTAL_ADDRESS onError" + error.getLocalizedMessage());

            }
        }) {



        };

        // Adding request to request queue
        request.setRetryPolicy(new DefaultRetryPolicy(
                AppConfig.TAG_VOLLERY_TIMEOUT,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        queue.add(request);
    }


    public ArrayList<YoutubeDataModel> parseVideoListFromResponse(JSONObject jsonObject) {
        ArrayList<YoutubeDataModel> mList = new ArrayList<>();

        if (jsonObject.has("items")) {
            try {
                JSONArray jsonArray = jsonObject.getJSONArray("items");
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject json = jsonArray.getJSONObject(i);
                    if (json.has("id")) {
                        JSONObject jsonID = json.getJSONObject("id");
                        String video_id = "";
                        if (jsonID.has("videoId")) {
                            video_id = jsonID.getString("videoId");
                        }
                        if (jsonID.has("kind")) {
                            if (jsonID.getString("kind").equals("youtube#video")) {
                                YoutubeDataModel youtubeObject = new YoutubeDataModel();
                                JSONObject jsonSnippet = json.getJSONObject("snippet");
                                String title = jsonSnippet.getString("title");
                                String description = jsonSnippet.getString("description");
                                String publishedAt = jsonSnippet.getString("publishedAt");
                                String thumbnail = jsonSnippet.getJSONObject("thumbnails").getJSONObject("high").getString("url");

                                youtubeObject.setTitle(title);
                                youtubeObject.setDescription(description);
                                youtubeObject.setPublishedAt(publishedAt);
                                youtubeObject.setThumbnail(thumbnail);
                                youtubeObject.setVideo_id(video_id);
                                mList.add(youtubeObject);

                            }
                        }
                    }

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return mList;

    }
    private void filter(String text) {
        ArrayList<YoutubeDataModel> filteredList = new ArrayList<>();

        for (YoutubeDataModel  item : mListData) {
            if (item.getTitle().toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(item);
            }
        }

        adapter.filterList(filteredList);
    }

}