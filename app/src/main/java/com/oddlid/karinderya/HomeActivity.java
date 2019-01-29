package com.oddlid.karinderya;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class HomeActivity extends AppCompatActivity {
    //Other initializations
    String name, email, uid;

    //initializations
    Button logoutBtn;
    Button registerBtn;
    private TextView mTextMessage;

    //firebase initializations
    FirebaseAuth fbAuth;
    FirebaseUser fbUser;
    DatabaseReference dbRef;
    StorageReference storeRef;
    FirebaseDatabase fbDatabase;
    FirebaseStorage fbStorage;


    public BottomNavigationView mainNav;
    public FrameLayout mainFrame;

    private HomeFragment homeFragment;
    private PromosFragment promosFragment;
    private SearchFragment searchFragment;
    private AccountFragment accountFragment;
    private ManageFragment manageFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        fbAuth = FirebaseAuth.getInstance();
        fbUser = fbAuth.getCurrentUser();
        fbDatabase = FirebaseDatabase.getInstance();

        mainFrame = (FrameLayout) findViewById(R.id.main_frame);
        mainNav = (BottomNavigationView) findViewById(R.id.main_nav);

        homeFragment = new HomeFragment();
        promosFragment = new PromosFragment();
        searchFragment = new SearchFragment();
        accountFragment = new AccountFragment();
        Bundle accountBundle = getDetails(findViewById(R.id.content));
        accountFragment.setArguments(accountBundle);

        manageFragment = new ManageFragment();

        mainNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId())
                {
                    case R.id.nav_home:
                        setFragment(homeFragment);
                        return true;
                    case R.id.nav_search:
                        setFragment(searchFragment);
                        return true;
                    case R.id.nav_promos:
                        setFragment(promosFragment);
                        return true;
                    case R.id.nav_manage:
                        setFragment(manageFragment);
                        return true;
                    case R.id.nav_account:
                        setFragment(accountFragment);
                        return true;
                    default:
                        return false;
                }
            }
        });


    }

    private void setFragment(Fragment fragment)
    {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.main_frame, fragment);
        fragmentTransaction.commit();
    }

    @Override
    public void onBackPressed() {
        //Include the code here

        return;
    }

    public Bundle getDetails(final View v)
    {
        final Bundle accountBundle = new Bundle();

        //Settings names and stuffs
        uid = fbAuth.getUid();
        dbRef = fbDatabase.getInstance().getReference().child("Users").child(uid);
        storeRef = FirebaseStorage.getInstance().getReference().child("Users").child(uid).child("profile_picture");
        final long ONE_MEGABYTE = 1024 * 1024;

        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                //setting names
                accountBundle.putString("name", "NAME: " + user.getName());
                accountBundle.putString("email", "EMAIL: " + fbUser.getEmail());
                if(user.getUser_level() == 1)
                {
                    accountBundle.putString("userType", "USER TYPE: Normal");
                }
                else if(user.getUser_level() == 2)
                {
                    accountBundle.putString("userType", "USER TYPE: Owner");
                }
                else if(user.getUser_level() == 3)
                {
                    accountBundle.putString("userType", "USER TYPE: Admin");
                }
                accountBundle.putInt("userLevel", user.getUser_level());

                storeRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                    @Override
                    public void onSuccess(byte[] bytes) {
                        accountBundle.putByteArray("profile_picture", bytes);
                    }
                });
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                snackbarMessage(v, databaseError.getMessage());
            }
        });

        return accountBundle;
    }

    private void snackbarMessage(View v, String message)
    {
        Snackbar snack = Snackbar.make(v, message, Snackbar.LENGTH_LONG);
        snack.show();
    }
}
