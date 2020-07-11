package com.example.baby_yoda;

import android.app.AppOpsManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.CountDownTimer;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;


public class TimerFragment extends Fragment {

    private static final String TAG = "TimerFragment";
    private static final long START_TIME_IN_MILLIS=600000;

    TextView timer;
    Button startPause, reset;

    CountDownTimer countDownTimer;



    private boolean timerRunnning;

    private long timeLeftInMillis;
    private long endTime;

    //For app usage stats
    private SharedPreferences appUsageStatsPrefs;
    private SharedPreferences.Editor appUsageEditor;
    private static final String appUsageDuration="AppUsageDuration";
    public static String facebookCounter="Facebook Usage Counter";
    public static String whatsappCounter="WhatsApp Usage Counter";
    public static String instagramCounter="Instagram Usage Counter";

    TextView facebookTime, whatsappTime, instagramTime;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.frag_timer, container, false);

        //Initialise SharedPreferences for AppUsageStats
        appUsageStatsPrefs=getActivity().getSharedPreferences(appUsageDuration, Context.MODE_PRIVATE);
        //check permission for using app usage
        if (!checkUsageStatsPermission()){
            //open settings if permission not granted.
            Intent usageAccessIntent=new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
            usageAccessIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(usageAccessIntent);

            if (checkUsageStatsPermission()){
                getActivity().startService(new Intent(getActivity(), BackgroundService.class));

            }
            else {
                Toast.makeText(getActivity(), "Please allow Bliss to access your App usage data.", Toast.LENGTH_LONG).show();
            }
        }
        else {
            getActivity().startService(new Intent(getActivity(), BackgroundService.class));
        }

        //Reference for layout variables
        startPause=view.findViewById(R.id.start_pause);
        timer=view.findViewById(R.id.timer);
        reset=view.findViewById(R.id.reset);

        facebookTime=view.findViewById(R.id.facebook_time);
        whatsappTime=view.findViewById(R.id.whatsapp_time);
        instagramTime=view.findViewById(R.id.instagram_time);

        TimerTask updateTimeView=new TimerTask() {
            @Override
            public void run() {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        long facebookUseTime=appUsageStatsPrefs.getLong(facebookCounter, 0);
                        long second=(facebookUseTime/1000)%60;
                        long minute=(facebookUseTime/(1000*60))%60;
                        long hour=(facebookUseTime/(1000*60*60));
                        String facebookVal=hour+"hr "+minute+"min "+second+"s";
                        facebookTime.setText(facebookVal);

                        long whatsappUseTime=appUsageStatsPrefs.getLong(whatsappCounter, 0);
                        second=(whatsappUseTime/1000)%60;
                        minute=(whatsappUseTime/(1000*60))%60;
                        hour=(whatsappUseTime/(1000*60*60));
                        String whatsappVal=hour+"hr "+minute+"min "+second+"s";
                        whatsappTime.setText(whatsappVal);

                        long instagramUseTime=appUsageStatsPrefs.getLong(instagramCounter, 0);
                        second=(instagramUseTime/1000)%60;
                        minute=(instagramUseTime/(1000*60))%60;
                        hour=(instagramUseTime/(1000*60*60));
                        String instagramVal=hour+"hr "+minute+"min "+second+"s";
                        instagramTime.setText(instagramVal);


                    }
                });
            }
        };

        Timer timer=new Timer();
        timer.scheduleAtFixedRate(updateTimeView, 0, 1000);


        startPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (timerRunnning){
                    pauseTimer();
                }
                else{
                    //sendTimerNotification();
                    startTimer();

                }
            }
        });

        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetTimer();
            }
        });

        return view;
    }

    private void startTimer(){
        endTime=System.currentTimeMillis()+timeLeftInMillis;
        countDownTimer=new CountDownTimer(timeLeftInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeLeftInMillis=millisUntilFinished;
                updateCountdownTimer();
            }

            @Override
            public void onFinish() {
                timerRunnning=false;
                updateButtons();
            }
        }.start();

        timerRunnning=true;
        updateButtons();
    }

    private void pauseTimer(){
        countDownTimer.cancel();

        timerRunnning=false;
        updateButtons();
    }

    private void resetTimer(){
        timeLeftInMillis=START_TIME_IN_MILLIS;
        updateCountdownTimer();
        updateButtons();
    }

    private void updateCountdownTimer(){
        int minutes=(int) (timeLeftInMillis/1000)/60;
        int seconds=(int) (timeLeftInMillis/1000)%60;

        String timeLeft=String.format(Locale.getDefault(),
                "%02d:%02d", minutes, seconds);

        timer.setText(timeLeft);
    }

    private void updateButtons(){
        if (timerRunnning){
            reset.setVisibility(View.INVISIBLE);
            startPause.setText("PAUSE");
        }
        else {
            startPause.setText("START");
            if (timeLeftInMillis<1000){
                startPause.setVisibility(View.INVISIBLE);
            }
            else {
                startPause.setVisibility(View.VISIBLE);
            }

            if (timeLeftInMillis<START_TIME_IN_MILLIS){
                reset.setVisibility(View.VISIBLE);
            }
            else {
                reset.setVisibility(View.INVISIBLE);
            }
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onStop() {
        super.onStop();

        SharedPreferences timePrefs=getActivity().getSharedPreferences("TimePrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor timeEditor=timePrefs.edit();

        timeEditor.putLong("millisLeft", timeLeftInMillis);
        timeEditor.putBoolean("timerRunning", timerRunnning);
        timeEditor.putLong("endTime", endTime);

        timeEditor.apply();
    }

    @Override
    public void onStart() {
        super.onStart();

        SharedPreferences timePrefs=getActivity().getSharedPreferences("TimePrefs", Context.MODE_PRIVATE);

        timeLeftInMillis=timePrefs.getLong("millisLeft", START_TIME_IN_MILLIS);
        timerRunnning=timePrefs.getBoolean("timerRunning", false);

        updateCountdownTimer();
        updateButtons();

        if (timerRunnning){
            endTime=timePrefs.getLong("endTime", 0);
            timeLeftInMillis=endTime-System.currentTimeMillis();

            if (timeLeftInMillis<0){
                timeLeftInMillis=0;
                timerRunnning=false;
                updateCountdownTimer();
                updateButtons();
            }
            else {
                startTimer();
            }
        }
    }

    public boolean checkUsageStatsPermission(){
        try {
            PackageManager packageManager=getActivity().getPackageManager();
            ApplicationInfo applicationInfo=getActivity().getApplicationInfo();
            AppOpsManager opsManager=(AppOpsManager)getActivity().getSystemService(Context.APP_OPS_SERVICE);
            int mode=opsManager.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS, applicationInfo.uid, applicationInfo.packageName);
            return (mode==AppOpsManager.MODE_ALLOWED);
        }
        catch (Exception e){
            Toast.makeText(getActivity(), "ERROR! Could not find any Usage Stats Manager.", Toast.LENGTH_LONG).show();
            return false;
        }
    }

    @Override
    public void onDestroy() {
        if (checkUsageStatsPermission()){
            getActivity().startService(new Intent(getActivity(), BackgroundService.class));
        }

        super.onDestroy();
    }
}