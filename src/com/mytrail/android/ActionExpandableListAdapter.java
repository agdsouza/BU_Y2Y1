package com.mytrail.android;

import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.salesforce.androidsdk.smartsync.manager.SyncManager;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class ActionExpandableListAdapter extends BaseExpandableListAdapter {

    private Context context;
    private List<String> listActionHeader;
    private HashMap<String, List<String>> listActionChild;

    private CheckBox cblblStepComplete;

    private RadioButton rbCompleted; // radio button for the completed status
    private RadioButton rbDropped; // radio button for the dropped status
    private RadioButton rbNone; // radio button for not making any changes
    private RadioGroup radio; // radio group for the three above buttons

    private ArrayList<ActionItem> actionItems; // holds the action items
    private HashMap<Integer, ArrayList<Boolean>> checkedSteps;
    private int lastSelectedIndex; // stores the last selected action item index

    UpdateListener UL; // interface variable to communicate and send to ActionFragment



    public ActionExpandableListAdapter(Context context, List<String> listActionHeader,
                                       HashMap<String, List<String>> listActionChild, UpdateListener UL, ArrayList<ActionItem> actionItems) {

        this.context = context;
        this.listActionHeader = listActionHeader;
        this.listActionChild = listActionChild;
        this.UL = UL;
        this.actionItems = actionItems;
        this.checkedSteps = new HashMap<Integer, ArrayList<Boolean>>();
        for (int i = 0; i < actionItems.size(); i++) {
            ArrayList<Boolean> checked = new ArrayList<Boolean>();
            for (int j = 0; j < actionItems.get(i).getActionItemNumSteps(); j++) {
                checked.add(actionItems.get(i).getActionItemSteps().get(j).getCompleted());
            }
            this.checkedSteps.put(i, checked);
        }

    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return this.listActionChild.get(this.listActionHeader.get(groupPosition)).get(childPosition);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    /*
     * Main code for populating child view
     */
    @Override
    public View getChildView(final int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {

        // get text of specific child
        final String childText = (String) getChild(groupPosition, childPosition);

        if (convertView == null) { // inflate view if it hasn't been already
            LayoutInflater layoutInflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.action_item_list_item, null);
        }

        // make text of child that of text at childPosition
        TextView tvlblStepItem = (TextView) convertView.findViewById(R.id.tvlblStepItem);
        tvlblStepItem.setText(childText);

        // handle presses to checkbox
        cblblStepComplete = (CheckBox) convertView.findViewById(R.id.cblblStepComplete);
        cblblStepComplete.setChecked(checkedSteps.get(groupPosition).get(childPosition));
        cblblStepComplete.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) { // handles when user sets checkbox to "true"
                    UL.showReasonBox(); // shows reason box to allow user to type reason
                    UL.clearText(); // clears text in case text was already typed before hand for another action item
                    lastSelectedIndex = groupPosition; // groupPosition (current index) is now the index the user last selected
                    UL.sendLastUpdatedIndex(groupPosition); // send the last selected index to the Action Fragment
                    UL.sendStepUpdate(groupPosition, childPosition); // send the position of the update to the fragment
                    addCheck(groupPosition, childPosition); // add check to check hash map
                }

                else {
                    UL.hideReasonBox(); // hide edit text
                    UL.clearText(); // clear text in edit text
                    lastSelectedIndex = -1; // set to -1 to indicated that no changes should be made
                    UL.sendLastUpdatedIndex(lastSelectedIndex); // send new index to fragment
                    UL.removeStepUpdate(groupPosition, childPosition); // remove the step update at this position if there was a change
                    removeCheck(groupPosition, childPosition); // remove check from check hash map
                }
            }
        });

        return convertView;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        if (this.listActionChild.get(this.listActionHeader.get(groupPosition)) != null)
            return this.listActionChild.get(this.listActionHeader.get(groupPosition)).size();
        else {
            return 0;
        }
    }

    @Override
    public Object getGroup(int groupPosition) {
        return this.listActionHeader.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return this.listActionHeader.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    /*
     * Main code for populating group view
     */
    @Override
    public View getGroupView(final int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {

        String headerTitle = (String) getGroup(groupPosition);

        if (convertView == null) { // inflate view if it hasn't been already
            LayoutInflater layoutInflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.action_item_group_header, null);
        }

        // set textview to name of action item
        TextView tvlblActionItem = (TextView) convertView.findViewById(R.id.tvlblActionItem);
        tvlblActionItem.setTypeface(null, Typeface.BOLD);
        tvlblActionItem.setText(headerTitle);

        // initialize radio group
        radio = (RadioGroup) convertView.findViewById(R.id.radio);

        // set check listener for radio button
        radio.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // initialize radio buttons
                rbCompleted = (RadioButton) group.findViewById(R.id.rbCompleted);
                rbDropped = (RadioButton) group.findViewById(R.id.rbDropped);
                rbNone = (RadioButton) group.findViewById(R.id.rbNone);

                if (rbCompleted.isChecked()) { // do actions when user presses the "Completed" radio button
                    UL.showReasonBox(); // show reason box so user can type up reason
                    UL.clearText(); // clear text in case user switched to another action item
                    lastSelectedIndex = groupPosition; // groupPosition (current index) is now the index the user last selected
                    UL.sendLastUpdatedIndex(lastSelectedIndex); // send lastUpdatedIndex to the fragment
                    UL.removeDroppedActionUpdate(groupPosition); // remove the dropped update at this index if one exists
                    UL.sendCompletedActionUpdate(groupPosition); // send the completed update at this index to the fragment
                }


                else if (rbDropped.isChecked()) { // do actions when user presses the "Dropped" radio button
                    UL.showReasonBox(); // show reason box so user can type up reason
                    UL.clearText(); // clear text in case user switched to another action item
                    lastSelectedIndex = groupPosition; // groupPosition (current index) is now the index the user last selected
                    UL.sendLastUpdatedIndex(lastSelectedIndex); // send lastUpdatedIndex to the fragment
                    UL.removeCompletedActionUpdate(groupPosition); // remove the completed update at this index if one exists
                    UL.sendDroppedActionUpdate(groupPosition); // send the completed update at this index to the fragment
                }

                else if (rbNone.isChecked()) { // do actions when user presses the "N/A" radio button
                    UL.hideReasonBox(); // hide the reason box, as the user does not want to update this index
                    UL.clearText(); // clear the text from the box
                    lastSelectedIndex = -1; // set to -1 to indicated that no changes should be made
                    UL.sendLastUpdatedIndex(lastSelectedIndex); // send new index to fragment
                    UL.removeCompletedActionUpdate(groupPosition); // remove the completed update at this index if one exists
                    UL.removeDroppedActionUpdate(groupPosition); // remove the dropped update at this index if one exists
                    UL.removeAllStepUpdates(groupPosition); // remove all step updates, as no changes should be made
                    // manually uncheck every step for this action item
                    for (int i = 0; i < checkedSteps.get(groupPosition).size(); i++) {
                        removeCheck(groupPosition, i);
                    }
                    notifyDataSetChanged();
                }
            }
        });

        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    /*
     * Add newly added checkmark to hash map to keep a running tab on all checks
     */
    public void addCheck(int groupPosition, int childPosition) {
        ArrayList<Boolean> checked = new ArrayList<Boolean>(checkedSteps.get(groupPosition));
        checked.set(childPosition, true);
        checkedSteps.put(groupPosition, checked);
    }

    /*
     * Remove checkmark from hash map
     */
    public void removeCheck(int groupPosition, int childPosition) {
        if (checkedSteps.containsKey(groupPosition)) {
            ArrayList<Boolean> unchecked = new ArrayList<Boolean>(checkedSteps.get(groupPosition));
            unchecked.set(childPosition, false);
            checkedSteps.put(groupPosition, unchecked);
        }
    }

}
