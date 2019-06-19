package com.mixapplications.iconegypt.activity;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.service.notification.StatusBarNotification;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NotificationCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;
import com.mixapplications.iconegypt.R;
import com.mixapplications.iconegypt.fragment.EmployeeFragment;
import com.mixapplications.iconegypt.fragment.EventsFragment;
import com.mixapplications.iconegypt.fragment.FormsFragment;
import com.mixapplications.iconegypt.fragment.MainFragment;
import com.mixapplications.iconegypt.fragment.NewsFragment;
import com.mixapplications.iconegypt.fragment.ProfileFragment;
import com.mixapplications.iconegypt.fragment.TasksFragment;
import com.mixapplications.iconegypt.models.AppData;
import com.mixapplications.iconegypt.models.Employee;
import com.mixapplications.iconegypt.models.EmployeeFirebaseNode;
import com.mixapplications.iconegypt.models.Events;
import com.mixapplications.iconegypt.models.News;
import com.mixapplications.iconegypt.models.Prefs;
import com.mixapplications.iconegypt.models.Tasks;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import java.util.LinkedList;
import java.util.Queue;

import agency.tango.android.avatarview.views.AvatarView;

import static com.mixapplications.iconegypt.models.AppData.allEmployeeNode;
import static com.mixapplications.iconegypt.models.AppData.currentUser;
import static com.mixapplications.iconegypt.models.AppData.employee;
import static com.mixapplications.iconegypt.models.AppData.myEmployeeNode;

public class SelectActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public static String fragmentTag = "";
    public TextView txt_email;
    public TextView txt_name;
    FirebaseAuth mAuth;
    AlertDialog dialog;
    FirebaseStorage firebaseStorage;
    StorageReference mainRef;
    NavigationView navigationView;
    DatabaseReference ref;
    FirebaseDatabase mDataBase;
    Context context;
    boolean isLogin = false;
    AvatarView profileImage;
    private long mBackPressed;
    private Queue<Runnable> toDoQueue = new LinkedList<>();
    private boolean isPaused = false;

    private static PendingIntent prepareIntent(Context context, String fragment) {
        Intent intent = new Intent(context, SelectActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        if (fragment != null && !fragment.equalsIgnoreCase(""))
            intent.putExtra("fragment", fragment);
        return PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select);
        Toolbar toolbar = findViewById(R.id.toolbar);
        context = getApplicationContext();
        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View hView = navigationView.getHeaderView(0);
        profileImage = hView.findViewById(R.id.imageView);
        txt_email = hView.findViewById(R.id.txt_email);
        txt_name = hView.findViewById(R.id.txt_name);
        setSupportActionBar(toolbar);
        FirebaseApp customApp = FirebaseApp.initializeApp(this);

        assert customApp != null;
        firebaseStorage = FirebaseStorage.getInstance(customApp);
        mainRef = firebaseStorage.getReference();
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        mDataBase = FirebaseDatabase.getInstance();
        ref = mDataBase.getReference();
        assert getIntent().getExtras() != null;
        isLogin = (boolean) getIntent().getExtras().get("isLogin");
        try {
            employee = new Employee();
            employee.setPhone((String) getIntent().getExtras().get("phone"));
            employee.setName((String) getIntent().getExtras().get("name"));
            employee.setEmail((String) getIntent().getExtras().get("email"));
            employee.setImage((String) getIntent().getExtras().get("image"));
            employee.setStatus((String) getIntent().getExtras().get("status"));
            employee.setType((String) getIntent().getExtras().get("type"));
            employee.setLastNews(getIntent().getExtras().getLong("lastNews"));
            employee.setLastTask(getIntent().getExtras().getLong("lastTask"));
            employee.setLastEvent(getIntent().getExtras().getLong("lastEvent"));
        } catch (Exception e) {
            if (currentUser != null) {

                Query fireQuery = ref.child("employees").orderByChild("email").equalTo(currentUser.getEmail());
                fireQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot1) {
                        for (DataSnapshot snapshot1 : dataSnapshot1.getChildren()) {
                            employee = snapshot1.getValue(Employee.class);
                            if (employee != null && employee.getImage() != null && !employee.getImage().equals("")) {
                                Picasso.get().load(employee.getImage()).placeholder(R.drawable.profile_placeholder).into(profileImage);
                            }
                            txt_name.setText(employee != null && employee.getName() != null ? employee.getName() : "");
                            txt_email.setText(employee.getEmail() != null ? employee.getEmail() : "");

                            break;
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        }

        if (employee.getEmail() == null || employee.getEmail().equalsIgnoreCase("")) {

            if (currentUser != null) {
                Query fireQuery = ref.child("employees").orderByChild("email").equalTo(currentUser.getEmail());
                fireQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot1) {
                        for (DataSnapshot snapshot1 : dataSnapshot1.getChildren()) {
                            employee = snapshot1.getValue(Employee.class);
                            if (employee != null && employee.getImage() != null && !employee.getImage().equals("")) {
                                Picasso.get().load(employee.getImage()).placeholder(R.drawable.profile_placeholder).into(profileImage);
                            }
                            txt_name.setText(employee.getName() != null ? employee.getName() : "");
                            txt_email.setText(employee.getEmail() != null ? employee.getEmail() : "");

                            break;
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        }

        if (employee.getImage() != null && !employee.getImage().equals("")) {
            Picasso.get().load(employee.getImage()).placeholder(R.drawable.profile_placeholder).into(profileImage);
        }
        txt_name.setText(employee.getName() != null ? employee.getName() : "");
        txt_email.setText(employee.getEmail() != null ? employee.getEmail() : "");
        setFragment(new MainFragment(), false, "main_fragment");
        if (getIntent().getExtras() != null && getIntent().getExtras().get("fragment") != null && !getIntent().getExtras().get("fragment").equals("")) {
            if (currentUser != null) {
                if (((String) getIntent().getExtras().get("fragment")).equalsIgnoreCase("news")) {
                    setFragment(new NewsFragment(), true, "news_fragment");
                }
            }
        }

        if (allEmployeeNode == null || myEmployeeNode == null) {
            String nodeJson = (String) getIntent().getExtras().get("nodeJson");
            Gson gson = new Gson();
            EmployeeFirebaseNode employeeFirebaseNode = gson.fromJson(nodeJson, EmployeeFirebaseNode.class);
            allEmployeeNode = employeeFirebaseNode.toEmployeeNode();
            myEmployeeNode = allEmployeeNode.findChild(employee.getEmail());
        }
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        //thread check news
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (isLogin) {
                    Query aFireQuery = ref.child("news").orderByChild("timestamp");
                    aFireQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (final DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                News news = snapshot.getValue(News.class);
                                if (news != null && news.getTimestamp() < AppData.employee.getLastNews() && !news.getFromEmail().equalsIgnoreCase(currentUser.getEmail())) {
                                    Prefs.initPrefs(getApplicationContext(), "icon_egypt", Context.MODE_PRIVATE);
                                    Prefs.putBoolean(currentUser.getEmail() + "-news", true);
                                    try {
                                        if (SelectActivity.fragmentTag.equalsIgnoreCase("main_fragment"))
                                            MainFragment.setRedDotVisibility(getApplicationContext(), 0, true);
                                    } catch (Exception e) {
                                    }
                                    Intent intent = new Intent(getApplicationContext(), SelectActivity.class);
                                    intent.putExtra("fragment", "news");
                                    intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                    if (getIntent().getExtras().get("fragment") == null || getIntent().getExtras().get("fragment").equals("") && isLogin)
                                        showNotification(news.getTitle(), news.getTextNews(), intent, "news", news.timestamp());

                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                    Query bFireQuery = ref.child("tasks").orderByChild("timestamp");
                    bFireQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (final DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                Tasks tasks = snapshot.getValue(Tasks.class);
                                if (tasks != null && tasks.getTimestamp() < AppData.employee.getLastTask() &&
                                        !tasks.getFromEmployee().getEmail().equalsIgnoreCase(currentUser.getEmail())
                                        && tasks.getToEmployee().getEmail().equals(currentUser.getEmail())) {
                                    Prefs.initPrefs(getApplicationContext(), "icon_egypt", Context.MODE_PRIVATE);
                                    Prefs.putBoolean(currentUser.getEmail() + "-tasks", true);
                                    try {
                                        if (SelectActivity.fragmentTag.equalsIgnoreCase("main_fragment"))
                                            MainFragment.setRedDotVisibility(getApplicationContext(), 1, true);
                                    } catch (Exception e) {
                                    }
                                    Intent intent = new Intent(getApplicationContext(), SelectActivity.class);
                                    intent.putExtra("fragment", "tasks");
                                    intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                    if (getIntent().getExtras().get("fragment") == null || getIntent().getExtras().get("fragment").equals("") && isLogin)
                                        showNotification(tasks.getTitle(), tasks.getDetails(), intent, "tasks", tasks.timestamp());

                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });


                    Query cFireQuery = ref.child("events").orderByChild("timestamp");
                    cFireQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (final DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                Events event = snapshot.getValue(Events.class);
                                if (event != null && event.getTimestamp() < AppData.employee.getLastEvent() &&
                                        !event.getFromEmployee().getEmail().equalsIgnoreCase(currentUser.getEmail())) {
                                    boolean isMine = false;
                                    for (Employee employee : event.getToEmployee()) {
                                        if (employee.getEmail().equalsIgnoreCase(AppData.employee.getEmail()))
                                            isMine = true;
                                    }
                                    if (isMine) {
                                        Prefs.initPrefs(getApplicationContext(), "icon_egypt", Context.MODE_PRIVATE);
                                        Prefs.putBoolean(currentUser.getEmail() + "-events", true);
                                        try {
                                            if (SelectActivity.fragmentTag.equalsIgnoreCase("main_fragment"))
                                                MainFragment.setRedDotVisibility(getApplicationContext(), 3, true);
                                        } catch (Exception e) {
                                        }
                                        Intent intent = new Intent(getApplicationContext(), SelectActivity.class);
                                        intent.putExtra("fragment", "events");
                                        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                        if (getIntent().getExtras().get("fragment") == null || getIntent().getExtras().get("fragment").equals("") && isLogin)
                                            showNotification(event.getTitle(), event.getDetails(), intent, "events", event.timestamp());

                                    }
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
            }
        }).start();

        // Util.createTaskDB(this,context,ref,"aaa", employee,employee, new Date().getTime(),new Date().getTime(),"bbb","company");

    }

    public void showNotification(String title, String body, Intent intent, String fragment, long timestamp) {
        int notificationId = (int) (timestamp + 1556318124);
        NotificationManager notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            StatusBarNotification[] notifications =
                    notificationManager.getActiveNotifications();
            for (StatusBarNotification notification : notifications) {

                if (notification.getId() == notificationId) {
                    return;
                }
            }
        }
        String name = "ICON Egypt Notifications";
        String channelId = "channel-03";
        String descriptionText = "ICON Egypt Notification Channel";
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addNextIntentWithParentStack(intent);

        PendingIntent resultPendingIntent = prepareIntent(context, fragment);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelId);
        builder.setDefaults(Notification.DEFAULT_ALL).setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
                .setColor(0x960000)
                .setContentTitle(title)
                .setContentIntent(resultPendingIntent)
                .setContentText(body)
                .setPriority(NotificationManager.IMPORTANCE_HIGH)
                .setAutoCancel(true)
                .setLights(0xffff0000, 300, 300)
                .setVibrate(new long[]{100, 100});

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = new NotificationChannel(channelId, name, importance);
            mChannel.setDescription(descriptionText);
            mChannel.enableLights(true);
            mChannel.setLightColor(0xffff0000);
            mChannel.setShowBadge(true);
            mChannel.setLockscreenVisibility(1);
            mChannel.enableVibration(true);
            mChannel.setVibrationPattern(new long[]{100, 100});
            if (notificationManager != null)
                notificationManager.createNotificationChannel(mChannel);
        }

        if (notificationManager != null)
            notificationManager.notify(notificationId, builder.build());
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (getSupportFragmentManager().getBackStackEntryCount() != 0) {
                getSupportFragmentManager().popBackStack();
            } else {
                int TIME_INTERVAL = 2000;
                if ((mBackPressed + TIME_INTERVAL > System.currentTimeMillis())) {
                    finish();
                    android.os.Process.killProcess(android.os.Process.myPid());
                } else {
                    mBackPressed = System.currentTimeMillis();
                    Toast.makeText(getBaseContext(), R.string.tap_again_to_exit, Toast.LENGTH_SHORT).show();
                }
            }
        }

        this.getSupportFragmentManager().addOnBackStackChangedListener(
                new FragmentManager.OnBackStackChangedListener() {
                    public void onBackStackChanged() {
                        Fragment current = getCurrentFragment();
                        if (current instanceof MainFragment) {
                            navigationView.setCheckedItem(R.id.nav_home);
                            fragmentTag = "main_fragment";
                        } else if (current instanceof ProfileFragment) {
                            navigationView.setCheckedItem(R.id.nav_profile);
                            fragmentTag = "profile_fragment";
                        } else if (current instanceof EmployeeFragment) {
                            navigationView.setCheckedItem(R.id.nav_employee);
                            fragmentTag = "employee_fragment";
                        } else if (current instanceof FormsFragment) {
                            navigationView.setCheckedItem(R.id.nav_form);
                            fragmentTag = "forms_fragment";
                        } else if (current instanceof TasksFragment) {
                            navigationView.setCheckedItem(R.id.nav_task);
                            fragmentTag = "tasks_fragment";
                        } else if (current instanceof NewsFragment) {
                            navigationView.setCheckedItem(R.id.nav_news);
                            fragmentTag = "news_fragment";
                        } else if (current instanceof EventsFragment) {
                            navigationView.setCheckedItem(R.id.nav_events);
                            fragmentTag = "events_fragment";
                        } /*else if (current instanceof ActivityFragment) {
                            //navigationView.setCheckedItem(R.id.nav_activity);
                            fragmentTag = "activity_fragment";
                        }else if (current instanceof FingerPrintFragment) {
                            //navigationView.setCheckedItem(R.id.nav_finger_print);
                            fragmentTag = "finger_print_fragment";
                        } */ else {
                            setFragment(new MainFragment(), false, "main_fragment");
                        }
                    }
                });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.profile, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify circle parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            signOut();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void signOut() {
        mAuth.signOut();
        currentUser = null;
        employee = null;
        Intent intent = new Intent(SelectActivity.this, LoginActivity.class);
        intent.putExtra("user", currentUser);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            setFragment(new MainFragment(), false, "main_fragment");
        } else if (id == R.id.nav_profile) {
            setFragment(new ProfileFragment(), true, "profile_fragment");
        } else if (id == R.id.nav_news) {
            setFragment(new NewsFragment(), true, "news_fragment");
        } else if (id == R.id.nav_form) {
            setFragment(new FormsFragment(), true, "forms_fragment");
        } else if (id == R.id.nav_employee) {
            setFragment(new EmployeeFragment(), true, "employee_fragment");
        } else if (id == R.id.nav_events) {
            setFragment(new EventsFragment(), true, "events_fragment");
        } else if (id == R.id.nav_task) {
            setFragment(new TasksFragment(), true, "tasks_fragment");
        } else if (id == R.id.nav_signout) {
            signOut();
        }

        /*else if (id == R.id.nav_finger_print) {
            setFragment(new FingerPrintFragment(), true, "finger_print_fragment");
        } else if (id == R.id.nav_activity) {
            setFragment(new ActivityFragment(), true, "activity_fragment");
        }*/

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void setFragment(final Fragment fragment, final boolean addToBackStack, final String tag) {
        if (!fragmentTag.equalsIgnoreCase(tag)) {
            if (isPaused)
                toDoQueue.add(new Runnable() {
                    @Override
                    public void run() {
                        setFragment(fragment, addToBackStack, tag);
                    }
                });
            else {
                try {
                    if (addToBackStack) {
                        getSupportFragmentManager()
                                .beginTransaction()
                                .addToBackStack(null)
                                .replace(R.id.fragmentContainer, fragment, fragment.toString())
                                .commit();
                    } else {
                        getSupportFragmentManager()
                                .beginTransaction()
                                .replace(R.id.fragmentContainer, fragment, fragment.toString())
                                .commit();
                    }
                    fragmentTag = tag;
                } catch (Exception e) {
                    // Skip
                }
            }
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();
        isPaused = false;
        for (int i = 0; i < toDoQueue.size(); i++) {
            toDoQueue.poll().run();
        }
    }

    @Override
    protected void onPause() {
        isPaused = true;
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        isPaused = false;
    }

    @Override
    public void finish() {
        super.finish();
    }

    public Fragment getCurrentFragment() {
        return this.getSupportFragmentManager().findFragmentById(R.id.fragmentContainer);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                setProfilePicture(result.getUri());
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }


    public void setProgressDialog() {

        int llPadding = 30;
        LinearLayout ll = new LinearLayout(this);
        ll.setOrientation(LinearLayout.HORIZONTAL);
        ll.setPadding(llPadding, llPadding, llPadding, llPadding);
        ll.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams llParam = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        llParam.gravity = Gravity.CENTER;
        ll.setLayoutParams(llParam);

        ProgressBar progressBar = new ProgressBar(this);
        progressBar.setIndeterminate(true);
        progressBar.setPadding(0, 0, llPadding, 0);
        progressBar.setLayoutParams(llParam);

        llParam = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        llParam.gravity = Gravity.CENTER;
        TextView tvText = new TextView(this);
        tvText.setText("Uploading ...");
        tvText.setTextColor(Color.parseColor("#000000"));
        tvText.setTextSize(20);
        tvText.setLayoutParams(llParam);

        ll.addView(progressBar);
        ll.addView(tvText);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setView(ll);

        dialog = builder.create();
        dialog.show();
        Window window = dialog.getWindow();
        if (window != null) {
            WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
            layoutParams.copyFrom(dialog.getWindow().getAttributes());
            layoutParams.width = LinearLayout.LayoutParams.WRAP_CONTENT;
            layoutParams.height = LinearLayout.LayoutParams.WRAP_CONTENT;
            dialog.getWindow().setAttributes(layoutParams);
        }
    }


    private void setProfilePicture(Uri uri) {
        setProgressDialog();
        final StorageReference riversRef = mainRef.child("images/" + uri.getLastPathSegment());
        UploadTask uploadTask = riversRef.putFile(uri);

        Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }

                // Continue with the task to get the download URL
                return riversRef.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    try {
                        String old = employee.getImage().substring(employee.getImage().indexOf("/o/") + 3, employee.getImage().lastIndexOf("?alt")).replace("%2F", "/");

                        StorageReference desertRef = mainRef.child(old);
                        // Delete the file
                        desertRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                // File deleted successfully
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {
                                // Uh-oh, an error occurred!
                            }
                        });
                    } catch (Exception e) {
                    }
                    Uri downloadUri = task.getResult();
                    employee.setImage(downloadUri.toString());
                    View hView = navigationView.getHeaderView(0);
                    AvatarView profileImage = hView.findViewById(R.id.imageView);
                    Picasso.get().load(employee.getImage()).placeholder(R.drawable.profile_placeholder).into(profileImage);
                    ((ProfileFragment) getCurrentFragment()).update();
                    if (dialog != null)
                        dialog.dismiss();
                } else {
                    // Handle failures
                    // ...
                }
            }
        });
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent != null && intent.getExtras() != null && intent.getExtras().get("fragment") != null && !intent.getExtras().get("fragment").equals("")) {
            if (currentUser != null) {
                if (((String) intent.getExtras().get("fragment")).equalsIgnoreCase("news")) {
                    setFragment(new NewsFragment(), true, "news_fragment");
                } else if (((String) intent.getExtras().get("fragment")).equalsIgnoreCase("tasks")) {
                    setFragment(new TasksFragment(), true, "tasks_fragment");
                } else if (((String) intent.getExtras().get("fragment")).equalsIgnoreCase("events")) {
                    setFragment(new TasksFragment(), true, "events_fragment");
                }
            }
        }
    }
}
