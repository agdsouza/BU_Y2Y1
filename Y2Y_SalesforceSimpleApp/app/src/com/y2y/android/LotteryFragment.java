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
    private TextView tvLastCallLabel;
    private TextView tvLastCallDate;
    private TextView tvLCLotteryNum;


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
        tvLastCallLabel = (TextView)view.findViewById(R.id.tvLastCallLabel);
        tvLastCallDate =(TextView)view.findViewById(R.id.tvLastCallDate);
        tvLCLotteryNum = (TextView)view.findViewById(R.id.tvLCLotteryNum);

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
        //currently using date 2018-04-10 which has all 3 bed types to demonstrate
        for (int i=0;i<records.length();i++) {
            if (records.getJSONObject(i).getString("Type__c").equals("Long Term")) {
                String longtermlabel = records.getJSONObject(i).getString("Type__c");
                tvLongtermLabel.setText("BedType: " + longtermlabel);
                String longtermdate = records.getJSONObject(i).getString("Lottery_Date__c");
                tvLongtermDate.setText("Longterm Date: " + longtermdate);
                String longtermlottery = records.getJSONObject(i).getString("Name");
                tvLTLotteryNum.setText("Longterm Lottery #: " + longtermlottery);
            }
            if (records.getJSONObject(i).getString("Type__c").equals("E-Bed")) {
                String ebedlabel = records.getJSONObject(i).getString("Type__c");
                tvEBedLabel.setText("BedType: " + ebedlabel);
                String ebeddate = records.getJSONObject(i).getString("Lottery_Date__c");
                tvEBedDate.setText("E Bed Date:" + ebeddate);
                String ebedlottery = records.getJSONObject(i).getString("Name");
                tvELotteryNum.setText("E Bed Lottery #: " + ebedlottery);
            }
            if (records.getJSONObject(i).getString("Type__c").equals("Last Call")) { //Type is Last Call
                String lastcalllabel = records.getJSONObject(i).getString("Type__c");
                tvLastCallLabel.setText("BedType: " + lastcalllabel);
                String lastcalldate = records.getJSONObject(i).getString("Lottery_Date__c");
                tvLastCallDate.setText("Lastcall Date: " + lastcalldate);
                String lastcalllottery = records.getJSONObject(i).getString("Name");
                tvLCLotteryNum.setText("Lastcall Lottery #: " + lastcalllottery);
            }
        }
    }
}
