package com.example.myapplication.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.myapplication.MainActivity;
import com.example.myapplication.R;
import com.example.myapplication.VideoPlayer;
import com.example.myapplication.message.Pose;
import com.example.myapplication.rosbridge.ROSBridgeClient;
import com.example.myapplication.message.ActionStateNumber;
import com.example.myapplication.message.PlanTargetLocationXd;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


public class XdNavigationFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private String mParam1;
    private VideoPlayer videoPlayer;
    public Button startNavigationXdBtn;
    public EditText nowStateXdEdit;
    public EditText nowPositionXXdEdit;
    public EditText nowPositionYXdEdit;
    public EditText targetXXdEdit;
    public EditText targetYXdEdit;
    public EditText targetZXdEdit;
    public EditText targetWXdEdit;
    public Spinner spinner = null;
    public Map<String, ROSBridgeClient> map_car;
    public ROSBridgeClient clientCar;
    public XdNavigationFragment() {

    }

    public static XdNavigationFragment newInstance(String param1) {
        XdNavigationFragment fragment = new XdNavigationFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.i("XdNavigationFragment","enter onCreate");
        super.onCreate(savedInstanceState);
        //对视频流信息进行初始化
        videoPlayer = new VideoPlayer(getActivity());
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
        }
        return ;
    }

    // funciton:从layout中初始化XdNavigationFragment的layout
    public void FindViewById(View view) {
        Log.i("XdNavigationFragment","enter FindViewById");
        nowStateXdEdit =view.findViewById(R.id.now_state_xd_edit);
        nowPositionXXdEdit = view.findViewById(R.id.now_position_x_xd_edit);
        nowPositionYXdEdit = view.findViewById(R.id.now_position_y_xd_edit);
        targetXXdEdit = view.findViewById(R.id.target_x_xd_edit);
        targetYXdEdit = view.findViewById(R.id.target_y_xd_edit);
        targetZXdEdit = view.findViewById(R.id.target_z_xd_edit);
        targetWXdEdit = view.findViewById(R.id.target_w_xd_edit);
        startNavigationXdBtn = view.findViewById(R.id.start_navigation_xd_btn);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.i("XdNavigationFragment","enter onCreateView");
        View view = inflater.inflate(R.layout.fragment_xd_navigation, container, false);
        return view;
    }

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        Log.i("XdNavigationFragment","enter onViewCreated");
        //获取视频流
        videoPlayer.GetVideo(view);
        super.onViewCreated(view, savedInstanceState);
        FindViewById(view);
        Log.i("XdNavigationFragment","start_spinner");
        /*******多车spinner选择模块**********/
        map_car = new HashMap<String, ROSBridgeClient>() {
            {
                put("车1", MainActivity.clientCar1);  // 设置名称为车1的原因是前面是HashMap顺序不定，保证Car的position后续是第一个
                put("车2", MainActivity.clientCar2);
                put("车3", MainActivity.clientCar3);
            }
        };
        List<String> keys = new ArrayList<>(map_car.keySet());
        // 配置adapter
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, keys);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner = (Spinner) view.findViewById(R.id.cars_spinner_xd);
        // adapter放入spinner
        spinner.setAdapter(spinnerAdapter);
        clientCar = map_car.get("车1");    // 需要预先设置一下
        clientCar.connect();
        // 下拉菜单项选择事件
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.i("XdNavigationFragment","enter onItemSelected");
                String selectedKey = keys.get(position);// position为选择第几个
                clientCar = map_car.get(selectedKey);
                clientCar.connect();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {  // 无点击时默认选择car1
                Log.i("XdNavigationFragment","enter onNothingSelected");
                clientCar = map_car.get("车1");
                clientCar.connect();
            }
        });
        /*******多车spinner选择模块结束**********/
        Log.i("XdNavigationFragment","after_spinner");

        /*
            普通的runable和handler不是新建一个线程，而是在原有主线程的基础上再开一段执行内容，单个没有问题，但是涉及到多个界面同时runable就会报错
            所以必须新开线程来进行，新开线程可以使用Thread但是这个是不循环的，所以使用HandlerThread新开线程，具有循环模板、、
         */
        startNavigationXdBtn.setOnClickListener(new View.OnClickListener() {  // 开始导航的触发函数
            @Override
            public void onClick(View v) {
                Log.i("XdNavigationFragmentBtn","enter xd onclick");
                com.example.myapplication.Topic<PlanTargetLocationXd> targetPoseTopicCar = new com.example.myapplication.Topic<PlanTargetLocationXd>("/subscribe_goal_xd", PlanTargetLocationXd.class, clientCar);
                Log.i("XdNavigationFragmentBtn","before advertise");
                targetPoseTopicCar.advertise();
                Log.i("XdNavigationFragmentBtn","after advertise");
                PlanTargetLocationXd targetPose = new PlanTargetLocationXd();
                targetPose.position_x[0] = Double.parseDouble(targetXXdEdit.getText().toString());
                targetPose.position_y[0] = Double.parseDouble(targetYXdEdit.getText().toString());
                targetPose.orientation_z[0] = Double.parseDouble(targetZXdEdit.getText().toString());
                targetPose.orientation_w[0] = Double.parseDouble(targetWXdEdit.getText().toString());
                targetPose.target_point_number = 1;
                Log.i("XdNavigationFragmentBtn","before publish");
                targetPoseTopicCar.publish(targetPose);
                Log.i("XdNavigationFragmentBtn","after publish");
            }
        });
        /*
            改动：
                普通的runable和handler不是新建一个线程，而是在原有主线程的基础上再开一段执行内容，单个没有问题，但是涉及到多个界面同时runable就会报错
                所以必须新开线程来进行，新开线程可以使用Thread但是这个是不循环的，所以使用HandlerThread新开线程，具有循环模板、、
         */
        HandlerThread threadXd = new HandlerThread("threadXd"); //handlerThread是内部有一个looper轮询器的Thread线程
        threadXd.start();
        Handler xdHandler = new Handler(threadXd.getLooper());
        clientCar.connect();
        xdHandler.post(new Runnable() {
            @Override
            public void run() {
                xdHandler.postDelayed(this::run,1000);//设置循环时间，此处是0.01秒

                com.example.myapplication.Topic<Pose> poseTopicCar = new com.example.myapplication.Topic<Pose>("/publish_pose", Pose.class, clientCar);
                poseTopicCar.subscribe();
                com.example.myapplication.Topic<ActionStateNumber> actionStateTopicCar = new com.example.myapplication.Topic<ActionStateNumber>("/action_state_number", ActionStateNumber.class, clientCar);
                actionStateTopicCar.subscribe();
                Log.i("XdNavigationFragment","after subscribe");
                // 位姿订阅
                Pose poseCar = null;
                try {
                    poseCar = poseTopicCar.take();
                }
                catch (InterruptedException ex) {}
                nowPositionXXdEdit.setText(String.valueOf(poseCar.position_x));
                nowPositionYXdEdit.setText(String.valueOf(poseCar.position_y));
                Log.i("XdNavigationFragment","position_x:"+String.valueOf(poseCar.position_x));
                Log.i("XdNavigationFragment","position_y:"+String.valueOf(poseCar.position_y));
                // 行动状态编号订阅
                ActionStateNumber actionStateNumberCar = null;
                try {
                    actionStateNumberCar = actionStateTopicCar.take();
                }
                catch (InterruptedException ex) {}
                // 行动状态显示
                if (actionStateNumberCar.data == 4) {
                    nowStateXdEdit.setText("进行中");
                }
                else if (actionStateNumberCar.data == 3) {
                    nowStateXdEdit.setText("已完成");
                }
                else {
                    nowStateXdEdit.setText("未开始");
                }
                Log.i("XdNavigationFragment","action_state："+String.valueOf(actionStateNumberCar.data));
            }
        });
    }
}