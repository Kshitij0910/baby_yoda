package com.example.baby_yoda;

import android.content.Intent;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


public class HomeFragment extends Fragment {

    CardView physical, mental;

    public HomeFragment() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.frag_home, container, false);

        physical=view.findViewById(R.id.physical);
        mental=view.findViewById(R.id.mental);

        physical.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent physicalIntent=new Intent(getActivity(), PhysicalFitnessActivity.class);
                startActivity(physicalIntent);

            }
        });

        mental.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mentalIntent=new Intent(getActivity(), MentalActivity.class);
                startActivity(mentalIntent);
            }
        });

        return view;
    }
}