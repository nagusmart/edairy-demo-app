package com.example.edairycodinground;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;
import android.view.View;

import com.example.edairycodinground.adapter.SpeedListAdapter;
import com.example.edairycodinground.databinding.ActivityScreenTwoBinding;
import com.example.edairycodinground.model.SpeedModel;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import org.w3c.dom.Comment;

import java.util.ArrayList;

public class ScreenTwoActivity extends AppCompatActivity {

    ActivityScreenTwoBinding binding;
    SpeedListAdapter adapter;
    ArrayList<SpeedModel> speedModelArrayList = new ArrayList<>();

    DatabaseReference databaseReference;

    Query searchAllQuery;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityScreenTwoBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference();


        setContentView(view);
        setSpeedListAdapter();

        searchAllQuery = databaseReference.child("SPEED_LIST");
        addDataListener();


        binding.goBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              onBackPressed();
            }
        });

        binding.searchImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                speedModelArrayList.clear();
                adapter.notifyDataSetChanged();
                searchAllQuery = databaseReference.child("SPEED_LIST").orderByChild("phoneNo").equalTo(binding.searchNo.getText().toString().trim());
                addDataListener();
            }
        });

    }

    void setSpeedListAdapter() {
        adapter = new SpeedListAdapter(speedModelArrayList, ScreenTwoActivity.this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(ScreenTwoActivity.this);
        binding.speedListRecycle.setLayoutManager(linearLayoutManager);
        binding.speedListRecycle.setAdapter(adapter);
    }

    void addDataListener() {
        ChildEventListener childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                fetchData(dataSnapshot);
            }
            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {
                fetchData(dataSnapshot);
            }
            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }
            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String previousChildName) {

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        searchAllQuery.addChildEventListener(childEventListener);
    }

    private void fetchData(DataSnapshot dataSnapshot) {



        String phone=dataSnapshot.child("phoneNo").getValue().toString();
        String downSpeed=dataSnapshot.child("downSpeed").getValue().toString();
        String upSpeed=dataSnapshot.child("upSpeed").getValue().toString();
        String timeStamp=dataSnapshot.child("timeStamp").getValue().toString();
        String totalSpeed=dataSnapshot.child("totalSpeed").getValue().toString();

        SpeedModel speedModel=new SpeedModel(phone,totalSpeed,upSpeed,downSpeed,timeStamp);

        speedModelArrayList.add(speedModel);

        if(speedModelArrayList.size()>0){
            binding.emptyResult.setVisibility(View.GONE);
        }else{
            binding.emptyResult.setVisibility(View.VISIBLE);
        }

        adapter.notifyDataSetChanged();
    }
}