package com.example.myapplication.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.myapplication.MainActivity;

import com.example.myapplication.R;
import com.example.myapplication.VideoPlayer;
import com.example.myapplication.message.PlanTargetLocation;
import com.example.myapplication.message.Pose;
import com.example.myapplication.structure.JWD;
import com.example.myapplication.rosbridge.ROSBridgeClient;
import com.example.myapplication.message.ActionStateNumber;
import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class JwdNavigationFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";

    private String mParam1;

    private VideoPlayer videoPlayer;
    public Handler handler = new Handler();
    public EditText nowLongitudeJwdEdit;
    public EditText nowLatitudeJwdEdit;
    public EditText targetLongitudeJwdEdit;
    public EditText targetLatitudeJwdEdit;
    public EditText nowStateJwdEdit;
    public EditText startNavigationJwdEdit;
    JWD initPose = new JWD(118.791667,31.940278,180); // 经纬度初始化
    public Spinner spinner;
    public Map<String, ROSBridgeClient> map_car;
    public ROSBridgeClient clientCar;
    public JwdNavigationFragment() {

    }
    private double mPd = 111194.926644;
    // function：输入经纬度，转换至该经纬度对应Pose坐标
    private Pose l2m(JWD lPose, JWD initPose){
        double de = mPd * Math.cos(Math.toRadians(initPose.latitude));
        double dn  = mPd;
        double e = (lPose.longitude - initPose.longitude) * de;
        double n = (lPose.latitude - initPose.latitude) * dn;
        double alpha = Math.toDegrees(Math.atan2(e,n) >0?Math.atan2(e,n):2*Math.PI+Math.atan2(e,n))-initPose.orientation;
        double x = Math.sqrt(e*e + n*n)*Math.cos(Math.toRadians(alpha));
        double y = -Math.sqrt(e*e + n*n)*Math.sin(Math.toRadians(alpha));
        return new Pose(x,y,0,0,0,0,1);
    }
    // function：根据初始经纬度，把当前坐标转换成经纬度
    private JWD m2l(Pose now_pose, JWD initPose) {
        double de = mPd * Math.cos(Math.toRadians(initPose.latitude));
        double dn = mPd;
        double t1 = (now_pose.position_x) * (now_pose.position_x) + (now_pose.position_y) * (now_pose.position_y);
        double t2 = Math.tan(Math.toRadians(initPose.orientation) + Math.atan2(-now_pose.position_y, now_pose.position_x));
        double n = Math.sqrt(t1 / (t2 * t2 + 1));
        double radians = Math.atan2(now_pose.position_x, now_pose.position_y) - Math.PI / 2;
        double degrees = Math.toDegrees(radians) > 0 ? Math.toDegrees(radians) : 360 + Math.toDegrees(radians);
        double delta = (degrees + initPose.orientation) < 360 ? (degrees + initPose.orientation) : (degrees + initPose.orientation - 360);
        if (delta >= 90 && delta < 270)
            n = -n;
        double e = t2 * n;
        return new JWD(initPose.longitude + e / de, initPose.latitude + n / dn, initPose.orientation);
    }

    public static JwdNavigationFragment newInstance(String param1) {
        JwdNavigationFragment fragment = new JwdNavigationFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.i("navigation","onCreate3");
        super.onCreate(savedInstanceState);

        //对视频流信息进行初始化
        videoPlayer = new VideoPlayer(getActivity());

        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
        }
    }
    // funciton:从layout中初始化JwdNavigationFragment的layout
    public void InitVariable(View view) {
        nowLongitudeJwdEdit = view.findViewById(R.id.now_longitude_jwd_edit);
        nowLatitudeJwdEdit = view.findViewById(R.id.now_latitude_jwd_edit);
        targetLongitudeJwdEdit = view.findViewById(R.id.target_longitude_jwd_edit);
        targetLatitudeJwdEdit = view.findViewById(R.id.target_latitude_jwd_edit);
        nowStateJwdEdit = view.findViewById(R.id.now_state_jwd_edit);
        startNavigationJwdEdit = view.findViewById(R.id.start_navigation_jwd_btn);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.i("navigation","onCreateView3");
        View view = inflater.inflate(R.layout.fragment_jwd_navigation, container, false);
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
                JWD nowJWD = m2l(poseCar, initPose);
                nowLongitudeJwdEdit.setText(String.valueOf(nowJWD.longitude));
                nowLatitudeJwdEdit.setText(String.valueOf(nowJWD.latitude));

                // 行动状态编号订阅
                ActionStateNumber actionStateNumberCar = null;
                try {
                    actionStateNumberCar = actionStateTopicCar.take();
                }
                catch (InterruptedException ex) {}
                // 行动状态显示
                if (actionStateNumberCar.action_state == 4) {
                    nowStateJwdEdit.setText("进行中");
                }
                else if (actionStateNumberCar.action_state == 3) {
                    nowStateJwdEdit.setText("已完成");
                }
                else {
                    nowStateJwdEdit.setText("未开始");
                }
                clientCar.disconnect();
            }
        };
        handler.post(task);
        startNavigationJwdEdit.setOnClickListener(new View.OnClickListener() {  // 开始导航的触发函数
            @Override
            public void onClick(View v) {
                com.example.myapplication.Topic<PlanTargetLocation> targetPoseTopicCar = new com.example.myapplication.Topic<PlanTargetLocation>("/subscribe_goal", PlanTargetLocation.class, clientCar);
                targetPoseTopicCar.advertise();
                // 目标点(JWD形式)
                JWD targetLPose = new JWD(Double.parseDouble(targetLongitudeJwdEdit.getText().toString()),Double.parseDouble(targetLatitudeJwdEdit.getText().toString()), initPose.orientation);
                // 目标点(XY坐标形式)
                Pose tempPose = new Pose();
                tempPose = l2m(targetLPose, initPose);
                PlanTargetLocation targetPose = new PlanTargetLocation();
                targetPose.position_x[0] = tempPose.position_x;
                targetPose.position_y[0] = tempPose.position_y;
                targetPose.position_z[0] = 0;
                targetPose.orientation_x[0] = 0;
                targetPose.orientation_y[0] = 0;
                targetPose.orientation_z[0] = 0;
                targetPose.orientation_w[0] = 1;
                targetPose.action_number = 8888;
                targetPose.target_point_number = 1;
                targetPoseTopicCar.publish(targetPose);
            }
        });
        return view;
    }

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        //获取视频流
        videoPlayer.GetVideo(view);
        Log.i("navigation","onViewCreated3");
    }
}