package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.myapplication.fragment.JdNavigationFragment;
import com.example.myapplication.fragment.JwdNavigationFragment;
import com.example.myapplication.fragment.XdNavigationFragment;

import java.util.ArrayList;
import java.util.List;

public class NavigationPageAdapter extends FragmentStateAdapter {

    private List<String> mData = new ArrayList<>();

    public NavigationPageAdapter(@NonNull FragmentActivity fragmentActivity, List<String> data) {
        super(fragmentActivity);
        mData = data;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position){
            case 0:
                return XdNavigationFragment.newInstance(mData.get(position));
            case 1:
                return JdNavigationFragment.newInstance(mData.get(position));
            case 2:
                return JwdNavigationFragment.newInstance(mData.get(position));
            default:
                return XdNavigationFragment.newInstance(mData.get(position));
        }
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }
}
