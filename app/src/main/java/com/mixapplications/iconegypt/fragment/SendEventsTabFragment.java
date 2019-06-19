package com.mixapplications.iconegypt.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.mixapplications.iconegypt.R;
import com.mixapplications.iconegypt.adapters.SentEventsAdapter;
import com.mixapplications.iconegypt.adapters.SentNewsAdapter;
import com.mixapplications.iconegypt.dialogs.CreateEventCustomDialog;
import com.mixapplications.iconegypt.models.AppData;
import com.mixapplications.iconegypt.models.Events;
import com.mixapplications.iconegypt.models.News;

import java.util.ArrayList;

public class SendEventsTabFragment extends Fragment {

    public RecyclerView mRecyclerView;
    Context context;
    AppCompatActivity activity;
    FirebaseDatabase database;
    DatabaseReference ref;
    ArrayList<News> newsArrayList = new ArrayList<>();
    SentNewsAdapter adapter;
    AlertDialog dialog;
    ImageButton fab;
    private RecyclerView.LayoutManager mLayoutManager;

    public SendEventsTabFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_send_events_tab, container, false);
        mRecyclerView = layout.findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(context);
        mRecyclerView.setLayoutManager(mLayoutManager);
        fab = layout.findViewById(R.id.fab_add);
        context = getContext();
        activity = (AppCompatActivity) getActivity();

        //setProgressDialog();
        database = FirebaseDatabase.getInstance();
        ref = database.getReference();

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (AppData.myEmployeeNode.getChildren().size() > 0)
                    new CreateEventCustomDialog(ref, activity, context, null).show();
                else
                    Toast.makeText(context, "Sorry you can not create Event", Toast.LENGTH_LONG).show();
            }
        });

        final ArrayList<Events> sendEventsArrayList = new ArrayList<>();
        final SentEventsAdapter[] sendEventsAdapter = new SentEventsAdapter[1];

        Query aFireQuery = ref.child("events").orderByChild("timestamp");
        aFireQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (final DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    final Events event = snapshot.getValue(Events.class);
                    if (event.getFromEmployee().getEmail().equalsIgnoreCase(AppData.currentUser.getEmail())) {
                        sendEventsArrayList.add(event);
                    }
                }
                sendEventsAdapter[0] = new SentEventsAdapter(context, ref, activity, sendEventsArrayList);
                mRecyclerView.setAdapter(sendEventsAdapter[0]);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        return layout;
    }

}