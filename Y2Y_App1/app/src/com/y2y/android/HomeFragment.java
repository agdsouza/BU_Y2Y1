package com.y2y.android;

import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.UnsupportedEncodingException;

public class HomeFragment extends Fragment {

    private TextView tvNameLabel;
    private TextView tvBedLabel;
    private TextView tvStayLabel;
    private TextView tvWarningLabel;
    private TextView tvLockerLabel;

    ControlFragInterface HFL;


    @Override
    public void onAttach(Context context) {   //The onAttach method, binds the fragment to the owner.  Fragments are hosted by Activities, therefore, context refers to: ____________?
        super.onAttach(context);
        HFL = (ControlFragInterface) context;  //context is a handle to the main activity, let's bind it to our interface.
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        tvNameLabel = (TextView)view.findViewById(R.id.tvNameLabel);
        tvBedLabel = (TextView)view.findViewById(R.id.tvBedLabel);
        tvStayLabel = (TextView)view.findViewById(R.id.tvStayLabel);
        tvWarningLabel = (TextView)view.findViewById(R.id.tvWarningLabel);
        tvLockerLabel = (TextView)view.findViewById(R.id.tvLockerLabel);

        return view;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        try {
            HFL.onFetchDetailsStay(view);
            HFL.onFetchBed(view);
        } catch (UnsupportedEncodingException e1) {
            e1.printStackTrace();
        }

        super.onViewCreated(view, savedInstanceState);
    }

    public void setDetailsStay(JSONArray records) throws JSONException {
        String the_name = records.getJSONObject(0).getString("Name");
        tvNameLabel.setText("Name: " + the_name);
        String the_staydate = records.getJSONObject(0).getString("Last_Date_of_Stay__c");
        tvStayLabel.setText("Last Day of Stay: " + the_staydate);
        String the_majorwarnings = records.getJSONObject(0).getString("Major_Warnings__c");
        String the_minorwarnings = records.getJSONObject(0).getString("Minor_Warnings__c");
        tvWarningLabel.setText("Major Warnings: " + the_majorwarnings + "\nMinor Warnings: " + the_minorwarnings);
        String the_lockercombo = records.getJSONObject(0).getString("Locker_Combination__c");
        tvLockerLabel.setText("Locker Combo: " + the_lockercombo);

    }

    public void setBed(JSONArray records) throws JSONException {
        String the_bed = records.getJSONObject(0).getString("Name");
        tvBedLabel.setText("Bed: " + the_bed);

    }



}
