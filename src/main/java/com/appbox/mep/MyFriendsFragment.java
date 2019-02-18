package com.appbox.mep;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MyFriendsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MyFriendsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MyFriendsFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private String mParamId;
    private DatabaseReference mDatabaseReference;
    private List<User> mUsers;
    private RecyclerView mRecyclerView;
    private UserAdapter mUserAdapter;

    private OnFragmentInteractionListener mListener;

    public MyFriendsFragment() {

    }


    public static MyFriendsFragment newInstance(String param1) {
        MyFriendsFragment fragment = new MyFriendsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParamId = getArguments().getString(ARG_PARAM1);
        }
        mUsers= new ArrayList<>();
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();
        mUserAdapter = new UserAdapter(mUsers,MyFriendsFragment.this.getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =inflater.inflate(R.layout.fragment_my_friends, container, false);
        mRecyclerView = view.findViewById(R.id.friends_recycler);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setAdapter(mUserAdapter);
        readData();
        return view;
    }
    private void readData(){
        mDatabaseReference.child(mParamId).child("Friends").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if (dataSnapshot.exists()){
                    User user = dataSnapshot.getValue(User.class);
                    mUsers.add(user);
                    mUserAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
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

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }private class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserHolder>{
        private List<User> mUser;
        private LayoutInflater mInflater;

        public UserAdapter(List<User> events, Context context){
            this.mUser = events;
            mInflater=LayoutInflater.from(context);
        }
        class UserHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            private ImageView mImageView;
            private TextView mTitle;
            private TextView mDate;
            UserHolder(@NonNull View itemView) {
                super(itemView);
                mImageView = itemView.findViewById(R.id.user_image);
                mTitle = itemView.findViewById(R.id.user_name);
                mDate = itemView.findViewById(R.id.user_date);
            }
            void bind(User user){
                mTitle.setText(user.getName());
                mDate.setText(user.getBio());
                Glide.with(MyFriendsFragment.this).setDefaultRequestOptions(new RequestOptions().circleCrop()).load(user.getPhoto()).into(mImageView);
            }
            @Override
            public void onClick(View v) {
                /*int pos = getLayoutPosition();
                User chat = mUsers.get(pos);
                Intent intent = ProfileActivity.newInstance(MyFriendsFragment.this.getActivity(),chat.getId());
                startActivity(intent);*/
            }
        }

        @NonNull
        @Override
        public UserHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            return new UserHolder(mInflater.inflate(R.layout.user_holder,viewGroup,false));
        }

        @Override
        public void onBindViewHolder(@NonNull UserHolder viewHolder, int i) {
            viewHolder.bind(mUser.get(i));
        }

        @Override
        public int getItemCount() {
            return mUser.size();
        }
    }
}
