package com.project.weatherman.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.icu.text.Transliterator;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputEditText;
import com.project.weatherman.R;
import com.project.weatherman.activity.global.AppConfig;
import com.project.weatherman.activity.model.ModelAreas;
import com.project.weatherman.utils.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dmax.dialog.SpotsDialog;


public class LocationSelectorActivity extends AppCompatActivity  {
    AutoCompleteTextView autoArea;
    private RequestQueue queue;
    private SpotsDialog dialog;
    ModelAreas modelAreas;
    List<ModelAreas> modelAreasList = new ArrayList<>();
    public static final String TAG_ID = "id";
    public static final String TAG_NAME = "name";
    public static final String TAG_STATUS_ID = "status_id";
    private SessionManager session_manager;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_selector);
        session_manager = new SessionManager(this);
        autoArea = (AutoCompleteTextView) findViewById(R.id.autoArea);
        dialog = new SpotsDialog(LocationSelectorActivity.this);
        dialog.show();
        queue = Volley.newRequestQueue(LocationSelectorActivity.this);
        Action_Postal_list();
        ArrayAdapter<ModelAreas> adapterArea =
                new ArrayAdapter<>(LocationSelectorActivity.this,
                        android.R.layout.simple_list_item_1, modelAreasList);

        adapterArea.setDropDownViewResource(android.R.layout.simple_list_item_1);
        autoArea.setAdapter(adapterArea);

        autoArea.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                ModelAreas modelAreas = adapterArea
                        .getItem(i);
                String id = modelAreas.getId();
                String name=modelAreas.getName();
                String status=modelAreas.getStatus_id();
                System.out.println("### areaId :" + id);
                session_manager.createLoginSession(id, name,
                        status);
                Intent intent=new Intent(LocationSelectorActivity.this,DashBoard.class);
                startActivity(intent);

            }
        });


    }

    public void Action_Postal_list() {

        System.out.println("### Appconfig.URL_POSTAL_ADDRESS " + AppConfig.URL_GET_LOCATION);

        String tag_json_obj = "json_obj_req";
        System.out.println("CAME 1");
        final StringRequest request = new StringRequest(Request.Method.GET,
                AppConfig.URL_GET_LOCATION, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                System.out.println("###  Appconfig.URL_POSTAL_ADDRESS onResposne " + response);

                Log.d("TAG", response.toString());
                try {
                    JSONObject obj = new JSONObject(response);
                        modelAreasList.clear();
                        JSONArray array_results = obj.getJSONArray("data");

                        for (int count = 0; count < array_results.length(); count++) {
                            JSONObject obj_data = array_results.getJSONObject(count);


                            String id=obj_data.getString(TAG_ID);
                            String name=obj_data.getString(TAG_NAME);
                            String status_id=obj_data.getString(TAG_STATUS_ID);

                            modelAreas = new ModelAreas();
                            modelAreas.setId(id);
                            modelAreas.setName(name);
                            modelAreas.setStatus_id(status_id);
                            modelAreasList.add(modelAreas);

                            System.out.println("### area_id" + id);
                            System.out.println("### area_name" + name);
                            System.out.println("### area_name" + status_id);

                        }



                } catch (JSONException e) {
                    //
                    e.printStackTrace();
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
    @Override
    public void onBackPressed() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("Exit Application?");
        alertDialogBuilder
                .setMessage("Click yes to exit!")
                .setCancelable(false)
                .setPositiveButton("Yes",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                moveTaskToBack(true);
                                android.os.Process.killProcess(android.os.Process.myPid());
                                System.exit(1);
                            }
                        })

                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        dialog.dismiss();
                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }


}