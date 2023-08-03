package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.content.Intent;

public class StartErrorPage extends AppCompatActivity {
    public Button ensureBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start_err_page);
        InitView();
    }

    // function：完成按键后的判断逻辑
    private void InitView() {
        ensureBtn = (Button) findViewById(R.id.ensure_btn);
        ensureBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StartErrorPage.this,MainActivity.class);
                startActivity(intent);
            }
        });
    }
}
