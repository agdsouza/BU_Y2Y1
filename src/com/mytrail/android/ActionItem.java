package com.mytrail.android;

import java.util.ArrayList;

/*
 * Holds the information for an action item
 */
public class ActionItem {

    private String actionItemName; // name of item
    private String actionItemId; // id of item
    private String actionItemDueDate; // due date
    private String actionItemNarrative; // description of the action item
    private String actionItemType; // type of item (Survey, Action Item, etc)
    private String actionItemStatus; // status of item (planned, completed, dropped)
    private int actionItemNumSteps; // number of steps for this item
    private ArrayList<Step> actionItemSteps; // array list that holds step objects, each representing 1 step

    public ActionItem(String name, String id) {
        this.actionItemName = name;
        this.actionItemId = id;
        this.actionItemSteps = new ArrayList<Step>();
    }

    // getter method; returns the name of the action item
    public String getActionItemName() {
        return actionItemName;
    }

    // setter method; set the name of the action item
    public void setActionItemName(String actionItemName) {
        this.actionItemName = actionItemName;
    }

    public void setActionItemId(String actionItemId) {
        this.actionItemId = actionItemId;
    }

    public String getActionItemId() {
        return actionItemId;
    }

    public ArrayList<Step> getActionItemSteps() {
        return actionItemSteps;
    }

    public String getActionItemDueDate() {
        return actionItemDueDate;
    }

    public void setActionItemDueDate(String actionItemDueDate) {
        this.actionItemDueDate = actionItemDueDate;
    }

    public String getActionItemNarrative() {
        return actionItemNarrative;
    }

    public void setActionItemNarrative(String actionItemNarrative) {
        this.actionItemNarrative = actionItemNarrative;
    }

    public String getActionItemType() {
        return actionItemType;
    }

    public void setActionItemType(String actionItemType) {
        this.actionItemType = actionItemType;
    }

    public String getActionItemStatus() {
        return actionItemStatus;
    }

    public void setActionItemStatus(String actionItemStatus) {
        this.actionItemStatus = actionItemStatus;
    }

    public int getActionItemNumSteps() {
        return actionItemNumSteps;
    }

    public void setActionItemNumSteps(int actionItemNumSteps) {
        this.actionItemNumSteps = actionItemNumSteps;
    }

    public void addStep(Step step) {
        this.actionItemSteps.add(step);
    }

    public void setActionItemSteps(ArrayList<Step> actionItemSteps) {
        this.actionItemSteps = actionItemSteps;
    }
}
