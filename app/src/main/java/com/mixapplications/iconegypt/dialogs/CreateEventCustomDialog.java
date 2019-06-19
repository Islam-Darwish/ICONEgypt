package com.mixapplications.iconegypt.dialogs;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
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
import com.mixapplications.iconegypt.models.Events;
import com.mixapplications.iconegypt.models.MultiSpinner;
import com.mixapplications.iconegypt.models.Util;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

import static io.fabric.sdk.android.Fabric.TAG;

public class CreateEventCustomDialog extends AlertDialog implements
        android.view.View.OnClickListener {

    public AppCompatActivity activity;
    public Button cancel;
    public Button share;
    Context context;
    List<Employee> listOfEmployee = new ArrayList<>();
    List<String> emNames;
    private EditText tvDetails;
    private EditText tvTitle;
    private MultiSpinner multiSpinnerTo;
    private EditText tvDate;
    private Events event;
    private DatabaseReference ref;
    private ArrayList<Employee> selectedEmployees;

    public CreateEventCustomDialog(DatabaseReference ref, AppCompatActivity activity, Context context, Events event) {
        super(activity);
        this.event = event;
        this.activity = activity;
        this.ref = ref;
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.create_event_custom_dialog);

        cancel = findViewById(R.id.btn_cancel);
        cancel.setOnClickListener(this);
        share = findViewById(R.id.btn_share);
        share.setOnClickListener(this);

        tvTitle = findViewById(R.id.tvTitle);
        tvDetails = findViewById(R.id.tvDetails);
        tvDate = findViewById(R.id.tvDate);
        multiSpinnerTo = findViewById(R.id.multi_spinner);
        tvTitle.requestFocus();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        setCancelable(false);
        emNames = new ArrayList<>();


        final List<EmployeeNode> listOfNodes = new ArrayList<>();
        final List<Employee> listOfEmployee = new ArrayList<>();

        for (int i = 0; i < AppData.myEmployeeNode.returnAllNodes(AppData.myEmployeeNode).size(); ++i) {
            EmployeeNode n = AppData.myEmployeeNode.returnAllNodes(AppData.myEmployeeNode).get(i);
            List<EmployeeNode> children = n.getChildren();
            if (children != null) {
                for (EmployeeNode child : children) {
                    if (!listOfNodes.contains(child)) {
                        listOfNodes.add(child);
                    }
                }
            }
        }

        for (EmployeeNode employeeNode : listOfNodes) {
            Query fireQuery = ref.child("employees").orderByChild("email").equalTo(employeeNode.getEmail());
            fireQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot1) {
                    for (DataSnapshot snapshot1 : dataSnapshot1.getChildren()) {
                        Employee em = snapshot1.getValue(Employee.class);
                        listOfEmployee.add(em);
                    }
                    if (listOfEmployee.size() == listOfNodes.size()) {


                        for (Employee e : listOfEmployee) {
                            if (!e.getName().equals(""))
                                emNames.add(e.getName());
                            else
                                emNames.add(e.getEmail());
                        }

                        selectedEmployees = new ArrayList<>();
                        selectedEmployees.addAll(listOfEmployee);

                        multiSpinnerTo.setItems(emNames, "All Employees", new MultiSpinner.MultiSpinnerListener() {
                            @Override
                            public void onItemsSelected(boolean[] selected) {
                                selectedEmployees = new ArrayList<>();
                                for (int i = 0; i < selected.length; i++) {
                                    if (selected[i])
                                        selectedEmployees.add(listOfEmployee.get(i));
                                }
                            }
                        });
                        if (event != null) {
                            selectedEmployees = new ArrayList<>();
                            List<Boolean> itemsSelection = new ArrayList<>();
                            for (int i = 0; i < listOfEmployee.size(); i++) {
                                itemsSelection.add(false);
                                if (i == listOfEmployee.size() - 1) {
                                    for (int j = 0; j < event.getToEmployee().size(); j++) {
                                        int index = listOfEmployee.indexOf(event.getToEmployee().get(j));
                                        if (index > -1) {
                                            selectedEmployees.add(listOfEmployee.get(index));
                                            itemsSelection.set(j, true);
                                        } else {
                                            itemsSelection.set(j, false);
                                        }
                                    }
                                }
                            }
                            multiSpinnerTo.setItems(emNames, itemsSelection, "All Employees", new MultiSpinner.MultiSpinnerListener() {
                                @Override
                                public void onItemsSelected(boolean[] selected) {
                                    selectedEmployees = new ArrayList<>();
                                    for (int i = 0; i < selected.length; i++) {
                                        if (selected[i])
                                            selectedEmployees.add(listOfEmployee.get(i));
                                    }
                                }
                            });
                        }
                    }
                }


                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }


        tvDate.setOnClickListener(new View.OnClickListener() {
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
                        tvDate.setText(Util.getDateCurrentTimeZone(date.getTime()));
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


        if (event != null) {
            tvTitle.setText(event.getTitle());
            tvDetails.setText(event.getDetails());
            tvDate.setText(Util.getDateCurrentTimeZone(event.getDate()));
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
                } else if (selectedEmployees.size() == 0) {
                    Toast.makeText(context, "You must select at lest one employee...", Toast.LENGTH_LONG).show();
                } else if (tvDate.getText().toString().equals("")) {
                    Toast.makeText(context, "Start Date must not be empty", Toast.LENGTH_LONG).show();
                } else if (tvDetails.getText().toString().equals("")) {
                    Toast.makeText(context, "Task Details must not be empty", Toast.LENGTH_LONG).show();
                } else {
                    if (event == null) {
                        Util.createEventDB(activity, context, ref, tvTitle.getText().toString(),
                                AppData.employee, selectedEmployees,
                                Util.timeZoneToTimestamp(tvDate.getText().toString())
                                , tvDetails.getText().toString());
                    } else {
                        Util.editEventDB(activity, context, ref, event, tvTitle.getText().toString(),
                                AppData.employee, selectedEmployees,
                                Util.timeZoneToTimestamp(tvDate.getText().toString())
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