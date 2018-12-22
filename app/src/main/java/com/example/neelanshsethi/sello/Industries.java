package com.example.neelanshsethi.sello;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.chip.Chip;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Industries extends AppCompatActivity {

    TextView textView;
    GridView gridView;
    //    String [] industry_names={"Managements","Finance","Medical","Lorem Ipsum","Neelansh","Abhishek","Chinmay","Khagesh"};
//  int [] industry_logos={R.drawable.chip_education,R.drawable.chip_education,R.drawable.chip_education,R.drawable.chip_education,R.drawable.chip_education,R.drawable.chip_education,R.drawable.chip_education,R.drawable.chip_education};
    private Drawable [] industry_logos=new Drawable[30];
    //    String [] industry_names = new String[20];
    private  List<String> industry_names = new ArrayList<String>();
    private  static List<String> ind_names = new ArrayList<String>();
    private static List<String> industry_uuid = new ArrayList<String>();
    //    List<String > industry_logos = new ArrayList<String>();
    IndustryAdapter industryAdapter;
    Button next;
    private String idToken;
    private static List<String> selectedChips = new ArrayList<String>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_industries);
        next= findViewById(R.id.Next);
        gridView=findViewById(R.id.gridview);
        industryAdapter= new IndustryAdapter(this, industry_names, industry_logos);
        gridView.setAdapter(industryAdapter);

        getdata();

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (selectedChips.isEmpty())
                    Toast.makeText(getApplicationContext(),"Please choose the Industires",Toast.LENGTH_SHORT).show();
                else if(!InternetConnection.checkConnection(Industries.this)) {
                    Intent intent = new Intent(Industries.this, AwwSnap.class);
                    startActivity(intent);
                    Log.d("zzz", selectedChips.toString());
                }
                else
                {
                    send_industries();
                }

            }
        });

        textView=findViewById(R.id.txtindustries);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Industries.this, NavigationDashboard.class);
                startActivity(intent);
            }
        });

    }

    private void getdata(){
        JSONObject json = new JSONObject();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,APIURL.url+"industry/list", json,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        Log.d("zzz", APIURL.url+"industry/list"+"\nonResponse: "+response);

                        try {
                            JSONArray array= response.getJSONArray("data");
                            Log.d("zzzarray",array.toString());
                            for (int i = 0; i < array.length(); i++) {
                                try {
                                    JSONObject object = array.getJSONObject(i);
                                    Log.d("zzzobjct",object.toString());
                                    String pic_base64 = object.getString("image_url");
                                    String name = object.getString("name");
                                    Log.d("zzz",pic_base64);
                                    String uuid=object.getString("industry_uuid");
                                    if(pic_base64.contains("data")) {


                                        Log.d("zzz j",String.valueOf(" " +i));
                                        String base64Image = pic_base64.split(",")[1];
                                        byte[] decodedString = Base64.decode(base64Image, Base64.DEFAULT);
                                        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                                        Drawable d = new BitmapDrawable(getResources(), decodedByte);
                                        industry_logos[i]=d;
                                        industry_names.add(i,name);
                                        ind_names.add(i,name);
                                        industry_uuid.add(i,uuid);


                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                            industryAdapter.notifyDataSetChanged();
                        }
                        catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                error.printStackTrace();
                Toast.makeText(Industries.this,"Oops! Please try again later",Toast.LENGTH_SHORT).show();
            }
        });

        RequestQueue requestQueue = Volley.newRequestQueue(Industries.this);
        requestQueue.add(jsonObjectRequest);
    }

    private void send_industries()
    {
        final FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();
        mUser.getIdToken(true)
                .addOnCompleteListener(new OnCompleteListener<GetTokenResult>() {
                    public void onComplete(@NonNull Task<GetTokenResult> task) {
                        if (task.isSuccessful()) {
                            idToken = task.getResult().getToken();
                            Log.d("tokennn",idToken);
                            // Send token to your backend via HTTPS
                            // ...
                        } else {
                            // Handle error -> task.getException();
                        }
                    }
                });

        JSONArray industries = new JSONArray(selectedChips);
        JSONObject json = new JSONObject();

        try {
            json.put("seller_uuid",mUser.getUid());
            json.put("industry_uuids",industries);
            json.put("firebase_token",idToken);
            Log.d("zzz json", json.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,APIURL.url+"user/insert_industries", json,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        Log.d("zzz", APIURL.url+"user/insert_industries"+"\nonResponse: "+response);

                        try {
                            if(response.getString("code").equals("200"))
                            {

                                    Intent intent = new Intent(Industries.this, NavigationDashboard.class);
                                    startActivity(intent);
                                    Log.d("zzz", selectedChips.toString());

                            }
                            else{
                                Toast.makeText(getApplicationContext(),"Oooops! Please try again later",Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                error.printStackTrace();
                Toast.makeText(Industries.this,"Oops! Please try again later",Toast.LENGTH_SHORT).show();
            }
        });

        RequestQueue requestQueue = Volley.newRequestQueue(Industries.this);
        requestQueue.add(jsonObjectRequest);
    }

    void selectChips(Chip chip)
    {
        selectedChips.add(industry_uuid.get(ind_names.indexOf(chip.getText().toString())));
        Log.d("zzz added",chip.getText().toString() +" "+selectedChips.toString());
        Log.d("zzzzzzzz added",ind_names.indexOf("Education") +" "+selectedChips.toString());
    }

    void removeChips(Chip chip)
    {
        selectedChips.remove(industry_uuid.get(ind_names.indexOf(chip.getText().toString())));

        Log.d("zzz remove",chip.getText().toString() +" "+selectedChips.toString());
    }

    void clearchips()
    {
        selectedChips.clear();
    }
}
