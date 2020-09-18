package com.example.coronaapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class RequestActivity extends Fragment {

    private Button requestBloodBtn;
    private Button requestPlasmaBtn;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.activity_request, container, false);

        requestBloodBtn = view.findViewById(R.id.bloodBtn);
        requestPlasmaBtn = view.findViewById(R.id.plasmaBtn);

        requestBloodBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RequestBloodEligibility bloodEligible = new RequestBloodEligibility();
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, bloodEligible,"Blood Eligbile Fragment")
                        .addToBackStack(null)
                        .commit();
            }
        });

        requestPlasmaBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RequestPlasmaEligibility plasmaEligible = new RequestPlasmaEligibility();
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, plasmaEligible,"Plasma Eligbile Fragment")
                        .addToBackStack(null)
                        .commit();
            }
        });
        return view;

    }
}