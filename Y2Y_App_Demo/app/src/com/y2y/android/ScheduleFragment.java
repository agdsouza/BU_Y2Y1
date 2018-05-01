package com.y2y.android;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.app.Fragment;
import android.support.v7.view.menu.ShowableListMenu;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

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
    CheckBox cbRSVP;

    @Override
    public void onAttach(Context context) {   //The onAttach method, binds the fragment to the owner activity
        super.onAttach(context);
        HFL = (ControlFragInterface) context;  //context is a handle to the main activity, binding to interface
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Get view that inflates the layout
        View view = inflater.inflate(R.layout.fragment_schedule, container, false);

        // Find view that is defined in layout having the respective id
        mOutputText = view.findViewById(R.id.tvSchedHeader);
        lvEvents = view.findViewById(R.id.lvEvents);
        cbRSVP = view.findViewById(R.id.cbRSVP);
        eventArray = new ArrayList<ListEvent>();

        // onFetchEvents calls methods in MainActivity that sends rest requests to Salesforce with the
        // appropriate soql queries and returns the records to setEvents that displays them.
        try{
            HFL.onFetchEvents(view);
        } catch(UnsupportedEncodingException e) {
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

        // get all the fields for the events object we need and retrieve them from the record
        for (int event_row = 0; event_row<num_events; event_row++){
            String ID = records.getJSONObject(event_row).getString("Id");
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

            // Uf the event occurs all day then just set the starttime variable to "all-day
            if(isAllDayEvent.equals("true")){
                startTimeString = "All-Day";
            }
            else{ // check for start and end time and format appropriately
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

                // if it's a one-day event, ActivityDate will just be the startDateString
                if (startDateString.equals(endDateString)){
                    ActivityDate = startDateString;
                } else {//If the event occurs across multiple days, you need to display the start
                    // and end date along with the start and end time.
                    ActivityDate = "";
                    startTimeString = startDateString + " " + startTimeString;
                    endTimeString = endDateString + " " + endTimeString;
                }

            }

            /** Logging for debugging. Can use to check what information is going into the adapter for
            //custom list views **/
//            Log.e("id", ID);
//            Log.e("name", EventName);
//            Log.e("start", startTimeString);
//            Log.e("end", endTimeString);
//            Log.e("date", ActivityDate);

            // Add the list event with the populated fields to the event array and then to the event custom adapter
            ListEvent e = new ListEvent(ID, EventName, startTimeString, endTimeString, ActivityDate, Description, Location);
            eventArray.add(e);
            lvAdapter = new EventCustomAdapter(vCont, eventArray);
            lvEvents.setAdapter(lvAdapter);
        }
    }


}