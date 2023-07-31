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

import com.example.myapplication.fragment.ControlFragment;
import com.example.myapplication.fragment.NavigationFragment;
import com.example.myapplication.fragment.RecognitionFragment;
import com.example.myapplication.fragment.VideoFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private Button startSystemBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start_page);
        InitView();
    }

    private void InitView() {
        startSystemBtn = (Button) findViewById(R.id.start_system_btn);
        startSystemBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,NavigationPage.class);
                startActivity(intent);
            }
        });
    }
}

