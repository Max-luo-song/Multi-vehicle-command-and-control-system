package com.example.myapplication.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.myapplication.R;
import com.example.myapplication.VideoPlayer;
import com.example.myapplication.message.PlanTargetLocation;
import com.example.myapplication.message.Pose;
import com.example.myapplication.structure.JWD;
import java.util.Timer;
import java.util.TimerTask;


public class XdNavigationFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";

    private String mParam1;

    private VideoPlayer videoPlayer;


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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.i("navigation","onCreateView2");
        View rootView = inflater.inflate(R.layout.fragment_xd_navigation, container, false);
//        text1 =rootView.findViewById(R.id.now_state_xd_edit);
//        int secondsInHour = 3600; // 一小时有3600秒
//        int interval = 1000; // 每隔1秒执行一次
//
//        Timer timer = new Timer();
//        timer.scheduleAtFixedRate(new TimerTask() {
//            int remainingSeconds = secondsInHour;
//
//            @Override
//            public void run() {
//                int hours = remainingSeconds / 3600;
//                int minutes = (remainingSeconds % 3600) / 60;
//                int seconds = remainingSeconds % 60;
//                String res = String.valueOf(hours) +":" +  String.valueOf(minutes)  +":" +   String.valueOf(seconds);
//                text1.setText(res);
//                videoPlayer.GetVideo(rootView);
//
//                remainingSeconds--;
//            }
//        }, 0, interval);
        return rootView;
    }

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        //获取视频流
        Log.i("navigation","onViewCreated2");
    }
}