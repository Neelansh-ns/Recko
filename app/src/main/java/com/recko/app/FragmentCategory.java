package com.recko.app;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.recko.app.Adapters.Category_InCategoryAndCompanyAdapter;
import com.recko.app.Misc.Constants;
import com.recko.app.Model.Category_InCategoryAndCompanyModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FragmentCategory.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FragmentCategory#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentCategory extends androidx.fragment.app.Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private RecyclerView rv_categorylist;
    private Category_InCategoryAndCompanyAdapter categoryInCategoryAndCompanyAdapter;

    private Activity thisActivity;

    List categorylist;
    public FragmentCategory() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FragmentCategory.
     */
    // TODO: Rename and change types and number of parameters
    public static FragmentCategory newInstance(String param1, String param2) {
        FragmentCategory fragment = new FragmentCategory();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v= inflater.inflate(R.layout.fragment_category, container, false);
        rv_categorylist=v.findViewById(R.id.rv_categorylist);
        thisActivity=(Activity)getActivity();

        categorylist = new ArrayList();

        rv_categorylist.setHasFixedSize(true);
        rv_categorylist.setLayoutManager(new LinearLayoutManager(getActivity(),RecyclerView.VERTICAL,false));
        categoryInCategoryAndCompanyAdapter =new Category_InCategoryAndCompanyAdapter(getActivity(),categorylist,thisActivity);
        rv_categorylist.setAdapter(categoryInCategoryAndCompanyAdapter);
        get_categories();






        return v;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }



    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    private void get_categories(){

        JSONObject json = new JSONObject();
        try {
            json.put("industry_uuid",getActivity().getIntent().getStringExtra("industry_uuid"));
            Log.d("zzz json", json.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,APIURL.url+"category/list", json,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        Log.d("zzz", APIURL.url+"category/list"+"\nonResponse: "+response);

                        try {
                            JSONArray array= response.getJSONArray("data");
                            Log.d("zzzarray",array.toString());
                            for (int i = 0; i < array.length(); i++) {
                                try {
                                    JSONObject object = array.getJSONObject(i);
                                    Log.d("zzzobjct",object.toString());
                                    String industry = object.getString("industry");
                                    String category_uuid = object.getString("category_uuid");
                                    String image_url = object.getString("image_url");
                                    String name = object.getString("name");
                                    String max_total_commission = object.getString("max_total_commission");
                                    max_total_commission = Constants.fixDoubleString(max_total_commission);

                                    Category_InCategoryAndCompanyModel category_inCategoryAndCompanyModel= new Category_InCategoryAndCompanyModel(industry,category_uuid,image_url,name);
                                    category_inCategoryAndCompanyModel.setMax_total_commission(max_total_commission);
                                    categorylist.add(category_inCategoryAndCompanyModel);
                                    Log.d("zzz id",industry +" "+name );

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                            categoryInCategoryAndCompanyAdapter.notifyDataSetChanged();
                        }
                        catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
				Constants.logVolleyError(error);
                error.printStackTrace();
                Toast.makeText(getActivity(),"Oops! Please try again later",Toast.LENGTH_SHORT).show();
            }
        });

        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        requestQueue.add(jsonObjectRequest);
    }
}
