package com.appbox.mep;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth mFirebaseAuth;
    private User mUser;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;
    private static final int REC_SIGN_IN = 1;
    private FragmentManager fm;
    private BottomNavigationView mBottomNavigationView;
    private final int REC_EDIT=9;
    private BottomNavigationView.OnNavigationItemSelectedListener mListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
            FragmentManager fm = getSupportFragmentManager();
            switch (menuItem.getItemId()){
                case R.id.home_button:
                    startFragment();
                    return true;
                case R.id.add_button:
                    startEvent();
                    return true;
                case R.id.profile_button:
                    startProfile();
                    return true;
                case R.id.search_button:
                    startSeach();
                    return true;
            }
            return false;
        }
    };
    private BottomNavigationView.OnNavigationItemReselectedListener mReselectedListener = new BottomNavigationView.OnNavigationItemReselectedListener() {
        @Override
        public void onNavigationItemReselected(@NonNull MenuItem menuItem) {
            switch (menuItem.getItemId()){
                case R.id.home_button:
                    startFragment();
                    break;
                case R.id.search_button:
                    startSeach();
                    break;
                case R.id.profile_button:
                    startProfile();
                    break;
                case R.id.add_button:
                    startEvent();
                    break;
            }

        }
    };
    private void startSeach(){
        Fragment fragment = fm.findFragmentById(R.id.container);
        if (fragment == null){
            fragment = SearchFragment.newInstance(mUser.getId());
            fm.beginTransaction()
                    .add(R.id.container,fragment)
                    .commit();
        }
        if (!(fragment instanceof SearchFragment)){
            fragment = SearchFragment.newInstance(mUser.getId());
            fm.beginTransaction()
                    .replace(R.id.container, fragment)
                    .commit();
        }
    }
    private void startProfile(){
        Fragment fragment = fm.findFragmentById(R.id.container);
        if (fragment == null){
            fragment = ProfileFragment.newInstance(mUser);
            fm.beginTransaction()
                    .add(R.id.container,fragment)
                    .commit();
        }
        if (!(fragment instanceof ProfileFragment)){
            fragment = ProfileFragment.newInstance(mUser);
            fm.beginTransaction()
                    .replace(R.id.container,fragment)
                    .commit();
        }
    }
    private void startEvent(){
        Fragment fragment = fm.findFragmentById(R.id.container);
        if (fragment == null){
            fragment = EventFragment.newInstance(mUser.getId());
            fm.beginTransaction()
                    .add(R.id.container,fragment)
                    .commit();
        }
        if (!(fragment instanceof  EventFragment)){
            fragment = EventFragment.newInstance(mUser.getId());
            fm.beginTransaction()
                    .replace(R.id.container,fragment)
                    .commit();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        fm = getSupportFragmentManager();
        mBottomNavigationView = findViewById(R.id.navigation);
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference();
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if(user!=null){
                    mUser = new User(user.getDisplayName(),user.getPhotoUrl().toString(),user.getEmail(),user.getUid());
                    writeDatabase(mUser);
                    startFragment();
                    startService(user.getUid());
                }else{
                    startActivityForResult(AuthUI.getInstance().createSignInIntentBuilder().build(), REC_SIGN_IN);

                }
            }
        };
    }
    private void startService(String id){
        boolean start = !MyIntentService.isAlarmOn(MainActivity.this,id);
        MyIntentService.setAlarm(MainActivity.this,id,start);
    }
    private void writeDatabase(User user) {
        mDatabaseReference.child(user.getId()).child("User").setValue(user);
        mDatabaseReference.child("users").child(user.getId()).setValue(user);
    }

    private void startFragment(){
        Fragment fragment = fm.findFragmentById(R.id.container);
        if (fragment == null){
            fragment = MainFragment.newInstance(mUser.getId());
            fm.beginTransaction()
                    .add(R.id.container,fragment)
                    .commit();
        }
        if (!(fragment instanceof  MainFragment)){
            fragment = MainFragment.newInstance(mUser.getId());
            fm.beginTransaction()
                    .replace(R.id.container,fragment)
                    .commit();
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode,resultCode,data);
        if(requestCode == REC_SIGN_IN ){
            if (resultCode == RESULT_OK){
                Toast.makeText(MainActivity.this,getString(com.appbox.mep.R.string.welcome),Toast.LENGTH_SHORT).show();
                Intent intent = EditProfileActivity.newInstance(MainActivity.this, mUser.getId());
                startActivityForResult(intent,REC_EDIT);
            }else{
                Toast.makeText(MainActivity.this, getString(com.appbox.mep.R.string.canceled), Toast.LENGTH_SHORT).show();
            }
        }
        if (requestCode ==REC_EDIT){
            startFragment();
        }
    }
    @Override
    protected void onPause(){
        super.onPause();
        if(mAuthStateListener== null){
            mFirebaseAuth.removeAuthStateListener(mAuthStateListener);
        }
    }
    @Override
    protected void onResume(){
        super.onResume();
        mBottomNavigationView.setOnNavigationItemSelectedListener(mListener);
        mBottomNavigationView.setOnNavigationItemReselectedListener(mReselectedListener);
        if (mAuthStateListener!= null){
            mFirebaseAuth.addAuthStateListener(mAuthStateListener);
            mAuthStateListener = null;
        }
    }
}
