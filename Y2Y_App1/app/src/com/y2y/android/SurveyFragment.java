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
import android.widget.SeekBar;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;

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
        SeekBar sb = view.findViewById(R.id.rb);
        final TextView rating = view.findViewById(R.id.rating);
        final EditText et = view.findViewById(R.id.response);
        final Button save = view.findViewById(R.id.save);

        sb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int progress;
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                progress = Math.round(i);
                rating.setText(Integer.toString(progress));

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                rating.setText(Integer.toString(progress));

            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //the following gives me errors so its commented out.
                /*
                try{
                    HFL.postSurvey(view);

                } catch (UnsupportedEncodingException e1){
                    e1.printStackTrace();
                }*/


            }
        });
        return view;
    }

}
