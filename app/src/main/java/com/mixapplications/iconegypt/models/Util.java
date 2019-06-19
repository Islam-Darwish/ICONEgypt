package com.mixapplications.iconegypt.models;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.mixapplications.iconegypt.R;
import com.mixapplications.iconegypt.adapters.EventsAdapter;
import com.mixapplications.iconegypt.adapters.NewsAdapter;
import com.mixapplications.iconegypt.adapters.SentEventsAdapter;
import com.mixapplications.iconegypt.adapters.SentNewsAdapter;
import com.mixapplications.iconegypt.adapters.SentTaskAdapter;
import com.mixapplications.iconegypt.adapters.TasksAdapter;
import com.mixapplications.iconegypt.dialogs.EditNewsCustomDialog;
import com.mixapplications.iconegypt.dialogs.ViewEventCustomDialog;
import com.mixapplications.iconegypt.dialogs.ViewNewsCustomDialog;
import com.mixapplications.iconegypt.dialogs.ViewTasksCustomDialog;
import com.mixapplications.iconegypt.fragment.ChildTasksTabFragment;
import com.mixapplications.iconegypt.fragment.EventsFragment;
import com.mixapplications.iconegypt.fragment.MyEventsTabFragment;
import com.mixapplications.iconegypt.fragment.MyNewsTabFragment;
import com.mixapplications.iconegypt.fragment.MyTasksTabFragment;
import com.mixapplications.iconegypt.fragment.NewsFragment;
import com.mixapplications.iconegypt.fragment.SendEventsTabFragment;
import com.mixapplications.iconegypt.fragment.SendTasksTabFragment;
import com.mixapplications.iconegypt.fragment.SentNewsTabFragment;
import com.mixapplications.iconegypt.fragment.TasksFragment;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class Util {

    public static void createAccount(final FirebaseAuth mAuth, final DatabaseReference ref, final Context context, final String email, String pass, final String name, final String img, final String type, final String phone) {
        mAuth.createUserWithEmailAndPassword(email, pass)
                .addOnCompleteListener((Activity) context, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Employee employee = new Employee();
                            employee.setEmail(email);
                            employee.setName(name);
                            employee.setPhone(phone);
                            employee.setStatus("holiday");
                            employee.setType(type);
                            employee.setImage(img);
                            ref.child("employees").push().setValue(employee);
                        } else {
                            ((Activity) context).runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(context, "error creating account", Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                    }
                });
    }


    public static void createNewsDB(final AppCompatActivity activity, final Context context, final DatabaseReference ref, String fromEmail, String title, String textNews) {
        News news = new News();
        news.setFromEmail(fromEmail);
        news.setTitle(title);
        news.setTextNews(textNews);
        ref.child("news").push().setValue(news).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                final ArrayList<News> newsArrayList = new ArrayList<>();
                final ArrayList<News> myNewsArrayList = new ArrayList<>();
                final ArrayList<Employee> employeeArrayList = new ArrayList<>();
                final NewsAdapter[] adapter = new NewsAdapter[1];
                final SentNewsAdapter[] myNewsAdapter = new SentNewsAdapter[1];
                Query aFireQuery = ref.child("news").orderByChild("timestamp");
                aFireQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (final DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            final News news = snapshot.getValue(News.class);
                            newsArrayList.add(news);
                            Query fireQuery = ref.child("employees").orderByChild("email").equalTo(news.getFromEmail());
                            fireQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    for (DataSnapshot snapshot1 : dataSnapshot.getChildren()) {
                                        Employee em = snapshot1.getValue(Employee.class);
                                        employeeArrayList.add(em);
                                        adapter[0] = new NewsAdapter(context, activity, newsArrayList, employeeArrayList);
                                        adapter[0].setOnItemClickListener(new View.OnClickListener() {
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
                                    //MyNewsTabFragment.mRecyclerView.setAdapter(adapter[0]);
                                    Fragment currentFragment = activity.getSupportFragmentManager().findFragmentById(R.id.fragmentContainer);
                                    if (currentFragment instanceof NewsFragment) {
                                        ((MyNewsTabFragment) ((NewsFragment) currentFragment).fragmentAdapter.getItem(0)).mRecyclerView.setAdapter(adapter[0]);
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                aFireQuery = ref.child("news").orderByChild("timestamp");
                aFireQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        final EditNewsCustomDialog[] dialog = new EditNewsCustomDialog[1];
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                            News Mynews = snapshot.getValue(News.class);
                            if (Mynews.getFromEmail().equalsIgnoreCase(AppData.employee.getEmail())) {
                                myNewsArrayList.add(snapshot.getValue(News.class));
                                myNewsAdapter[0] = new SentNewsAdapter(context, ref, activity, myNewsArrayList);
                                myNewsAdapter[0].setOnItemClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        RecyclerView.ViewHolder viewHolder = (RecyclerView.ViewHolder) v.getTag();
                                        int position = viewHolder.getAdapterPosition();
                                        dialog[0] = new EditNewsCustomDialog(ref, activity, context, myNewsArrayList.get(position));
                                        try {
                                            dialog[0].requestWindowFeature(Window.FEATURE_NO_TITLE);
                                            dialog[0].getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                            dialog[0].show();
                                        } catch (Exception e) {
                                            dialog[0].show();
                                        }

                                    }
                                });
                                if (dialog[0] != null)
                                    dialog[0].dismiss();
                            }
                        }
                        //SentNewsTabFragment.mRecyclerView.setAdapter(myNewsAdapter[0]);
                        Fragment currentFragment = activity.getSupportFragmentManager().findFragmentById(R.id.fragmentContainer);
                        if (currentFragment instanceof NewsFragment) {
                            ((SentNewsTabFragment) ((NewsFragment) currentFragment).fragmentAdapter.getItem(1)).mRecyclerView.setAdapter(myNewsAdapter[0]);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });
    }

    public static void editNewsDB(final AppCompatActivity activity, final Context context, final DatabaseReference ref, final News news, String title, String textNews) {
        long ts = news.getTimestamp();
        news.setTitle(title);
        news.setTextNews(textNews);
        Query query = ref.child("news").orderByChild("timestamp").equalTo(ts);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                final ArrayList<News> newsArrayList = new ArrayList<>();
                final ArrayList<News> myNewsArrayList = new ArrayList<>();
                final ArrayList<Employee> employeeArrayList = new ArrayList<>();
                final NewsAdapter[] adapter = new NewsAdapter[1];
                final SentNewsAdapter[] myNewsAdapter = new SentNewsAdapter[1];

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    snapshot.getRef().setValue(news).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Query aFireQuery = ref.child("news").orderByChild("timestamp");
                            aFireQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    for (final DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                        final News news = snapshot.getValue(News.class);
                                        newsArrayList.add(news);

                                        Query fireQuery = ref.child("employees").orderByChild("email").equalTo(news.getFromEmail());
                                        fireQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                for (DataSnapshot snapshot1 : dataSnapshot.getChildren()) {
                                                    Employee em = snapshot1.getValue(Employee.class);
                                                    employeeArrayList.add(em);
                                                    adapter[0] = new NewsAdapter(context, activity, newsArrayList, employeeArrayList);
                                                    adapter[0].setOnItemClickListener(new View.OnClickListener() {
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
                                                //MyNewsTabFragment.mRecyclerView.setAdapter(adapter[0]);
                                                Fragment currentFragment = activity.getSupportFragmentManager().findFragmentById(R.id.fragmentContainer);
                                                if (currentFragment instanceof NewsFragment) {
                                                    ((MyNewsTabFragment) ((NewsFragment) currentFragment).fragmentAdapter.getItem(0)).mRecyclerView.setAdapter(adapter[0]);
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                            }
                                        });
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                            aFireQuery = ref.child("news").orderByChild("timestamp");
                            aFireQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                    final EditNewsCustomDialog[] dialog = new EditNewsCustomDialog[1];
                                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                                        News Mynews = snapshot.getValue(News.class);
                                        if (Mynews.getFromEmail().equalsIgnoreCase(AppData.employee.getEmail())) {
                                            myNewsArrayList.add(snapshot.getValue(News.class));
                                            myNewsAdapter[0] = new SentNewsAdapter(context, ref, activity, myNewsArrayList);
                                            myNewsAdapter[0].setOnItemClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    RecyclerView.ViewHolder viewHolder = (RecyclerView.ViewHolder) v.getTag();
                                                    int position = viewHolder.getAdapterPosition();
                                                    dialog[0] = new EditNewsCustomDialog(ref, activity, context, myNewsArrayList.get(position));
                                                    try {
                                                        dialog[0].requestWindowFeature(Window.FEATURE_NO_TITLE);
                                                        dialog[0].getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                                        dialog[0].show();
                                                    } catch (Exception e) {
                                                        dialog[0].show();
                                                    }

                                                }
                                            });
                                            if (dialog[0] != null)
                                                dialog[0].dismiss();
                                        }
                                    }
                                    //SentNewsTabFragment.mRecyclerView.setAdapter(myNewsAdapter[0]);
                                    Fragment currentFragment = activity.getSupportFragmentManager().findFragmentById(R.id.fragmentContainer);
                                    if (currentFragment instanceof NewsFragment) {
                                        ((SentNewsTabFragment) ((NewsFragment) currentFragment).fragmentAdapter.getItem(1)).mRecyclerView.setAdapter(myNewsAdapter[0]);
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public static void removeNewsDB(final AppCompatActivity activity, final Context context, final DatabaseReference ref, final News news) {

        Query query = ref.child("news").orderByChild("timestamp").equalTo(news.getTimestamp());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                final ArrayList<News> newsArrayList = new ArrayList<>();
                final ArrayList<Employee> employeeArrayList = new ArrayList<>();
                final NewsAdapter[] adapter = new NewsAdapter[1];
                final SentNewsAdapter[] myNewsAdapter = new SentNewsAdapter[1];
                final EditNewsCustomDialog[] dialog = new EditNewsCustomDialog[1];
                final ArrayList<News> myNewsArrayList = new ArrayList<>();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    snapshot.getRef().removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            Query aFireQuery = ref.child("news").orderByChild("timestamp");
                            aFireQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    for (final DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                        final News news = snapshot.getValue(News.class);
                                        newsArrayList.add(news);

                                        Query fireQuery = ref.child("employees").orderByChild("email").equalTo(news.getFromEmail());
                                        fireQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                for (DataSnapshot snapshot1 : dataSnapshot.getChildren()) {
                                                    Employee em = snapshot1.getValue(Employee.class);
                                                    employeeArrayList.add(em);
                                                    adapter[0] = new NewsAdapter(context, activity, newsArrayList, employeeArrayList);
                                                    adapter[0].setOnItemClickListener(new View.OnClickListener() {
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
                                                //MyNewsTabFragment.mRecyclerView.setAdapter(adapter[0]);

                                                Fragment currentFragment = activity.getSupportFragmentManager().findFragmentById(R.id.fragmentContainer);
                                                if (currentFragment instanceof NewsFragment) {
                                                    ((MyNewsTabFragment) ((NewsFragment) currentFragment).fragmentAdapter.getItem(0)).mRecyclerView.setAdapter(adapter[0]);
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                            }
                                        });

                                        News MyNews = snapshot.getValue(News.class);
                                        if (MyNews.getFromEmail().equalsIgnoreCase(AppData.employee.getEmail())) {
                                            myNewsArrayList.add(snapshot.getValue(News.class));
                                            myNewsAdapter[0] = new SentNewsAdapter(context, ref, activity, myNewsArrayList);
                                            if (dialog[0] != null)
                                                dialog[0].dismiss();
                                        }

                                    }
                                    //SentNewsTabFragment.mRecyclerView.setAdapter(myNewsAdapter[0]);
                                    Fragment currentFragment = activity.getSupportFragmentManager().findFragmentById(R.id.fragmentContainer);
                                    if (currentFragment instanceof NewsFragment) {
                                        ((SentNewsTabFragment) ((NewsFragment) currentFragment).fragmentAdapter.getItem(1)).mRecyclerView.setAdapter(myNewsAdapter[0]);
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public static void createTaskDB(final AppCompatActivity activity, final Context context, final DatabaseReference ref,
                                    String title, Employee fromEmployee, Employee toEmployee, long fromDate, long toDate, String details) {
        Tasks tasks = new Tasks(title, fromEmployee, toEmployee, fromDate, toDate, details);
        ref.child("tasks").push().setValue(tasks).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                final ArrayList<Tasks> childTasksArrayList = new ArrayList<>();
                final ArrayList<Tasks> myTasksArrayList = new ArrayList<>();
                final ArrayList<Tasks> sendTasksArrayList = new ArrayList<>();
                final TasksAdapter[] myTasksAdapter = new TasksAdapter[1];
                final TasksAdapter[] childTasksAdapter = new TasksAdapter[1];
                final SentTaskAdapter[] sendTasksAdapter = new SentTaskAdapter[1];

                Query aFireQuery = ref.child("tasks").orderByChild("timestamp");
                aFireQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (final DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            final Tasks task = snapshot.getValue(Tasks.class);
                            if (task.getToEmployee().getEmail().equalsIgnoreCase(AppData.currentUser.getEmail())) {
                                myTasksArrayList.add(task);
                            } else if (task.getFromEmployee().getEmail().equalsIgnoreCase(AppData.currentUser.getEmail())) {
                                sendTasksArrayList.add(task);
                            } else {
                                boolean isChild = AppData.myEmployeeNode.findChild(task.getToEmployee().getEmail()) != null &&
                                        !AppData.myEmployeeNode.findChild(task.getToEmployee().getEmail()).getEmail().equalsIgnoreCase(AppData.currentUser.getEmail());
                                if (isChild && !task.getFromEmployee().getEmail().equalsIgnoreCase(AppData.currentUser.getEmail())) {
                                    childTasksArrayList.add(task);
                                }
                            }
                        }
                        myTasksAdapter[0] = new TasksAdapter(context, activity, myTasksArrayList);
                        childTasksAdapter[0] = new TasksAdapter(context, activity, childTasksArrayList);
                        sendTasksAdapter[0] = new SentTaskAdapter(context, ref, activity, sendTasksArrayList);
                        myTasksAdapter[0].setOnItemClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                RecyclerView.ViewHolder viewHolder = (RecyclerView.ViewHolder) v.getTag();
                                int position = viewHolder.getAdapterPosition();
                                ViewTasksCustomDialog dialog = new ViewTasksCustomDialog(activity, myTasksArrayList.get(position));
                                try {
                                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                    dialog.show();
                                } catch (Exception e) {
                                    dialog.show();
                                }

                            }
                        });

                        childTasksAdapter[0].setOnItemClickListener(new View.OnClickListener() {
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
                        /*SendTasksTabFragment.mRecyclerView.setAdapter(sendTasksAdapter[0]);
                        MyTasksTabFragment.mRecyclerView.setAdapter(myTasksAdapter[0]);
                        ChildTasksTabFragment.mRecyclerView.setAdapter(childTasksAdapter[0])*/
                        Fragment currentFragment = activity.getSupportFragmentManager().findFragmentById(R.id.fragmentContainer);

                        if (currentFragment instanceof TasksFragment) {
                            ((MyTasksTabFragment) ((TasksFragment) currentFragment).fragmentAdapter.getItem(0)).mRecyclerView.setAdapter(myTasksAdapter[0]);
                            ((ChildTasksTabFragment) ((TasksFragment) currentFragment).fragmentAdapter.getItem(1)).mRecyclerView.setAdapter(childTasksAdapter[0]);
                            ((SendTasksTabFragment) ((TasksFragment) currentFragment).fragmentAdapter.getItem(2)).mRecyclerView.setAdapter(sendTasksAdapter[0]);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });
    }

    public static void editTaskDB(final AppCompatActivity activity, final Context context, final DatabaseReference ref, final Tasks task,
                                  String title, Employee fromEmployee, Employee toEmployee, long fromDate, long toDate, String details) {
        long ts = task.getTimestamp();
        task.setTitle(title);
        task.setFromEmployee(fromEmployee);
        task.setToEmployee(toEmployee);
        task.setFromDate(fromDate);
        task.setToDate(toDate);
        task.setDetails(details);

        Query query = ref.child("tasks").orderByChild("timestamp").equalTo(ts);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    snapshot.getRef().setValue(task).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            final ArrayList<Tasks> childTasksArrayList = new ArrayList<>();
                            final ArrayList<Tasks> myTasksArrayList = new ArrayList<>();
                            final ArrayList<Tasks> sendTasksArrayList = new ArrayList<>();
                            final TasksAdapter[] myTasksAdapter = new TasksAdapter[1];
                            final TasksAdapter[] childTasksAdapter = new TasksAdapter[1];
                            final SentTaskAdapter[] sendTasksAdapter = new SentTaskAdapter[1];

                            Query aFireQuery = ref.child("tasks").orderByChild("timestamp");
                            aFireQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    for (final DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                        final Tasks task = snapshot.getValue(Tasks.class);
                                        if (task.getToEmployee().getEmail().equalsIgnoreCase(AppData.currentUser.getEmail())) {
                                            myTasksArrayList.add(task);
                                        } else if (task.getFromEmployee().getEmail().equalsIgnoreCase(AppData.currentUser.getEmail())) {
                                            sendTasksArrayList.add(task);
                                        } else {
                                            boolean isChild = AppData.myEmployeeNode.findChild(task.getToEmployee().getEmail()) != null &&
                                                    !AppData.myEmployeeNode.findChild(task.getToEmployee().getEmail()).getEmail().equalsIgnoreCase(AppData.currentUser.getEmail());
                                            if (isChild && !task.getFromEmployee().getEmail().equalsIgnoreCase(AppData.currentUser.getEmail())) {
                                                childTasksArrayList.add(task);
                                            }
                                        }
                                    }
                                    myTasksAdapter[0] = new TasksAdapter(context, activity, myTasksArrayList);
                                    childTasksAdapter[0] = new TasksAdapter(context, activity, childTasksArrayList);
                                    sendTasksAdapter[0] = new SentTaskAdapter(context, ref, activity, sendTasksArrayList);
                                    myTasksAdapter[0].setOnItemClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            RecyclerView.ViewHolder viewHolder = (RecyclerView.ViewHolder) v.getTag();
                                            int position = viewHolder.getAdapterPosition();
                                            ViewTasksCustomDialog dialog = new ViewTasksCustomDialog(activity, myTasksArrayList.get(position));
                                            try {
                                                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                                dialog.show();
                                            } catch (Exception e) {
                                                dialog.show();
                                            }

                                        }
                                    });

                                    childTasksAdapter[0].setOnItemClickListener(new View.OnClickListener() {
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
                                    /*SendTasksTabFragment.mRecyclerView.setAdapter(sendTasksAdapter[0]);
                                    MyTasksTabFragment.mRecyclerView.setAdapter(myTasksAdapter[0]);
                                    ChildTasksTabFragment.mRecyclerView.setAdapter(childTasksAdapter[0])*/
                                    Fragment currentFragment = activity.getSupportFragmentManager().findFragmentById(R.id.fragmentContainer);

                                    if (currentFragment instanceof TasksFragment) {
                                        ((MyTasksTabFragment) ((TasksFragment) currentFragment).fragmentAdapter.getItem(0)).mRecyclerView.setAdapter(myTasksAdapter[0]);
                                        ((ChildTasksTabFragment) ((TasksFragment) currentFragment).fragmentAdapter.getItem(1)).mRecyclerView.setAdapter(childTasksAdapter[0]);
                                        ((SendTasksTabFragment) ((TasksFragment) currentFragment).fragmentAdapter.getItem(2)).mRecyclerView.setAdapter(sendTasksAdapter[0]);
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public static void removeEventDB(final AppCompatActivity activity, final Context context, final DatabaseReference ref, final Events event) {

        Query query = ref.child("events").orderByChild("timestamp").equalTo(event.getTimestamp());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    snapshot.getRef().removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            final ArrayList<Events> eventsArrayList = new ArrayList<>();
                            final ArrayList<Events> sendEventsArrayList = new ArrayList<>();
                            final EventsAdapter[] eventsAdapters = new EventsAdapter[1];
                            final SentEventsAdapter[] sentEventsAdapters = new SentEventsAdapter[1];

                            Query aFireQuery = ref.child("events").orderByChild("timestamp");
                            aFireQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    for (final DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                        final Events events = snapshot.getValue(Events.class);
                                        if (events != null && events.getToEmployee() != null && events.getToEmployee().indexOf(AppData.employee) > -1) {
                                            eventsArrayList.add(events);
                                        } else if (events.getFromEmployee().getEmail().equalsIgnoreCase(AppData.currentUser.getEmail())) {
                                            sendEventsArrayList.add(events);
                                        }
                                    }
                                    eventsAdapters[0] = new EventsAdapter(context, activity, eventsArrayList);
                                    sentEventsAdapters[0] = new SentEventsAdapter(context, ref, activity, sendEventsArrayList);
                                    eventsAdapters[0].setOnItemClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            RecyclerView.ViewHolder viewHolder = (RecyclerView.ViewHolder) v.getTag();
                                            int position = viewHolder.getAdapterPosition();
                                            ViewEventCustomDialog dialog = new ViewEventCustomDialog(activity, eventsArrayList.get(position));
                                            try {
                                                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                                dialog.show();
                                            } catch (Exception e) {
                                                dialog.show();
                                            }

                                        }
                                    });

                                    Fragment currentFragment = activity.getSupportFragmentManager().findFragmentById(R.id.fragmentContainer);

                                    if (currentFragment instanceof EventsFragment) {
                                        ((MyEventsTabFragment) ((EventsFragment) currentFragment).fragmentAdapter.getItem(0)).mRecyclerView.setAdapter(eventsAdapters[0]);
                                        ((SendEventsTabFragment) ((EventsFragment) currentFragment).fragmentAdapter.getItem(1)).mRecyclerView.setAdapter(sentEventsAdapters[0]);
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public static void createEventDB(final AppCompatActivity activity, final Context context, final DatabaseReference ref,
                                     String title, Employee fromEmployee, List<Employee> toEmployees, long date, String details) {
        Events events = new Events(title, fromEmployee, toEmployees, date, details);
        ref.child("events").push().setValue(events).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                final ArrayList<Events> eventsArrayList = new ArrayList<>();
                final ArrayList<Events> sendEventsArrayList = new ArrayList<>();
                final EventsAdapter[] eventsAdapters = new EventsAdapter[1];
                final SentEventsAdapter[] sentEventsAdapters = new SentEventsAdapter[1];

                Query aFireQuery = ref.child("events").orderByChild("timestamp");
                aFireQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (final DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            final Events events = snapshot.getValue(Events.class);
                            if (events != null && events.getToEmployee() != null && events.getToEmployee().indexOf(AppData.employee) > -1) {
                                eventsArrayList.add(events);
                            } else if (events.getFromEmployee().getEmail().equalsIgnoreCase(AppData.currentUser.getEmail())) {
                                sendEventsArrayList.add(events);
                            }
                        }
                        eventsAdapters[0] = new EventsAdapter(context, activity, eventsArrayList);
                        sentEventsAdapters[0] = new SentEventsAdapter(context, ref, activity, sendEventsArrayList);
                        eventsAdapters[0].setOnItemClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                RecyclerView.ViewHolder viewHolder = (RecyclerView.ViewHolder) v.getTag();
                                int position = viewHolder.getAdapterPosition();
                                ViewEventCustomDialog dialog = new ViewEventCustomDialog(activity, eventsArrayList.get(position));
                                try {
                                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                    dialog.show();
                                } catch (Exception e) {
                                    dialog.show();
                                }

                            }
                        });

                        Fragment currentFragment = activity.getSupportFragmentManager().findFragmentById(R.id.fragmentContainer);

                        if (currentFragment instanceof EventsFragment) {
                            ((MyEventsTabFragment) ((EventsFragment) currentFragment).fragmentAdapter.getItem(0)).mRecyclerView.setAdapter(eventsAdapters[0]);
                            ((SendEventsTabFragment) ((EventsFragment) currentFragment).fragmentAdapter.getItem(1)).mRecyclerView.setAdapter(sentEventsAdapters[0]);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });
    }

    public static void editEventDB(final AppCompatActivity activity, final Context context, final DatabaseReference ref, final Events events,
                                   String title, Employee fromEmployee, List<Employee> toEmployee, long date, String details) {
        long ts = events.getTimestamp();
        events.setTitle(title);
        events.setFromEmployee(fromEmployee);
        events.setToEmployee(toEmployee);
        events.setDate(date);
        events.setDetails(details);

        Query query = ref.child("events").orderByChild("timestamp").equalTo(ts);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    snapshot.getRef().setValue(events).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            final ArrayList<Events> eventsArrayList = new ArrayList<>();
                            final ArrayList<Events> sendEventsArrayList = new ArrayList<>();
                            final EventsAdapter[] eventsAdapters = new EventsAdapter[1];
                            final SentEventsAdapter[] sentEventsAdapters = new SentEventsAdapter[1];

                            Query aFireQuery = ref.child("events").orderByChild("timestamp");
                            aFireQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    for (final DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                        final Events events = snapshot.getValue(Events.class);
                                        if (events != null && events.getToEmployee() != null && events.getToEmployee().indexOf(AppData.employee) > -1) {
                                            eventsArrayList.add(events);
                                        } else if (events.getFromEmployee().getEmail().equalsIgnoreCase(AppData.currentUser.getEmail())) {
                                            sendEventsArrayList.add(events);
                                        }
                                    }
                                    eventsAdapters[0] = new EventsAdapter(context, activity, eventsArrayList);
                                    sentEventsAdapters[0] = new SentEventsAdapter(context, ref, activity, sendEventsArrayList);
                                    eventsAdapters[0].setOnItemClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            RecyclerView.ViewHolder viewHolder = (RecyclerView.ViewHolder) v.getTag();
                                            int position = viewHolder.getAdapterPosition();
                                            ViewEventCustomDialog dialog = new ViewEventCustomDialog(activity, eventsArrayList.get(position));
                                            try {
                                                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                                dialog.show();
                                            } catch (Exception e) {
                                                dialog.show();
                                            }

                                        }
                                    });

                                    Fragment currentFragment = activity.getSupportFragmentManager().findFragmentById(R.id.fragmentContainer);

                                    if (currentFragment instanceof EventsFragment) {
                                        ((MyEventsTabFragment) ((EventsFragment) currentFragment).fragmentAdapter.getItem(0)).mRecyclerView.setAdapter(eventsAdapters[0]);
                                        ((SendEventsTabFragment) ((EventsFragment) currentFragment).fragmentAdapter.getItem(1)).mRecyclerView.setAdapter(sentEventsAdapters[0]);
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public static void removeTaskDB(final AppCompatActivity activity, final Context context, final DatabaseReference ref, final Tasks task) {

        Query query = ref.child("tasks").orderByChild("timestamp").equalTo(task.getTimestamp());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    snapshot.getRef().removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            final ArrayList<Tasks> childTasksArrayList = new ArrayList<>();
                            final ArrayList<Tasks> myTasksArrayList = new ArrayList<>();
                            final ArrayList<Tasks> sendTasksArrayList = new ArrayList<>();
                            final TasksAdapter[] myTasksAdapter = new TasksAdapter[1];
                            final TasksAdapter[] childTasksAdapter = new TasksAdapter[1];
                            final SentTaskAdapter[] sendTasksAdapter = new SentTaskAdapter[1];

                            Query aFireQuery = ref.child("tasks").orderByChild("timestamp");
                            aFireQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    for (final DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                        final Tasks task = snapshot.getValue(Tasks.class);
                                        if (task.getToEmployee().getEmail().equalsIgnoreCase(AppData.currentUser.getEmail())) {
                                            myTasksArrayList.add(task);
                                        } else if (task.getFromEmployee().getEmail().equalsIgnoreCase(AppData.currentUser.getEmail())) {
                                            sendTasksArrayList.add(task);
                                        } else {
                                            boolean isChild = AppData.myEmployeeNode.findChild(task.getToEmployee().getEmail()) != null &&
                                                    !AppData.myEmployeeNode.findChild(task.getToEmployee().getEmail()).getEmail().equalsIgnoreCase(AppData.currentUser.getEmail());
                                            if (isChild && !task.getFromEmployee().getEmail().equalsIgnoreCase(AppData.currentUser.getEmail())) {
                                                childTasksArrayList.add(task);
                                            }
                                        }
                                    }
                                    myTasksAdapter[0] = new TasksAdapter(context, activity, myTasksArrayList);
                                    childTasksAdapter[0] = new TasksAdapter(context, activity, childTasksArrayList);
                                    sendTasksAdapter[0] = new SentTaskAdapter(context, ref, activity, sendTasksArrayList);
                                    myTasksAdapter[0].setOnItemClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            RecyclerView.ViewHolder viewHolder = (RecyclerView.ViewHolder) v.getTag();
                                            int position = viewHolder.getAdapterPosition();
                                            ViewTasksCustomDialog dialog = new ViewTasksCustomDialog(activity, myTasksArrayList.get(position));
                                            try {
                                                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                                dialog.show();
                                            } catch (Exception e) {
                                                dialog.show();
                                            }

                                        }
                                    });

                                    childTasksAdapter[0].setOnItemClickListener(new View.OnClickListener() {
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
                                    /*SendTasksTabFragment.mRecyclerView.setAdapter(sendTasksAdapter[0]);
                                    MyTasksTabFragment.mRecyclerView.setAdapter(myTasksAdapter[0]);
                                    ChildTasksTabFragment.mRecyclerView.setAdapter(childTasksAdapter[0])*/
                                    Fragment currentFragment = activity.getSupportFragmentManager().findFragmentById(R.id.fragmentContainer);

                                    if (currentFragment instanceof TasksFragment) {
                                        ((MyTasksTabFragment) ((TasksFragment) currentFragment).fragmentAdapter.getItem(0)).mRecyclerView.setAdapter(myTasksAdapter[0]);
                                        ((ChildTasksTabFragment) ((TasksFragment) currentFragment).fragmentAdapter.getItem(1)).mRecyclerView.setAdapter(childTasksAdapter[0]);
                                        ((SendTasksTabFragment) ((TasksFragment) currentFragment).fragmentAdapter.getItem(2)).mRecyclerView.setAdapter(sendTasksAdapter[0]);
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    public static String getDateCurrentTimeZone(long timestamp) {
        try {
            Calendar calendar = Calendar.getInstance();
            TimeZone tz = TimeZone.getTimeZone("Egypt");
            calendar.setTimeInMillis(timestamp - 7200000);
            calendar.add(Calendar.MILLISECOND, tz.getOffset(calendar.getTimeInMillis()));
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm a", new Locale("en", "EG"));
            Date currentTimeZone = calendar.getTime();
            return sdf.format(currentTimeZone);
        } catch (Exception e) {
        }
        return "";
    }

    public static long timeZoneToTimestamp(String dateTime) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm a", new Locale("en", "EG"));

        Calendar calendar = Calendar.getInstance();
        TimeZone tz = TimeZone.getTimeZone("Egypt");
        Date date = new Date();
        try {
            date = sdf.parse(dateTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        calendar.setTime(date);
        return (calendar.getTimeInMillis());
    }

    public static void test2(final FirebaseAuth mAuth, final DatabaseReference ref, final Context context) {
        createAccount(mAuth, ref, context, "owner@iconegypt.com", "123456", "ICON Egypt Owner", "", "Owner", "01234567890");
        createAccount(mAuth, ref, context, "marketing.director@iconegypt.com", "123456", "ICON Egypt Marketing Director", "", "Marketing Director", "01234567890");
        createAccount(mAuth, ref, context, "domestic.marketing@iconegypt.com", "123456", "ICON Egypt Domestic Marketing", "", "Domestic Marketing", "01234567890");
        createAccount(mAuth, ref, context, "abroad.marketing@iconegypt.com", "123456", "ICON Egypt Abroad Marketing", "", "Abroad Marketing", "01234567890");
        createAccount(mAuth, ref, context, "public.relation.officer@iconegypt.com", "123456", "ICON Egypt Relation Officer", "", "Relation Officer", "01234567890");
        createAccount(mAuth, ref, context, "local.agent@iconegypt.com", "123456", "ICON Egypt Local Agent", "", "Local Agent", "01234567890");
        createAccount(mAuth, ref, context, "general.manager@iconegypt.com", "123456", "ICON Egypt General Manager", "", "GM", "01234567890");
        createAccount(mAuth, ref, context, "ticketing.officer@iconegypt.com", "123456", "ICON Egypt Ticketing Officer", "", "Ticketing Officer", "01234567890");
        createAccount(mAuth, ref, context, "office.secretary@iconegypt.com", "123456", "ICON Egypt Office Secretary", "", "Office Secretary", "01234567890");
        createAccount(mAuth, ref, context, "receptionst@iconegypt.com", "123456", "ICON Egypt Receptionst", "", "Receptionst", "01234567890");
        createAccount(mAuth, ref, context, "representative.for.embassay@iconegypt.com", "123456", "ICON Egypt RFE", "", "RFE", "01234567890");
        createAccount(mAuth, ref, context, "office.boy@iconegypt.com", "123456", "ICON Egypt Office Boy", "", "Office Boy", "01234567890");
        createAccount(mAuth, ref, context, "security.guard@iconegypt.com", "123456", "ICON Egypt Security Guard", "", "Security Guard", "01234567890");
        createAccount(mAuth, ref, context, "executive.director@iconegypt.com", "123456", "ICON Egypt Executive Director", "", "Executive Director", "01234567890");
        createAccount(mAuth, ref, context, "legal.advisior@iconegypt.com", "123456", "ICON Egypt Legal Advisior", "", "Legal Advisior", "01234567890");
        createAccount(mAuth, ref, context, "laision.officer@iconegypt.com", "123456", "ICON Egypt Laision Officer", "", "Laision Officer", "01234567890");
        createAccount(mAuth, ref, context, "auditor@iconegypt.com", "123456", "ICON Egypt Auditor", "", "Auditor", "01234567890");
        createAccount(mAuth, ref, context, "accountant@iconegypt.com", "123456", "ICON Egypt Accountant", "", "Accountant", "01234567890");
        createAccount(mAuth, ref, context, "cashier@iconegypt.com", "123456", "ICON Egypt Cashier", "", "Cashier", "01234567890");


    }

    public static String testNode() {
        EmployeeFirebaseNode parentNode = new EmployeeFirebaseNode("owner@iconegypt.com");
        EmployeeFirebaseNode childrenNode1 = new EmployeeFirebaseNode("marketing.director@iconegypt.com");
        EmployeeFirebaseNode childrenNode4 = new EmployeeFirebaseNode("domestic.marketing@iconegypt.com");
        EmployeeFirebaseNode childrenNode5 = new EmployeeFirebaseNode("abroad.marketing@iconegypt.com");
        EmployeeFirebaseNode childrenNode6 = new EmployeeFirebaseNode("public.relation.officer@iconegypt.com");
        EmployeeFirebaseNode childrenNode7 = new EmployeeFirebaseNode("local.agent@iconegypt.com");
        childrenNode6.addChild(childrenNode7);
        childrenNode4.addChild(childrenNode6);
        childrenNode1.addChildren(childrenNode4, childrenNode5);

        EmployeeFirebaseNode childrenNode2 = new EmployeeFirebaseNode("general.manager@iconegypt.com");
        EmployeeFirebaseNode childrenNode8 = new EmployeeFirebaseNode("ticketing.officer@iconegypt.com");
        EmployeeFirebaseNode childrenNode9 = new EmployeeFirebaseNode("office.secretary@iconegypt.com");
        EmployeeFirebaseNode childrenNode10 = new EmployeeFirebaseNode("receptionst@iconegypt.com");
        EmployeeFirebaseNode childrenNode12 = new EmployeeFirebaseNode("representative.for.embassay@iconegypt.com");
        EmployeeFirebaseNode childrenNode13 = new EmployeeFirebaseNode("office.boy@iconegypt.com");
        EmployeeFirebaseNode childrenNode14 = new EmployeeFirebaseNode("security.guard@iconegypt.com");
        childrenNode10.addChildren(childrenNode13, childrenNode14);
        childrenNode9.addChildren(childrenNode10, childrenNode12);
        childrenNode2.addChildren(childrenNode9, childrenNode8);

        EmployeeFirebaseNode childrenNode3 = new EmployeeFirebaseNode("executive.director@iconegypt.com");

        EmployeeFirebaseNode childrenNode15 = new EmployeeFirebaseNode("legal.advisior@iconegypt.com");
        EmployeeFirebaseNode childrenNode16 = new EmployeeFirebaseNode("laision.officer@iconegypt.com");
        EmployeeFirebaseNode childrenNode17 = new EmployeeFirebaseNode("auditor@iconegypt.com");
        EmployeeFirebaseNode childrenNode18 = new EmployeeFirebaseNode("accountant@iconegypt.com");
        EmployeeFirebaseNode childrenNode19 = new EmployeeFirebaseNode("cashier@iconegypt.com");
        childrenNode18.addChild(childrenNode19);
        childrenNode17.addChild(childrenNode18);
        childrenNode15.addChild(childrenNode16);
        childrenNode3.addChildren(childrenNode15, childrenNode17);
        parentNode.addChildren(childrenNode1, childrenNode2, childrenNode3);

        Gson gson = new Gson();
        String json = gson.toJson(parentNode);
        return json;
    }


}
