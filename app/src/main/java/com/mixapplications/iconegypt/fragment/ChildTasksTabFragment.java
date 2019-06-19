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
import com.mixapplications.iconegypt.adapters.TasksAdapter;
import com.mixapplications.iconegypt.dialogs.ViewTasksCustomDialog;
import com.mixapplications.iconegypt.models.AppData;
import com.mixapplications.iconegypt.models.Prefs;
import com.mixapplications.iconegypt.models.Tasks;

import java.util.ArrayList;

public class ChildTasksTabFragment extends Fragment {

    public RecyclerView mRecyclerView;
    Context context;
    Activity activity;
    FirebaseDatabase database;
    DatabaseReference ref;
    private RecyclerView.LayoutManager mLayoutManager;

    public ChildTasksTabFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_child_tasks_tab, container, false);
        mRecyclerView = layout.findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(context);
        mRecyclerView.setLayoutManager(mLayoutManager);

        context = getContext();
        activity = getActivity();

        //setProgressDialog();
        database = FirebaseDatabase.getInstance();
        ref = database.getReference();

        final ArrayList<Tasks> childTasksArrayList = new ArrayList<>();
        final TasksAdapter[] childTaskAdapter = new TasksAdapter[1];

        Query aFireQuery = ref.child("tasks").orderByChild("timestamp");
        aFireQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (final DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    final Tasks task = snapshot.getValue(Tasks.class);
                    boolean isChild = AppData.myEmployeeNode.findChild(task.getToEmployee().getEmail()) != null &&
                            !AppData.myEmployeeNode.findChild(task.getToEmployee().getEmail()).getEmail().equalsIgnoreCase(AppData.currentUser.getEmail());
                    if (isChild && !task.getFromEmployee().getEmail().equalsIgnoreCase(AppData.currentUser.getEmail())) {
                        childTasksArrayList.add(task);
                    }
                }
                childTaskAdapter[0] = new TasksAdapter(context, activity, childTasksArrayList);
                childTaskAdapter[0].setOnItemClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        RecyclerView.ViewHolder viewHolder = (RecyclerView.ViewHolder) v.getTag();
                        int position = viewHolder.getAdapterPosition();
                        ViewTasksCustomDialog dialog = new ViewTasksCustomDialog(activity, childTasksArrayList.get(position));
                        try {
                            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                            dialog.show();
                        } catch (Exception e) {
                            dialog.show();
                        }

                    }
                });
                mRecyclerView.setAdapter(childTaskAdapter[0]);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        Prefs.initPrefs(context, "icon_egypt", Context.MODE_PRIVATE);
        Prefs.putBoolean(AppData.currentUser.getEmail() + "-tasks", false);
        return layout;
    }

    public RecyclerView getmRecyclerView() {
        return mRecyclerView;
    }
}
