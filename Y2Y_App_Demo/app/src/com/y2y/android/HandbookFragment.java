package com.y2y.android;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.util.Log;
import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener;
import com.github.barteksc.pdfviewer.listener.OnPageChangeListener;
import com.github.barteksc.pdfviewer.scroll.DefaultScrollHandle;
import com.shockwave.pdfium.PdfDocument;
import java.util.List;

/*
Uses barteksc's AndroidPdfViewer which is a library for displaying PDF documents on Android,
with animations, gestures, zoom and double tap support.

*/

public class HandbookFragment extends Fragment implements OnPageChangeListener,OnLoadCompleteListener{

    private static final String TAG = MainActivity.class.getSimpleName();
    public static final String SAMPLE_FILE = "Guest-Handbook.pdf";
    PDFView pdfView;
    Integer pageNumber = 0;
    String pdfFileName;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_handbook, container, false);

        // Get ref of pdfView in layout
        pdfView = view.findViewById(R.id.pdfView);
        displayFromAsset(SAMPLE_FILE);

        return view;
    }

    private void displayFromAsset(String assetFileName) {

        pdfFileName = assetFileName;

        // Loads combination of settings from package that allows for advanced user interaction
        pdfView.fromAsset(SAMPLE_FILE)
                .defaultPage(pageNumber)
                .enableSwipe(true) // allows to block changing pages using swipe
                .swipeHorizontal(false)
                .onPageChange(this)
                .enableAnnotationRendering(true) // render annotations (such as comments, colors or forms)
                .onLoad(this) // called after document is loaded and starts to be rendered
                .scrollHandle(new DefaultScrollHandle(getActivity())) //replacement for ScrollBar that allows for better scrolling
                .load();
    }

    // Page contains the current page
    @Override
    public void onPageChanged(int page, int pageCount) {
        pageNumber = page;
    }

    // Gets table of contents and prints it in a tree format
    @Override
    public void loadComplete(int nbPages) {
        PdfDocument.Meta meta = pdfView.getDocumentMeta();
        printBookmarksTree(pdfView.getTableOfContents(), "-");

    }

    // Prints bookmarks in a tree format
    public void printBookmarksTree(List<PdfDocument.Bookmark> tree, String sep) {
        for (PdfDocument.Bookmark b : tree) {

            Log.e(TAG, String.format("%s %s, p %d", sep, b.getTitle(), b.getPageIdx()));

            if (b.hasChildren()) {
                printBookmarksTree(b.getChildren(), sep + "-");
            }
        }
    }

}
