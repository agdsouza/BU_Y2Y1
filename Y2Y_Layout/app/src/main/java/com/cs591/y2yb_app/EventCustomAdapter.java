package com.cs591.y2yb_app;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class EventCustomAdapter extends BaseAdapter {

    private ArrayList<ListEvent> eventArray;
    private Context context;

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
    public View getView(int position, View convertView, ViewGroup parent) {

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

        tvEventName.setText(eventArray.get(position).getEventName());

        if (eventArray.get(position).getEventStartTime() != ""){
            tvEventTime.setText(eventArray.get(position).getEventDate() + "\n" +
                    eventArray.get(position).getEventStartTime() + " to " +
                    eventArray.get(position).getEventEndTime());
        }
        else{
            tvEventTime.setText(eventArray.get(position).getEventDate());
        }

        return row;
    }
}
