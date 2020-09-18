package com.example.coronaapp;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

public class UserListViewFragment extends Fragment {

    public static final String TAG = "TAG";
    private TextView fullNameText;
    private TextView cityText;
    private TextView genderText;
    private TextView ageText;
    private TextView bloodGroupText;
    private TextView phoneText;

    private FirebaseFirestore fStore;
    private FirebaseAuth fAuth;

    @RequiresApi(api = Build.VERSION_CODES.P)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.activity_user_list_view_fragment, container, false);

        Bundle args = getArguments();
        String userId = args.getString("id");
        System.out.println("ID = "+userId);

        fullNameText = view.findViewById(R.id.fullNameGet);
        cityText = view.findViewById(R.id.cityGet);
        genderText = view.findViewById(R.id.genderGet);
        ageText = view.findViewById(R.id.ageGet);
        bloodGroupText = view.findViewById(R.id.bloodGroupGet);
        phoneText = view.findViewById(R.id.phoneGet);

        fStore = FirebaseFirestore.getInstance();
        fAuth = FirebaseAuth.getInstance();

        final DocumentReference documentReference = fStore.collection("users").document(userId);
        documentReference.addSnapshotListener(getActivity().getApplicationContext().getMainExecutor(), new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                fullNameText.setText(value.getString("FirstName")+" "+value.getString("LastName"));
                Log.d(TAG,"onEvent: "+fullNameText.getText().toString());
                genderText.setText(value.getString("Gender"));
                cityText.setText(value.getString("City"));
                bloodGroupText.setText(value.getString("BloodGroup"));
                ageText.setText(value.getString("Age"));
                phoneText.setText(value.getString("Phone"));
            }
        });

        return view;
    }
}