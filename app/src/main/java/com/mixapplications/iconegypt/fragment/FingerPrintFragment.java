package com.mixapplications.iconegypt.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.mixapplications.iconegypt.R;

public class FingerPrintFragment extends Fragment {

    public FingerPrintFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_finger_print, container, false);
        getActivity().setTitle("Finger Print");

        // Inflate the layout for this fragment
        return layout;
    }
}
