package com.y2y.android;

import android.view.View;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

public interface ControlFragInterface {

    public void onFetchDetailsStay(View v) throws UnsupportedEncodingException;
    public void onFetchBed(View v) throws UnsupportedEncodingException;
    public void onFetchLottery(View v) throws UnsupportedEncodingException;
    public void onFetchLotteryNumber(View v) throws UnsupportedEncodingException;
    public void postSurvey(float rating, String surveyfeedback) throws IOException;
    public void postFeedback(String feedback) throws IOException;

    public void onFetchEvents(View v) throws UnsupportedEncodingException;
    public void postRSVP(String eventID, String rsvp) throws IOException;

}
