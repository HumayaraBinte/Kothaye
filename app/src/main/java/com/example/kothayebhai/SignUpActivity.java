package com.example.kothayebhai;

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
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.database.FirebaseDatabase;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText signUpNameEditText, signUpEmailEditText, signUpPasswordEditText, signUpPhoneEditText,
            signUpEducationEditText, signUpJobEditText, signUpNextJobEditText;
    private TextView signInTextView;
    private Button signUpButton;
    private FirebaseAuth mAuth;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        this.setTitle("Sign up Activity");

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        progressBar = findViewById(R.id.progressbarId);

        signUpNameEditText = findViewById(R.id.signUpNameEditTextId);

        signUpEmailEditText = findViewById(R.id.signUpEmailEditTextId);
        signUpPasswordEditText = findViewById(R.id.signUpPasswordEditTextId);

        signUpPhoneEditText = findViewById(R.id.signUpPhoneEditTextId);
        signUpEducationEditText = findViewById(R.id.signUpEducationEditTextId);
        signUpJobEditText = findViewById(R.id.signUpJobEditTextId);
        signUpNextJobEditText = findViewById(R.id.signUpNextJobEditTextId);

        signInTextView = findViewById(R.id.signInTextViewId);
        signUpButton = findViewById(R.id.signUpButtonId);

        signInTextView.setOnClickListener(this);
        signUpButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.signUpButtonId:
                userRegister();
                break;

            case R.id.signInTextViewId:
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                break;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(mAuth.getCurrentUser() != null){
            //handles already logged in user
            Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
    }

    private void userRegister() {
        final String name = signUpNameEditText.getText().toString().trim();

        final String email = signUpEmailEditText.getText().toString().trim();
        String password = signUpPasswordEditText.getText().toString().trim();

        final String phone = signUpPhoneEditText.getText().toString().trim();
        final String education = signUpEducationEditText.getText().toString().trim();
        final String current_job = signUpJobEditText.getText().toString().trim();
        final String future_job = signUpNextJobEditText.getText().toString().trim();

        if(email.isEmpty())
        {
            signUpEmailEditText.setError("Enter an email address");
            signUpEmailEditText.requestFocus();
            return;
        }

        if(!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches())
        {
            signUpEmailEditText.setError("Enter a valid email address");
            signUpEmailEditText.requestFocus();
            return;
        }

        //checking the validity of the password
        if(password.isEmpty())
        {
            signUpPasswordEditText.setError("Enter a password");
            signUpPasswordEditText.requestFocus();
            return;
        }

        if(password.length()<6){
            signUpPasswordEditText.setError("Minimum lengh of a password should be 6");
            signUpPasswordEditText.requestFocus();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    //Toast.makeText(getApplicationContext(), "Registered Successfully", Toast.LENGTH_SHORT).show();
                    User user = new User(
                            name,
                            email,
                            phone,
                            education,
                            current_job,
                            future_job

                    );
                    FirebaseDatabase.getInstance().getReference("Users")
                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                            .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                progressBar.setVisibility(View.GONE);
                                Toast.makeText(getApplicationContext(), "Registered Successfully", Toast.LENGTH_LONG).show();
                            }else{
                                Toast.makeText(getApplicationContext(), "Faced error", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                } else {
                        Toast.makeText(getApplicationContext(), "Error:"+ task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
