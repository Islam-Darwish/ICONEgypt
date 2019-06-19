package com.mixapplications.iconegypt.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.mixapplications.iconegypt.R;
import com.mixapplications.iconegypt.activity.SelectActivity;
import com.mixapplications.iconegypt.models.AppData;
import com.mixapplications.iconegypt.models.Prefs;

import static com.mixapplications.iconegypt.models.AppData.currentUser;

public class MainFragment extends Fragment {

    public static ImageView news_red_dot;
    public static ImageView tasks_red_dot;
    public static ImageView forms_red_dot;
    public static ImageView events_red_dot;
    Context context;
    Activity activity;

    public MainFragment() {
        // Required empty public constructor
    }

    public static void setRedDotVisibility(final Context context, final int type, final boolean visible) {

        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                Prefs.initPrefs(context, "icon_egypt", Context.MODE_PRIVATE);
                switch (type) {
                    case 0:
                        try {
                            Prefs.putBoolean(currentUser.getEmail() + "-news", visible);
                            news_red_dot.setVisibility(visible ? View.VISIBLE : View.INVISIBLE);
                        } catch (Exception e) {
                            Log.d("error", "visible");
                        }
                        break;
                    case 1:
                        try {
                            tasks_red_dot.setVisibility(visible ? View.VISIBLE : View.INVISIBLE);
                        } catch (Exception e) {
                            Log.d("error", "visible");
                        }
                        break;
                    case 2:
                        try {
                            forms_red_dot.setVisibility(visible ? View.VISIBLE : View.INVISIBLE);
                        } catch (Exception e) {
                            Log.d("error", "visible");
                        }
                        break;
                    case 3:
                        try {
                            events_red_dot.setVisibility(visible ? View.VISIBLE : View.INVISIBLE);
                        } catch (Exception e) {
                            Log.d("error", "visible");
                        }
                        break;
                }
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.fragment_main, container, false);
        context = getContext();
        activity = getActivity();
        getActivity().setTitle("ICON Egypt");
        CardView card_profile = layout.findViewById(R.id.card_profile);
        CardView card_employee = layout.findViewById(R.id.card_employee);
        CardView card_forms = layout.findViewById(R.id.card_forms);
        CardView card_news = layout.findViewById(R.id.card_news);
        CardView card_tasks = layout.findViewById(R.id.card_tasks);
        //CardView card_finger_print = layout.findViewById(R.id.card_finger_print);
        CardView card_event = layout.findViewById(R.id.card_event);
        //CardView card_activity = layout.findViewById(R.id.card_activity);

        tasks_red_dot = layout.findViewById(R.id.tasks_red_dot);
        forms_red_dot = layout.findViewById(R.id.forms_red_dot);
        news_red_dot = layout.findViewById(R.id.news_red_dot);
        events_red_dot = layout.findViewById(R.id.events_red_dot);

        card_employee.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((SelectActivity) getActivity()).setFragment(new EmployeeFragment(), true, "employee_fragment");
            }
        });
        card_forms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((SelectActivity) getActivity()).setFragment(new FormsFragment(), true, "forms_fragment");
            }
        });
        card_news.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((SelectActivity) getActivity()).setFragment(new NewsFragment(), true, "news_fragment");
            }
        });

        card_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((SelectActivity) getActivity()).setFragment(new ProfileFragment(), true, "profile_fragment");
            }
        });
        card_tasks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((SelectActivity) getActivity()).setFragment(new TasksFragment(), true, "tasks_fragment");
            }
        });

        card_event.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((SelectActivity) getActivity()).setFragment(new EventsFragment(), true, "events_fragment");
            }
        });

  /*      card_activity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((SelectActivity) getActivity()).setFragment(new ActivityFragment(), true, "activity_fragment");
            }
        });
        card_finger_print.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((SelectActivity) getActivity()).setFragment(new FingerPrintFragment(), true, "finger_print_fragment");
            }
        });

*/
        Prefs.initPrefs(context, "icon_egypt", Context.MODE_PRIVATE);
        boolean hasNews = Prefs.getBoolean(AppData.currentUser.getEmail() + "-news", false);
        news_red_dot.setVisibility(hasNews ? View.VISIBLE : View.INVISIBLE);

        boolean hasForms = Prefs.getBoolean(AppData.currentUser.getEmail() + "-forms", false);
        forms_red_dot.setVisibility(hasForms ? View.VISIBLE : View.INVISIBLE);

        boolean hasTasks = Prefs.getBoolean(AppData.currentUser.getEmail() + "-tasks", false);
        tasks_red_dot.setVisibility(hasTasks ? View.VISIBLE : View.INVISIBLE);

        boolean hasEvents = Prefs.getBoolean(AppData.currentUser.getEmail() + "-events", false);
        events_red_dot.setVisibility(hasEvents ? View.VISIBLE : View.INVISIBLE);

        return layout;
    }
}
