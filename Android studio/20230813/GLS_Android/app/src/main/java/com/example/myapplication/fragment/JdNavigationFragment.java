package com.example.myapplication.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.widget.Spinner;

import com.example.myapplication.R;
import com.example.myapplication.VideoPlayer;
import com.example.myapplication.message.Pose;
import com.example.myapplication.rosbridge.ROSBridgeClient;
import com.example.myapplication.MainActivity;
import com.example.myapplication.message.ActionStateNumber;
import com.example.myapplication.message.PlanTargetLocationJd;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

public class JdNavigationFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";

    private String mParam1;

    private VideoPlayer videoPlayer;
    public EditText nowPositionXJdEdit;
    public EditText nowPositionYJdEdit;
    public EditText targetPositionXJdEdit;
    public EditText targetPositionYJdEdit;
    public EditText nowStateJdEdit;
    public Button startNavigationJdBtn;

    public Spinner spinner = null;
    public Map<String, ROSBridgeClient> map_car;
    public ROSBridgeClient clientCar;

    public JdNavigationFragment() {

    }

    public static JdNavigationFragment newInstance(String param1) {
        JdNavigationFragment fragment = new JdNavigationFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.i("JdNavigationFragment","enter onCreate");
        super.onCreate(savedInstanceState);
        videoPlayer = new VideoPlayer(getActivity());

        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
        }
        return ;
    }

    // funciton:从layout中初始化JdNavigationFragment的layout
    public void FindViewById(View view) {
        Log.i("JdNavigationFragment","enter FindViewById");
        nowPositionXJdEdit =view.findViewById(R.id.now_position_x_jd_edit);
        nowPositionYJdEdit = view.findViewById(R.id.now_position_y_jd_edit);
        targetPositionXJdEdit = view.findViewById(R.id.target_position_x_jd_edit);
        targetPositionYJdEdit = view.findViewById(R.id.target_position_y_jd_edit);
        nowStateJdEdit = view.findViewById(R.id.now_state_jd_edit);
        startNavigationJdBtn = view.findViewById(R.id.start_navigation_jd_btn);
        return ;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.i("JdNavigationFragment","enter onCreateView");
        View view = inflater.inflate(R.layout.fragment_jd_navigation, container, false);
        return view;
    }

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        //获取视频流
        videoPlayer.GetVideo(view);
        Log.i("JdNavigationFragment","enter onViewCreated");
        super.onViewCreated(view, savedInstanceState);
        FindViewById(view);
        Log.i("JdNavigationFragment","start_spinner");
        /*******多车spinner选择模块**********/
        map_car = new HashMap<String, ROSBridgeClient>() { // 使用了HashMap顺序是不可控的
            {
                put("车1", MainActivity.clientCar1);       // 设置名称为车1的原因是前面是HashMap顺序不定，保证Car的position后续是第一个
                put("车2", MainActivity.clientCar2);
                put("车3", MainActivity.clientCar3);
            }
        };
        List<String> keys = new ArrayList<>(map_car.keySet());
        // 配置adapter
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, keys);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner = (Spinner) view.findViewById(R.id.cars_spinner_jd);
        // adapter放入spinner
        spinner.setAdapter(spinnerAdapter);
        clientCar = map_car.get("车1");    // 需要预先设置一下
        clientCar.connect();
        // 下拉菜单项选择事件
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {  // 默认初始化会调用一次
                Log.i("JdNavigationFragment","enter onItemSelected");
                String selectedKey = keys.get(position);// position为选择第几个
                Log.i("JdNavigationFragment","position is:"+String.valueOf(position));
                Log.i("JdNavigationFragment","selectKey is:"+selectedKey);
                clientCar = map_car.get(selectedKey);
                clientCar.connect();
                return ;
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {  // 无点击时默认选择car1
                Log.i("JdNavigationFragment","enter onNothingSelected");
                clientCar = map_car.get("车1");
                clientCar.connect();
                return ;
            }
        });
        /*******多车spinner选择模块结束**********/
        Log.i("JdNavigationFragment","finish Jd spinner");

        startNavigationJdBtn.setOnClickListener(new View.OnClickListener() {  // 开始导航的触发函数
            @Override
            public void onClick(View v) {
                Log.i("JdNavigationFragmentBtn","enter startNavigationJdBtn onclick");
                Log.i("JdNavigationFragmentBtn","before advertise");
                com.example.myapplication.Topic<PlanTargetLocationJd> targetPoseTopicCar = new com.example.myapplication.Topic<PlanTargetLocationJd>("/subscribe_goal_jd", PlanTargetLocationJd.class, clientCar);
                targetPoseTopicCar.advertise();
                Log.i("JdNavigationFragmentBtn","after advertise");
                PlanTargetLocationJd targetPose = new PlanTargetLocationJd();
                targetPose.position_x[0] = Double.parseDouble(targetPositionXJdEdit.getText().toString());
                targetPose.position_y[0] = Double.parseDouble(targetPositionYJdEdit.getText().toString());
                targetPose.orientation_w[0] = 1;
                targetPose.target_point_number = 1;
                Log.i("JdNavigationFragmentBtn","before publish");
                targetPoseTopicCar.publish(targetPose);
                Log.i("JdNavigationFragmentBtn","after publish");
            }
        });
        /*
            改动：
                普通的runable和handler不是新建一个线程，而是在原有主线程的基础上再开一段执行内容，单个没有问题，但是涉及到多个界面同时runable就会报错
                所以必须新开线程来进行，新开线程可以使用Thread但是这个是不循环的，所以使用HandlerThread新开线程，具有循环模板、、
         */
        HandlerThread threadJd = new HandlerThread("threadJd");
        threadJd.start();
        Handler jdHandler = new Handler(threadJd.getLooper());
        Runnable task = new Runnable() {
            @Override
            public void run() {
                jdHandler.postDelayed(this::run,1000);//设置循环时间，不能设置太快
                com.example.myapplication.Topic<Pose> poseTopicCar = new com.example.myapplication.Topic<Pose>("/publish_pose", Pose.class, clientCar);
                poseTopicCar.subscribe();

                com.example.myapplication.Topic<ActionStateNumber> actionStateTopicCar = new com.example.myapplication.Topic<ActionStateNumber>("/action_state_number", ActionStateNumber.class, clientCar);
                actionStateTopicCar.subscribe();
                Log.i("JdNavigationFragment","after subscribe");

                // 位姿订阅
                Pose poseCar = null;
                try {
                    poseCar = poseTopicCar.take();
                }
                catch (InterruptedException ex) {}
                Log.i("JdNavigationFragment","position_x："+String.valueOf(poseCar.position_x));
                Log.i("JdNavigationFragment","position_y："+String.valueOf(poseCar.position_y));
                nowPositionXJdEdit.setText(String.valueOf(poseCar.position_x));
                nowPositionYJdEdit.setText(String.valueOf(poseCar.position_y));

                // 行动状态编号订阅
                ActionStateNumber actionStateNumberCar = null;
                try {
                    actionStateNumberCar = actionStateTopicCar.take();
                }
                catch (InterruptedException ex) {}
                // 行动状态显示
                if (actionStateNumberCar.data == 4) {
                    nowStateJdEdit.setText("进行中");
                }
                else if (actionStateNumberCar.data == 3) {
                    nowStateJdEdit.setText("已完成");
                }
                else {
                    nowStateJdEdit.setText("未开始");
                }
                Log.i("JdNavigationFragment","action_state："+String.valueOf(actionStateNumberCar.data));
            }
        };
        jdHandler.post(task);
    }
}