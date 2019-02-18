package com.appbox.mep;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.Serializable;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ProfileFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment implements View.OnClickListener {
    private static final String ARG_PARAM1 = "param1";
    private User mParamUser;
    private ImageView mImageView;
    private TextView mName;
    private TextView mBio;
    private Button mFriends;
    private Button mEvents;
    private DatabaseReference mDatabaseReference;
    private FragmentManager mFragmentManager;

    private OnFragmentInteractionListener mListener;

    public ProfileFragment() {
        // Required empty public constructor
    }

    public static ProfileFragment newInstance(User param1) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PARAM1, (Serializable) param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParamUser = (User) getArguments().getSerializable(ARG_PARAM1);
        }
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();
        mFragmentManager = getChildFragmentManager();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        mImageView = view.findViewById(R.id.profile_url1);
        mName= view.findViewById(R.id.profile_name1);
        mBio = view.findViewById(R.id.bio);
        Glide.with(ProfileFragment.this).load(mParamUser.getPhoto()).into(mImageView);
        mName.setText(mParamUser.getName());
        mBio.setText(mParamUser.getBio());
        mFriends = view.findViewById(R.id.friends_button1);
        mEvents = view.findViewById(R.id.events_button1);
        mEvents.setOnClickListener(this);
        mFriends.setOnClickListener(this);
        startFrag();
        return view;
    }
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
    void startFriends(){
        Fragment fragment = mFragmentManager.findFragmentById(R.id.child_fragments1);
        if (fragment == null){
            fragment = MyFriendsFragment.newInstance(mParamUser.getId());
            mFragmentManager.beginTransaction()
                    .add(R.id.child_fragments1,fragment)
                    .commit();
        }
        if (!(fragment instanceof  MyFriendsFragment)){
            fragment = MyFriendsFragment.newInstance(mParamUser.getId());
            mFragmentManager.beginTransaction()
                    .replace(R.id.child_fragments1,fragment)
                    .commit();
        }
    }
    void startEvents(){
        Fragment fragment =  mFragmentManager.findFragmentById(R.id.child_fragments1);
        if (fragment == null){
            fragment = MyEventsFragment.newInstance(mParamUser.getId());
            mFragmentManager.beginTransaction()
                    .add(R.id.child_fragments1,fragment)
                    .commit();
        }
        if (!(fragment instanceof MyEventsFragment)){
            fragment = MyEventsFragment.newInstance(mParamUser.getId());
            mFragmentManager.beginTransaction()
                    .replace(R.id.child_fragments1,fragment)
                    .commit();
        }
    }
    void startFrag(){
        startEvents();
        mEvents.setEnabled(false);
        mFriends.setEnabled(true);
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
        }
    }
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
