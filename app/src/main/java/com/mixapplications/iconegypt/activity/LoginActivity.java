package com.mixapplications.iconegypt.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Space;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.HttpsCallableResult;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;
import com.mixapplications.iconegypt.R;
import com.mixapplications.iconegypt.models.Employee;
import com.mixapplications.iconegypt.models.EmployeeFirebaseNode;
import com.mixapplications.iconegypt.models.Prefs;
import com.squareup.picasso.Picasso;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import agency.tango.android.avatarview.views.AvatarView;

import static com.mixapplications.iconegypt.models.AppData.allEmployeeNode;
import static com.mixapplications.iconegypt.models.AppData.currentUser;
import static com.mixapplications.iconegypt.models.AppData.employee;
import static com.mixapplications.iconegypt.models.AppData.myEmployeeNode;

public class LoginActivity extends AppCompatActivity {
    FirebaseAuth mAuth;
    Button btnLogin;
    AlertDialog dialog;
    boolean doubleBackToExitPressedOnce = false;
    FirebaseDatabase mDataBase;
    DatabaseReference ref;
    TextView username_txt;
    Button btn_continue;
    Button btn_logout;
    AvatarView profileImage;
    int passTryCount = 0;
    long unlockTime = 0;
    boolean lock = false;
    FirebaseFunctions mFunctions;

    private static String timeConversion(int totalSeconds) {

        final int MINUTES_IN_AN_HOUR = 60;
        final int SECONDS_IN_A_MINUTE = 60;

        int seconds = totalSeconds % SECONDS_IN_A_MINUTE;
        int totalMinutes = totalSeconds / SECONDS_IN_A_MINUTE;
        int minutes = totalMinutes % MINUTES_IN_AN_HOUR;
        int hours = totalMinutes / MINUTES_IN_AN_HOUR;

        return minutes + "min : " + seconds + " sec";
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        overridePendingTransition(0, 0);
        View relativeLayout = findViewById(R.id.login_container);
        Animation animation = AnimationUtils.loadAnimation(this, android.R.anim.fade_in);
        relativeLayout.startAnimation(animation);
        profileImage = findViewById(R.id.imageView);

        username_txt = findViewById(R.id.username_txt);
        btn_continue = findViewById(R.id.btn_continue);
        btn_logout = findViewById(R.id.btn_logout);
        final Space space_login = findViewById(R.id.space_login);
        final Space space_login1 = findViewById(R.id.space_login1);
        final EditText userName = findViewById(R.id.username_edit_text);
        final EditText passWord = findViewById(R.id.password_edit_text);
        btnLogin = findViewById(R.id.btn_login);

        mAuth = FirebaseAuth.getInstance();
        currentUser = (FirebaseUser) getIntent().getExtras().get("user");
        mDataBase = FirebaseDatabase.getInstance();
        ref = mDataBase.getReference();
        mFunctions = FirebaseFunctions.getInstance();

        Prefs.initPrefs(getApplicationContext(), "icon_egypt", Context.MODE_PRIVATE);

        unlockTime = Prefs.getLong("unlockTime", 0);
        lock = Prefs.getBoolean("lock", false);

        if (currentUser != null) {
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
        }


        if (currentUser != null) {
            space_login.setVisibility(View.GONE);
            space_login1.setVisibility(View.GONE);
            userName.setVisibility(View.GONE);
            passWord.setVisibility(View.GONE);
            btnLogin.setVisibility(View.GONE);
            username_txt.setVisibility(View.VISIBLE);
            btn_continue.setVisibility(View.VISIBLE);
            btn_logout.setVisibility(View.VISIBLE);
            username_txt.setText(employee.getName());
            if (employee.getImage() != null && !employee.getImage().equals("")) {
                Picasso.get().load(employee.getImage()).placeholder(R.drawable.profile_placeholder).into(profileImage);
            }
            profileImage.setVisibility(View.VISIBLE);
        } else {
            space_login.setVisibility(View.VISIBLE);
            space_login1.setVisibility(View.VISIBLE);
            userName.setVisibility(View.VISIBLE);
            passWord.setVisibility(View.VISIBLE);
            btnLogin.setVisibility(View.VISIBLE);
            username_txt.setVisibility(View.GONE);
            btn_continue.setVisibility(View.GONE);
            btn_logout.setVisibility(View.GONE);
            profileImage.setVisibility(View.GONE);
        }
        btn_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signOut();
                space_login.setVisibility(View.VISIBLE);
                space_login1.setVisibility(View.VISIBLE);
                userName.setVisibility(View.VISIBLE);
                passWord.setVisibility(View.VISIBLE);
                btnLogin.setVisibility(View.VISIBLE);
                username_txt.setVisibility(View.GONE);
                btn_continue.setVisibility(View.GONE);
                btn_logout.setVisibility(View.GONE);
                profileImage.setVisibility(View.GONE);
                employee = null;
                currentUser = null;
            }
        });
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                unlockTime = Prefs.getLong("unlockTime", 0);
                lock = Prefs.getBoolean("lock", false);
                if (lock) {

                    getTimeStamp().addOnCompleteListener(new OnCompleteListener<String>() {
                        @Override
                        public void onComplete(@NonNull Task<String> task) {
                            if (task.isSuccessful()) {
                                long curTime = Long.parseLong(task.getResult() != null ? task.getResult() : getCurrentTime() + "");
                                int dif = (int) (unlockTime - curTime);
                                if (dif <= 0) {
                                    unlockTime = 0;
                                    lock = false;
                                    Prefs.putLong("unlockTime", unlockTime);
                                    Prefs.putBoolean("lock", lock);

                                    if (userName.getText().toString() == null || userName.getText().toString().trim().equals("")) {
                                        Toast.makeText(LoginActivity.this, "Please Enter User Name",
                                                Toast.LENGTH_SHORT).show();
                                    } else if (passWord.getText().toString() == null || passWord.getText().toString().trim().equals("")) {
                                        Toast.makeText(LoginActivity.this, "Please Enter Password",
                                                Toast.LENGTH_SHORT).show();
                                    } else {
                                        String email = userName.getText().toString().trim();
                                        email = email.contains("@") ? email : email + "@iconegypt.com";
                                        String pass = passWord.getText().toString().trim();
                                        signin(email, pass);
                                    }
                                } else {
                                    lock = true;
                                    Toast.makeText(LoginActivity.this, "No attempts left, Retry after " + timeConversion(dif / 1000) + " min.",
                                            Toast.LENGTH_SHORT).show();
                                }

                            }
                        }
                    });
                } else {
                    if (userName.getText().toString() == null || userName.getText().toString().trim().equals("")) {
                        Toast.makeText(LoginActivity.this, "Please Enter User Name",
                                Toast.LENGTH_SHORT).show();
                    } else if (passWord.getText().toString() == null || passWord.getText().toString().trim().equals("")) {
                        Toast.makeText(LoginActivity.this, "Please Enter Password",
                                Toast.LENGTH_SHORT).show();
                    } else {
                        String email = userName.getText().toString().trim();
                        email = email.contains("@") ? email : email + "@iconegypt.com";
                        String pass = passWord.getText().toString().trim();
                        signin(email, pass);
                    }
                }
            }
        });

        btn_continue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseMessaging.getInstance().subscribeToTopic("notifications_news");
                FirebaseMessaging.getInstance().subscribeToTopic("notifications_tasks");
                FirebaseMessaging.getInstance().subscribeToTopic("notifications_events");
                Query afireQuery = ref.child("employees_tree");
                afireQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot dataSnapshot2 : dataSnapshot.getChildren()) {
                            Intent intent = new Intent(LoginActivity.this, SelectActivity.class);
                            intent.putExtra("email", employee.getEmail());
                            intent.putExtra("image", employee.getImage());
                            intent.putExtra("name", employee.getName());
                            intent.putExtra("phone", employee.getPhone());
                            intent.putExtra("status", employee.getStatus());
                            intent.putExtra("type", employee.getType());
                            intent.putExtra("lastNews", employee.getLastNews());
                            intent.putExtra("lastTask", employee.getLastTask());
                            intent.putExtra("lastEvent", employee.getLastEvent());
                            intent.putExtra("isLogin", false);

                            String nodeJson = dataSnapshot2.getValue(String.class);
                            Gson gson = new Gson();
                            EmployeeFirebaseNode employeeFirebaseNode = gson.fromJson(nodeJson, EmployeeFirebaseNode.class);
                            allEmployeeNode = employeeFirebaseNode.toEmployeeNode();
                            myEmployeeNode = allEmployeeNode.findChild(employee.getEmail());
                            intent.putExtra("nodeJson", nodeJson);
                            startActivity(intent);
                            finish();
                            break;
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });

    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        currentUser = mAuth.getCurrentUser();
        //updateUI(employee);
    }

    private void signin(String email, String password) {

        btnLogin.setEnabled(false);
        setProgressDialog();
        // [START sign_in_with_email]
        mAuth.signInWithEmailAndPassword(email, password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                currentUser = authResult.getUser();
                loadAccount();
                if (dialog != null)
                    dialog.dismiss();
                btnLogin.setEnabled(true);
            }
        }).addOnCanceledListener(new OnCanceledListener() {
            @Override
            public void onCanceled() {
                Toast.makeText(getApplicationContext(), "Auth Canceled.", Toast.LENGTH_LONG).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    Toast.makeText(getApplicationContext(), "Invalid Password", Toast.LENGTH_LONG).show();
                    btnLogin.setEnabled(true);
                    passTryCount++;
                    if (passTryCount >= 3) {
                        getTimeStamp().addOnCompleteListener(new OnCompleteListener<String>() {
                            @Override
                            public void onComplete(@NonNull Task<String> task) {
                                if (task.isSuccessful()) {
                                    unlockTime = Long.parseLong(task.getResult() != null ? task.getResult() : getCurrentTime() + "") + 60000;
                                    lock = true;
                                    passTryCount = 0;
                                    Prefs.putLong("unlockTime", unlockTime);
                                    Prefs.putBoolean("lock", lock);
                                }
                            }
                        });
                    }
                } else if (e instanceof FirebaseAuthInvalidUserException) {

                    String errorCode =
                            ((FirebaseAuthInvalidUserException) e).getErrorCode();

                    if (errorCode.equals("ERROR_USER_NOT_FOUND")) {
                        // notifyUser("No matching account found");
                        Toast.makeText(getApplicationContext(), "No matching account found", Toast.LENGTH_LONG).show();
                    } else if (errorCode.equals("ERROR_USER_DISABLED")) {
                        //notifyUser("User account has been disabled");
                        Toast.makeText(getApplicationContext(), "User account has been disabled", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(getApplicationContext(), e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                        // notifyUser(e.getLocalizedMessage());
                    }
                }
                if (dialog != null)
                    dialog.dismiss();
                btnLogin.setEnabled(true);
            }
        });
        // [END sign_in_with_email]
    }

    private void signOut() {
        mAuth.signOut();
        currentUser = null;
        employee = null;
    }


    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            finish();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }

    public void loadAccount() {
        Query fireQuery = ref.child("employees").orderByChild("email").equalTo(currentUser.getEmail());
        fireQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot1 : dataSnapshot.getChildren()) {
                    employee = snapshot1.getValue(Employee.class);
                    FirebaseMessaging.getInstance().subscribeToTopic("notifications_news");
                    FirebaseMessaging.getInstance().subscribeToTopic("notifications_tasks");
                    FirebaseMessaging.getInstance().subscribeToTopic("notifications_events");

                    Query afireQuery = ref.child("employees_tree");
                    afireQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (DataSnapshot dataSnapshot2 : dataSnapshot.getChildren()) {
                                Intent intent = new Intent(LoginActivity.this, SelectActivity.class);
                                intent.putExtra("email", employee.getEmail());
                                intent.putExtra("image", employee.getImage());
                                intent.putExtra("name", employee.getName());
                                intent.putExtra("phone", employee.getPhone());
                                intent.putExtra("status", employee.getStatus());
                                intent.putExtra("type", employee.getType());
                                intent.putExtra("lastNews", employee.getLastNews());
                                intent.putExtra("lastTask", employee.getLastTask());
                                intent.putExtra("lastEvent", employee.getLastEvent());
                                intent.putExtra("isLogin", true);

                                String nodeJson = dataSnapshot2.getValue(String.class);
                                Gson gson = new Gson();
                                EmployeeFirebaseNode employeeFirebaseNode = gson.fromJson(nodeJson, EmployeeFirebaseNode.class);
                                allEmployeeNode = employeeFirebaseNode.toEmployeeNode();
                                myEmployeeNode = allEmployeeNode.findChild(employee.getEmail());
                                intent.putExtra("nodeJson", nodeJson);
                                startActivity(intent);
                                finish();
                                break;
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                    break;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
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
        tvText.setText("Singing in, please wait.");
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

    public long getCurrentTime() {
        return new Date().getTime();
    }


    private Task<String> getTimeStamp() {
        // Create the arguments to the callable function.
        Map<String, Object> data = new HashMap<>();
        data.put("text", "");
        data.put("push", true);

        return mFunctions
                .getHttpsCallable("getTime")
                .call(data)
                .continueWith(new Continuation<HttpsCallableResult, String>() {
                    @Override
                    public String then(@NonNull Task<HttpsCallableResult> task) throws Exception {
                        // This continuation runs on either success or failure, but if the task
                        // has failed then getResult() will throw an Exception which will be
                        // propagated down.
                        String result = String.valueOf(task.getResult().getData());
                        return result;
                    }
                });
    }
}
