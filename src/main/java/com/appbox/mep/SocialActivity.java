package com.appbox.mep;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.CalendarContract;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.dynamic.IFragmentWrapper;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class SocialActivity extends AppCompatActivity implements View.OnClickListener{

    private static final String EVENT_ID_CONSTANT = "1";
    private static final String EVENT_CONSTANT = "2";
    private static final String USER_CONSTANT = "3";
    private static final int REC_VIEW_PROF = 11;
    private static final int REC_PIC_RES = 12;
    private List<Chat> mChats;
    private DatabaseReference mDatabaseReference;
    private String mEventId;
    private Event mEvent;
    private String mUid;
    private User mUser;
    private ValueEventListener mEventListener;
    private EditText mEdit;
    private ChatAdapter chatAdapter;

    public static Intent newInstance(Context context, String id, Event event, String uid){
        return new Intent(context, SocialActivity.class)
                .putExtra(EVENT_ID_CONSTANT,id)
                .putExtra(EVENT_CONSTANT, event)
                .putExtra(USER_CONSTANT,uid);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_social);
        mEvent = (Event) getIntent().getSerializableExtra(EVENT_CONSTANT);
        mEventId = getIntent().getStringExtra(EVENT_ID_CONSTANT);
        mUid = getIntent().getStringExtra(USER_CONSTANT);
        mChats = new ArrayList<>();
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = firebaseDatabase.getReference();
        chatAdapter = new ChatAdapter(mChats, SocialActivity.this);
        RecyclerView recyclerView = findViewById(R.id.chats_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(SocialActivity.this));
        recyclerView.setAdapter(chatAdapter);
        Button photo= findViewById(R.id.image_button);
        photo.setOnClickListener(this);
        Button saver = findViewById(R.id.send_button);
        mEdit =findViewById(R.id.edit_chat);
        saver.setOnClickListener(this);
        readDatabase();
        Button cal_button = findViewById(R.id.cal_id);
        cal_button.setOnClickListener(this);
    }
    private void ad_cal(){
        String number = mEvent.getDate();
        Date date = new Date();
        Calendar.getInstance().set(Integer.parseInt(number.substring(0, 4)),
                Integer.parseInt(number.substring(5, 7))
                , Integer.parseInt(number.substring(8)));
        Intent intent = new Intent(Intent.ACTION_INSERT)
                .setData(CalendarContract.Events.CONTENT_URI)
                /*.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, startMillis)
                .putExtra(CalendarContract.EXTRA_EVENT_END_TIME, endMillis)*/
                .putExtra(CalendarContract.Events.TITLE, mEvent.getName())
                .putExtra(CalendarContract.Events.DESCRIPTION, mEvent.getDate())
                .putExtra(CalendarContract.Events.EVENT_LOCATION, mEvent.getLocation())
                .putExtra(CalendarContract.Events.AVAILABILITY, CalendarContract.Events.AVAILABILITY_BUSY)
                .putExtra(Intent.EXTRA_EMAIL, mUser.getEmail());
        startActivity(intent);
    }
    private void newPic(Uri uri){
        Chat chat = new Chat();
        chat.setPhoto(uri);
        chat.setEventId(mEventId);
        chat.setUserId(mUser.getId());
        mDatabaseReference.child("chats").child(mUser.getId()).push().setValue(chat);
        Toast.makeText(SocialActivity.this,"File uploaded successfully",Toast.LENGTH_SHORT).show();
    }
    private void writeDatabase(){
        Chat chat = new Chat();
        chat.setEventId(mEventId);
        chat.setUserId(mUser.getId());
        chat.setName(mEdit.getText().toString());
        mDatabaseReference.child("chats").child(mUid).setValue(chat);
    }
    private void readDatabase() {
        mDatabaseReference.child(mUid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    mUser = dataSnapshot.getValue(User.class);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        mEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    for (DataSnapshot data : dataSnapshot.getChildren()){
                        Chat chat = data.getValue(Chat.class);
                        mChats.add(chat);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        ValueEventListener listener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot data : dataSnapshot.getChildren()) {
                        String data1 = data.getValue(String.class);
                        mDatabaseReference.child("chats")
                                .child(data1)
                                .orderByChild(mEventId)
                                .addValueEventListener(mEventListener);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        mDatabaseReference.child("groups").child(mUser.getId()).orderByValue().addValueEventListener(listener);
        mDatabaseReference.child("chats").child(mUser.getId()).orderByChild(mEventId).addValueEventListener(mEventListener);
        /*todo
        read chats from database to array list
        data from friends and user account
         */
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode,resultCode,data);
        if (requestCode == REC_VIEW_PROF){
            readDatabase();
        }
        if (requestCode ==REC_PIC_RES){
            newPic(data.getData());
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.cal_id:
                ad_cal();
                break;
            case R.id.image_button:
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent,REC_PIC_RES);
                break;
            case R.id.send_button:
                writeDatabase();
                break;
        }
    }

    private class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatHolder>{
        private List<Chat> mChats;
        private LayoutInflater mInflater;

        public ChatAdapter(List<Chat> chats, Context context){
            this.mChats = chats;
            this.mInflater = LayoutInflater.from(context);
        }
        class ChatHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
            private ImageView mImageView;
            private TextView mName;
            private TextView mChat;
            ChatHolder(@NonNull View itemView) {
                super(itemView);
                mImageView= itemView.findViewById(R.id.chat_image);
                mName = itemView.findViewById(R.id.chat_name);
                mChat = itemView.findViewById(R.id.chat_text);
            }
            private void bind(Chat chat){
                mName.setText(chat.getName());
                if (chat.getPhoto()!= null){
                    mImageView.setVisibility(View.VISIBLE);
                    Glide.with(SocialActivity.this).load(chat.getPhoto()).into(mImageView);
                }else {
                    mChat.setText(chat.getText());
                }
            }

            @Override
            public void onClick(View v) {
                int pos = getLayoutPosition();
                Chat chat = mChats.get(pos);
                Intent intent = ProfileActivity.newInstance(SocialActivity.this,chat.getUserId());
                startActivityForResult(intent,REC_VIEW_PROF);
            }
        }
        @NonNull
        @Override
        public ChatHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            return new ChatHolder(mInflater.inflate(R.layout.chat_holder,viewGroup,false));
        }

        @Override
        public void onBindViewHolder(@NonNull ChatHolder viewHolder, int i) {
            viewHolder.bind(mChats.get(i));
        }

        @Override
        public int getItemCount() {
            return mChats.size();
        }
    }

}
