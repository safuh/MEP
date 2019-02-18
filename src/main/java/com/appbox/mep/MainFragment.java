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

import com.google.android.gms.tasks.OnSuccessListener;
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
 * {@link MainFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MainFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MainFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    // TODO: Rename and change types of parameters
    private String mUserId;
    private DatabaseReference mDatabaseReference;
    private RecyclerView mRecyclerView;
    private List<Event> mEvents;
    private EventAdapter mEventAdapter;

    private OnFragmentInteractionListener mListener;

    public MainFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @return A new instance of fragment MainFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MainFragment newInstance(String param1) {
        MainFragment fragment = new MainFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mUserId = getArguments().getString(ARG_PARAM1);
        }
        mEvents = new ArrayList<>();
        mEventAdapter = new EventAdapter(mEvents,MainFragment.this.getActivity());
        mDatabaseReference= FirebaseDatabase.getInstance().getReference().child("Events");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        mRecyclerView = view.findViewById(R.id.main_recycler);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this.getActivity()));
        mRecyclerView.setAdapter(mEventAdapter);
        mDatabaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if (dataSnapshot.exists()){
                    Event event = dataSnapshot.getValue(Event.class);
                    event.setEventId(dataSnapshot.getKey());
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
        return view;
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

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
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
            private TextView mDetails;
            private TextView mDate;
            EventHolder(@NonNull View itemView) {
                super(itemView);
                mImageView = itemView.findViewById(R.id.event_image);
                mTitle = itemView.findViewById(R.id.event_name);
                mDetails = itemView.findViewById(R.id.event_details);
                mDate = itemView.findViewById(R.id.event_date);
            }
            void bind(Event event){
                //todo use glide
                mTitle.setText(event.getName());
                mDetails.setText(event.getDetail());
                mDate.setText(event.getDate());
            }
            @Override
            public void onClick(View v) {
                int position = getLayoutPosition();
                final Event event = mEvents.get(position);
                mDatabaseReference.child(mUserId).child("MyEvents").push().setValue(event).addOnSuccessListener(MainFragment.this.getActivity(), new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Intent intent = SocialActivity.newInstance(MainFragment.this.getActivity(),event.getEventId(),event, mUserId);
                        startActivity(intent);
                    }
                });
            }
        }

        @NonNull
        @Override
        public EventHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            return new EventHolder(mInflater.inflate(R.layout.event_holder,viewGroup,false));
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
