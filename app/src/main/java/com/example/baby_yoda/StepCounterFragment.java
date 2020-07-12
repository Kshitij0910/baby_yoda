package com.example.baby_yoda;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.audiofx.BassBoost;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.provider.Settings;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;

import me.itangqi.waveloadingview.WaveLoadingView;


public class StepCounterFragment extends Fragment implements SensorEventListener {

    private static final String TAG = "StepCounterFragment";
    private static final String steps="STEPSCOUNT";
    private static final String initialCountString="Footstep_inital_counter";
    private static final String currentStepsString="Current_stepscount_in_sensor";
    private static final String goalString="Today's_goal";
    
    private SensorManager stepSensorManager;
    Sensor countSensor;

    boolean activityRunning;
    float[] stepSensorValues=null;
    int progressPercentage;

    String goalSave, goalRead;

    SharedPreferences stepCountPrefs;
    SharedPreferences.Editor stepEditor;
    float dailyStartingStepCount, currentStepsInSensor;


    TextView stepsCount, todayGoal, todayProgress;
    EditText setTodayGoal;
    RelativeLayout setGoal;

    WaveLoadingView wave;




    Calendar calendar;

    AlertDialog.Builder goalDialog;

    public StepCounterFragment() {
        // Required empty public constructor
    }




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.frag_step_counter, container, false);

        //initialise SharedPreferences for maintaining timer
        stepCountPrefs=getActivity().getSharedPreferences(steps, Context.MODE_PRIVATE);
        dailyStartingStepCount=stepCountPrefs.getFloat(initialCountString, 0);
        Log.d(TAG, "onCreateView: dailyStartingStepCount" +dailyStartingStepCount);



        //Reference for layout variables
        stepsCount=view.findViewById(R.id.step_count);
        setGoal=view.findViewById(R.id.set_goal);
        wave=view.findViewById(R.id.wave);

        //todayGoal=view.findViewById(R.id.today_goal);
        //todayProgress=view.findViewById(R.id.today_progress);

        //Extract today's goal from SharedPreferences
        goalRead=stepCountPrefs.getString(goalString, "7000");
        //todayGoal.setText("Today's Goal:"+goalRead+" steps");
        wave.setTopTitle("Goal:"+goalRead);



        stepSensorManager=(SensorManager)getActivity().getSystemService(Context.SENSOR_SERVICE);

        setGoal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setTodayGoal=new EditText(v.getContext());
                goalDialog= new AlertDialog.Builder(v.getContext());
                setGoal(v);


            }
        });



        //Use calendar and AlarmManager to reset the stepCount daily at 11:59 PM
        calendar=Calendar.getInstance();
        Log.d(TAG, "onCreateView: "+calendar.get(Calendar.YEAR)+", "+calendar.get(Calendar.MONTH)+", "+calendar.get(Calendar.DAY_OF_MONTH));
        calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), 23, 59, 0);

        Log.d(TAG, "onCreateView: "+calendar.get(Calendar.YEAR)+ ", "+calendar.get(Calendar.MONTH)+", "+calendar.get(Calendar.DAY_OF_MONTH)+"; " +calendar.getTimeInMillis());

        //reset the counter
        resetCounter(calendar);



        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        //start counting the moment the fragment is launched, no requirement of button
        activityRunning=true;
        countSensor=stepSensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        if (countSensor != null){
            //start listening
            stepSensorManager.registerListener(this, countSensor, SensorManager.SENSOR_DELAY_UI);
        }
        else {
            //notify user that hs/her device does not have a step counter sensor;
            Toast.makeText(getActivity(), "Sorry! Your device does not have a Step Count Sensor.", Toast.LENGTH_LONG).show();
        }

    }

    //Methods for stepSensor
    @Override
    public void onSensorChanged(SensorEvent event) {
        if (activityRunning){
            stepSensorValues=event.values;

            currentStepsInSensor=stepSensorValues[0];

            stepEditor=stepCountPrefs.edit();
            stepEditor.putFloat(currentStepsString, currentStepsInSensor);
            stepEditor.apply();
            Log.d(TAG, "onSensorChanged: CurrentStepsInSensor "+currentStepsInSensor);

            //show today's progress;
            //goal=setTodayGoal.getText().toString();
            /*if (goalR==null){
                goal="7000";
            }*/
            progressPercentage=Math.round((stepSensorValues[0]-dailyStartingStepCount)*100/Integer.parseInt(goalRead));
            wave.setProgressValue(progressPercentage);
            wave.setCenterTitle(progressPercentage +"%");
            //todayProgress.setText(progressPercentage + "%");
            //Update the steps
            stepsCount.setText(String.valueOf((int)(stepSensorValues[0]-dailyStartingStepCount)));

        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    private void resetCounter(Calendar calendar){
        Log.d(TAG, "resetCounter: "+calendar.getTimeInMillis());
        AlarmManager resetCounterAlarmManager=(AlarmManager)getActivity().getSystemService(Context.ALARM_SERVICE);
        Intent resetCounterIntent=new Intent(getContext(), ResetStepCounterAtMidnight.class);

        PendingIntent resetCounterPendingIntent=PendingIntent.getBroadcast(getContext(), 0, resetCounterIntent, 0);
        resetCounterAlarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, resetCounterPendingIntent);
        Log.d("WalkFragment", "resetCounter: Counter will be reset at midnight");
    }

    private void setGoal(View v){
        //setTodayGoal=new EditText(v.getContext());
        setTodayGoal.setInputType(InputType.TYPE_CLASS_NUMBER);
        //goalDialog= new AlertDialog.Builder(v.getContext());
        goalDialog.setTitle("MY GOAL");
        goalDialog.setMessage("Today my goal is");
        goalDialog.setView(setTodayGoal);




        goalDialog.setPositiveButton("WALK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                goalSave=setTodayGoal.getText().toString();
                if (goalSave.trim().length()>0){
                    stepEditor.putString(goalString, goalSave);
                    stepEditor.apply();

                    goalRead=stepCountPrefs.getString(goalString, "7000");
                    //todayGoal.setText("Today's Goal:"+goalRead+" steps");
                    wave.setTopTitle("Goal:"+goalRead);



                    progressPercentage=Math.round((stepSensorValues[0]-dailyStartingStepCount)*100/Integer.parseInt(goalRead));
                    //todayProgress.setText(progressPercentage + "%");
                    wave.setProgressValue(progressPercentage);
                    wave.setCenterTitle(progressPercentage +"%");

                }
                else {
                    Toast.makeText(getActivity(), "Enter your goal.", Toast.LENGTH_SHORT).show();
                }


            }
        });

        goalDialog.setNegativeButton("CLOSE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //close dialog;
            }
        });

        goalDialog.create().show();








    }
}