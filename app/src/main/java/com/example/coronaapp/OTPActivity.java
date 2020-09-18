package com.example.coronaapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.w3c.dom.Text;

import java.util.concurrent.TimeUnit;

public class OTPActivity extends AppCompatActivity {

    private EditText otpPhoneEdit, otpPhoneNumber;
    private TextView otpPhoneText;
    private String verificationId;
    private PhoneAuthProvider.ForceResendingToken token;
    private ProgressBar progressBar;
    private Button registerBtn;
    private Boolean verificationInProgress = false;
    private String userOTP;

    private FirebaseAuth fAuth;
    private FirebaseFirestore fStore;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_o_t_p);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        otpPhoneNumber = findViewById(R.id.enterPhoneEdit);
        otpPhoneEdit = findViewById(R.id.OTPEdit);
        registerBtn = findViewById(R.id.saveBtn);
        otpPhoneText = findViewById(R.id.phoneOTPText);
        progressBar = findViewById(R.id.progressBar);

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!verificationInProgress){
                    if(otpPhoneNumber.getText().toString() != null)
                    {
                        progressBar.setVisibility(View.VISIBLE);
                        otpPhoneText.setVisibility(View.VISIBLE);
                        otpPhoneEdit.setVisibility(View.VISIBLE);
                        requestOTP(otpPhoneNumber.getText().toString());
                    }
                }else{
                    userOTP = otpPhoneEdit.getText().toString();
                    if(!userOTP.isEmpty() && userOTP.length() == 6)
                    {
                        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, userOTP);
                        System.out.println("Credential = "+credential);
                        verifyAuth(credential);
                    }else{
                        Toast.makeText(OTPActivity.this, "Valid OTP is required", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });


    }

    private void verifyAuth(PhoneAuthCredential credential) {
        fAuth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Toast.makeText(OTPActivity.this, "Authentication is successful", Toast.LENGTH_SHORT).show();
                    checkUserProfile();
                }
                else {
                    Toast.makeText(OTPActivity.this, "Authentication Failed", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    private void requestOTP(String phone) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(phone, 60L, TimeUnit.SECONDS, this, new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(s, forceResendingToken);
                progressBar.setVisibility(View.GONE);
                verificationInProgress = true;

                verificationId = s;
                token = forceResendingToken;

                registerBtn.setText("Verify");
//                registerBtn.setEnabled(false);
            }

            @Override
            public void onCodeAutoRetrievalTimeOut(@NonNull String s) {
                super.onCodeAutoRetrievalTimeOut(s);
                Toast.makeText(OTPActivity.this, "OTP Expired, Re-request the OTP", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                verifyAuth(phoneAuthCredential);
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                Toast.makeText(OTPActivity.this, "Cannot Create Account "+ e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        if(fAuth.getCurrentUser() != null)
        {
            progressBar.setVisibility(View.VISIBLE);
            checkUserProfile();
        }
    }

    private void checkUserProfile() {
        DocumentReference docRef = fStore.collection("users").document(fAuth.getCurrentUser().getUid());
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.exists()){
                    startActivity(new Intent(getApplicationContext(), WelcomeActivity.class).putExtra("UserId", fAuth.getCurrentUser().getUid()));
                    System.out.println("Welcome");
//                    finish();
                }else{
                    startActivity(new Intent(getApplicationContext(), RegisterActivity.class).putExtra("Phone", otpPhoneNumber.getText().toString()));
                    System.out.println("Register");
//                    finish();
                }
            }
        });
    }


}