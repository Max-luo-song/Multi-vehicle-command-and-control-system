package com.example.myapplication.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.example.myapplication.NavigationPageAdapter;
import com.example.myapplication.R;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;
import java.util.List;

public class NavigationFragment extends Fragment {

    private TabLayout tabLayout;
    private ViewPager2 viewPager2;
    private List<String> data = new ArrayList<>();

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_navigation, container, false);
        initData();
        initView(rootView);
        return rootView;
    }
    private void initView(View rootView) {
        tabLayout = rootView.findViewById(R.id.navigation_indicator);
        viewPager2 = rootView.findViewById(R.id.navigation_pager);
        NavigationPageAdapter navigationPageAdapter = new NavigationPageAdapter(getActivity(), data);
        viewPager2.setAdapter(navigationPageAdapter);
        new TabLayoutMediator(tabLayout, viewPager2, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                tab.setText(data.get(position));
            }
        }).attach();
    }

    private void initData() {
        data.add("相对导航");
        data.add("绝对导航");
        data.add("经纬度导航");
    }

}
