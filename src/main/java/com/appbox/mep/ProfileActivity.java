package com.appbox.mep;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener{
    private static final String REC_USER_EXTRA = "10";
    private String mId;
    private User mParamId;
    private ImageView mImageView;
    private TextView mName;
    private TextView mBio;
    private Button mFriends;
    private String mainId;
    private Button mEvents;
    private Button mAdd;
    private DatabaseReference mDatabaseReference;
    private FragmentManager mFragmentManager;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    static Intent newInstance(Context context, String id){
        Intent intent = new Intent(context, ProfileActivity.class);
        intent.putExtra(REC_USER_EXTRA, id);
        return intent;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();
        mId = getIntent().getStringExtra(REC_USER_EXTRA);
        mImageView = findViewById(R.id.profile_url);
        mName= findViewById(R.id.profile_name);
        mFirebaseAuth = FirebaseAuth.getInstance();
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if(user!=null){
                    mainId = user.getUid();
                    getCurrentUser(mainId);
                }
            }
        };
        mFragmentManager = getSupportFragmentManager();
        mBio = findViewById(R.id.bio2);
        mFriends = findViewById(R.id.friends_button);
        mEvents = findViewById(R.id.events_button);
        mAdd = findViewById(R.id.add_friend);
        mAdd.setOnClickListener(this);
        mEvents.setOnClickListener(this);
        mFriends.setOnClickListener(this);
        mDatabaseReference.child(mId).child("User").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    mParamId = dataSnapshot.getValue(User.class);
                    Glide.with(ProfileActivity.this).load(mParamId.getPhoto()).into(mImageView);
                    mName.setText(mParamId.getName());
                    mBio.setText(mParamId.getBio());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        startFrag();

    }
    @Override
    protected void onResume(){
        super.onResume();
        setResult(RESULT_OK);
        if(mAuthStateListener != null){
            mFirebaseAuth.addAuthStateListener(mAuthStateListener);
            mAuthStateListener = null;
        }
    }
    @Override
    protected void onPause(){
        super.onPause();
        if (mAuthStateListener == null){
            mFirebaseAuth.removeAuthStateListener(mAuthStateListener);
        }
    }
    private void getCurrentUser(String id){
        mDatabaseReference.child(mainId).child("Friends")
                .orderByChild("mName")
                .startAt(mId)
                .endAt(mId+"\uf8ff")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()){
                            mAdd.setVisibility(View.INVISIBLE);
                            mAdd.setEnabled(false);
                        }else {
                            mAdd.setEnabled(true);
                            mAdd.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }
    private User getOwner(String id){
        final User[] user = new User[1];
        mDatabaseReference.child(id).child("User").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
               user[0] = dataSnapshot.getValue(User.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        return user[0];
    }
    void startFrag(){
        startEvents();
        mEvents.setEnabled(false);
        mFriends.setEnabled(true);
    }
    void startFriends(){
        Fragment fragment = mFragmentManager.findFragmentById(R.id.child_fragments1);
        if (fragment == null){
            fragment = MyFriendsFragment.newInstance(mId);
            mFragmentManager.beginTransaction()
                    .add(R.id.child_fragments1,fragment)
                    .commit();
        }
        if (!(fragment instanceof  MyFriendsFragment)){
            fragment = MyFriendsFragment.newInstance(mId);
            mFragmentManager.beginTransaction()
                    .replace(R.id.child_fragments1,fragment)
                    .commit();
        }
    }
    void startEvents(){
        Fragment fragment =  mFragmentManager.findFragmentById(R.id.child_fragments1);
        if (fragment == null){
            fragment = MyEventsFragment.newInstance(mId);
            mFragmentManager.beginTransaction()
                    .add(R.id.child_fragments,fragment)
                    .commit();
        }
        if (!(fragment instanceof MyEventsFragment)){
            fragment = MyEventsFragment.newInstance(mId);
            mFragmentManager.beginTransaction()
                    .replace(R.id.child_fragments,fragment)
                    .commit();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.events_button1:
                startFrag();
                break;
            case R.id.friends_button1:
                startFriends();
                mFriends.setEnabled(false);
                mEvents.setEnabled(true);
                break;
            case R.id.add_friend:
                mDatabaseReference.child(mainId).child("Friends").push().setValue(getOwner(mId));
                mDatabaseReference.child(mId).child("Friends").push().setValue(getOwner(mainId));
                Toast.makeText(ProfileActivity.this,"You have a new Friend!",Toast.LENGTH_SHORT).show();
                //snackbar
                break;
        }
    }
}
