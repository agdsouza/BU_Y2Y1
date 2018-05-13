package com.y2y.android;

import android.support.annotation.NonNull;

/*
 * Holds the information for a given step
 */
public class Step implements Comparable<Step> {

    private String actionItemId; // action item id that step is associated with
    private String stepId; // step id
    private String stepName; // name of the step
    private int stepNum; // number of step
    private boolean isCompleted; // completion status (either true or false)
    private String stepDue; // due date of step

    public Step(String actionItemId, String stepId) {
        this.actionItemId = actionItemId;
        this.stepId = stepId;
    }

    // getter method; returns action item id
    public String getActionItemId() {
        return actionItemId;
    }

    // setter method; set action item id
    public void setActionItemId(String actionItemId) {
        this.actionItemId = actionItemId;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public void setCompleted(boolean completed) {
        isCompleted = completed;
    }

    public String getStepId() {
        return stepId;
    }

    @Override
    public int compareTo(@NonNull Step s) {
        int compareStepNum = ((Step) s).getStepNum();
        return compareStepNum;
    }

    public void setStepId(String stepId) {
        this.stepId = stepId;
    }

    public String getStepName() {
        return stepName;
    }

    public void setStepName(String stepName) {
        this.stepName = stepName;
    }

    public int getStepNum() {
        return stepNum;
    }

    public void setStepNum(int stepNum) {
        this.stepNum = stepNum;
    }

    public Boolean getCompleted() {
        return isCompleted;
    }

    public void setCompleted(Boolean completed) {
        isCompleted = completed;
    }

    public String getStepDue() {
        return stepDue;
    }

    public void setStepDue(String stepDue) {
        this.stepDue = stepDue;
    }
}
