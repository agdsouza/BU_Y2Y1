package com.mytrail.android;


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
    ControlFragInterface HFL;

    @Override
    public void onAttach(Context context) {   //The onAttach method, binds the fragment to the owner.  Fragments are hosted by Activities, therefore, context refers to: ____________?
        super.onAttach(context);
        HFL = (ControlFragInterface) context;  //context is a handle to the main activity, let's bind it to our interface.
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_survey, container, false);
        ratingbar = (RatingBar) view.findViewById(R.id.ratingbar);
        edtSurveyBox = view.findViewById(R.id.edtSurveyBox);
        btnSubmitSurvey = view.findViewById(R.id.btnSubmitSurvey);

        btnSubmitSurvey.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try{
                    String edtValue = edtSurveyBox.getText().toString();
                    float rating =  ratingbar.getRating();
                    HFL.postSurvey(rating, edtValue);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                edtSurveyBox.setText("");

            }
        });
        return view;
    }

}
