package com.oddlid.karinderya;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


/**
 * A simple {@link Fragment} subclass.
 */
public class AccountFragment extends Fragment {
    //Other initializations
    String name, email, uid;

    //initializations
    Button logoutBtn;
    Button registerBtn;
    Button openAdmin;

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

        openAdmin = (Button) view.findViewById(R.id.openAdmin);

        //setting up
        final TextView nameText = (TextView) view.findViewById(R.id.nameText);
        final TextView emailText = (TextView) view.findViewById(R.id.emailText);
        final TextView leveluserText = (TextView) view.findViewById(R.id.userlevelText);
        setDetails(nameText, emailText, leveluserText);
        switch(getArguments().getInt("userLevel"))
        {
            case 3:
                openAdmin.setVisibility(View.VISIBLE);
                break;
            default:
                    break;
        }

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

    //Set texts
    private void setDetails(TextView name, TextView email, TextView userType)
    {
        name.setText(getArguments().getString("name"));
        email.setText(getArguments().getString("email"));
        userType.setText(getArguments().getString("userType"));
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
