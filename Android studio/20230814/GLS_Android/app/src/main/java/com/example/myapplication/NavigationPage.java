package com.example.myapplication;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import android.view.View;
import android.util.Log;

import com.example.myapplication.fragment.ControlFragment;
import com.example.myapplication.fragment.NavigationFragment;
import com.example.myapplication.fragment.RecognitionFragment;
import com.example.myapplication.fragment.VideoFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class NavigationPage extends AppCompatActivity{
    private BottomNavigationView mNavigationView;
    private FragmentManager mFragmentManager;
    private Fragment[] fragments;
    private int lastFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mNavigationView = findViewById(R.id.navigation);
        initFragment();
        initListener();
        Log.i("message_gls","NavigationPage ok");
    }
    private void initFragment() {
        NavigationFragment navigationFragment = new NavigationFragment();
        ControlFragment controlFragment = new ControlFragment();
        RecognitionFragment recognitionFragment = new RecognitionFragment();
        VideoFragment videoFragment = new VideoFragment();

        fragments = new Fragment[]{navigationFragment, controlFragment, recognitionFragment, videoFragment};
        mFragmentManager = getSupportFragmentManager();
        //默认显示HomeFragment
        mFragmentManager.beginTransaction()
                .replace(R.id.viewPager, navigationFragment)
                .show(navigationFragment)
                .commit();
    }

    private void initListener() {
        mNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.navigation:
                        if (lastFragment != 0) {
                            NavigationPage.this.switchFragment(lastFragment, 0);
                            lastFragment = 0;
                        }
                        return true;
                    case R.id.control:
                        if (lastFragment != 1) {
                            NavigationPage.this.switchFragment(lastFragment, 1);
                            lastFragment = 1;
                        }
                        return true;
                    case R.id.recognition:
                        if (lastFragment != 2) {
                            NavigationPage.this.switchFragment(lastFragment, 2);
                            lastFragment = 2;
                        }
                        return true;
                    case R.id.video:
                        if (lastFragment != 3) {
                            NavigationPage.this.switchFragment(lastFragment, 3);
                            lastFragment = 3;
                        }
                        return true;
                }
                return false;
            }
        });
    }

    private void switchFragment(int lastFragment, int index) {
        FragmentTransaction transaction = mFragmentManager.beginTransaction();
        transaction.hide(fragments[lastFragment]);
        if (!fragments[index].isAdded()){
            transaction.add(R.id.viewPager,fragments[index]);
        }
        transaction.show(fragments[index]).commitAllowingStateLoss();
    }
}
