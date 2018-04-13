package com.y2y.android;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class LotteryFragment extends Fragment {

    private TextView tvLongtermLabel;
    private TextView tvLongtermDate;
    private TextView tvLTLotteryNum;
    private TextView tvEBedLabel;
    private TextView tvEBedDate;
    private TextView tvELotteryNum;
    private Button btnFetchLottery;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_lottery, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }
}
