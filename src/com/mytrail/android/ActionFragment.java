package com.mytrail.android;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.Toast;

import com.google.api.services.calendar.Calendar;
import com.google.firebase.database.ThrowOnExtraProperties;
import com.salesforce.androidsdk.smartsync.manager.SyncManager;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class ActionFragment extends Fragment implements UpdateListener {

    ExpandableListAdapter listAdapter;
    ExpandableListView expListView;
    List<String> listActionHeader;
    EditText edtWhy;
    Button btnSaveNote;
    Button btnSave;
    HashMap<String, List<String>> listActionChild; // stores the child strings for a given group string

    ArrayList<Integer> actionCompletedPositions; // stores the positions of all updated completed action items
    ArrayList<Integer> actionDroppedPositions; // stores the positions of all the updated dropped action items
    HashMap<Integer, ArrayList<Integer>> stepPositions; // stores the position of all updated completed action item steps, along with the associated action item position

    HashMap<Integer, String> updateReasons; // stores the reasons for each update, along with the index of the action item updated

    int lastSelectedIndex; // the position of the last action item that was updated by the user; used to match the reason with the action item

    final int REASON_MIN = 10; // minimum amount of characters for a reason
    final int REASON_MAX = 256; // maximum amount of characters for a reason

    ControlFragInterface AFL; // interface for communicating with main activity

    ArrayList<ActionItem> actionI; // stores the action items

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        AFL = (ControlFragInterface) context; // initialize the interface and pass in the context
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        // inflate the view to initialize child views
        View view = inflater.inflate(R.layout.fragment_action, container, false);

        // initialize any child views
        expListView = (ExpandableListView) view.findViewById(R.id.exlvActionItems);
        btnSave = (Button) view.findViewById(R.id.btnSave);
        edtWhy = (EditText) view.findViewById(R.id.edtWhy);
        btnSaveNote = (Button) view.findViewById(R.id.btnSaveNote);

        // initialize arrays and hashmaps
        actionCompletedPositions = new ArrayList<Integer>();
        actionDroppedPositions = new ArrayList<Integer>();
        stepPositions = new HashMap<Integer, ArrayList<Integer>>();
        updateReasons = new HashMap<Integer, String>();

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // set up edit text so that the "Done" button appears on the keyboard to allow easy keyboard hiding when finished typing
        edtWhy.setImeOptions(EditorInfo.IME_ACTION_DONE);
        edtWhy.setRawInputType(InputType.TYPE_CLASS_TEXT);

        try {
            // get all the action items by making a SOQL call within the Main Activity
            AFL.onFetchActionItems(view);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        btnSaveNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String reason = edtWhy.getText().toString().trim();
                if (reason.length() < REASON_MIN) { // reason is too short
                    edtWhy.setError("Reason is not long enough");
                    edtWhy.requestFocus();
                    return;
                }

                else if (reason.length() >= REASON_MAX) { // reason is too long
                    edtWhy.setError("Reason is too long!");
                    edtWhy.requestFocus();
                    return;
                }

                else {
                    if (lastSelectedIndex >= 0) { // create a key-value pair of the last selected index and the reason as long as something was selected beforehand
                        updateReasons.put(lastSelectedIndex, reason);
                        Toast.makeText(getActivity().getApplicationContext(), "Changes saved!", Toast.LENGTH_SHORT).show();
                        edtWhy.setText("");
                    }
                }
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    // update completed action items
                    for (int i = 0; i < actionCompletedPositions.size(); i++) {
                        AFL.insertCompletedActionUpdate(actionCompletedPositions.get(i));
                    }

                    // update dropped action items
                    for (int i = 0; i < actionDroppedPositions.size(); i++) {
                        AFL.insertDroppedActionUpdate(actionDroppedPositions.get(i));
                    }

                    // update completed steps
                    for (Integer key : stepPositions.keySet()) {
                        if (stepPositions.get(key).size() >= 0) { // update as long as some step updates were made
                            for (int i = 0; i < stepPositions.get(key).size(); i++) {
                                AFL.insertStepUpdate(key, stepPositions.get(key).get(i));

                            }
                        }
                    }

                    // update reasons for completed action items
                    for (Integer action : actionCompletedPositions) {
                        AFL.insertReasonUpdate(action, updateReasons.get(action));
                    }

                    // update reasons for dropped action items
                    for (Integer action : actionDroppedPositions) {
                        AFL.insertReasonUpdate(action, updateReasons.get(action));
                    }

                    // update reasons for completed steps
                    for (Integer action : stepPositions.keySet()) {
                        AFL.insertReasonUpdate(action, updateReasons.get(action));
                    }

                    Toast.makeText(getActivity().getApplicationContext(), "Updated action items!", Toast.LENGTH_SHORT).show();

                } catch (IOException e) {
                    e.printStackTrace();
                }

                // clear all the indices for the arrays so updates don't redo themselves
                actionCompletedPositions.clear();
                actionDroppedPositions.clear();
                stepPositions.clear();
                updateReasons.clear();

            }
        });

    }

    /*
     * Given a list of action items, populate the expandable list adapter with the action items (group items)
     * and steps (child items) and render the adapter onto the listview
     */
    public void setActionItems(ArrayList<ActionItem> actionItems) {
        listActionHeader = new ArrayList<String>();
        listActionChild = new HashMap<String, List<String>>();
        actionI = new ArrayList<ActionItem>(actionItems);

        // add the names of the action items to the group header
        for (int i = 0; i < actionItems.size(); i++) {
            listActionHeader.add(actionItems.get(i).getActionItemName());
        }

        // iterate through each action item to get each item's steps (if they have them)
        for (int i = 0; i < actionItems.size(); i++) {
            List<String> stepList = new ArrayList<String>(); // create a new step list
            if (actionItems.get(i).getActionItemNumSteps() != 0) {
                // iterate through the steps as long as there is at least 1
                for (int j = 0; j < actionItems.get(i).getActionItemSteps().size(); j++) {
                    Step step = actionItems.get(i).getActionItemSteps().get(j); // get the steps of the action item as an array list
                    stepList.add(Integer.toString(step.getStepNum() + 1) + ") " + step.getStepName()); // add name of step with the step number to the step list
                }
                // add the step list to the corresponding action item header
                listActionChild.put(listActionHeader.get(i), stepList);
            }
        }

        // prepare list data
        listAdapter = new ActionExpandableListAdapter(getActivity(), listActionHeader, listActionChild, this, actionI);
        // setting list adapter
        expListView.setAdapter(listAdapter);
    }

    /*
     * Adds inputted action item position to the completed action item array
     */
    public void sendCompletedActionUpdate(int actionPosition) {
        actionCompletedPositions.add(actionPosition);
    }

    /*
     * Adds inputted action item position to the dropped action item array
     */
    public void sendDroppedActionUpdate(int actionPosition) {
        actionDroppedPositions.add(actionPosition);
    }

    /*
     * Adds inputted step position and action item position to a hash map that stores the updated step
     * positions at its corresponding action item position key
     */
    public void sendStepUpdate(int actionPosition, int stepPosition) {
        if (!stepPositions.containsKey(actionPosition)) { // if the action item position is present, add step position to the value's step position array
            ArrayList<Integer> actionSteps = new ArrayList<Integer>();
            actionSteps.add(stepPosition);
            // add new step position to step position array list at corresponding action position
            stepPositions.put(actionPosition, actionSteps);
        }

        else { // create new key-value pair for the action item and step position array
            ArrayList<Integer> actionSteps = stepPositions.get(actionPosition);
            actionSteps.add(stepPosition);
            stepPositions.put(actionPosition, actionSteps);
        }
    }

    /*
     * If the inputted completed action item position exists within the array list, remove it
     */
    public void removeCompletedActionUpdate(int actionPosition) {
        int removeInd = actionCompletedPositions.indexOf(actionPosition);
        if (removeInd >= 0) {
            actionCompletedPositions.remove(removeInd);
        }
    }

    /*
     * If the inputted dropped action item position exists within the array list, remove it
     */
    public void removeDroppedActionUpdate(int actionPosition) {
        int removeInd = actionDroppedPositions.indexOf(actionPosition);
        if (removeInd >= 0) {
            actionDroppedPositions.remove(removeInd);
        }
    }

    /*
     * If the step position hash map has the inputted action position as a key, then remove the inputted
     * step position from the corresponding array list as long as the step position exists within the array list
     */
    public void removeStepUpdate(int actionPosition, int stepPosition) {
        if (stepPositions.containsKey(actionPosition)) { // check if actionPosition is a key in the step position array
            ArrayList<Integer> oldActionStep = new ArrayList<Integer>(stepPositions.get(actionPosition));
            int removeInd = oldActionStep.indexOf(stepPosition);
            if (removeInd >= 0) { // check if stepPosition is an element in the corresponding step position value array list
                oldActionStep.remove(removeInd);
            }
            stepPositions.put(actionPosition, oldActionStep);
        }
    }

    /*
     * If the step position hash map has any steps for the inputted action position as a key, clear the
     * array list value at the given key
     */
    public void removeAllStepUpdates(int actionPosition) {
        if (stepPositions.containsKey(actionPosition)) {
            ArrayList<Integer> oldActionstep = new ArrayList<Integer>();
            stepPositions.put(actionPosition, oldActionstep);
        }
    }

    /*
     * Makes the edit text for the reason visible if it is not already
     */
    public void showReasonBox() {
        if (edtWhy.getVisibility() == View.GONE) {
            edtWhy.setVisibility(View.VISIBLE);
        }
    }

    /*
     * Hides the edit text for the reason if it is not already hidden
     */
    public void hideReasonBox() {
        if (edtWhy.getVisibility() == View.VISIBLE) {
            edtWhy.setVisibility(View.GONE);
        }
    }

    /*
     * Sets the lastUpdatedIndex variable to the inputted action position (can either be an index number or -1)
     */
    public void sendLastUpdatedIndex(int actionPosition) {
        lastSelectedIndex = actionPosition;
    }

    /*
     * Clears the edit text and sets its text value to an empty string
     */
    public void clearText() {
        edtWhy.setText("");
    }

}
