package com.example.baby_yoda;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


public class CalorimeterResultsFragment extends Fragment {

    TextView bmr_view, calory_need,bmi;
    SharedPreferences pref;


    public CalorimeterResultsFragment() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.frag_calorimeter_results, container, false);

        bmr_view = view.findViewById(R.id.bmr_view);
        calory_need = view.findViewById(R.id.calory_need);
        bmi = view.findViewById(R.id.bmi);
        pref = getActivity().getSharedPreferences("calorimeter", Context.MODE_PRIVATE);
        final float bmr = pref.getFloat("bmr",0);
        final float calory = pref.getFloat("calory",0);
        final float bmi_calculate =pref.getFloat("bmi",0);
        bmr_view.setText(Float.toString(bmr));
        calory_need.setText(Float.toString(calory));
        bmi.setText(Float.toString(bmi_calculate));

        return view;
    }
}