package com.mytrail.android;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class ScheduleFragment extends Fragment{

    private TextView mOutputText;
    ControlFragInterface HFL;
    private ListView lvEvents;
    private ListAdapter lvAdapter;
    private ArrayList<ListEvent> eventArray;
    private Context vCont;

    @Override
    public void onAttach(Context context) {   //The onAttach method, binds the fragment to the owner.  Fragments are hosted by Activities, therefore, context refers to: ____________?
        super.onAttach(context);
        HFL = (ControlFragInterface) context;  //context is a handle to the main activity, let's bind it to our interface.
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_schedule, container, false);
        mOutputText = (TextView) view.findViewById(R.id.tvSchedHeader);
        lvEvents = (ListView) view.findViewById(R.id.lvEvents);
        eventArray = new ArrayList<ListEvent>();

        //Call the method in the Control Fragment Interface that gets all the events.
        try{
            HFL.onFetchEvents(view);
        }

        catch(UnsupportedEncodingException e) {
            Log.v("ERROR:", "YOU CANT DO THIS");
            e.printStackTrace();
        }

        return view;

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        vCont = view.getContext();

    }

    public void setEvents(JSONArray records) throws JSONException{
        List<String> eventStrings = new ArrayList<String>();
        int num_events = records.length();
        for (int event_row = 0; event_row<num_events; event_row++){
            String EventName = records.getJSONObject(event_row).getString("Subject");
            String ActivityDate = records.getJSONObject(event_row).getString("ActivityDate");
            String Description = records.getJSONObject(event_row).getString("Description");
            String isAllDayEvent = records.getJSONObject(event_row).getString("IsAllDayEvent");
            String StartDateTime = records.getJSONObject(event_row).getString("StartDateTime");
            String EndDateTime = records.getJSONObject(event_row).getString("EndDateTime");
            String RecordTypeId = records.getJSONObject(event_row).getString("RecordTypeId");
            String Location = records.getJSONObject(event_row).getString("Location");
            String startTimeString = "";
            String endTimeString = "";

            if(isAllDayEvent.equals("true")){
                startTimeString = "All-Day";
            }

            else{
                String[] startDate_splice = StartDateTime.split("T");
                String startDateString = startDate_splice[0];

                String[] startTime_splice = startDate_splice[1].split("\\+");
                StringBuilder sb = new StringBuilder(startTime_splice[0]);
                sb.delete(4, 11);
                startTimeString = sb.toString();


                String[] endDate_splice = EndDateTime.split("T");
                String endDateString = endDate_splice[0];

                String[] endTime_splice = endDate_splice[1].split("\\+");
                sb = new StringBuilder(endTime_splice[0]);
                sb.delete(4, 11);
                endTimeString = sb.toString();

                if (startDateString.equals(endDateString)){
                    ActivityDate = startDateString;
                }

                else {
                    ActivityDate = "";
                    startTimeString = startDateString + " " + startTimeString;
                    endTimeString = endDateString + " " + endTimeString;
                }

            }

            Log.e("name", EventName);
            Log.e("start", startTimeString);
            Log.e("end", endTimeString);
            Log.e("date", ActivityDate);
            ListEvent e = new ListEvent(EventName, startTimeString, endTimeString, ActivityDate);
            eventArray.add(e);
            Log.e("HELLO", e.getEventName());
            lvAdapter = new EventCustomAdapter(vCont, eventArray);
            lvEvents.setAdapter(lvAdapter);
        }
    }


}