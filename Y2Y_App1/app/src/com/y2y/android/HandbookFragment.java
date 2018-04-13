package com.y2y.android;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

public class HandbookFragment extends Fragment {

    WebView wvHandbook;
    String uri;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.handbook_frag, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        wvHandbook = view.findViewById(R.id.wvHandbook);
        // change this URL later to one that links to PDF
        uri = "https://www.google.com";
        wvHandbook.loadUrl(uri);
        super.onViewCreated(view, savedInstanceState);
    }
}
