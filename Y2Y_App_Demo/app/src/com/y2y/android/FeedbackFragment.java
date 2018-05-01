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

public class FeedbackFragment extends Fragment {

        private EditText edtFeedbackBox;
        private Button btnSubmit;

        // Get instance of interface to communicate with MainActivity
        ControlFragInterface HFL;

        @Override
        public void onAttach(Context context) {   //The onAttach method, binds the fragment to the owner activity
            super.onAttach(context);
            HFL = (ControlFragInterface) context;  //context is a handle to the main activity, binding to interface
        }

        @Nullable
        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            // Get view that inflates the layout
            View view = inflater.inflate(R.layout.fragment_feedback, container, false);

            // Find view that is defined in layout having the respective id
            edtFeedbackBox = view.findViewById(R.id.edtFeedbackBox);
            btnSubmit = view.findViewById(R.id.btnSubmit);

            // Button listens for input for the submit button at which point the input in the feedback box edittext will be sent to
            // the method postFeedback in MainActivity which will call the necessary soql queries and post the feedback survey in Salesforce
            btnSubmit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String edtValue = edtFeedbackBox.getText().toString();
                    try {
                        HFL.postFeedback(edtValue);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    // Clear feedback box text after submitting
                    edtFeedbackBox.setText("");
                }
            });
            return view;
        }

        @Override
        public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);
        }



}
