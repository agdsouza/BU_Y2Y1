package com.y2y.android;


import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.SeekBar;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

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

import com.salesforce.androidsdk.rest.RestRequest;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.UnsupportedEncodingException;

public class SurveyFragment extends Fragment {

    private RatingBar ratingbar;
    private EditText edtSurveyBox;
    private Button btnSubmitSurvey;

    // Get instance of interface to communicate with MainActivity
    ControlFragInterface HFL;

    @Override
    public void onAttach(Context context) {   //The onAttach method, binds the fragment to the owner activity
        super.onAttach(context);
        HFL = (ControlFragInterface) context;  //context is a handle to the main activity, binding to interface
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Get view that inflates the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_survey, container, false);

        // Find view that is defined in layout having the respective id
        ratingbar = (RatingBar) view.findViewById(R.id.ratingbar);
        edtSurveyBox = view.findViewById(R.id.edtSurveyBox);
        btnSubmitSurvey = view.findViewById(R.id.btnSubmitSurvey);

        // Button listens for input for the submit button at which point the input in the survey rating and survey box edittext will be sent to
        // the method postSurvey in MainActivity which will call the necessary soql queries and post the standard survey in Salesforce
        btnSubmitSurvey.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try{
                    // Get the comments in survey box and a float rating and send to postSurvey method
                    String edtValue = edtSurveyBox.getText().toString();
                    float rating =  ratingbar.getRating();
                    HFL.postSurvey(rating, edtValue);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                // Clear survey box once submitted
                edtSurveyBox.setText("");

            }
        });
        return view;
    }

}
