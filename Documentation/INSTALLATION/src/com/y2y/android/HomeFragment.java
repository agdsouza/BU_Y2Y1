package com.y2y.android;

import android.app.FragmentTransaction;
import android.content.Context;
import android.graphics.Color;
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

    // Get instance of interface to communicate with MainActivity
    ControlFragInterface CFL;

    @Override
    public void onAttach(Context context) {   //The onAttach method, binds the fragment to the owner activity
        super.onAttach(context);
        CFL = (ControlFragInterface) context;  //context is a handle to the main activity, binding to interface
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Get view that inflates the layout
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // Find view that is defined in layout having the respective id
        tvNameLabel = view.findViewById(R.id.tvNameLabel);
        tvBedLabel = view.findViewById(R.id.tvBedLabel);
        tvStayLabel = view.findViewById(R.id.tvStayLabel);
        tvWarningLabel = view.findViewById(R.id.tvWarningLabel);
        tvLockerLabel = view.findViewById(R.id.tvLockerLabel);

        return view;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        // onFetchDetailsStay and onFetchBed calls methods in MainActivity that sends rest requests to Salesforce with the
        // appropriate soql queries and returns the records to setDetailsStay and setBed that displays them.
        try {
            CFL.onFetchDetailsStay(view);
            CFL.onFetchBed(view);
        } catch (UnsupportedEncodingException e1) {
            e1.printStackTrace();
        }
        super.onViewCreated(view, savedInstanceState);
    }

    // setDetailsStay goes through the records sent from MainActivity and depending on the bed type,
    // diplays the name of consumer, last date of stay, warnings, and locker combination
    public void setDetailsStay(JSONArray records) throws JSONException {
        String the_name = records.getJSONObject(0).getString("Name");
        tvNameLabel.setText("Hello " + the_name + "!");
        tvNameLabel.setTextColor(Color.BLACK);
        String the_staydate = records.getJSONObject(0).getString("Last_Date_of_Stay__c");
        tvStayLabel.setText("Last Day of Stay: " + the_staydate);
        String the_majorwarnings = records.getJSONObject(0).getString("Major_Warnings__c");
        String the_minorwarnings = records.getJSONObject(0).getString("Minor_Warnings__c");
        tvWarningLabel.setText("Major Warnings: " + the_majorwarnings + "\nMinor Warnings: " + the_minorwarnings);
        String the_lockercombo = records.getJSONObject(0).getString("Locker_Combination__c");
        tvLockerLabel.setText("Locker Combo: " + the_lockercombo);

    }

    // setBed displays the records from MainActivity for the bed area and is separate from setDetailsStay
    // due to a different nested soql query that requires a different set of methods
    public void setBed(JSONArray records) throws JSONException {
        String the_bed = records.getJSONObject(0).getString("Name");
        tvBedLabel.setText("Bed: " + the_bed);
    }



}
