package com.example.coronaapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    public static final String TAG = "TAG";
    private UserModel user;
    private EditText firstname, lastname, ageEdit;
    private Button registerBtn;
    private RadioGroup radioGroup;
    private int selectedGenderId;
    private Spinner bloodGroupSpinner, citySpinner;
    private String phoneNumber;
    String[] bloodGroup = { "A+", "A-", "B+", "B-", "O+", "O-", "AB+", "AB-" };
    String[] citySpinnerString = {"Ahmedabad", "Amritsar", "Delhi","Hyderabad","Ludhiana","Mumbai"};

    private FirebaseAuth fAuth;
    private FirebaseFirestore fStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);

        if(savedInstanceState == null){
            setContentView(R.layout.register);
        }

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        // to check if user is already logged-in

        Log.d(TAG, "onCreate: "+fAuth.getCurrentUser());

        if(fAuth.getCurrentUser() != null){
//            user.setUserId(fAuth.getCurrentUser().getUid());
            startActivity(new Intent(getApplicationContext(),WelcomeActivity.class));
            finish();
        }

        user = new UserModel();

        Bundle extras = getIntent().getExtras();
        if(extras == null){
            Toast.makeText(this, "There is nothing in the intent", Toast.LENGTH_SHORT).show();
        }

        phoneNumber = extras.getString("Phone");
        user.setPhone(phoneNumber);
//        user = (UserModel)extras.getParcelable("user");
//        user.setEmail(extras.getString("email"));
//        user.setPassword(extras.getString("password"));
        firstname = findViewById(R.id.firstNameEdit);
        lastname = findViewById(R.id.lastNameEdit);
        registerBtn = findViewById(R.id.registerButton);
        radioGroup = (RadioGroup) findViewById(R.id.genderGroup);
        ageEdit = findViewById(R.id.ageEdit);

        bloodGroupSpinner = findViewById(R.id.bloodGroupSpinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, bloodGroup);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        bloodGroupSpinner.setAdapter(adapter);
        bloodGroupSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                user.setBloodGroup(bloodGroup[position]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        citySpinner = findViewById(R.id.citySpinner);
        ArrayAdapter<String> adapterCity = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, citySpinnerString);
        adapterCity.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        citySpinner.setAdapter(adapterCity);
        citySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                user.setCity(citySpinnerString[position]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                user.setFirstName(firstname.getText().toString());

                if(TextUtils.isEmpty(firstname.getText().toString())){
                    firstname.setError("First Name is required!");
                    return;
                }

                user.setLastName(lastname.getText().toString());

                if(TextUtils.isEmpty(lastname.getText().toString())){
                    lastname.setError("Last Name is required!");
                    return;
                }

                selectedGenderId = radioGroup.getCheckedRadioButtonId();

                if(selectedGenderId == R.id.maleRadio) {
                    user.setGender("Male");
                } else if(selectedGenderId == R.id.femaleRadio) {
                    user.setGender("Female");
                } else if(selectedGenderId == R.id.anotherRadio){
                    user.setGender("Prefer not to say");
                }

                user.setAge(ageEdit.getText().toString());

//                System.out.println("UserEmail - "+user.getEmail()+ user.getPassword());

                //register to firebase

                fAuth.createUserWithEmailAndPassword(user.getEmail(),user.getPassword()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Intent intent = new Intent();
                            intent.putExtra("user",user);
                            Toast.makeText(RegisterActivity.this, "User Created", Toast.LENGTH_SHORT).show();
                            user.setUserId(fAuth.getCurrentUser().getUid());
                            DocumentReference documentReference = fStore.collection("users").document(user.getUserId());
                            Map<String, Object> userMap = new HashMap<>();
                            userMap.put("UserId", user.getUserId());
                            userMap.put("FirstName", user.getFirstName());
                            userMap.put("LastName", user.getLastName());
                            userMap.put("Gender", user.getGender());
                            userMap.put("Age", user.getAge());
                            userMap.put("BloodGroup", user.getBloodGroup());
                            userMap.put("City", user.getCity());
                            userMap.put("Email", user.getEmail());
                            userMap.put("Phone", user.getPhone());

                            documentReference.set(userMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d(TAG, "onSuccess: user profile is created for "+user.getFirstName());
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d(TAG, "onFailure: " + e.toString());
                                }
                            });
                            startActivity(new Intent(getApplicationContext(),WelcomeActivity.class));
                        }else{
                            Toast.makeText(RegisterActivity.this, "Error "+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }
        });

    }
}