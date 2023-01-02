package com.project.weatherman.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;
import com.project.weatherman.R;
import com.project.weatherman.activity.adapter.CommentAdapter;
import com.project.weatherman.activity.global.AppConfig;
import com.project.weatherman.activity.model.YoutubeCommentModel;
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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import dmax.dialog.SpotsDialog;

public class YouTubeDetailActivity extends YouTubeBaseActivity implements YouTubePlayer.OnInitializedListener{
    private static final int READ_STORAGE_PERMISSION_REQUEST_CODE = 1;
    private static String GOOGLE_YOUTUBE_API = "AIzaSyCfwpeRzUqSImQ2LdmXkh5c7DhoE803yt8";//here you should use your api key for testing purpose you can use this api also
    private YoutubeDataModel youtubeDataModel = null;
    TextView textViewName;
    TextView textViewDes;
    // ImageView imageViewIcon;
    public static final String VIDEO_ID = "c2UNv38V6y4";
    YouTubePlayerView mYoutubePlayerView = null;
    YouTubePlayer mYoutubePlayer = null;
     ArrayList<YoutubeCommentModel> mListData = new ArrayList<>();
     CommentAdapter mAdapter = null;
     RecyclerView mList_videos = null;
     RequestQueue queue;
     SpotsDialog dialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_you_tube_detail);
        youtubeDataModel = getIntent().getParcelableExtra(YoutubeDataModel.class.toString());
        Log.e("", youtubeDataModel.getDescription());

        mYoutubePlayerView = findViewById(R.id.youtube_player);
        mYoutubePlayerView.initialize(GOOGLE_YOUTUBE_API, this);

        textViewName = (TextView) findViewById(R.id.textViewName);
        textViewDes = (TextView) findViewById(R.id.textViewDes);

        textViewName.setText(youtubeDataModel.getTitle());
        textViewDes.setText(youtubeDataModel.getDescription());

        mList_videos = (RecyclerView) findViewById(R.id.mList_videos);
        dialog = new SpotsDialog(YouTubeDetailActivity.this);
        dialog.show();
        queue = Volley.newRequestQueue(YouTubeDetailActivity.this);
        Action_Category_list();

        // Initialize the Mobile Ads SDK
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
                Toast.makeText(getApplicationContext(), " successful ", Toast.LENGTH_SHORT).show();
            }
        });

        AdView mAdView;
        mAdView = findViewById(R.id.adViewYoutube);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

    }


    @Override
        public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean wasRestored) {
            youTubePlayer.setPlayerStateChangeListener(playerStateChangeListener);
            youTubePlayer.setPlaybackEventListener(playbackEventListener);
            if (!wasRestored) {
                youTubePlayer.cueVideo(youtubeDataModel.getVideo_id());
            }
            mYoutubePlayer = youTubePlayer;
        }

        private YouTubePlayer.PlaybackEventListener playbackEventListener = new YouTubePlayer.PlaybackEventListener() {
            @Override
            public void onPlaying() {

            }

            @Override
            public void onPaused() {

            }

            @Override
            public void onStopped() {

            }

            @Override
            public void onBuffering(boolean b) {

            }

            @Override
            public void onSeekTo(int i) {

            }
        };

        private YouTubePlayer.PlayerStateChangeListener playerStateChangeListener = new YouTubePlayer.PlayerStateChangeListener() {
            @Override
            public void onLoading() {

            }

            @Override
            public void onLoaded(String s) {

            }

            @Override
            public void onAdStarted() {

            }

            @Override
            public void onVideoStarted() {

            }

            @Override
            public void onVideoEnded() {

            }

            @Override
            public void onError(YouTubePlayer.ErrorReason errorReason) {

            }
        };

        @Override
        public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {

        }

        public void share_btn_pressed(View view) {
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            String link = ("https://www.youtube.com/watch?v=" + youtubeDataModel.getVideo_id());
            // this is the text that will be shared
            sendIntent.putExtra(Intent.EXTRA_TEXT, link);
            sendIntent.putExtra(Intent.EXTRA_SUBJECT, youtubeDataModel.getTitle()
                    + "Share");

            sendIntent.setType("text/plain");
            startActivity(Intent.createChooser(sendIntent, "share"));
        }


        private ProgressDialog pDialog;


        private class RequestDownloadVideoStream extends AsyncTask<String, String, String> {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                pDialog = new ProgressDialog(YouTubeDetailActivity.this);
                pDialog.setMessage("Downloading file. Please wait...");
                pDialog.setIndeterminate(false);
                pDialog.setMax(100);
                pDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                pDialog.setCancelable(false);
                pDialog.show();
            }

            @Override
            protected String doInBackground(String... params) {
                InputStream is = null;
                URL u = null;
                int len1 = 0;
                int temp_progress = 0;
                int progress = 0;
                try {
                    u = new URL(params[0]);
                    is = u.openStream();
                    URLConnection huc = (URLConnection) u.openConnection();
                    huc.connect();
                    int size = huc.getContentLength();

                    if (huc != null) {
                        String file_name = params[1] + ".mp4";
                        String storagePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)+"/YoutubeVideos";
                        File f = new File(storagePath);
                        if (!f.exists()) {
                            f.mkdir();
                        }

                        FileOutputStream fos = new FileOutputStream(f+"/"+file_name);
                        byte[] buffer = new byte[1024];
                        int total = 0;
                        if (is != null) {
                            while ((len1 = is.read(buffer)) != -1) {
                                total += len1;
                                // publishing the progress....
                                // After this onProgressUpdate will be called
                                progress = (int) ((total * 100) / size);
                                if(progress >= 0) {
                                    temp_progress = progress;
                                    publishProgress("" + progress);
                                }else
                                    publishProgress("" + temp_progress+1);

                                fos.write(buffer, 0, len1);
                            }
                        }

                        if (fos != null) {
                            publishProgress("" + 100);
                            fos.close();
                        }
                    }
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if (is != null) {
                        try {
                            is.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
                return null;
            }

            @Override
            protected void onProgressUpdate(String... values) {
                super.onProgressUpdate(values);
                pDialog.setProgress(Integer.parseInt(values[0]));
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                if (pDialog.isShowing())
                    pDialog.dismiss();
            }
        }


    public void Action_Category_list() {
        String VIDEO_COMMENT_URL = "https://www.googleapis.com/youtube/v3/commentThreads?part=snippet&maxResults=100&videoId=" + youtubeDataModel.getVideo_id() + "&key=" + GOOGLE_YOUTUBE_API;


        System.out.println("### Appconfig.URL_POSTAL_ADDRESS " + VIDEO_COMMENT_URL);

        String tag_json_obj = "json_obj_req";
        System.out.println("CAME 1");
        final StringRequest request = new StringRequest(Request.Method.GET,
                VIDEO_COMMENT_URL, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                System.out.println("###  Appconfig.URL_POSTAL_ADDRESS onResposne " + response);

                Log.d("TAG", response.toString());

                if (response != null) {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        Log.e("response", jsonObject.toString());
                        mListData = parseJson(jsonObject);
                        initVideoList(mListData);
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


    public void initVideoList(ArrayList<YoutubeCommentModel> mListData) {
            mList_videos.setLayoutManager(new LinearLayoutManager(this));
            mAdapter = new CommentAdapter(this, mListData);
            mList_videos.setAdapter(mAdapter);
        }

        public ArrayList<YoutubeCommentModel> parseJson(JSONObject jsonObject) {
            ArrayList<YoutubeCommentModel> mList = new ArrayList<>();

            if (jsonObject.has("items")) {
                try {
                    JSONArray jsonArray = jsonObject.getJSONArray("items");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject json = jsonArray.getJSONObject(i);

                        YoutubeCommentModel youtubeObject = new YoutubeCommentModel();
                        JSONObject jsonTopLevelComment = json.getJSONObject("snippet").getJSONObject("topLevelComment");
                        JSONObject jsonSnippet = jsonTopLevelComment.getJSONObject("snippet");

                        String title = jsonSnippet.getString("authorDisplayName");
                        String thumbnail = jsonSnippet.getString("authorProfileImageUrl");
                        String comment = jsonSnippet.getString("textDisplay");

                        youtubeObject.setTitle(title);
                        youtubeObject.setComment(comment);
                        youtubeObject.setThumbnail(thumbnail);
                        mList.add(youtubeObject);


                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            return mList;

        }
}