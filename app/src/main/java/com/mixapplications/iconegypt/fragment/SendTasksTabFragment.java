package com.mixapplications.iconegypt.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

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
import com.mixapplications.iconegypt.adapters.SentTaskAdapter;
import com.mixapplications.iconegypt.dialogs.CreateTaskCustomDialog;
import com.mixapplications.iconegypt.models.AppData;
import com.mixapplications.iconegypt.models.Tasks;

import java.util.ArrayList;


public class SendTasksTabFragment extends Fragment {


    public RecyclerView mRecyclerView;
    Context context;
    AppCompatActivity activity;
    FirebaseDatabase database;
    DatabaseReference ref;
    AlertDialog dialog;
    ImageButton fab;
    private RecyclerView.LayoutManager mLayoutManager;

    public SendTasksTabFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_send_tasks_tab, container, false);
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
                new CreateTaskCustomDialog(ref, activity, context, null).show();
            }
        });

        final ArrayList<Tasks> sendTasksArrayList = new ArrayList<>();
        final SentTaskAdapter[] sendTasksAdapter = new SentTaskAdapter[1];

        Query aFireQuery = ref.child("tasks").orderByChild("timestamp");
        aFireQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (final DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    final Tasks task = snapshot.getValue(Tasks.class);
                    if (task.getFromEmployee().getEmail().equalsIgnoreCase(AppData.currentUser.getEmail())) {
                        sendTasksArrayList.add(task);
                    }
                }
                sendTasksAdapter[0] = new SentTaskAdapter(context, ref, activity, sendTasksArrayList);
                mRecyclerView.setAdapter(sendTasksAdapter[0]);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        return layout;
    }

}
