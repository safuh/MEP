package com.appbox.mep;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.Layout;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SearchFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SearchFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SearchFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private String mParam1;
    private DatabaseReference mFirebaseDatabase;
    private RecyclerView mRecyclerView;
    private FriendAdapter mFriendAdapter;
    private List<User> mUsers;
    private OnFragmentInteractionListener mListener;
    private  ValueEventListener eventListener;
    public SearchFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @return A new instance of fragment SearchFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SearchFragment newInstance(String param1) {
        SearchFragment fragment = new SearchFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
        }
        mUsers = new ArrayList<>();
        setHasOptionsMenu(true);
        mFirebaseDatabase = FirebaseDatabase.getInstance().getReference();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_seach, container, false);
        mRecyclerView = view.findViewById(R.id.user_recycler);
        mFriendAdapter = new FriendAdapter(mUsers,SearchFragment.this.getActivity());
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setAdapter(mFriendAdapter);
        return view;
    }
    private void readDatabase(String searchText){
        mFirebaseDatabase.child("users").orderByChild("mName").startAt(searchText).endAt(searchText +"\uf8ff").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    for (DataSnapshot data:dataSnapshot.getChildren()){
                        if (!data.getKey().equals(mParam1)){
                            mUsers.add(data.getValue(User.class));
                            mFriendAdapter.notifyDataSetChanged();
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    // TODO: Rename method, update argument and hook method into UI event
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
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater){
        super.onCreateOptionsMenu(menu, menuInflater);
        menuInflater.inflate(R.menu.search_menu,menu);
        MenuItem searchItem = menu.findItem(R.id.item_search);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                QueryPreferences.setPreferences(s,SearchFragment.this.getContext());
                readDatabase(QueryPreferences.getPreferences(SearchFragment.this.getContext()));
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                readDatabase(s);
                return false;
            }
        });
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.item_clear:
                QueryPreferences.setPreferences(null, SearchFragment.this.getActivity());
                return true;
            default:
                return super.onOptionsItemSelected(item);
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
    class FriendAdapter extends RecyclerView.Adapter<FriendAdapter.FriendHolder>{
        private List<User> mUsers;
        private LayoutInflater mInflater;
        FriendAdapter(List<User> users, Context context){
            this.mInflater = LayoutInflater.from(context);
            this.mUsers= users;
        }
        class FriendHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
            private ImageView mImage;
            private TextView mName;
            private TextView mBio;
            private Button mAdd;
            FriendHolder(@NonNull View itemView){
                super(itemView);
                mAdd = itemView.findViewById(R.id.friend_add);
                mBio = itemView.findViewById(R.id.friend_bio);
                mName = itemView.findViewById(R.id.friend_name);
                mImage = itemView.findViewById(R.id.friend_image);
                mAdd.setOnClickListener(this);
                eventListener =new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()){
                            mAdd.setEnabled(false);
                            mAdd.setText(getString(R.string.palss));
                        }else{
                            mAdd.setVisibility(View.VISIBLE);
                            mAdd.setEnabled(true);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                };
            }
            void bind(User user){
                Glide.with(SearchFragment.this).load(user.getPhoto()).into(mImage);
                mBio.setText(user.getBio());
                mName.setText(user.getName());
                mFirebaseDatabase.child(mParam1).child("Pending Requests").orderByChild("mName")
                        .startAt(user.getName())
                        .endAt(user.getName()+"\uf8ff")
                        .addValueEventListener(eventListener);
                mFirebaseDatabase.child(mParam1).child("Friends").orderByChild("mName")
                        .startAt(user.getName())
                        .endAt(user.getName()+"\uf8ff")
                        .addValueEventListener(eventListener);
            }
            @Override
            public void onClick(View v) {
                int pos = getLayoutPosition();
                User user = mUsers.get(pos);
                mFirebaseDatabase.child(user.getId()).child("Pending Requests").push().setValue(user);
                Toast.makeText(SearchFragment.this.getActivity(), "friend request sent", Toast.LENGTH_SHORT).show();
            }
        }
        @NonNull
        @Override
        public FriendHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            return new FriendHolder(mInflater.inflate(R.layout.friend_holder,viewGroup,false));
        }

        @Override
        public void onBindViewHolder(@NonNull FriendHolder viewHolder, int i) {
            viewHolder.bind(mUsers.get(i));
        }

        @Override
        public int getItemCount() {
            return mUsers.size();
        }
    }
}
