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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.myapplication.MainActivity;

import com.example.myapplication.R;
import com.example.myapplication.VideoPlayer;
import com.example.myapplication.message.Pose;
import com.example.myapplication.structure.JWD;
import com.example.myapplication.rosbridge.ROSBridgeClient;
import com.example.myapplication.message.ActionStateNumber;
import com.example.myapplication.message.PlanTargetLocationJwd;
import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class JwdNavigationFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";

    private String mParam1;

    private VideoPlayer videoPlayer;
    public EditText nowLongitudeJwdEdit;
    public EditText nowLatitudeJwdEdit;
    public EditText targetLongitudeJwdEdit;
    public EditText targetLatitudeJwdEdit;
    public EditText nowStateJwdEdit;
    public Button startNavigationJwdBtn;
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
        Log.i("JwdNavigationFragment","enter onCreate");
        super.onCreate(savedInstanceState);
        //对视频流信息进行初始化
        videoPlayer = new VideoPlayer(getActivity());
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
        }
    }

    // funciton:从layout中初始化JwdNavigationFragment的layout
    public void FindViewById(View view) {
        Log.i("JwdNavigationFragment","enter FindViewById");
        nowLongitudeJwdEdit = view.findViewById(R.id.now_longitude_jwd_edit);
        nowLatitudeJwdEdit = view.findViewById(R.id.now_latitude_jwd_edit);
        targetLongitudeJwdEdit = view.findViewById(R.id.target_longitude_jwd_edit);
        targetLatitudeJwdEdit = view.findViewById(R.id.target_latitude_jwd_edit);
        nowStateJwdEdit = view.findViewById(R.id.now_state_jwd_edit);
        startNavigationJwdBtn = view.findViewById(R.id.start_navigation_jwd_btn);
        return ;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.i("JwdNavigationFragment","enter onCreateView");
        View view = inflater.inflate(R.layout.fragment_jwd_navigation, container, false);
        return view;
    }

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        Log.i("JwdNavigationFragment","enter onViewCreated");
        super.onViewCreated(view, savedInstanceState);
        FindViewById(view);

        Log.i("JwdNavigationFragment","start spinner");
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
        spinner = (Spinner) view.findViewById(R.id.cars_spinner_jwd);
        // adapter放入spinner
        spinner.setAdapter(spinnerAdapter);
        clientCar = map_car.get("车1");    // 需要预先设置一下
        clientCar.connect();
        // 下拉菜单项选择事件
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view1, int position, long id) {// 此处的view1是有问题的，要使用全局的view才能正常显示，做区别写成了view1
                Log.i("JwdNavigationFragment","enter onItemSelected");
                String selectedKey = keys.get(position);// position为选择第几个
                clientCar = map_car.get(selectedKey);
                clientCar.connect();
                //获取视频流
                if (selectedKey.equals("车1")) {
                    videoPlayer.GetVideo(view, MainActivity.car1IpEdit.getText().toString());
                }
                else if (selectedKey.equals("车2")) {
                    videoPlayer.GetVideo(view, MainActivity.car2IpEdit.getText().toString());
                }
                else if (selectedKey.equals("车3")) {
                    videoPlayer.GetVideo(view, MainActivity.car3IpEdit.getText().toString());
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {  // 无点击时默认选择car1
                Log.i("JwdNavigationFragment","enter onNothingSelected");
                clientCar = map_car.get("车1");
                clientCar.connect();
            }
        });
        Log.i("JwdNavigationFragment","after spinner");
        /*******多车spinner选择模块结束**********/

        startNavigationJwdBtn.setOnClickListener(new View.OnClickListener() {  // 开始导航的触发函数
            @Override
            public void onClick(View v) {
                Log.i("JwdNavigationFragmentBtn","enter jwd onClick");
                com.example.myapplication.Topic<PlanTargetLocationJwd> targetPoseTopicCar = new com.example.myapplication.Topic<PlanTargetLocationJwd>("/subscribe_goal_jwd", PlanTargetLocationJwd.class, clientCar);
                Log.i("JwdNavigationFragmentBtn","before advertise");
                targetPoseTopicCar.advertise();
                Log.i("JwdNavigationFragmentBtn","after advertise");
                // 目标点(JWD形式)
                JWD targetLPose = new JWD(Double.parseDouble(targetLongitudeJwdEdit.getText().toString()),Double.parseDouble(targetLatitudeJwdEdit.getText().toString()), initPose.orientation);
                // 目标点(XY坐标形式)
                Pose tempPose = new Pose();
                tempPose = l2m(targetLPose, initPose);
                PlanTargetLocationJwd targetPose = new PlanTargetLocationJwd();
                targetPose.position_x[0] = tempPose.position_x;
                targetPose.position_y[0] = tempPose.position_y;
                targetPose.position_z[0] = 0;
                targetPose.orientation_x[0] = 0;
                targetPose.orientation_y[0] = 0;
                targetPose.orientation_z[0] = 0;
                targetPose.orientation_w[0] = 1;
                targetPose.target_point_number = 1;
                Log.i("JwdNavigationFragmentBtn","before publish");
                targetPoseTopicCar.publish(targetPose);
                Log.i("JwdNavigationFragmentBtn","after publish");
            }
        });
        /*
            普通的runable和handler不是新建一个线程，而是在原有主线程的基础上再开一段执行内容，单个没有问题，但是涉及到多个界面同时runable就会报错
            所以必须新开线程来进行，新开线程可以使用Thread但是这个是不循环的，所以使用HandlerThread新开线程，具有循环模板、、
         */
        HandlerThread threadJwd = new HandlerThread("threadJwd");
        threadJwd.start();
        Handler jwdHandler = new Handler(threadJwd.getLooper());
        Runnable task = new Runnable() {
            @Override
            public void run() {
                jwdHandler.postDelayed(this,1000);//设置循环时间，此处是0.01秒

                com.example.myapplication.Topic<Pose> poseTopicCar = new com.example.myapplication.Topic<Pose>("/publish_pose", Pose.class, clientCar);
                poseTopicCar.subscribe();
                com.example.myapplication.Topic<ActionStateNumber> actionStateTopicCar = new com.example.myapplication.Topic<ActionStateNumber>("/action_state_number_jwd", ActionStateNumber.class, clientCar);
                actionStateTopicCar.subscribe();
                Log.i("JwdNavigationFragment","after subscribe");

                // 位姿订阅
                Pose poseCar = null;
                try {
                    poseCar = poseTopicCar.take();
                }
                catch (InterruptedException ex) {}
                JWD nowJWD = m2l(poseCar, initPose);
                nowLongitudeJwdEdit.setText(String.valueOf(nowJWD.longitude));
                nowLatitudeJwdEdit.setText(String.valueOf(nowJWD.latitude));
                Log.i("JwdNavigationFragment","longitude:"+String.valueOf(nowJWD.longitude));
                Log.i("JwdNavigationFragment","latitude:"+String.valueOf(nowJWD.latitude));

                // 行动状态编号订阅
                ActionStateNumber actionStateNumberCar = null;
                try {
                    actionStateNumberCar = actionStateTopicCar.take();
                }
                catch (InterruptedException ex) {}
                // 行动状态显示
                if (actionStateNumberCar.data == 4) {
                    nowStateJwdEdit.setText("进行中");
                }
                else if (actionStateNumberCar.data == 3) {
                    nowStateJwdEdit.setText("已完成");
                }
                else {
                    nowStateJwdEdit.setText("未开始");
                }
                Log.i("JwdNavigationFragment","action_state："+String.valueOf(actionStateNumberCar.data));
            }
        };
        jwdHandler.post(task);
    }
}