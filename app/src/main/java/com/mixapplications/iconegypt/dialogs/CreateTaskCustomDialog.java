package com.mixapplications.iconegypt.dialogs;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.kunzisoft.switchdatetime.SwitchDateTimeDialogFragment;
import com.mixapplications.iconegypt.R;
import com.mixapplications.iconegypt.models.AppData;
import com.mixapplications.iconegypt.models.Employee;
import com.mixapplications.iconegypt.models.EmployeeNode;
import com.mixapplications.iconegypt.models.Tasks;
import com.mixapplications.iconegypt.models.Util;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

import static io.fabric.sdk.android.Fabric.TAG;

public class CreateTaskCustomDialog extends AlertDialog implements
        android.view.View.OnClickListener {

    public AppCompatActivity activity;
    public Button cancel;
    public Button share;
    Context context;
    List<EmployeeNode> employeeNode;
    List<Employee> listOfEmployee = new ArrayList<>();
    List<String> emNames;
    private EditText tvDetails;
    private EditText tvTitle;
    private Spinner spinner_to;
    private EditText tvStartDate;
    private EditText tvEndDate;
    private Tasks task;
    private DatabaseReference ref;

    public CreateTaskCustomDialog(DatabaseReference ref, AppCompatActivity activity, Context context, Tasks task) {
        super(activity);
        this.task = task;
        this.activity = activity;
        this.ref = ref;
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.create_task_custom_dialog);

        cancel = findViewById(R.id.btn_cancel);
        cancel.setOnClickListener(this);
        share = findViewById(R.id.btn_share);
        share.setOnClickListener(this);

        tvTitle = findViewById(R.id.tvTitle);
        tvDetails = findViewById(R.id.tvDetails);
        tvStartDate = findViewById(R.id.tvStartDate);
        tvEndDate = findViewById(R.id.tvEndDate);
        spinner_to = findViewById(R.id.tvTo);
        tvTitle.requestFocus();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        setCancelable(false);

        employeeNode = AppData.myEmployeeNode.returnAllNodes(AppData.myEmployeeNode);
        for (EmployeeNode node : employeeNode) {
            Query fireQuery = ref.child("employees").orderByChild("email").equalTo(node.getEmail());
            fireQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot1) {
                    for (DataSnapshot snapshot1 : dataSnapshot1.getChildren()) {
                        Employee em = snapshot1.getValue(Employee.class);
                        listOfEmployee.add(em);
                        emNames = new ArrayList<>();
                        for (Employee e : listOfEmployee) {
                            if (!e.getName().equals(""))
                                emNames.add(e.getName());
                            else
                                emNames.add(e.getEmail());
                        }
                    }
                    ArrayAdapter employeeAdapter = new ArrayAdapter(context, android.R.layout.simple_spinner_item, emNames);
                    spinner_to.setAdapter(employeeAdapter);
                    if (task != null) {
                        int index = 0;
                        for (int i = 0; i < listOfEmployee.size(); i++) {
                            if (listOfEmployee.get(i).getEmail().equalsIgnoreCase(task.getToEmployee().getEmail())) {
                                index = i;
                                break;
                            }
                        }
                        spinner_to.setSelection(index);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

        tvStartDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Initialize
                final SwitchDateTimeDialogFragment dateTimeDialogFragment = SwitchDateTimeDialogFragment.newInstance(
                        "Pick Start Date",
                        "OK",
                        "Cancel"
                );

// Assign values
                dateTimeDialogFragment.startAtCalendarView();
                dateTimeDialogFragment.set24HoursMode(false);
                dateTimeDialogFragment.setMinimumDateTime(new GregorianCalendar(2018, Calendar.JANUARY, 1).getTime());
                dateTimeDialogFragment.setMaximumDateTime(new GregorianCalendar(2030, Calendar.DECEMBER, 31).getTime());
                dateTimeDialogFragment.setDefaultDateTime(new GregorianCalendar(new GregorianCalendar().get(Calendar.YEAR),
                        new GregorianCalendar().get(Calendar.MONTH),
                        new GregorianCalendar().get(Calendar.DAY_OF_MONTH), new GregorianCalendar().get(Calendar.HOUR_OF_DAY),
                        new GregorianCalendar().get(Calendar.MINUTE)).getTime());

// Define new day and month format
                try {
                    dateTimeDialogFragment.setSimpleDateMonthAndDayFormat(new SimpleDateFormat("yyyy-MM-dd hh:mm a", new Locale("en", "EG")));
                } catch (SwitchDateTimeDialogFragment.SimpleDateMonthAndDayFormatException e) {
                    Log.e(TAG, e.getMessage());
                }

// Set listener
                dateTimeDialogFragment.setOnButtonClickListener(new SwitchDateTimeDialogFragment.OnButtonClickListener() {
                    @Override
                    public void onPositiveButtonClick(Date date) {
                        // Date is get on positive button click
                        // Do something
                        tvStartDate.setText(Util.getDateCurrentTimeZone(date.getTime()));
                    }

                    @Override
                    public void onNegativeButtonClick(Date date) {
                        // Date is get on negative button click
                        dateTimeDialogFragment.dismiss();
                    }
                });

// Show
                dateTimeDialogFragment.show(activity.getSupportFragmentManager(), "dialog_time");
            }
        });

        tvEndDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Initialize
                final SwitchDateTimeDialogFragment dateTimeDialogFragment = SwitchDateTimeDialogFragment.newInstance(
                        "Pick End Date",
                        "OK",
                        "Cancel"
                );

// Assign values
                dateTimeDialogFragment.startAtCalendarView();
                dateTimeDialogFragment.set24HoursMode(false);
                dateTimeDialogFragment.setMinimumDateTime(new GregorianCalendar(2018, Calendar.JANUARY, 1).getTime());
                dateTimeDialogFragment.setMaximumDateTime(new GregorianCalendar(2030, Calendar.DECEMBER, 31).getTime());
                dateTimeDialogFragment.setDefaultDateTime(new GregorianCalendar(new GregorianCalendar().get(Calendar.YEAR),
                        new GregorianCalendar().get(Calendar.MONTH),
                        new GregorianCalendar().get(Calendar.DAY_OF_MONTH), new GregorianCalendar().get(Calendar.HOUR_OF_DAY),
                        new GregorianCalendar().get(Calendar.MINUTE)).getTime());

// Define new day and month format
                try {
                    dateTimeDialogFragment.setSimpleDateMonthAndDayFormat(new SimpleDateFormat("yyyy-MM-dd hh:mm a", new Locale("en", "EG")));
                } catch (SwitchDateTimeDialogFragment.SimpleDateMonthAndDayFormatException e) {
                    Log.e(TAG, e.getMessage());
                }

// Set listener
                dateTimeDialogFragment.setOnButtonClickListener(new SwitchDateTimeDialogFragment.OnButtonClickListener() {
                    @Override
                    public void onPositiveButtonClick(Date date) {
                        // Date is get on positive button click
                        // Do something
                        tvEndDate.setText(Util.getDateCurrentTimeZone(date.getTime()));
                    }

                    @Override
                    public void onNegativeButtonClick(Date date) {
                        // Date is get on negative button click
                        dateTimeDialogFragment.dismiss();
                    }
                });

// Show
                dateTimeDialogFragment.show(activity.getSupportFragmentManager(), "dialog_time");
            }
        });

        if (task != null) {
            tvTitle.setText(task.getTitle());
            tvDetails.setText(task.getDetails());
            tvStartDate.setText(Util.getDateCurrentTimeZone(task.getFromDate()));
            tvEndDate.setText(Util.getDateCurrentTimeZone(task.getToDate()));
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_cancel:
                dismiss();
                break;
            case R.id.btn_share:
                if (tvTitle.getText().toString().equals("")) {
                    Toast.makeText(context, "Title must not be empty", Toast.LENGTH_LONG).show();
                } else if (spinner_to.getSelectedItemPosition() == 0) {
                    Toast.makeText(context, "Can't send task to yourself...", Toast.LENGTH_LONG).show();
                } else if (tvStartDate.getText().toString().equals("")) {
                    Toast.makeText(context, "Start Date must not be empty", Toast.LENGTH_LONG).show();
                } else if (tvEndDate.getText().toString().equals("")) {
                    Toast.makeText(context, "End Date must not be empty", Toast.LENGTH_LONG).show();
                } else if (Util.timeZoneToTimestamp(tvStartDate.getText().toString()) >= Util.timeZoneToTimestamp(tvEndDate.getText().toString())) {
                    Toast.makeText(context, "End Date must be after Start Date", Toast.LENGTH_LONG).show();
                } else if (tvDetails.getText().toString().equals("")) {
                    Toast.makeText(context, "Task Details must not be empty", Toast.LENGTH_LONG).show();
                } else {
                    if (task == null) {
                        Util.createTaskDB(activity, context, ref, tvTitle.getText().toString(),
                                AppData.employee, listOfEmployee.get(spinner_to.getSelectedItemPosition()),
                                Util.timeZoneToTimestamp(tvStartDate.getText().toString()), Util.timeZoneToTimestamp(tvEndDate.getText().toString())
                                , tvDetails.getText().toString());
                    } else {
                        Util.editTaskDB(activity, context, ref, task, tvTitle.getText().toString(),
                                AppData.employee, listOfEmployee.get(spinner_to.getSelectedItemPosition()),
                                Util.timeZoneToTimestamp(tvStartDate.getText().toString()), Util.timeZoneToTimestamp(tvEndDate.getText().toString())
                                , tvDetails.getText().toString());
                    }
                    dismiss();
                }
                break;
            default:
                break;
        }

    }
}