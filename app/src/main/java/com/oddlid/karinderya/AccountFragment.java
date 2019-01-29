package com.oddlid.karinderya;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


/**
 * A simple {@link Fragment} subclass.
 */
public class AccountFragment extends Fragment {
    //Other initializations
    String name, email, uid;

    //initializations
    Button logoutBtn;
    Button registerBtn;


    //firebase initializations
    FirebaseAuth fbAuth;
    FirebaseUser fbUser;
    DatabaseReference dbRef;
    FirebaseDatabase fbDatabase;

    public static final int REQUEST_CODE=1010;

    public AccountFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_account, container, false);
        fbAuth = FirebaseAuth.getInstance();
        fbUser = fbAuth.getCurrentUser();
        fbDatabase = FirebaseDatabase.getInstance();

        final TextView nameText = (TextView) view.findViewById(R.id.nameText);
        final TextView emailText = (TextView) view.findViewById(R.id.emailText);
        final TextView leveluserText = (TextView) view.findViewById(R.id.userlevelText);

        //Settings names and stuffs
        uid = fbAuth.getUid();
        dbRef = fbDatabase.getInstance().getReference().child("Users").child(uid);
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                //setting names
                nameText.setText("NAME: " + user.getName());

                emailText.setText("EMAIL: " + fbUser.getEmail());
                if(user.getUser_level() == 1)
                {
                    leveluserText.setText("USER TYPE: Normal");
                }
                else if(user.getUser_level() == 2)
                {
                    leveluserText.setText("USER TYPE: Owner");
                }
                else if(user.getUser_level() == 3)
                {
                    leveluserText.setText("USER TYPE: Admin");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                snackbarMessage(view, databaseError.getMessage());
            }
        });

        //logout button logic
        logoutBtn = (Button) view.findViewById(R.id.logoutBtn);

        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setMessage("Are you sure you wanted to logout?").setPositiveButton("Yes", dialogClickListener)
                        .setNegativeButton("No", dialogClickListener).show();
            }
        });

        //register button logic
        registerBtn = (Button) view.findViewById(R.id.registerKarinderya);
        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), RegisterActivity.class);
                startActivity(intent);
            }
        });

        return view;
    }

    private void snackbarMessage(View v, String message)
    {
        Snackbar snack = Snackbar.make(v, message, Snackbar.LENGTH_LONG);
        snack.show();
    }

    private DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which)
            {
                case DialogInterface.BUTTON_POSITIVE:
                    try {
                        fbAuth.getInstance().signOut();
                        Intent intent = new Intent(getActivity(), MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    break;
                case DialogInterface.BUTTON_NEGATIVE:
                    break;
            }

        }
    };

}
