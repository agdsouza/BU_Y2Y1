package com.mytrail.android;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EventCustomAdapter extends BaseAdapter {

    private ArrayList<ListEvent> eventArray;
    private Context context;
    private String rsvp_str;
    private List rsvp_lst;
    private String event_id;
    private ControlFragInterface HFL;


    public EventCustomAdapter(Context c, ArrayList<ListEvent> le) {
        this.context = c;
        this.eventArray = le;
    }

    @Override
    public int getCount() {
        return eventArray.size();
    }

    @Override
    public Object getItem(int position) {
        return eventArray.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        HFL = (ControlFragInterface) context;

        View row;
        if (convertView == null) {
             LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
             row = inflater.inflate(R.layout.event_row_layout, parent, false);
        }

        else {
             row = convertView;
        }

        TextView tvEventName = row.findViewById(R.id.tvEventName);
        TextView tvEventTime = row.findViewById(R.id.tvEventTime);
        final CheckBox cbRSVP = row.findViewById(R.id.cbRSVP);

        tvEventName.setText(eventArray.get(position).getEventName());

        rsvp_str = eventArray.get(position).getEventDescription();

        //Get all the contacts in the Description
        String[] rsvp_contacts = rsvp_str.split(", ");
        rsvp_lst = Arrays.asList(rsvp_contacts);

        //If the list of RSVPs contains the current signed-in user, make sure the view for the
        //checkbox will be checked
        if (rsvp_lst.contains("Monica Chiu")){
            cbRSVP.setChecked(true);
        }

        //Is there isn't a given location follow this view format
        if (eventArray.get(position).getEventLocation().equals("null")){
            //If the event isn't an All-Day event, show the date, start time and end time.
            if (eventArray.get(position).getEventStartTime() != "All-Day"){
                tvEventTime.setText(eventArray.get(position).getEventDate() + "\n" +
                        eventArray.get(position).getEventStartTime() + " to " +
                        eventArray.get(position).getEventEndTime());
            }
            //If it is an all-day event, just show the event date.
            else{
                tvEventTime.setText(eventArray.get(position).getEventDate());
            }
        }

        //If there is a given location, follow this view format
        else{
            if (eventArray.get(position).getEventStartTime() != "All-Day"){
                tvEventTime.setText(eventArray.get(position).getEventLocation() + "\n" +
                        eventArray.get(position).getEventDate() + "\n" +
                        eventArray.get(position).getEventStartTime() + " to " +
                        eventArray.get(position).getEventEndTime());
            }
            else{
                tvEventTime.setText(eventArray.get(position).getEventDate());
            }
        }


        cbRSVP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(cbRSVP.isChecked()){
                    try{
                        event_id = eventArray.get(position).getEventId();
                        Log.v("B4 main_act ", event_id);
                        if (rsvp_str.equals("null")){
                            HFL.postRSVP(event_id, rsvp_str + ", dumb");
                        }
                        else{
                            HFL.postRSVP(event_id, "dumber");
                        }
                    }
                    catch (IOException e){
                        e.printStackTrace();
                    }
                }
            }
        });

        return row;
    }
}
