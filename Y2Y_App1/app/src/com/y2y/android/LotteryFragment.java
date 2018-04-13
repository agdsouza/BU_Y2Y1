package com.y2y.android;

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

public class LotteryFragment extends Fragment {

    private TextView tvLongtermLabel;
    private TextView tvLongtermDate;
    private TextView tvLTLotteryNum;
    private TextView tvEBedLabel;
    private TextView tvEBedDate;
    private TextView tvELotteryNum;
    private Button btnFetchLottery;


    ControlFragInterface HFL;

    @Override
    public void onAttach(Context context) {   //The onAttach method, binds the fragment to the owner.  Fragments are hosted by Activities, therefore, context refers to: ____________?
        super.onAttach(context);
        HFL = (ControlFragInterface) context;  //context is a handle to the main activity, let's bind it to our interface.
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_lottery, container, false);

        tvLongtermLabel = (TextView)view.findViewById(R.id.tvLongtermLabel);
        tvLongtermDate = (TextView)view.findViewById(R.id.tvLongtermDate);
        tvLTLotteryNum = (TextView)view.findViewById(R.id.tvLTLotteryNum);
        tvEBedLabel = (TextView)view.findViewById(R.id.tvEBedLabel);
        tvEBedDate = (TextView)view.findViewById(R.id.tvEBedDate);
        tvELotteryNum = (TextView)view.findViewById(R.id.tvELotteryNum);
        btnFetchLottery = (Button)view.findViewById(R.id.btnFetchLottery);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        try {
            HFL.onFetchLottery(view);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        super.onViewCreated(view, savedInstanceState);
    }

    public void setLottery(JSONArray records) throws JSONException {
        String longtermlabel = records.getJSONObject(0).getString("Type__c");
        tvLongtermLabel.setText("BedType: " + longtermlabel);
        String longtermdate = records.getJSONObject(0).getString("Lottery_Date__c");
        tvLongtermDate.setText("Longterm Date: " + longtermdate);
        String longtermlottery = records.getJSONObject(0).getString("Name");
        tvLTLotteryNum.setText("L Lottery #: " + longtermlottery);

        String ebedlabel = records.getJSONObject(1).getString("Type__c");
        tvEBedLabel.setText("BedType: " + ebedlabel);
        String ebeddate = records.getJSONObject(1).getString("Lottery_Date__c");
        tvEBedDate.setText("E Bed Date:" + ebeddate);
        String ebedlottery = records.getJSONObject(1).getString("Name");
        tvELotteryNum.setText("E Lottery #: " + ebedlottery);

    }
}
