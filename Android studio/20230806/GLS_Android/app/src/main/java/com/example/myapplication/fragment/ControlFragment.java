package com.example.myapplication.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.MainActivity;
import com.example.myapplication.R;
import com.example.myapplication.VideoPlayer;
import com.example.myapplication.rosbridge.ROSBridgeClient;
import com.example.myapplication.message.Move;
import com.example.myapplication.structure.Vector3;
//import com.example.myapplication.ArrayAdapter;

// spinner多车选择模块头文件
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import java.util.ArrayList;
import java.util.Map;
import java.util.List;
import java.util.HashMap;

public class ControlFragment extends Fragment {

    private VideoPlayer videoPlayer;
    public Button forworkBtn;
    public Button leftBtn;
    public Button stopBtn;
    public Button rightBtn;
    public Button backBtn;
    public EditText linearVelocityEdit;
    public EditText angularVelocityEdit;
    public Spinner spinner;
    public Map<String, ROSBridgeClient> mapCar;
    public ROSBridgeClient clientCar;
    public com.example.myapplication.Topic<Move> moveTopic;
    public ControlFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.i("navigation","onCreate");
        super.onCreate(savedInstanceState);
        //对视频流信息进行初始化
        videoPlayer = new VideoPlayer(getActivity());
    }

    // funciton:从layout中初始化ControlFragment的layout
    public void InitVariable(View view) {
        forworkBtn = view.findViewById(R.id.forward_btn);
        leftBtn = view.findViewById(R.id.left_btn);
        stopBtn = view.findViewById(R.id.stop_btn);
        rightBtn = view.findViewById(R.id.right_btn);
        backBtn = view.findViewById(R.id.back_btn);
        linearVelocityEdit = view.findViewById(R.id.linear_velocity_edit);
        angularVelocityEdit = view.findViewById(R.id.angular_velocity_edit);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.i("navigation","onCreateView");
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_control, container, false);
        InitVariable(view);

        /*******多车spinner选择模块**********/
        mapCar = new HashMap<String, ROSBridgeClient>() {
            {
                put("car1", MainActivity.clientCar1);
                put("car2", MainActivity.clientCar2);
                put("car3", MainActivity.clientCar3);
            }
        };
        List<String> keys = new ArrayList<>(mapCar.keySet());
        // 配置adapter
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, keys);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // adapter放入spinner
        spinner.setAdapter(spinnerAdapter);

        // 下拉菜单项选择事件
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // position为选择第几个
                String selectedKey = keys.get(position);
                clientCar = mapCar.get(selectedKey);
                moveTopic = new com.example.myapplication.Topic<Move>("/cmd_vel", Move.class, clientCar);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {  // 无点击时默认选择car1
                clientCar = mapCar.get("car1");
            }
        });
        /*******多车spinner选择模块结束**********/
        return view;
    }

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        //获取视频流
        videoPlayer.GetVideo(view);
        Log.i("navigation","onViewCreated");
        com.example.myapplication.Topic<Move> moveTopic = new com.example.myapplication.Topic<Move>("/cmd_vel", Move.class, clientCar);
        // 数字顺序：前后左右停
        forworkBtn.setOnClickListener(new MyOnClickListener(1, moveTopic, linearVelocityEdit, angularVelocityEdit));
        backBtn.setOnClickListener(new MyOnClickListener(2, moveTopic, linearVelocityEdit, angularVelocityEdit));
        leftBtn.setOnClickListener(new MyOnClickListener(3, moveTopic, linearVelocityEdit, angularVelocityEdit));
        rightBtn.setOnClickListener(new MyOnClickListener(4, moveTopic, linearVelocityEdit, angularVelocityEdit));
        stopBtn.setOnClickListener(new MyOnClickListener(5, moveTopic, linearVelocityEdit, angularVelocityEdit));
    }

    static public Move move = new Move();
    static class MyOnClickListener implements View.OnClickListener {
        private final int choice;
        private final com.example.myapplication.Topic<Move> moveTopic;
        private final Double linearVelocityEdit;
        private final Double angularVelocityEdit;
        public MyOnClickListener(int choice, com.example.myapplication.Topic<Move> moveTopic, EditText linearVelocityEdit, EditText angularVelocityEdit) {
            this.choice = choice;
            this.moveTopic = moveTopic;
            this.linearVelocityEdit = Double.parseDouble(linearVelocityEdit.getText().toString());
            this.angularVelocityEdit = Double.parseDouble(angularVelocityEdit.getText().toString());
        }
        @Override
        public void onClick(View v) {
            move.linear = new Vector3();
            move.angular = new Vector3();
            if (choice == 1) {  // 前
                move.linear.x = linearVelocityEdit;
                move.linear.y = 0;
                move.linear.z = 0;
                move.angular.x = 0;
                move.angular.y = 0;
                move.angular.z = 0;
            }
            else if (choice == 2) {  // 后
                move.linear.x = -linearVelocityEdit;
                move.linear.y = 0;
                move.linear.z = 0;
                move.angular.x = 0;
                move.angular.y = 0;
                move.angular.z = 0;
            }
            else if (choice == 3) { // 左
                move.linear.x = 0;
                move.linear.y = 0;
                move.linear.z = 0;
                move.angular.x = 0;
                move.angular.y = 0;
                move.angular.z = angularVelocityEdit;
            }
            else if (choice == 4) { // 右
                move.linear.x = 0;
                move.linear.y = 0;
                move.linear.z = 0;
                move.angular.x = 0;
                move.angular.y = 0;
                move.angular.z = -angularVelocityEdit;
            }
            else if (choice == 5) { // 停
                move.linear.x = 0;
                move.linear.y = 0;
                move.linear.z = 0;
                move.angular.x = 0;
                move.angular.y = 0;
                move.angular.z = 0;
            }
            moveTopic.publish(move);
        }
    }
}