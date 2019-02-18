package com.appbox.mep;

import android.content.Context;
import android.content.Intent;
import android.net.ProxyInfo;
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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;


public class MyEventsFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private String mParamId;
    private OnFragmentInteractionListener mListener;
    private DatabaseReference mDatabaseReference;
    private List<Event> mEvents;
    private RecyclerView mRecyclerView;
    private EventAdapter mEventAdapter;

    public MyEventsFragment() {
    }
    public static MyEventsFragment newInstance(String param1) {
        MyEventsFragment fragment = new MyEventsFragment();
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
        mEvents = new ArrayList<>();
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();
        mEventAdapter = new EventAdapter(mEvents, MyEventsFragment.this.getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_events, container, false);
        mRecyclerView = view.findViewById(R.id.events_recycler);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setAdapter(mEventAdapter);
        readDatabase();
        return view;
    }
    private void readDatabase(){
        mDatabaseReference.child(mParamId).child("MyEvents").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if (dataSnapshot.exists()){
                    Event event = dataSnapshot.getValue(Event.class);
                    mEvents.add(event);
                    mEventAdapter.notifyDataSetChanged();
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
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
    private class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventHolder>{
        private List<Event> mEvents;
        private LayoutInflater mInflater;

        public EventAdapter(List<Event> events, Context context){
            this.mEvents= events;
            mInflater=LayoutInflater.from(context);
        }
        class EventHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            private ImageView mImageView;
            private TextView mTitle;
            private TextView mDate;
            EventHolder(@NonNull View itemView) {
                super(itemView);
                mImageView = itemView.findViewById(R.id.user_image);
                mTitle = itemView.findViewById(R.id.user_name);
                mDate = itemView.findViewById(R.id.user_date);
            }
            void bind(Event event){
                mTitle.setText(event.getName());
                mDate.setText(event.getDate());
                Glide.with(MyEventsFragment.this).setDefaultRequestOptions(new RequestOptions().circleCrop()).load(event.getImage()).into(mImageView);
            }
            @Override
            public void onClick(View v) {
                int position = getLayoutPosition();
            }
        }

        @NonNull
        @Override
        public EventHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            return new EventAdapter.EventHolder(mInflater.inflate(R.layout.user_holder,viewGroup,false));
        }

        @Override
        public void onBindViewHolder(@NonNull EventHolder viewHolder, int i) {
            viewHolder.bind(mEvents.get(i));
        }

        @Override
        public int getItemCount() {
            return mEvents.size();
        }
    }
}
