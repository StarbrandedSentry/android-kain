package com.oddlid.karinderya;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {
    Button signupBtn, loginBtn;
    EditText editEmail, editPassword;

    FirebaseAuth fbAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        fbAuth = FirebaseAuth.getInstance();
        //connecting the view to the logic
        loginBtn = (Button) findViewById(R.id.loginBtn);
        editEmail = (EditText) findViewById(R.id.editEmail);
        editPassword = (EditText) findViewById(R.id.editPass);
        signupBtn = (Button) findViewById(R.id.signBtn);

        signupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent openIntent = new Intent(getApplicationContext(), SignupActivity.class);
                startActivity(openIntent);
            }
        });

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                loginBtn.setClickable(false);
                fbAuth = FirebaseAuth.getInstance();
                fbAuth.signInWithEmailAndPassword(editEmail.getText().toString(), editPassword.getText().toString())
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            loginBtn.setClickable(true);
                            if(task.isSuccessful())
                            {
                                if(fbAuth.getCurrentUser().isEmailVerified())
                                {
                                    startActivity(new Intent(MainActivity.this, HomeActivity.class));
                                }
                                else
                                {
                                    snackbarMessage(v, "Verify your email first by opening the email we sent!");
                                }
                            }
                            else
                            {
                                Toast toast = Toast.makeText(getApplicationContext(), task.getException().getMessage(),
                                        Toast.LENGTH_SHORT);
                                toast.show();
                            }
                        }
                    });
            }
        });


    }

    private void snackbarMessage(View v, String message)
    {
        Snackbar snack = Snackbar.make(v, message, Snackbar.LENGTH_LONG);
        snack.show();
    }

    private void toastMessage(Context c, String message)
    {
        Toast toast = Toast.makeText(c, message, Toast.LENGTH_LONG);
        toast.show();
    }

}
