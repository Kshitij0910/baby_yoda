package com.example.baby_yoda;

import android.app.Service;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static com.example.baby_yoda.TimerFragment.facebookCounter;
import static com.example.baby_yoda.TimerFragment.instagramCounter;
import static com.example.baby_yoda.TimerFragment.whatsappCounter;

public class BackgroundService extends Service {

    private static final String TAG = "BackgroundService";
    private SharedPreferences appUsageStatsPrefs;
    private SharedPreferences.Editor appUsageEditor;
    private static final String appUsageDuration="AppUsageDuration";


    public BackgroundService(){
        //Required constructor
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        appUsageStatsPrefs=getSharedPreferences(appUsageDuration, MODE_PRIVATE);
        appUsageEditor=appUsageStatsPrefs.edit();
        TimerTask detectApp=new TimerTask() {
            @Override
            public void run() {
                appUsageStatsPrefs=getSharedPreferences(appUsageDuration, MODE_PRIVATE);
                appUsageEditor=appUsageStatsPrefs.edit();
                UsageStatsManager usageStatsManager=(UsageStatsManager)getSystemService(USAGE_STATS_SERVICE);
                long endingTime=System.currentTimeMillis();
                long beginningTIme=endingTime - (1000);

                List<UsageStats> usageStats=usageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, beginningTIme, endingTime);
                if (usageStats != null){
                    for (UsageStats usageStat:usageStats){
                        if (usageStat.getPackageName().toLowerCase().contains("com.whatsapp")){
                            appUsageEditor.putLong(whatsappCounter, usageStat.getTotalTimeInForeground());
                            Log.d(TAG, "run: whatsappCount"+usageStat.getTotalTimeInForeground());
                        }
                        if (usageStat.getPackageName().toLowerCase().contains("com.facebook.katana")){
                            appUsageEditor.putLong(facebookCounter, usageStat.getTotalTimeInForeground());
                            Log.d(TAG, "run: facebookCount"+usageStat.getTotalTimeInForeground());
                        }
                        if (usageStat.getPackageName().toLowerCase().contains("com.instagram.android")){
                            appUsageEditor.putLong(instagramCounter, usageStat.getTotalTimeInForeground());
                            Log.d(TAG, "run: instagramCount"+usageStat.getTotalTimeInForeground());
                        }
                        appUsageEditor.apply();

                    }
                }
            }
        };
        Timer detectAppTimer=new Timer();
        detectAppTimer.scheduleAtFixedRate(detectApp, 0, 1000);
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
