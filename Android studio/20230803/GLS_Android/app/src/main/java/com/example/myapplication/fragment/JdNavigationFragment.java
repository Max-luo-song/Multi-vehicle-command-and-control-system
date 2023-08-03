package com.example.myapplication.fragment;

import android.graphics.Bitmap;
import android.net.Uri;
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

import com.example.myapplication.MyVLCVideoLayout;
import com.example.myapplication.R;
import com.example.myapplication.VideoPlayer;
import com.example.myapplication.message.PlanTargetLocation;
import com.example.myapplication.message.Pose;
import com.example.myapplication.structure.JWD;

import org.videolan.libvlc.LibVLC;
import org.videolan.libvlc.Media;
import org.videolan.libvlc.MediaPlayer;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class JdNavigationFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";

    private String mParam1;

    private VideoPlayer videoPlayer;

    public JdNavigationFragment() {

    }

    private TextView text1;

    public static JdNavigationFragment newInstance(String param1) {
        JdNavigationFragment fragment = new JdNavigationFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.i("navigation","onCreate1");
        super.onCreate(savedInstanceState);
        videoPlayer = new VideoPlayer(getActivity());

        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.i("navigation","onCreateView1");
        View rootView = inflater.inflate(R.layout.fragment_jd_navigation, container, false);
        videoPlayer.GetVideo(rootView);

        return rootView;
    }



    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        //获取视频流
        Log.i("navigation","onViewCreated1");
    }
}