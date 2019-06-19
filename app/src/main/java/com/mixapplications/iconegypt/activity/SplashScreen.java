package com.mixapplications.iconegypt.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.crashlytics.android.Crashlytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.mixapplications.iconegypt.R;
import com.mixapplications.iconegypt.models.Employee;
import com.mixapplications.iconegypt.models.EmployeeFirebaseNode;
import com.mixapplications.iconegypt.models.InternetConnection;

import io.fabric.sdk.android.Fabric;

import static com.mixapplications.iconegypt.models.AppData.allEmployeeNode;
import static com.mixapplications.iconegypt.models.AppData.currentUser;
import static com.mixapplications.iconegypt.models.AppData.employee;
import static com.mixapplications.iconegypt.models.AppData.myEmployeeNode;

public class SplashScreen extends AppCompatActivity {
    FirebaseDatabase mDataBase;
    DatabaseReference ref;
    Animation translateScale;
    ImageView imageView;
    Context context;
    boolean loaded;
    boolean treeLoaded;
    boolean called;
    TextView txt_error;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_splash_screen);
        txt_error = findViewById(R.id.txt_error);
        context = this;
        loaded = false;
        treeLoaded = false;
        called = false;
        if (!InternetConnection.checkConnection(this))
            Toast.makeText(this, "Network Connection Error...", Toast.LENGTH_LONG).show();
        mAuth = FirebaseAuth.getInstance();
        mDataBase = FirebaseDatabase.getInstance();
        ref = mDataBase.getReference();
        //ref.child("employees_tree").child("tree").setValue(Util.testNode());
        imageView = findViewById(R.id.header_icon);
        Animation hold = AnimationUtils.loadAnimation(this, R.anim.hold);
        translateScale = AnimationUtils.loadAnimation(this, R.anim.translate_scale);
        translateScale.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                imageView.clearAnimation();
                if (!called) {
                    called = true;
                    Intent intent = new Intent(SplashScreen.this, LoginActivity.class);
                    intent.putExtra("user", currentUser);
                    if (currentUser != null) {
                        intent.putExtra("email", employee.getEmail());
                        intent.putExtra("image", employee.getImage());
                        intent.putExtra("name", employee.getName());
                        intent.putExtra("phone", employee.getPhone());
                        intent.putExtra("status", employee.getStatus());
                        intent.putExtra("type", employee.getType());
                        intent.putExtra("lastNews", employee.getLastNews());
                        intent.putExtra("lastTask", employee.getLastTask());
                        intent.putExtra("lastEvent", employee.getLastEvent());
                    }
                    startActivity(intent);
                    finish();
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        hold.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if (!loaded || !treeLoaded) {
                    Toast.makeText(context, "Can not connect to server, check connection then reopen app", Toast.LENGTH_LONG).show();
                    txt_error.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        imageView.startAnimation(hold);
        checkLogin();

    }

    public void checkLogin() {
        currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            loadAccount(currentUser);
        } else {
            if (imageView != null) {
                imageView.clearAnimation();
                imageView.startAnimation(translateScale);
            }
        }
    }


    public void loadAccount(final FirebaseUser user) {
        Query fireQuery = ref.child("employees").orderByChild("email").equalTo(user.getEmail());
        fireQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot1) {
                for (DataSnapshot snapshot1 : dataSnapshot1.getChildren()) {
                    employee = snapshot1.getValue(Employee.class);
                    txt_error.setVisibility(View.INVISIBLE);


                    Query afireQuery = ref.child("employees_tree");
                    afireQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (DataSnapshot dataSnapshot2 : dataSnapshot.getChildren()) {
                                String nodeJson = dataSnapshot2.getValue(String.class);
                                Gson gson = new Gson();
                                EmployeeFirebaseNode employeeFirebaseNode = gson.fromJson(nodeJson, EmployeeFirebaseNode.class);
                                allEmployeeNode = employeeFirebaseNode.toEmployeeNode();
                                myEmployeeNode = allEmployeeNode.findChild(employee.getEmail());
                                treeLoaded = true;
                                imageView.clearAnimation();
                                imageView.startAnimation(translateScale);
                                break;
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                    loaded = true;
                    break;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
