package com.mixapplications.iconegypt.fragment;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
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
import com.mixapplications.iconegypt.adapters.NewsAdapter;
import com.mixapplications.iconegypt.dialogs.ViewNewsCustomDialog;
import com.mixapplications.iconegypt.models.AppData;
import com.mixapplications.iconegypt.models.Employee;
import com.mixapplications.iconegypt.models.News;
import com.mixapplications.iconegypt.models.Prefs;

import java.util.ArrayList;

import static com.mixapplications.iconegypt.models.AppData.currentUser;

public class NewsTabFragment extends Fragment {

    public RecyclerView mRecyclerView;
    Context context;
    Activity activity;
    FirebaseDatabase database;
    DatabaseReference ref;
    ArrayList<News> newsArrayList = new ArrayList<>();
    ArrayList<Employee> employeeArrayList = new ArrayList<>();
    NewsAdapter adapter;
    AlertDialog dialog;
    private RecyclerView.LayoutManager mLayoutManager;

    public NewsTabFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_news_tab, container, false);
        mRecyclerView = layout.findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(context);
        mRecyclerView.setLayoutManager(mLayoutManager);

        context = getContext();
        activity = getActivity();

        //setProgressDialog();
        database = FirebaseDatabase.getInstance();
        ref = database.getReference();

        final News[] lastnews = {null};
        Query aFireQuery = ref.child("news").orderByChild("timestamp");
        aFireQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (final DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    if (lastnews[0] == null || (snapshot.getValue(News.class) != null && lastnews[0].getTimestamp() > snapshot.getValue(News.class).getTimestamp()))
                        lastnews[0] = snapshot.getValue(News.class);

                    final News news = snapshot.getValue(News.class);
                    newsArrayList.add(news);
                    Query fireQuery = ref.child("employees").orderByChild("email").equalTo(news.getFromEmail());
                    fireQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (DataSnapshot snapshot1 : dataSnapshot.getChildren()) {
                                Employee em = snapshot1.getValue(Employee.class);
                                employeeArrayList.add(em);
                                adapter = new NewsAdapter(context, activity, newsArrayList, employeeArrayList);
                                adapter.setOnItemClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        RecyclerView.ViewHolder viewHolder = (RecyclerView.ViewHolder) v.getTag();
                                        int position = viewHolder.getAdapterPosition();
                                        ViewNewsCustomDialog dialog = new ViewNewsCustomDialog(activity, newsArrayList.get(position), employeeArrayList.get(position));
                                        try {
                                            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                            dialog.show();
                                        } catch (Exception e) {
                                            dialog.show();
                                        }

                                    }
                                });
                            }
                            mRecyclerView.setAdapter(adapter);
                            if (dialog != null)
                                dialog.dismiss();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            if (dialog != null)
                                dialog.dismiss();
                        }
                    });
                }

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        if (lastnews[0] != null) {
                            Query fireQuery = ref.child("employees").orderByChild("email").equalTo(currentUser.getEmail());
                            fireQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    for (DataSnapshot snapshot1 : dataSnapshot.getChildren()) {
                                        MainFragment.setRedDotVisibility(context, 0, false);
                                        snapshot1.getRef().child("lastNews").setValue(lastnews[0].getTimestamp());
                                        break;
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                        }
                    }
                }).start();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        Prefs.initPrefs(context, "icon_egypt", Context.MODE_PRIVATE);
        Prefs.putBoolean(AppData.currentUser.getEmail() + "-news", false);
        return layout;
    }
}
