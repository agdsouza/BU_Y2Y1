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

        ControlFragInterface HFL;

        @Override
        public void onAttach(Context context) {   //The onAttach method, binds the fragment to the owner.  Fragments are hosted by Activities, therefore, context refers to: ____________?
            super.onAttach(context);
            HFL = (ControlFragInterface) context;  //context is a handle to the main activity, let's bind it to our interface.
        }

        @Nullable
        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.fragment_feedback, container, false);

            edtFeedbackBox = (EditText) view.findViewById(R.id.edtFeedbackBox);
            btnSubmit = (Button) view.findViewById(R.id.btnSubmit);

            btnSubmit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String edtValue = edtFeedbackBox.getText().toString();
                    try {
                        HFL.postFeedback(edtValue);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
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
