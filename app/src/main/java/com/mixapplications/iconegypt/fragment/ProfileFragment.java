package com.mixapplications.iconegypt.fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.mixapplications.iconegypt.R;
import com.mixapplications.iconegypt.activity.LoginActivity;
import com.mixapplications.iconegypt.activity.SelectActivity;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import agency.tango.android.avatarview.views.AvatarView;

import static com.mixapplications.iconegypt.models.AppData.currentUser;
import static com.mixapplications.iconegypt.models.AppData.employee;


public class ProfileFragment extends Fragment implements
        AdapterView.OnItemSelectedListener {
    AlertDialog progressDialog;
    FirebaseAuth mAuth;
    Context context;
    private FragmentActivity activity;
    private String[] status = {"Company", "Errand", "Holiday"};
    private AvatarView avatarView;
    private DatabaseReference ref;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.fragment_profile, container, false);
        context = getContext();
        activity = getActivity();
        getActivity().setTitle("Profile");
        TextView textLocation = layout.findViewById(R.id.textLocation);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        ref = database.getReference();
        mAuth = FirebaseAuth.getInstance();

        avatarView = layout.findViewById(R.id.imageView);
        Picasso.get().load(employee.getImage().equals("") ? null : employee.getImage()).placeholder(R.drawable.profile_placeholder).into(avatarView);
        Button btn_add_image = layout.findViewById(R.id.btn_add_image);
        Button btn_remove_image = layout.findViewById(R.id.btn_remove_image);
        Button btn_change_password = layout.findViewById(R.id.btn_change_password);
        btn_add_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setAspectRatio(1, 1)
                        .start(activity);
            }
        });

        btn_remove_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                employee.setImage("null");
                try {

                    FirebaseStorage firebaseStorage;
                    StorageReference mainRef;
                    FirebaseApp customApp = FirebaseApp.initializeApp(context);
                    firebaseStorage = FirebaseStorage.getInstance(customApp);
                    mainRef = firebaseStorage.getReference();
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
                update();
            }
        });


        if (employee.getStatus().equalsIgnoreCase("company"))
            textLocation.setText(status[0]);
        if (employee.getStatus().equalsIgnoreCase("errand"))
            textLocation.setText(status[1]);
        if (employee.getStatus().equalsIgnoreCase("holiday"))
            textLocation.setText(status[2]);


        EditText edit_name = layout.findViewById(R.id.edit_name);
        EditText edit_phone = layout.findViewById(R.id.edit_phone);

        edit_name.setText(employee.getName());
        edit_phone.setText(employee.getPhone());
        edit_name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                employee.setName(s.toString());
                ((SelectActivity) activity).txt_name.setText(employee.getName());
                update();
            }
        });
        edit_phone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                employee.setPhone(s.toString());
                ((SelectActivity) activity).txt_name.setText(employee.getPhone());
                update();
            }
        });


        btn_change_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                LayoutInflater inflater = getLayoutInflater();
                View alertLayout = inflater.inflate(R.layout.password_dialog, null);
                final EditText et_oldPassword = alertLayout.findViewById(R.id.et_oldPassword);
                final EditText et_newPassword = alertLayout.findViewById(R.id.et_newPassword);
                final EditText et_confirm_password = alertLayout.findViewById(R.id.et_confirm_password);
                final LinearLayout error_layout = alertLayout.findViewById(R.id.layout_error_pass);
                final LinearLayout layout_match_pass = alertLayout.findViewById(R.id.layout_match_pass);
                final LinearLayout layout_enter_pass = alertLayout.findViewById(R.id.layout_enter_pass);
                final LinearLayout layout_password_length = alertLayout.findViewById(R.id.layout_password_length);
                error_layout.setVisibility(View.GONE);
                layout_match_pass.setVisibility(View.GONE);
                layout_enter_pass.setVisibility(View.GONE);
                layout_password_length.setVisibility(View.GONE);


                final AlertDialog passDialog = new AlertDialog.Builder(context)
                        .setView(alertLayout)
                        .setTitle("Change Password")
                        .setPositiveButton(android.R.string.ok, null) //Set to null. We override the onclick
                        .setNegativeButton(android.R.string.cancel, null)
                        .setCancelable(false)
                        .create();

                passDialog.setOnShowListener(new DialogInterface.OnShowListener() {

                    @Override
                    public void onShow(DialogInterface dialogInterface) {

                        Button positiveButton = passDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                        Button negativeeButton = passDialog.getButton(AlertDialog.BUTTON_NEGATIVE);
                        positiveButton.setOnClickListener(new View.OnClickListener() {

                            @Override
                            public void onClick(View view) {

                                String old_pass = et_oldPassword.getText().toString();
                                final String new_pass = et_newPassword.getText().toString();
                                String confirm_password = et_confirm_password.getText().toString();
                                layout_enter_pass.setVisibility(View.GONE);
                                layout_match_pass.setVisibility(View.GONE);
                                layout_password_length.setVisibility(View.GONE);
                                error_layout.setVisibility(View.GONE);
                                if (old_pass.equals("") || (!new_pass.equals(confirm_password)) || new_pass.length() < 6) {
                                    if (old_pass.equals("")) {
                                        layout_enter_pass.setVisibility(View.VISIBLE);
                                    }
                                    if (!new_pass.equals(confirm_password)) {
                                        layout_match_pass.setVisibility(View.VISIBLE);
                                    }
                                    if (new_pass.length() < 6) {
                                        layout_password_length.setVisibility(View.VISIBLE);
                                    }
                                } else {
                                    layout_match_pass.setVisibility(View.GONE);
                                    layout_enter_pass.setVisibility(View.GONE);
                                    layout_password_length.setVisibility(View.GONE);
                                    error_layout.setVisibility(View.GONE);
                                    setProgressDialog();
                                    mAuth.signInWithEmailAndPassword(employee.getEmail(), old_pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                        @Override
                                        public void onComplete(@NonNull Task<AuthResult> task) {

                                            if (task.isSuccessful()) {
                                                FirebaseUser user = mAuth.getCurrentUser();
                                                user.updatePassword(new_pass)
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if (task.isSuccessful()) {
                                                                    if (passDialog != null)
                                                                        passDialog.dismiss();
                                                                    currentUser = null;
                                                                    employee = null;
                                                                    Intent intent = new Intent(context, LoginActivity.class);
                                                                    intent.putExtra("user", currentUser);
                                                                    if (currentUser != null) {
                                                                        intent.putExtra("email", employee.getEmail());
                                                                        intent.putExtra("image", employee.getImage());
                                                                        intent.putExtra("name", employee.getName());
                                                                        intent.putExtra("phone", employee.getPhone());
                                                                        intent.putExtra("status", employee.getStatus());
                                                                        intent.putExtra("type", employee.getType());
                                                                    }
                                                                    startActivity(intent);
                                                                    activity.finish();
                                                                } else {
                                                                    Toast.makeText(context, "An error occurred while changing password.", Toast.LENGTH_LONG).show();
                                                                }
                                                            }
                                                        });

                                            } else {
                                                if (progressDialog != null)
                                                    progressDialog.dismiss();
                                                error_layout.setVisibility(View.VISIBLE);
                                            }
                                        }
                                    });
                                    // [END sign_in_with_email]
                                }
                            }
                        });

                        negativeeButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                passDialog.dismiss();

                            }
                        });
                    }
                });
                passDialog.show();
            }
        });
        return layout;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public void update() {
        Picasso.get().load(employee.getImage().equals("") ? null : employee.getImage()).placeholder(R.drawable.profile_placeholder).into(avatarView);
        Query query = ref.child("employees").orderByChild("email").equalTo(employee.getEmail());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    snapshot.getRef().setValue(employee);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void setProgressDialog() {

        int llPadding = 30;
        LinearLayout ll = new LinearLayout(context);
        ll.setOrientation(LinearLayout.HORIZONTAL);
        ll.setPadding(llPadding, llPadding, llPadding, llPadding);
        ll.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams llParam = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        llParam.gravity = Gravity.CENTER;
        ll.setLayoutParams(llParam);

        ProgressBar progressBar = new ProgressBar(context);
        progressBar.setIndeterminate(true);
        progressBar.setPadding(0, 0, llPadding, 0);
        progressBar.setLayoutParams(llParam);

        llParam = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        llParam.gravity = Gravity.CENTER;
        TextView tvText = new TextView(context);
        tvText.setText("Please Wait...");
        tvText.setTextColor(Color.parseColor("#000000"));
        tvText.setTextSize(20);
        tvText.setLayoutParams(llParam);

        ll.addView(progressBar);
        ll.addView(tvText);

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setCancelable(false);
        builder.setView(ll);

        progressDialog = builder.create();
        progressDialog.show();
        Window window = progressDialog.getWindow();
        if (window != null) {
            WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
            layoutParams.copyFrom(progressDialog.getWindow().getAttributes());
            layoutParams.width = LinearLayout.LayoutParams.WRAP_CONTENT;
            layoutParams.height = LinearLayout.LayoutParams.WRAP_CONTENT;
            progressDialog.getWindow().setAttributes(layoutParams);
        }
    }

}
