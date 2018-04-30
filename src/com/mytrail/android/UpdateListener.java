package com.mytrail.android;

/*
 * Listener that allows adapter to speak to Action Fragment; descriptions of functions are in
 * ActionFragment
 */
public interface UpdateListener {

    public void sendCompletedActionUpdate(int actionPosition);
    public void sendDroppedActionUpdate(int actionPosition);
    public void sendStepUpdate(int actionPosition, int stepPosition);
    public void removeCompletedActionUpdate(int actionPosition);
    public void removeDroppedActionUpdate(int actionPosition);
    public void removeStepUpdate(int actionPosition, int stepPosition);
    public void removeAllStepUpdates(int actionPosition);
    public void showReasonBox();
    public void hideReasonBox();
    public void sendLastUpdatedIndex(int actionPosition);
    public void clearText();

}
