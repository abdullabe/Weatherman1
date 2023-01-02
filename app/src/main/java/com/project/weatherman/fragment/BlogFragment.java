package com.project.weatherman.fragment;

import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SearchView;
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
import com.project.weatherman.R;
import com.project.weatherman.activity.adapter.Adapter_Blog;
import com.project.weatherman.activity.adapter.Adapter_Radar_Gif;
import com.project.weatherman.activity.global.AppConfig;
import com.project.weatherman.utils.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.Inflater;

import dmax.dialog.SpotsDialog;


public class BlogFragment extends Fragment {
    public static final String TAG_ID = "id";
    public static final String TAG_LOCATION_ID = "location_id";
    public static final String TAG_GIF_IMAGE = "gif_image";
    public static final String TAG_TITLE = "title";
    public static final String TAG_BODY_CONTENT = "body_content";
    public static final String TAG_IMAGE = "image";
    public static final String TAG_CATEGORY_ID = "category_id";
    private RequestQueue queue;
    private SpotsDialog dialog;
    private List<HashMap<String, String>> areasList = new ArrayList<>();
    private SearchView searchView;
//    String str_id = "", str_name = "",str_status="";
//    SessionManager session;
    RecyclerView recBlogList;
    Adapter_Blog adapter_blog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_blog, container, false);
        init(root);
        function();
        return root;
    }
    private void init(View root) {
        recBlogList =(RecyclerView) root.findViewById(R.id.recBlogList);
        EditText editText = root.findViewById(R.id.edittext);
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
//        session = new SessionManager(getActivity().getApplicationContext());
//        session.checkLogin();
//        if (!session.isLoggedIn()) {
//            return;
//        }
//        HashMap<String, String> user = session.getUserDetails();
//        str_id = user.get(SessionManager.KEY_ID);
//        str_name = user.get(SessionManager.KEY_NAME);
//        str_status = user.get(SessionManager.KEY_STATUS_ID);
//        System.out.println("### strid : "+str_id);
//        System.out.println("### str_name : "+str_name);
//        System.out.println("### str_status : "+str_status);
        AdView adView = root.findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
        MobileAds.initialize(getContext(), new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

    }
    private void function() {
        adapter_blog = new
                Adapter_Blog(getActivity(), areasList);
        dialog = new SpotsDialog(getActivity());
        dialog.show();
        queue = Volley.newRequestQueue(getActivity());
        Action_Category_list();

    }
    public void Action_Category_list() {
        System.out.println("### Appconfig.URL_POSTAL_ADDRESS " + AppConfig.URL_GET_NEWS);
        String tag_json_obj = "json_obj_req";
        System.out.println("CAME 1");
        final StringRequest request = new StringRequest(Request.Method.POST,
                AppConfig.URL_GET_NEWS, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                System.out.println("###  Appconfig.URL_POSTAL_ADDRESS onResposne " + response);
                Log.d("TAG", response.toString());
                try {
                    JSONObject obj = new JSONObject(response);
                    JSONArray array_results = obj.getJSONArray("data");
                    for (int count = 0; count < array_results.length(); count++) {
                        JSONObject obj_data = array_results.getJSONObject(count);
                            String id=obj_data.getString(TAG_ID);
                            String location_id=obj_data.getString(TAG_LOCATION_ID);
                            String gif_image=obj_data.getString(TAG_GIF_IMAGE);
                            String title=obj_data.getString(TAG_TITLE);
                            String body_content=obj_data.getString(TAG_BODY_CONTENT);
                            String image=obj_data.getString(TAG_IMAGE);
                            String category_id=obj_data.getString(TAG_CATEGORY_ID);
                            System.out.println("### id" + id);
                            System.out.println("### location_id" + location_id);
                            System.out.println("### gif_image" + gif_image);
                            System.out.println("### title" + title);
                            System.out.println("### body_content" + body_content);
                            System.out.println("### image" + image);
                            System.out.println("### category_id" + category_id);
                            HashMap<String, String> item = new HashMap<>();
                            item.put(TAG_ID, id);
                            item.put(TAG_LOCATION_ID, location_id);
                            item.put(TAG_GIF_IMAGE, gif_image);
                            item.put(TAG_TITLE, title);
                            item.put(TAG_BODY_CONTENT, body_content);
                            item.put(TAG_IMAGE, image);
                            item.put(TAG_CATEGORY_ID, category_id);
                            areasList.add(item);
                        }
                } catch (JSONException e) {
                    //
                    e.printStackTrace();
                }

//                recBlogList.setLayoutManager(new GridLayoutManager(getActivity(), 2));
                recBlogList.setLayoutManager(new LinearLayoutManager
                        (getActivity(), LinearLayoutManager.VERTICAL, false));
                recBlogList.setAdapter(adapter_blog);
                adapter_blog.notifyDataSetChanged();
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

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("type_id", "2");
//                params.put("location_id", str_id);
                System.out.println("### type_id " + "1");
//                System.out.println("### location_id " + str_id);
                return params;
            }
        };
        // Adding request to request queue
        request.setRetryPolicy(new DefaultRetryPolicy(
                AppConfig.TAG_VOLLERY_TIMEOUT,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(request);
    }

    private void filter(String text) {
         List<HashMap<String, String>> filteredList = new ArrayList<>();

        for (HashMap<String, String>  item : areasList) {
            if (item.get("title").toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(item);
            }
        }

        adapter_blog.filterList(filteredList);
    }
}