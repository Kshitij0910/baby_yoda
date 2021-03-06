package com.example.baby_yoda;

import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;


public class SuggestedWorkoutVideosFragment extends Fragment {

    TextView yogaVideos, homeWorkoutVideos, hiitVideos, absVideos;
    WebView webView;
    ProgressBar progressBar;

    String yogaUrl="https://www.artofliving.org/yoga-videos";
    String homeworkoutUrl="https://www.fitnessblender.com/videos/15-minute-bodyweight-cardio-workout-for-fat-burn-and-energy-boost-feel-good-total-body-cardio";
    String hiitUrl="https://www.fitnessblender.com/videos/intense-at-home-hiit-routine-no-equipment-hiit-workout-video-with-low-impact-modifications";
    String absUrl="https://www.healthline.com/health/fitness-exercise/best-workouts-under-20-minutes#4";

    public SuggestedWorkoutVideosFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             final Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.frag_suggested_workout_videos, container, false);


        yogaVideos=view.findViewById(R.id.yoga_page);
        homeWorkoutVideos=view.findViewById(R.id.home_workout_page);
        hiitVideos=view.findViewById(R.id.hiit_page);
        absVideos=view.findViewById(R.id.abs_page);
        webView=view.findViewById(R.id.web);
        progressBar=view.findViewById(R.id.progress_bar);


        webView.setWebChromeClient(new MyChrome());
        webView.getSettings().setJavaScriptEnabled(true);

        yogaVideos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                loadYogaPage(savedInstanceState);
            }
        });

        homeWorkoutVideos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                loadHomeWorkoutPage(savedInstanceState);
            }
        });

        hiitVideos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                loadHIITPage(savedInstanceState);
            }
        });
        absVideos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                loadAbsPage(savedInstanceState);
            }
        });




        return view;
    }

    private void loadYogaPage(Bundle savedInstanceState){

        if (savedInstanceState==null){
            webView.post(new Runnable() {
                @Override
                public void run() {
                    webView.loadUrl(yogaUrl);
                    progressBar.setVisibility(View.GONE);


                }
            });
        }
    }

    private void loadHomeWorkoutPage(Bundle savedInstanceState){

        if (savedInstanceState==null){
            webView.post(new Runnable() {
                @Override
                public void run() {
                    webView.loadUrl(homeworkoutUrl);
                    progressBar.setVisibility(View.GONE);


                }
            });
        }
    }

    private void loadHIITPage(Bundle savedInstanceState){
        if (savedInstanceState==null){
            webView.post(new Runnable() {
                @Override
                public void run() {
                    webView.loadUrl(hiitUrl);
                    progressBar.setVisibility(View.GONE);


                }
            });
        }
    }

    private void loadAbsPage(Bundle savedInstanceState){
        if (savedInstanceState==null){
            webView.post(new Runnable() {
                @Override
                public void run() {
                    webView.loadUrl(absUrl);
                    progressBar.setVisibility(View.GONE);


                }
            });
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        webView.saveState(savedInstanceState);
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        webView.restoreState(savedInstanceState);
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