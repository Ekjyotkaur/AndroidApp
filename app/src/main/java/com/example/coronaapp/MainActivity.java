package com.example.coronaapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthCredential;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = "TAG";
    private SignInButton signInBtn;
    private GoogleSignInClient mGoogleSignInClient;
    private FirebaseAuth mAuth;
    private FirebaseFirestore fStore;

    private Button reisterBtn, loginBtn;
    private int RC_SIGN_IN= 1;
    private EditText username, password;
    private String usernameString, passwordString;
    private UserModel user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        user = new UserModel();
        fStore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        username = findViewById(R.id.usernameEdit);
        password = findViewById(R.id.passwordEdit);
        signInBtn = findViewById(R.id.signInGoogle);
        reisterBtn = findViewById(R.id.register);
        loginBtn = findViewById(R.id.loginWithEmailBtn);

        GoogleSignInOptions options = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, options);

        signInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                googleSignIn();
            }
        });

        reisterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                usernameString = username.getText().toString();
                passwordString = password.getText().toString();

                if(TextUtils.isEmpty(usernameString)){
                    username.setError("Email is required!");
                    return;
                }

                if(TextUtils.isEmpty(passwordString)){
                    password.setError("Password is required!");
                    return;
                }

                if(password.length() < 6)
                {
                    password.setError("Password should be at least 6 characters");
                }

                user.setEmail(usernameString);
                user.setPassword(passwordString);
                Intent intent = new Intent(MainActivity.this,RegisterActivity.class);
                intent.putExtra("user", user);
                startActivity(intent);
            }
        });

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getData();

                //authenticate the user
                mAuth.signInWithEmailAndPassword(usernameString,passwordString).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Intent intent = new Intent(MainActivity.this,RegisterActivity.class);
                            intent.putExtra("user",user);
                            Toast.makeText(MainActivity.this, "Logged in successful", Toast.LENGTH_SHORT).show();
                            startActivity(intent);
                        }else {
                            Toast.makeText(MainActivity.this, "Failed!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }

    private void googleSignIn(){
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent,RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask)
    {
        try {
            GoogleSignInAccount acc = completedTask.getResult(ApiException.class);
            Toast.makeText(MainActivity.this, "Signed in Successfully", Toast.LENGTH_SHORT).show();
            FirebaseGoogleAuth(acc);
        }catch(ApiException e){
            Toast.makeText(MainActivity.this, "Sign In Failed", Toast.LENGTH_SHORT).show();
            FirebaseGoogleAuth(null);
        }
    }

    private void FirebaseGoogleAuth(GoogleSignInAccount acct){
        AuthCredential authCredential = GoogleAuthProvider.getCredential(acct.getIdToken(),null);
        mAuth.signInWithCredential(authCredential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Toast.makeText(MainActivity.this, "Successful", Toast.LENGTH_SHORT).show();
                    FirebaseUser user = mAuth.getCurrentUser();
                    updateUI(user);
                }
                else{
                    Toast.makeText(MainActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                    updateUI(null);
                }
            }
        });
    }

    private void updateUI(FirebaseUser fUser){
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(getApplicationContext());
        if(account!=null)
        {
            user.setFirstName(account.getGivenName());
            user.setLastName(account.getFamilyName());
            user.setEmail(account.getEmail());
            Uri personPhoto = account.getPhotoUrl();
            user.setUserId(account.getId());
            System.out.println("USer id Token- " + account.getIdToken() + "USer id - "+user.getUserId());

            DocumentReference documentReference = fStore.collection("users").document(user.getUserId());
            Map<String, Object> userMap = new HashMap<>();
            userMap.put("FirstName", user.getFirstName());
            userMap.put("LastName", user.getLastName());
            userMap.put("Email", user.getEmail());
            userMap.put("UserId", user.getUserId());

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
            Toast.makeText(MainActivity.this, "Welcome "+user.getFirstName()+"!", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(getApplicationContext(),WelcomeActivity.class));
        }
    }

    private void getData(){
        usernameString = username.getText().toString();
        passwordString = password.getText().toString();

        if(TextUtils.isEmpty(usernameString)){
            username.setError("Email is required!");
            return;
        }

        if(TextUtils.isEmpty(passwordString)){
            password.setError("Password is required!");
            return;
        }

        if(password.length() < 6)
        {
            password.setError("Password should be at least 6 characters");
        }
    }

}
