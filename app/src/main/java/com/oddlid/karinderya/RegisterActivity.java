package com.oddlid.karinderya;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Random;

public class RegisterActivity extends AppCompatActivity {
    //other initializations
    private final int REQUEST_CODE = 1553, REQUEST_EXTERNAL_STORAGE = 1;

    Button selectImageBtn, removeImageBtn;
    LinearLayout gallery;

    //firebase initializations
    FirebaseAuth fbAuth;
    FirebaseDatabase fbDatabase;
    FirebaseStorage fbStorage;
    DatabaseReference dbRef;
    StorageReference storeRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        fbAuth = FirebaseAuth.getInstance();

        selectImageBtn = (Button) findViewById(R.id.selectImageBtn);
    }


    //changing the back press behavior
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Go back?").setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener).show();
    }
    //creating an event catcher for the alert
    private DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which)
            {
                case DialogInterface.BUTTON_POSITIVE:
                    finish();

                    break;
                case DialogInterface.BUTTON_NEGATIVE:
                    break;
            }

        }
    };

    //random ID generator
    public static String random() {
        Random generator = new Random();
        StringBuilder randomStringBuilder = new StringBuilder();
        char tempChar;
        for (int i = 0; i < 6; i++){
            tempChar = (char) (generator.nextInt(96) + 32);
            randomStringBuilder.append(tempChar);
        }
        return randomStringBuilder.toString();
    }

    //GETTING IMAGE FROM GALLERY
    private void handlePermission()
    {
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.M)
        {
            openImageChooser();
        }
        else
        {
            if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
            {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_EXTERNAL_STORAGE);
            }
            else
            {
                openImageChooser();
            }
        }
    }
    public void onRequestPermissionResult(int requestCode, String permissions[], int[] grantResults)
    {
        switch(requestCode)
        {
            case REQUEST_EXTERNAL_STORAGE:{
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    openImageChooser();
                }
                else
                {
                    snackbarMessage(findViewById(android.R.id.content), "You need to permit access to storage. Good luck!");
                }
                return;
            }
        }
    }
    private void openImageChooser()
    {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select images"), REQUEST_CODE);
    }
    @Override
    public void onActivityResult(final int requestCode, final int resultCode, @Nullable final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (null != data) {
            ClipData clipData = data.getClipData();
            gallery = (LinearLayout) findViewById(R.id.gallery);

            for(int i = 0; i < clipData.getItemCount(); i++)
            {
                ImageView storeImage = new ImageView(this);
                //storeImage.setMaxWidth((int) getResources().getDimension(R.dimen.squareImage));
                //storeImage.setMaxHeight((int) getResources().getDimension(R.dimen.squareImage));
                storeImage.setBackground(getDrawable(R.drawable.round_border_btn));
                storeImage.setPadding(25, 25, 25, 25);
                storeImage.setAdjustViewBounds(true);
                storeImage.setImageURI(clipData.getItemAt(i).getUri());

                gallery.addView(storeImage);
            }
            removeImageBtn = (Button) findViewById(R.id.removeImageBtn);
            removeImageBtn.setVisibility(View.VISIBLE);
        }
    }
    //END

    private void snackbarMessage(View v, String message)
    {
        Snackbar snack = Snackbar.make(v, message, Snackbar.LENGTH_LONG);
        snack.show();
    }

    //select image button logic
    public void selectImage(View view)
    {
        handlePermission();
    }

    //remove image button logic
    public void removeImage(View view)
    {
        gallery = (LinearLayout) findViewById(R.id.gallery);
        gallery.removeAllViews();

        removeImageBtn = (Button) findViewById(R.id.removeImageBtn);
        removeImageBtn.setVisibility(View.GONE);
    }

}
