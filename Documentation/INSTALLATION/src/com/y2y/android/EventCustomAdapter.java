package com.y2y.android;


import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.google.api.client.util.StringUtils;

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
    private String current_user;


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

    public String getUsername(Context context){
        String curr = context.getApplicationContext().getString(R.string.username);
        return curr;
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

        current_user = getUsername(context);

        rsvp_str = eventArray.get(position).getEventDescription();

        //Get all the contacts in the Description and split it by the delimiter to produce a list of
        //RSVP names.
        String[] rsvp_contacts = rsvp_str.split(", ");
        rsvp_lst = Arrays.asList(rsvp_contacts);

        //If the list of RSVPs contains the current signed-in user, make sure the view for the
        //checkbox will be checked
        if (rsvp_lst.contains(current_user)){
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
                tvEventTime.setText(eventArray.get(position).getEventDate() + "\n" +
                "All-Day");
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
                tvEventTime.setText(eventArray.get(position).getEventLocation() + "\n" +
                        eventArray.get(position).getEventDate() + "\n" +
                        "All-Day");
            }
        }

        //If someone checks or unchecks the RSVP checkbox, this method will be alerted
        cbRSVP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //If the RSVP button has been checked, this will be executed
                if(cbRSVP.isChecked()){
                    try{
                        event_id = eventArray.get(position).getEventId();
                        //If there are no previous RSVPs, call the method to add a comma then the name
                        if (rsvp_str != "null"){
                            HFL.postRSVP(event_id, rsvp_str + "," + current_user);
                        }
                        //If there are previous RSVPs, call the method and just add the name into the descripton
                        else{
                            HFL.postRSVP(event_id, current_user);
                        }
                    }
                    catch (IOException e){
                        e.printStackTrace();
                    }
                }

                //If RSVP is unchecked, remove current user from the list of RSVPs.
                else{

                    try{
                        event_id = eventArray.get(position).getEventId();
                        //If the rsvp lst is not null, check for the current user's name
                        if (rsvp_str != ""){

                            StringBuilder strcat = new StringBuilder();
                            String delimiter = "";
                            String[] split = rsvp_str.split(",");
                            List lst = Arrays.asList(split);

                            //If it contains the current user, remove the user from the lst.
                            if (lst.contains(current_user)){
                                lst.remove(current_user);
                            }

                            //Recreate the RSVP lst in the description as comma seprarted string
                            if (!(lst.contains("null"))){
                                for(int i = 0; i < lst.size(); i++){
                                    strcat.append(delimiter);
                                    strcat.append(lst.get(i));
                                    delimiter = ",";
                                }
                            }

                            //Set the new rspv_str and push it to the database.
                            rsvp_str = strcat.toString();
                            Log.v("RSVP", rsvp_str);
                            HFL.postRSVP(event_id, rsvp_str);
                        }
                    }
                    catch (IOException e){
                        e.printStackTrace();
                    }
                }
            }
        });
        //The view of the event row is returned to be displayed
        return row;
    }
}
