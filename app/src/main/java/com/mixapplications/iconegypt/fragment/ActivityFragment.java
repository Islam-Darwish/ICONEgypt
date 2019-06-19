package com.mixapplications.iconegypt.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.mixapplications.iconegypt.R;

public class ActivityFragment extends Fragment {

    public ActivityFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_activity, container, false);
        getActivity().setTitle("Activity");

        // Inflate the layout for this fragment
        return layout;
    }

}
