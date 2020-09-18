package com.example.coronaapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class DonateActivity extends Fragment {

    private Button donateBloodBtn;
    private Button donatePlasmaBtn;

    public DonateActivity() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_donate, container,false);

        donateBloodBtn = view.findViewById(R.id.bloodBtn);
        donatePlasmaBtn = view.findViewById(R.id.plasmaBtn);

        donateBloodBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),DonateBloodEligibility.class);
                startActivity(intent);
            }
        });

        donatePlasmaBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),DonatePlasmaEligibility.class);
                startActivity(intent);
            }
        });
        return view;

    }
}
