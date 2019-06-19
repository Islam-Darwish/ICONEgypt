package com.mixapplications.iconegypt.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.mixapplications.iconegypt.R;
import com.mixapplications.iconegypt.adapters.FragmentAdapter;

public class NewsFragment extends Fragment {
    public FragmentAdapter fragmentAdapter;

    public NewsFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View layout = inflater.inflate(R.layout.fragment_news, container, false);
        getActivity().setTitle("News");
        // Setting ViewPager for each Tabs
        ViewPager viewPager = layout.findViewById(R.id.viewpager);
        setupViewPager(viewPager);
        // Set Tabs inside Toolbar
        TabLayout tabs = layout.findViewById(R.id.result_tabs);
        tabs.setupWithViewPager(viewPager);


        return layout;

    }


    // Add Fragments to Tabs
    private void setupViewPager(ViewPager viewPager) {

        fragmentAdapter = new FragmentAdapter(getChildFragmentManager());
        fragmentAdapter.addFragment(new MyNewsTabFragment(), "News");
        fragmentAdapter.addFragment(new SentNewsTabFragment(), "My News");
        viewPager.setAdapter(fragmentAdapter);
    }
}
