package com.pointofsalesandroid.androidbasedpos_inventory.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.pointofsalesandroid.androidbasedpos_inventory.R;
import com.pointofsalesandroid.androidbasedpos_inventory.Utils;
import com.pointofsalesandroid.androidbasedpos_inventory.adapter.ChatUserListAdapter;
import com.pointofsalesandroid.androidbasedpos_inventory.adapter.MessageRecycleAdapter;
import com.pointofsalesandroid.androidbasedpos_inventory.mapModel.ChatMapmodel;
import com.pointofsalesandroid.androidbasedpos_inventory.mapModel.ChatUserListMapModel;
import com.pointofsalesandroid.androidbasedpos_inventory.models.ChatDataModel;
import com.pointofsalesandroid.androidbasedpos_inventory.models.ChatUserListModel;
import com.yarolegovich.slidingrootnav.SlidingRootNav;
import com.yarolegovich.slidingrootnav.SlidingRootNavBuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ChatActivity extends AppCompatActivity {
    DatabaseReference mDatabase;
    FirebaseAuth mAuth;
    RecyclerView UserChatListRecycle;
    ArrayList<ChatUserListModel> chatUserListModelArrayList = new ArrayList<>();
    ChatUserListAdapter chatUserListAdapter;
    private SlidingRootNav slidingRootNav;
    MessageRecycleAdapter messageRecycleAdapter;
    Toolbar chatToolBar;
    RecyclerView chatListRecycleView;
    ArrayList<ChatDataModel> messageDataModelsArrayList = new ArrayList<>();
    Button btnSubmit;
    EditText inputMsg;
    String ofTheUserClicked;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        chatToolBar = (Toolbar) findViewById(R.id.inventoryToolbar);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        chatListRecycleView = (RecyclerView) findViewById(R.id.chatListRecycleView);
        messageRecycleAdapter = new MessageRecycleAdapter(ChatActivity.this,messageDataModelsArrayList,mAuth.getUid());
        chatListRecycleView.setLayoutManager(new StaggeredGridLayoutManager(1,StaggeredGridLayoutManager.VERTICAL));
        chatListRecycleView.setAdapter(messageRecycleAdapter);
        btnSubmit = (Button) findViewById(R.id.btnSend);
        inputMsg = (EditText) findViewById(R.id.inputmsg);
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String key = mDatabase.push().getKey();

                ChatMapmodel messageMapmodel = new ChatMapmodel(key
                        ,inputMsg.getText().toString(),Utils.getDateToStrig(),mAuth.getCurrentUser().getDisplayName(),mAuth.getUid());
                Map<String,Object> chatMsgValue = messageMapmodel.toMap();
                Map<String,Object> childUpdates = new HashMap<>();
                childUpdates.put(key,chatMsgValue);
                mDatabase.child(Utils.chattingSystem).child(Utils.chatsList).child(mAuth.getUid()).child(ofTheUserClicked).updateChildren(childUpdates).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        inputMsg.setText("");
                    }
                });

            }
        });
        slidingRootNav = new SlidingRootNavBuilder(this)
                .withMenuOpened(false)
                .withContentClickableWhenMenuOpened(false)
                .withSavedState(savedInstanceState)
                .withMenuLayout(R.layout.chatlistslidingrootnav)
                .withDragDistance(250)
                .withRootViewScale(1f)
                .withToolbarMenuToggle(chatToolBar)
                .withContentClickableWhenMenuOpened(true)
                .inject();
        //get user chatList
        UserChatListRecycle = (RecyclerView) findViewById(R.id.chatUserListRecycle);
        chatUserListAdapter = new ChatUserListAdapter(ChatActivity.this,chatUserListModelArrayList);
        UserChatListRecycle.setLayoutManager(new StaggeredGridLayoutManager(1,StaggeredGridLayoutManager.VERTICAL));
        UserChatListRecycle.setAdapter(chatUserListAdapter);
        chatUserListAdapter.setOnItemClickListener(new ChatUserListAdapter.OnItemClickLitener() {
            @Override
            public void onItemClick(View view, int position) {
                chatGetConvo(chatUserListModelArrayList.get(position).getUserID());
            }
        });


        mDatabase.child(Utils.chattingSystem).child(Utils.chatUserList).child(mAuth.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                chatUserListModelArrayList.clear();
                for(DataSnapshot dataSnapshot1:dataSnapshot.getChildren()){
                    ChatUserListModel chatUserListModel = new ChatUserListModel();
                    ChatUserListMapModel chatUserListMapModel = dataSnapshot1.getValue(ChatUserListMapModel.class);
                    chatUserListModel.setDisplayName(chatUserListMapModel.displayName);
                    chatUserListModel.setUserID(chatUserListMapModel.userID);
                    chatUserListModel.setTimeStamp(chatUserListMapModel.timeStamp);
                    chatUserListModelArrayList.add(chatUserListModel);
                    chatUserListAdapter.notifyDataSetChanged();

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });








    }

    private void chatGetConvo(final String userId){
        messageDataModelsArrayList.clear();
        ofTheUserClicked = userId;
        mDatabase.child(Utils.chattingSystem).child(Utils.chatsList).child(mAuth.getUid()).child(userId).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {


            ChatDataModel chatDataModel = new ChatDataModel();
            ChatMapmodel chatMapmodel = dataSnapshot.getValue(ChatMapmodel.class);
            chatDataModel.setMessage(chatMapmodel.message);
            chatDataModel.setUserID(chatMapmodel.userID);
            messageDataModelsArrayList.add(chatDataModel);
            messageRecycleAdapter.notifyDataSetChanged();
            slidingRootNav.closeMenu();




            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
