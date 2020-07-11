package com.example.baby_yoda;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

public class ResetStepCounterAtMidnight extends BroadcastReceiver {

    private static final String TAG = "ResetStepCounter";
    private static final String initialCountString="Footstep_inital_counter";
    private static final String currentStepsString="Current_stepscount_in_sensor";
    private static final String steps="STEPSCOUNT";

    SharedPreferences stepCountPrefs;
    SharedPreferences.Editor stepEditor;

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "onReceive: Counter getting reset");

        stepCountPrefs=context.getSharedPreferences(steps, Context.MODE_PRIVATE);
        float dailyStartingStepCount=stepCountPrefs.getFloat(currentStepsString, 0);

        Log.d(TAG, "onReceive: dailyStartingStepCount "+dailyStartingStepCount);
        stepEditor=stepCountPrefs.edit();
        stepEditor.putFloat(initialCountString, dailyStartingStepCount);
        stepEditor.apply();
    }
}
