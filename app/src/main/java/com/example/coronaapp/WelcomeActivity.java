package com.example.coronaapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

public class WelcomeActivity extends AppCompatActivity {

    public static final String TAG = "TAG";
    private TextView fullName;
    private FirebaseAuth fAuth;
    private FirebaseFirestore fStore;

    private String userId;

    private UserModel user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome3);

        BottomNavigationView bottomView = findViewById(R.id.bottom_navigation);
        bottomView.setOnNavigationItemSelectedListener(navListener);

        fullName = findViewById(R.id.fullNameText);

        fStore = FirebaseFirestore.getInstance();
        fAuth = FirebaseAuth.getInstance();

        user = new UserModel();
        userId = fAuth.getCurrentUser().getUid();
        System.out.println("User id in Welcome - "+userId);

        final DocumentReference documentReference = fStore.collection("users").document(userId);
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                fullName.setText("Welcome " + value.getString("FirstName")+" "+value.getString("LastName"));
                user.setFirstName(value.getString("FirstName"));
                user.setLastName(value.getString("LastName"));
                Log.d(TAG,"onEvent: "+user.getFirstName());
                user.setGender(value.getString("Gender"));
                user.setCity(value.getString("City"));
                user.setBloodGroup(value.getString("BloodGroup"));
                user.setAge(value.getString("Age"));
                user.setEmail(value.getString("Email"));
                user.setUserId(userId);
                user.setPhone(value.getString("Phone"));
            }
        });

//        fullName.setText("Welcome " + user.getFirstname() + " " + user.getLastname());

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new DonateActivity()).commitNow();
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    Fragment selectedFragment = null;

                    switch (item.getItemId()) {
                        case R.id.donor:
                            selectedFragment = new DonateActivity();
                            break;

                        case R.id.request:
                            selectedFragment = new RequestActivity();
                            break;

                        case R.id.profile:
                            selectedFragment = new MyProfileActivity();
                            break;
                    }

                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                            selectedFragment).commit();

                    return true;
                }
            };
    }