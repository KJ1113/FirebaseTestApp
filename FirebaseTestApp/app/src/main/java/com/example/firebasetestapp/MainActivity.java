package com.example.firebasetestapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;
    private ChildEventListener mChildEventListener;
    private RecyclerAdapter recycleradapter;
    private EditText et_user_name;
    private EditText et_user_email;
    private Button btn_save;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init_mainActivity();
        init_Recycler();
        init_mDBChildEventListener();
    }
    private void init_mainActivity(){
        btn_save = findViewById(R.id.btn_save);
        et_user_name = findViewById(R.id.et_user_name);
        et_user_email = findViewById(R.id.et_user_email);

        btn_save.setOnClickListener(new btnSelectedListener());
        et_user_name.setOnKeyListener(new EditTextOnKeyListener());
        et_user_email.setOnKeyListener(new EditTextOnKeyListener());

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference().child("users");
    }
    private void init_mDBChildEventListener(){
        mChildEventListener = new ChildEventListener(){
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                recycleradapter.addItem(snapshot.getValue(User.class));
                recycleradapter.notifyDataSetChanged();
            }
            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                recycleradapter.addItem(snapshot.getValue(User.class));
                recycleradapter.notifyDataSetChanged();
            }
            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                recycleradapter.addItem(snapshot.getValue(User.class));
                recycleradapter.notifyDataSetChanged();
            }
            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        mDatabaseReference.addChildEventListener(mChildEventListener);
    }
    private void init_Recycler(){
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        recycleradapter = new RecyclerAdapter();
        recyclerView.setAdapter(recycleradapter);
    }
    // 엔터키 처리
    private class EditTextOnKeyListener implements View.OnKeyListener{
        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            if (v.getId() == R.id.et_user_name && keyCode == KeyEvent.KEYCODE_ENTER){
                et_user_email.requestFocus();
                return true;
            }
            if(v.getId() == R.id.et_user_email && keyCode == KeyEvent.KEYCODE_ENTER ){
                if(et_user_email.getText().toString().isEmpty()){
                    return true;
                }else{
                    btn_save.callOnClick();
                    return true;
                }
            }
            return false;
        }
    }
    private class btnSelectedListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            String getUserName = et_user_name.getText().toString();
            String getUserEmail = et_user_email.getText().toString();
            //hashmap 만들기
            if(!getUserName.isEmpty() && !getUserEmail.isEmpty() ){
                HashMap result = new HashMap<>();
                result.put("name", getUserName);
                result.put("email", getUserEmail);
                writeNewUser(getUserName,getUserEmail);
                Toast.makeText(getBaseContext(),"입력값 저장",Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(getBaseContext(),"입력값이 없습니다.",Toast.LENGTH_SHORT).show();
            }
        }
    }
    private void writeNewUser( String name, String email) {
        User user = new User(name, email);
        mDatabaseReference.push().setValue(user);
        et_user_name.setText("");
        et_user_email.setText("");
    }
}