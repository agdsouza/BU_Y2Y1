package com.y2y.android;

import android.content.Context;
import android.os.AsyncTask;
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

import com.google.android.gms.tasks.OnCompleteListener;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

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
    private JSONArray lotteryrecords;
    private String LT_lottery_numbers = "";
    private String E_lottery_numbers = "";
    private String LC_lottery_numbers = "";

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
        View view = inflater.inflate(R.layout.fragment_lottery, container, false);

        // Find view that is defined in layout having the respective id
        tvLongtermLabel = (TextView) view.findViewById(R.id.tvLongtermLabel);
        tvLongtermDate = (TextView) view.findViewById(R.id.tvLongtermDate);
        tvLTLotteryNum = (TextView) view.findViewById(R.id.tvLTLotteryNum);
        tvEBedLabel = (TextView) view.findViewById(R.id.tvEBedLabel);
        tvEBedDate = (TextView) view.findViewById(R.id.tvEBedDate);
        tvELotteryNum = (TextView) view.findViewById(R.id.tvELotteryNum);
        tvLastCallLabel = (TextView) view.findViewById(R.id.tvLastCallLabel);
        tvLastCallDate = (TextView) view.findViewById(R.id.tvLastCallDate);
        tvLCLotteryNum = (TextView) view.findViewById(R.id.tvLCLotteryNum);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        // onFetchLottery and onFetchLotteryNumber calls methods in MainActivity that sends rest requests to Salesforce with the
        // appropriate soql queries and returns the records to setLottery and setLotteryNumber that displays them.
        try {
            CFL.onFetchLottery(view);
            CFL.onFetchLotteryNumber(view);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        super.onViewCreated(view, savedInstanceState);
    }

    public void setLottery(JSONArray records) throws JSONException {
        // save lottery records (need bed type) to get lottery number
        lotteryrecords = records;
        // currently using date 2018-05-01 which has all 3 bed types to demonstrate
        // according to bed type, display the bed type and date of the bed assignment
        for (int i = 0; i < records.length(); i++) {
            if (records.getJSONObject(i).getString("Type__c").equals("Long Term")) {
                String longtermlabel = records.getJSONObject(i).getString("Type__c");
                tvLongtermLabel.setText("BedType: " + longtermlabel);
                String longtermdate = records.getJSONObject(i).getString("Lottery_Date__c");
                tvLongtermDate.setText("Longterm Date: " + longtermdate);
            }
            if (records.getJSONObject(i).getString("Type__c").equals("E-Bed")) {
                String ebedlabel = records.getJSONObject(i).getString("Type__c");
                tvEBedLabel.setText("BedType: " + ebedlabel);
                String ebeddate = records.getJSONObject(i).getString("Lottery_Date__c");
                tvEBedDate.setText("E Bed Date:" + ebeddate);
            }
            if (records.getJSONObject(i).getString("Type__c").equals("Last Call")) { //Type is Last Call
                String lastcalllabel = records.getJSONObject(i).getString("Type__c");
                tvLastCallLabel.setText("BedType: " + lastcalllabel);
                String lastcalldate = records.getJSONObject(i).getString("Lottery_Date__c");
                tvLastCallDate.setText("Lastcall Date: " + lastcalldate);
            }
        }
    }

    public void setLotteryNumber(JSONArray records) throws JSONException {
        // currently using date 2018-04-10 which has all 3 bed types to demonstrate
        for (int i = 0; i < records.length(); i++) {
            String lotterynum = records.getJSONObject(i).getString("Lottery_Number_Daily__c");
            for (int j = 0; j < lotteryrecords.length(); j++) {
                String lotteryID = records.getJSONObject(i).getString("Lottery__c");
                // lottery ID in lottery entry object contains 3 chars at the end that doesn't match with lottery object so needs to be removed
                lotteryID = lotteryID.substring(0,lotteryID.length()-3);

                // add lottery number winners to respective lottery number types to be displayed later
                if ( lotteryrecords.getJSONObject(j).getString("Name").equals(lotteryID) ){
                    if (lotteryrecords.getJSONObject(j).getString("Type__c").equals("Long Term")) {
                        LT_lottery_numbers = LT_lottery_numbers + lotterynum + ", ";
                    }
                    if (lotteryrecords.getJSONObject(j).getString("Type__c").equals("E-Bed")) {
                        E_lottery_numbers = E_lottery_numbers + lotterynum + ", ";
                    }
                    if (lotteryrecords.getJSONObject(j).getString("Type__c").equals("Last Call")) {
                        LC_lottery_numbers = LC_lottery_numbers + lotterynum + ", ";
                    }
                }
            }
        }
        // display the lottery number winners for each respective bed type
        tvLTLotteryNum.setText("Lottery #: " + LT_lottery_numbers);
        tvELotteryNum.setText("Lottery #: " + E_lottery_numbers);
        tvLCLotteryNum.setText("Lottery #: " + LC_lottery_numbers);
    }
}
