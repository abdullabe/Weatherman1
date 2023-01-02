package com.project.weatherman.fragment;

import static android.content.Context.INPUT_METHOD_SERVICE;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.material.card.MaterialCardView;
import com.project.weatherman.R;
import com.project.weatherman.activity.GifActivity;
import com.project.weatherman.activity.adapter.Adapter_Radar_Gif;
import com.project.weatherman.activity.adapter.WeatherRVadapter;
import com.project.weatherman.activity.global.AppConfig;
import com.project.weatherman.activity.model.ModelAreas;
import com.project.weatherman.activity.model.WeatherRVmodal;
import com.project.weatherman.utils.SessionManager;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import dmax.dialog.SpotsDialog;


public class HomeFragment extends Fragment {
    public static final String TAG_ID = "id";
    public static final String TAG_LOCATION_ID = "location_id";
    public static final String TAG_GIF_IMAGE = "gif_image";
    public static final String TAG_TITLE = "title";
    public static final String TAG_BODY_CONTENT = "body_content";
    public static final String TAG_IMAGE = "image";
    public static final String TAG_CATEGORY_ID = "category_id";
   public static ImageView imgGifView;
    private RequestQueue queue;
    private SpotsDialog dialog;
    private List<HashMap<String, String>> areasList = new ArrayList<>();

//    String str_id = "", str_name = "",str_status="";
//    SessionManager session;
    RecyclerView recRadarList;
    ////////////////////////////////
    private ConstraintLayout constraintLayout;
    private ProgressBar initalloading;
    private TextView tvcityname, tvtemp, tvcurrwea, tvTempFeelslike, tvDateAndTime;
    private ImageView ivtempicon, llsearchicon, backimg;
    private RecyclerView recyclerView;
    private ArrayList<WeatherRVmodal> WeatherRVmodalArrayList;
    private WeatherRVadapter adapter;
    private String CityName;
    private EditText llSearchEdittxt;
    private Button tryAgainbtn;
    private RelativeLayout NoInternetlayout;
    int day;
    private LinearLayoutManager linearLayoutManager;
    String[] DaysArray = {"Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"};


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View root = inflater.inflate(R.layout.fragment_home, container, false);

        init(root);
        weather(root);
        function();
        AdView adView = root.findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
        MobileAds.initialize(getContext(), new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });
        return root;

    }

    private void init(View root) {
        imgGifView = (ImageView) root.findViewById(R.id.imgGifView);
        recRadarList=(RecyclerView) root.findViewById(R.id.recRadarList);
        constraintLayout = root.findViewById(R.id.home);
        recyclerView =root. findViewById(R.id.RVforecastcards);
        ivtempicon = root.findViewById(R.id.TVtempicon);
        tvDateAndTime = root.findViewById(R.id.TVdate_and_time);
        tvTempFeelslike = root.findViewById(R.id.TVTempFeel);
        tvcityname = root.findViewById(R.id.Cityname);
        tvtemp = root.findViewById(R.id.TVtemprature);
        tvcurrwea = root.findViewById(R.id.TVcurrwea);
        initalloading = root.findViewById(R.id.progressbar);
        constraintLayout = root.findViewById(R.id.home);
        tryAgainbtn = root.findViewById(R.id.TryAgainBtn);
        NoInternetlayout = root.findViewById(R.id.RLNoInternet);
        llsearchicon = root.findViewById(R.id.llsearchicon);
        llSearchEdittxt = root.findViewById(R.id.lledittxtcityinput);
        backimg = root.findViewById(R.id.blockclr);
        llSearchEdittxt.setOnEditorActionListener(editorListner);

    }
    private void weather(View root){


        WeatherRVmodalArrayList = new ArrayList<>();


        CityName = getCityName(76.961632, 11.004556);
        getWeather(11.004556,76.961632, CityName);

        llsearchicon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String city = llSearchEdittxt.getText().toString();
                if (city.isEmpty()) {
                    Toast.makeText(getActivity(), "Please enter city name", Toast.LENGTH_SHORT).show();
                } else {
                    //tvcityname.setText(CityName);
                    InputMethodManager inputMethodManager = (InputMethodManager)getActivity().getSystemService(INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
                    getLatLong(city);
                }
            }
        });
        tryAgainbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                llSearchEdittxt.setText("");
                getWeather(11.004556,76.961632, CityName);
            }
        });
    }

    private void function() {

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

                            if(count==0){
                                String image=obj_data.getString(TAG_IMAGE);
                                if(image!=null){
                                    Glide.with(getActivity()).load(image).placeholder(R.drawable.radar_gif).into(imgGifView);
                                }
                            }
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
                Adapter_Radar_Gif adapter_radar_gif = new
                        Adapter_Radar_Gif(getActivity(), areasList);
                recRadarList.setLayoutManager(new GridLayoutManager(getActivity(), 2));
//                recRadarList.setLayoutManager(new LinearLayoutManager
//                        (getActivity(), LinearLayoutManager.VERTICAL, false));
                recRadarList.setAdapter(adapter_radar_gif);
                adapter_radar_gif.notifyDataSetChanged();
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

                params.put("type_id", "1");
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

    private TextView.OnEditorActionListener editorListner = new TextView.OnEditorActionListener() {
        @Override
        public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
            if (i == EditorInfo.IME_ACTION_SEARCH) {
                String city = llSearchEdittxt.getText().toString();
                // for closing the key board
                InputMethodManager inputMethodManager = (InputMethodManager)getActivity(). getSystemService(INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
                getLatLong(city);
            }
            return false;
        }
    };



    private String getCityName(Double longi, Double lati) {
        String cityName = "Not found";
        Geocoder geocoder = new Geocoder(getActivity().getBaseContext(), Locale.getDefault());
        try {
            List<Address> addressList = geocoder.getFromLocation(lati, longi, 10);
            for (Address address : addressList) {
                if (address != null) {
                    String city = address.getLocality();
                    if (city != null && !city.equals("")) {
                        cityName = city;
                        //Log.i("got_city", cityName);
                    } else {
                        llSearchEdittxt.setText("");
                    }
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return cityName;
    }

    public void getLatLong(String cityName) {
        String url = "http://api.openweathermap.org/geo/1.0/direct?q=" + cityName + "&limit=1&appid=9da6ee5d7126194e4330f568c0a97467";
        Log.i("check_url", url);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONArray jsonArray = new JSONArray(response);
                    if (jsonArray.length() == 0) {
                        tvcityname.setText("");
                        Toast.makeText(getActivity(), "Couldn't Find City", Toast.LENGTH_SHORT).show();
                    } else {
                        JSONObject jsonObject = jsonArray.getJSONObject(0);
                        String city = jsonObject.getString("name");
                        Double lati = jsonObject.getDouble("lat");
                        Double longi = jsonObject.getDouble("lon");
                        llSearchEdittxt.setText("");
                        getWeather(lati, longi, city);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                //Log.d("latlong_response", response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error instanceof NetworkError) {
                    SetNoInternet(true);
                } else if (error instanceof ParseError) {
                    llSearchEdittxt.setText("");
                    Toast.makeText(getActivity(), "Couldn't Find City", Toast.LENGTH_SHORT).show();
                }
                error.printStackTrace();
            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        requestQueue.add(stringRequest);
    }

    private void getWeather(Double lat, Double longi, String cityname) {
        String url = "https://api.openweathermap.org/data/2.5/onecall?lat=" + lat + "&" + "lon=" + longi + "&units=metric&exclude=hourly,minutely&appid=9da6ee5d7126194e4330f568c0a97467";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                initalloading.setVisibility(View.GONE);
                constraintLayout.setVisibility(View.VISIBLE);
                try {
                    WeatherRVmodalArrayList.clear();
                    DecimalFormat decimalFormat = new DecimalFormat("0");
                    Double temp = response.getJSONObject("current").getDouble("temp");
                    tvcityname.setText(cityname);
                    String timezone = response.getString("timezone");
                    String dateinfo = dateAndTime(timezone);
                    day = getdaynum(dateinfo);
                    String value = String.valueOf(day);
                    Log.d("check day", value);
                    Double Temp_feels_like = response.getJSONObject("current").getDouble("feels_like");
                    JSONArray currentArray = response.getJSONObject("current").getJSONArray("weather");
                    JSONObject currentWeatherObject = currentArray.getJSONObject(0);
                    String condition = currentWeatherObject.getString("description");
                    String Wea_Icon_url = "https://openweathermap.org/img/wn/" + currentWeatherObject.getString("icon") + "@2x.png";
                    tvcurrwea.setText(condition);
                    tvtemp.setText(decimalFormat.format(temp) + "°C");
                    tvTempFeelslike.setText("Feels Like " + decimalFormat.format(Temp_feels_like) + "°C");
                    Picasso.get().load(Wea_Icon_url).into(ivtempicon);
                    JSONArray rvDailyArray = response.getJSONArray("daily");
                    int count = 0;
                    String dayName = "";
                    for (int i = 0; i < rvDailyArray.length(); i++) {
                        JSONObject dailyObject = rvDailyArray.getJSONObject(i);
                        if (i == 0) {
                            dayName = "Today";
                        } else if ((day + i) > DaysArray.length - 1) {
                            dayName = DaysArray[count];
                            count++;
                        } else {
                            dayName = DaysArray[day + i];
                        }
                        Double maxTemp = dailyObject.getJSONObject("temp").getDouble("max");
                        Double minTemp = dailyObject.getJSONObject("temp").getDouble("min");
                        JSONArray jsonArray = dailyObject.getJSONArray("weather");
                        String conditionIcon = jsonArray.getJSONObject(0).getString("icon");
                        String description = jsonArray.getJSONObject(0).getString("description");
                        WeatherRVmodalArrayList.add(new WeatherRVmodal(dayName, decimalFormat.format(maxTemp), decimalFormat.format(minTemp), conditionIcon, description));
                    }
                    initRecyclerView();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error instanceof NetworkError) {
                    SetNoInternet(false);
                } else if (error instanceof ParseError) {
                    tvcityname.setText("");
                    Toast.makeText(getActivity(), "Enter Valid City Name", Toast.LENGTH_SHORT).show();
                    error.printStackTrace();
                }
            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        requestQueue.add(jsonObjectRequest);

    }

    public String dateAndTime(String timezone) {
        Date date = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("E',' dd/MM/yy hh:mm a");
        dateFormat.setTimeZone(TimeZone.getTimeZone(timezone));
        String today = dateFormat.format(date);
        today = today.replace("am", "AM");
        today = today.replace("pm", "PM");
        tvDateAndTime.setText(today);
        final String substring = today.substring(0, 3);
        return substring;
    }

    public int getdaynum(String day) {
        switch (day) {
            case "Mon": {
                return 0;
            }
            case "Tue": {
                return 1;
            }
            case "Wed": {
                return 2;
            }
            case "Thu": {
                return 3;
            }
            case "Fri": {
                return 4;
            }
            case "Sat": {
                return 5;
            }
            case "Sun": {
                return 6;
            }
            default:
                return -1;
        }
    }

    public void initRecyclerView() {

        linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(RecyclerView.HORIZONTAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        adapter = new WeatherRVadapter(WeatherRVmodalArrayList);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    public void SetNoInternet(boolean flag) {
        if (flag == true) constraintLayout.setVisibility(View.GONE);
        initalloading.setVisibility(View.GONE);
        NoInternetlayout.setVisibility(View.VISIBLE);
    }
}