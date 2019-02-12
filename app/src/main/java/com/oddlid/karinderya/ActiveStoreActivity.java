package com.oddlid.karinderya;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class ActiveStoreActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_active_store);
        Intent intent = getIntent();
        //IF ACTIVITY WAS OPENED BY OWNER
        if(intent.getExtras().getBoolean("byOwner"))
        {
            //add to show
            Button addAvailable = findViewById(R.id.a_activeStore_addAvailable_btn);
            addAvailable.setVisibility(View.VISIBLE);
            Button changeBanner = findViewById(R.id.a_activeStore_changeBanner_btn);
            changeBanner.setVisibility(View.VISIBLE);
            Button cancelChange = findViewById(R.id.a_activeStore_cancelChangeBanner_btn);
            cancelChange.setVisibility(View.VISIBLE);
        }

    }
}
