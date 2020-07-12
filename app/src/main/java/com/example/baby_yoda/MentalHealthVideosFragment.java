package com.example.baby_yoda;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;


public class MentalHealthVideosFragment extends Fragment {

    TextView meditationVideos, meditationIshaKriyaVideos;
    WebView webView;
    ProgressBar progressBar;

    String meditationUrl="https://isha.sadhguru.org/in/en/yoga-meditation/yoga-program-for-beginners/yoga-videos/health";
    String ishaKriyaUrl="https://isha.sadhguru.org/in/en/yoga-meditation/yoga-program-for-beginners/isha-kriya-meditation";

    public MentalHealthVideosFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             final Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.frag_mental_health_videos, container, false);

        meditationVideos=view.findViewById(R.id.meditation_page);
        meditationIshaKriyaVideos=view.findViewById(R.id.isha_kriya_page);
        webView=view.findViewById(R.id.web);
        progressBar=view.findViewById(R.id.progress_bar);

        webView.setWebChromeClient(new MyChrome());
        webView.getSettings().setJavaScriptEnabled(true);

        meditationVideos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                loadMeditationPage(savedInstanceState);
            }
        });

        meditationIshaKriyaVideos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                loadIshaKriyaPage(savedInstanceState);
            }
        });

        return view;
    }

    private void loadMeditationPage(Bundle savedInstanceState){
        if (savedInstanceState==null){
            webView.post(new Runnable() {
                @Override
                public void run() {
                    webView.loadUrl(meditationUrl);
                    progressBar.setVisibility(View.GONE);


                }
            });
        }
    }

    private void loadIshaKriyaPage(Bundle savedInstanceState){
        if (savedInstanceState==null){
            webView.post(new Runnable() {
                @Override
                public void run() {
                    webView.loadUrl(ishaKriyaUrl);
                    progressBar.setVisibility(View.GONE);


                }
            });
        }
    }

    private class MyChrome extends WebChromeClient {
        private View mCustomView;
        private WebChromeClient.CustomViewCallback mCustomViewCallback;
        protected FrameLayout mFullscreenContainer;
        private int mOriginalOrientation;
        private int mOriginalSystemUiVisibility;

        MyChrome() {}

        public Bitmap getDefaultVideoPoster()
        {
            if (mCustomView == null) {
                return null;
            }
            return BitmapFactory.decodeResource(getContext().getResources(), 2130837573);
        }

        public void onHideCustomView()
        {
            ((FrameLayout)getActivity().getWindow().getDecorView()).removeView(this.mCustomView);
            this.mCustomView = null;
            getActivity().getWindow().getDecorView().setSystemUiVisibility(this.mOriginalSystemUiVisibility);
            getActivity().setRequestedOrientation(this.mOriginalOrientation);
            this.mCustomViewCallback.onCustomViewHidden();
            this.mCustomViewCallback = null;
        }

        public void onShowCustomView(View paramView, WebChromeClient.CustomViewCallback paramCustomViewCallback)
        {
            if (this.mCustomView != null)
            {
                onHideCustomView();
                return;
            }
            this.mCustomView = paramView;
            this.mOriginalSystemUiVisibility = getActivity().getWindow().getDecorView().getSystemUiVisibility();
            this.mOriginalOrientation = getActivity().getRequestedOrientation();
            this.mCustomViewCallback = paramCustomViewCallback;
            ((FrameLayout)getActivity().getWindow().getDecorView()).addView(this.mCustomView, new FrameLayout.LayoutParams(-1, -1));
            getActivity().getWindow().getDecorView().setSystemUiVisibility(3846 | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        }
    }
}