package com.oddlid.karinderya;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class SignupActivity extends AppCompatActivity {
    Button signupBtn;
    EditText editEmail;
    EditText editPassword;
    EditText editConfirmPassword;
    EditText editName;
    TextView linkLogin;
    Uri imageUri;
    //altar of the almigthy based firebase
    FirebaseAuth fbAuth;
    FirebaseDatabase fbDatabase;
    FirebaseAuth.AuthStateListener fbAuthListener;
    FirebaseStorage fbStorage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        signupBtn = (Button) findViewById(R.id.signinBtn);
        editEmail = (EditText) findViewById(R.id.editEmail);
        editPassword = (EditText) findViewById(R.id.editPassword);
        editConfirmPassword = (EditText) findViewById(R.id.editConfirmPassword);
        editName = (EditText) findViewById(R.id.editName);
        fbAuth = FirebaseAuth.getInstance();

        //sign up event
        signupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                String password = editPassword.getText().toString();
                String cPassword = editConfirmPassword.getText().toString();
                if(password.equals(cPassword))
                {
                    fbAuth.createUserWithEmailAndPassword(editEmail.getText().toString(), editPassword.getText().toString())
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if(task.isSuccessful())
                                    {
                                        //If registration is successful, verify email
                                        fbAuth.getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if(task.isSuccessful())
                                                {
                                                    String userID = fbAuth.getCurrentUser().getUid();
                                                    DatabaseReference currentUser = FirebaseDatabase.getInstance().getReference().child("Users").child(userID);
                                                    StorageReference storageUser = FirebaseStorage.getInstance().getReference().child("Users").child(userID).child("profile_picture");

                                                    //saving to database I MEAN FIREBASE NIGGA!
                                                    User user = new User(editName.getText().toString(), 1);
                                                    currentUser.setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if(task.isSuccessful())
                                                            {
                                                                //clear fields
                                                                editEmail.setText("");
                                                                editPassword.setText("");
                                                                editConfirmPassword.setText("");
                                                                editName.setText("");
                                                                //registration complete
                                                                snackbarMessage(v,"Mission accomplished! Please check email for verification");
                                                            }
                                                            else
                                                            {
                                                                snackbarMessage(v, task.getException().getMessage().toString());
                                                            }
                                                        }
                                                    });

                                                }
                                                else
                                                {
                                                    snackbarMessage(v, task.getException().getMessage().toString());
                                                }
                                            }
                                        });
                                    }
                                    else
                                    {
                                        snackbarMessage(v, task.getException().getMessage());
                                    }
                                }
                            });
                }
                else
                {
                    snackbarMessage(v,"Entered password didn't match!");
                }

            }
        });

        linkLogin = (TextView) findViewById(R.id.openLogin);
        linkLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    private void snackbarMessage(View v, String message)
    {
        Snackbar snack = Snackbar.make(v, message, Snackbar.LENGTH_LONG);
        snack.show();
    }

}
