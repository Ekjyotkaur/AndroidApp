package com.example.coronaapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;


public class MyProfileActivity extends Fragment {

    public static final String TAG = "TAG";
    Context thisContext;

    private TextView fullNameText, cityText;
    private TextView emailText, bloodGroupText;
    private TextView ageText, genderText;

    private Button editProfileBtn;

    private String userId;

    private FirebaseFirestore fStore;
    private FirebaseAuth fAuth;

    private UserModel user;

    public MyProfileActivity(){

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        thisContext=context;
    }
    @RequiresApi(api = Build.VERSION_CODES.P)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view =  inflater.inflate(R.layout.activity_my_profile, container,false);

        fullNameText = view.findViewById(R.id.fullName);
        genderText = view.findViewById(R.id.gender);
        cityText = view.findViewById(R.id.cityText);
        bloodGroupText = view.findViewById(R.id.bloodGroup);
        ageText = view.findViewById(R.id.age);
        emailText = view.findViewById(R.id.email);

        editProfileBtn = view.findViewById(R.id.editBtn);

        fStore = FirebaseFirestore.getInstance();
        fAuth = FirebaseAuth.getInstance();

        userId = fAuth.getCurrentUser().getUid();

        user = new UserModel();
        user.setUserId(userId);

        final DocumentReference documentReference = fStore.collection("users").document(userId);
        documentReference.addSnapshotListener(getActivity().getApplicationContext().getMainExecutor(), new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                fullNameText.setText(value.getString("FirstName")+" "+value.getString("LastName"));
                Log.d(TAG ,"onEvent: "+fullNameText.getText().toString());
                genderText.setText(value.getString("Gender"));
                cityText.setText(value.getString("City"));
                bloodGroupText.setText(value.getString("BloodGroup"));
                ageText.setText(value.getString("Age"));
                emailText.setText(value.getString("Email"));
            }
        });

        return  view;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.logout_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.logout){
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(getActivity(), MainActivity.class));
//            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}