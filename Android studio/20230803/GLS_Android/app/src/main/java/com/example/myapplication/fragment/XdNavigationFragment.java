package com.example.myapplication.fragment;

import android.os.Bundle;
import android.os.Handler;
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
import com.example.myapplication.message.Move;
import com.example.myapplication.message.PlanTargetLocation;
import com.example.myapplication.message.Pose;
import com.example.myapplication.rosbridge.ROSBridgeClient;
import com.example.myapplication.structure.JWD;
import com.example.myapplication.message.ActionStateNumber;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import android.os.Handler;


public class XdNavigationFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";

    private String mParam1;

    private VideoPlayer videoPlayer;
    private Handler handler = new Handler();
    public Button startNavigationXdBtn;
    public EditText nowStateXdEdit;
    public EditText nowPositionXXdEdit;
    public EditText nowPositionYXdEdit;
    public EditText targetXXdEdit;
    public EditText targetYXdEdit;
    public EditText targetZXdEdit;
    public EditText targetWXdEdit;

    public Spinner spinner;
    public Map<String, ROSBridgeClient> map_car;
    public ROSBridgeClient clientCar;
    public XdNavigationFragment() {

    }
    private TextView text1;

    public static XdNavigationFragment newInstance(String param1) {
        XdNavigationFragment fragment = new XdNavigationFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.i("navigation","onCreate2");
        super.onCreate(savedInstanceState);

        //对视频流信息进行初始化
        videoPlayer = new VideoPlayer(getActivity());

        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
        }
    }
    // funciton:从layout中初始化XdNavigationFragment的layout
    public void InitVariable(View view) {
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
        Log.i("navigation","onCreateView2");
        View view = inflater.inflate(R.layout.fragment_xd_navigation, container, false);
        InitVariable(view);

        /*******多车spinner选择模块**********/
        map_car = new HashMap<String, ROSBridgeClient>() {
            {
                put("car1", MainActivity.clientCar1);
                put("car2", MainActivity.clientCar2);
                put("car3", MainActivity.clientCar3);
            }
        };
        List<String> keys = new ArrayList<>(map_car.keySet());
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
                clientCar = map_car.get(selectedKey);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {  // 无点击时默认选择car1
                clientCar = map_car.get("car1");
            }
        });
        /*******多车spinner选择模块结束**********/

        Runnable task = new Runnable() {
            @Override
            public void run() {
                handler.postDelayed(this,10);//设置循环时间，此处是0.01秒
                clientCar.connect();

                com.example.myapplication.Topic<Pose> poseTopicCar = new com.example.myapplication.Topic<Pose>("/publish_pose", Pose.class, clientCar);
                poseTopicCar.subscribe();

                com.example.myapplication.Topic<ActionStateNumber> actionStateTopicCar = new com.example.myapplication.Topic<ActionStateNumber>("/action_state_number", ActionStateNumber.class, clientCar);
                actionStateTopicCar.subscribe();

                // 位姿订阅
                Pose poseCar = null;
                try {
                    poseCar = poseTopicCar.take();
                }
                catch (InterruptedException ex) {}
                nowPositionXXdEdit.setText(String.valueOf(poseCar.position_x));
                nowPositionYXdEdit.setText(String.valueOf(poseCar.position_y));

                // 行动状态编号订阅
                ActionStateNumber actionStateNumberCar = null;
                try {
                    actionStateNumberCar = actionStateTopicCar.take();
                }
                catch (InterruptedException ex) {}
                // 行动状态显示
                if (actionStateNumberCar.action_state == 4) {
                    nowStateXdEdit.setText("进行中");
                }
                else if (actionStateNumberCar.action_state == 3) {
                    nowStateXdEdit.setText("已完成");
                }
                else {
                    nowStateXdEdit.setText("未开始");
                }
                clientCar.disconnect();
            }
        };
        handler.post(task);
        startNavigationXdBtn.setOnClickListener(new View.OnClickListener() {  // 开始导航的触发函数
            @Override
            public void onClick(View v) {
                com.example.myapplication.Topic<PlanTargetLocation> targetPoseTopicCar = new com.example.myapplication.Topic<PlanTargetLocation>("/subscribe_goal", PlanTargetLocation.class, clientCar);
                targetPoseTopicCar.advertise();
                PlanTargetLocation targetPose = new PlanTargetLocation();
                targetPose.position_x[0] = Double.parseDouble(targetXXdEdit.getText().toString());
                targetPose.position_y[0] = Double.parseDouble(targetYXdEdit.getText().toString());
                targetPose.position_z[0] = Double.parseDouble(targetZXdEdit.getText().toString());
                targetPose.orientation_w[0] = Double.parseDouble(targetWXdEdit.getText().toString());
                targetPose.action_number = 8888;
                targetPose.target_point_number = 1;
                targetPoseTopicCar.publish(targetPose);
            }
        });
        return view;
    }

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        //获取视频流
        Log.i("navigation","onViewCreated2");
    }
}