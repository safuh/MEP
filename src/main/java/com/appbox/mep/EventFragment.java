package com.appbox.mep;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.android.gms.dynamic.IFragmentWrapper;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.security.PublicKey;

public class EventFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final int REC_IMAGE = 6;
    private String mUserId;
    private ImageView mImageView;
    private EditText mName;
    private EditText mDetail;
    private EditText mDate;
    private EditText mTime;
    private EditText mLocation;
    private String mNamed;
    private String mDetailed;
    private String  mDated;
    private int mTimed;
    private DatabaseReference mDatabaseReference;
    private String mLocated;
    private Uri mImage;
    private OnFragmentInteractionListener mListener;
    private Button mSave;

    public EventFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1
     * @return A new instance of fragment EventFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static EventFragment newInstance(String param1) {
        EventFragment fragment = new EventFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1) ;
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mUserId = getArguments().getString(ARG_PARAM1);
        }
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_event, container, false);
        mName = view.findViewById(R.id.name);
        mDate = view.findViewById(R.id.date);
        mDetail = view.findViewById(R.id.detail);
        mTime = view.findViewById(R.id.time);
        mLocation = view.findViewById(R.id.location);
        mImageView = view.findViewById(R.id.new_image);
        mSave = view.findViewById(R.id.saver);
        mSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mNamed= mName.getText().toString();
                mDated = mDate.getText().toString();
                mDetailed = mDetail.getText().toString();
                mTimed= Integer.parseInt(mTime.getText().toString());
                mLocated = mLocation.getText().toString();
                Event event = new Event(mNamed,mDetailed,mDated,mTimed,mUserId,mImage,mLocated);
                mDatabaseReference.child("Events").setValue(event);
            }
        });
        mTime.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (String.valueOf(Integer.parseInt(s.toString())).trim().length() == 2){
                    mTime.append(":");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        mDate.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().trim().length()==4){
                    mDate.append("/");
                }
                if (s.toString().trim().length()==7){
                    mDate.append("/");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        mImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(Intent.ACTION_GET_CONTENT)
                        .setType("image/*"), REC_IMAGE);
            }
        });
        return view;
    }
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode,resultCode,data);
        if (requestCode == REC_IMAGE && data != null){
            mImage = data.getData();
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
}
