package com.mytrail.android;

import android.view.View;
import android.widget.ArrayAdapter;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;

public interface ControlFragInterface {

    public void onFetchDetailsStay(View v) throws UnsupportedEncodingException;
    public void onFetchBed(View v) throws UnsupportedEncodingException;
    public void onFetchLottery(View v) throws UnsupportedEncodingException;
    public void onFetchActionItems(View v) throws UnsupportedEncodingException;
    public void insertCompletedActionUpdate(int actionCompletedPosition) throws IOException;
    public void insertDroppedActionUpdate(int actionDroppedPosition) throws IOException;
    public void insertStepUpdate(int actionPosition, int stepPosition) throws IOException;
    public void insertReasonUpdate(int actionPosition, String reason) throws IOException;

}
