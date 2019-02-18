package com.appbox.mep;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class EditProfileActivity extends AppCompatActivity {
    private static final int REC_PROF_PIC = 7;
    private final String EXTRA_UID ="9";
    private String mUid;
    private User mUser;
    private DatabaseReference mDatabaseReference;
    private EditText mNameEdit;
    private ImageView mUserView;
    private EditText mBio;
    private Button mButton;

    public static Intent newInstance(Context context, String paramId){
        Intent intent = new Intent(context,EditProfileActivity.class);
        intent.putExtra(Intent.EXTRA_UID,paramId);
        return intent;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        mUid = getIntent().getStringExtra(EXTRA_UID);
        mNameEdit = findViewById(R.id.name_edit);
        mUserView = findViewById(R.id.edit_url);
        mBio =findViewById(R.id.edit_bio);
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();
        mDatabaseReference.child(mUid).child("User").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    mUser = dataSnapshot.getValue(User.class);
                    mBio.setText(mUser.getBio());
                    mNameEdit.setText(mUser.getName());
                    Glide.with(EditProfileActivity.this).setDefaultRequestOptions(new RequestOptions().circleCrop()).load(mUser.getPhoto()).into(mUserView);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        mButton = findViewById(R.id.edit_profile);
        mUserView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(Intent.ACTION_GET_CONTENT),REC_PROF_PIC);
            }
        });
        mButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                mUser.setBio(mBio.getText().toString());
                mUser.setName(mNameEdit.getText().toString());
                mDatabaseReference.child(mUid).child("User").setValue(mUser).addOnSuccessListener(EditProfileActivity.this, new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        setResult(RESULT_OK);
                        finish();
                    }
                });
            }
        });
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode,resultCode,data);
        if (requestCode == REC_PROF_PIC){
            Uri image = data.getData();
            mUser.setPhoto(image);
            Glide.with(EditProfileActivity.this).setDefaultRequestOptions(new RequestOptions().circleCrop()).load(image).into(mUserView);
        }
    }
}
