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
import com.mixapplications.iconegypt.adapters.SentNewsAdapter;
import com.mixapplications.iconegypt.dialogs.CreateNewsCustomDialog;
import com.mixapplications.iconegypt.models.AppData;
import com.mixapplications.iconegypt.models.News;

import java.util.ArrayList;

public class MyNewsTabFragment extends Fragment {

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

    public MyNewsTabFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_my_news_tab, container, false);
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
                new CreateNewsCustomDialog(ref, activity, context, AppData.employee).show();
            }
        });

        Query aFireQuery = ref.child("news").orderByChild("timestamp");
        aFireQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    News news = snapshot.getValue(News.class);
                    if (news.getFromEmail().equalsIgnoreCase(AppData.employee.getEmail())) {
                        newsArrayList.add(snapshot.getValue(News.class));

                        adapter = new SentNewsAdapter(context, ref, activity, newsArrayList);
                        mRecyclerView.setAdapter(adapter);
                        if (dialog != null)
                            dialog.dismiss();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                if (dialog != null)
                    dialog.dismiss();
            }
        });

        return layout;
    }
}
