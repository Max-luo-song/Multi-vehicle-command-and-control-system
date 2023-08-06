package com.example.myapplication.fragment;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.myapplication.MyVLCVideoLayout;
import com.example.myapplication.R;

import org.videolan.libvlc.LibVLC;
import org.videolan.libvlc.Media;
import org.videolan.libvlc.MediaPlayer;

import java.io.IOException;
import java.util.ArrayList;

public class VideoFragment extends Fragment {
    //不使用TextureView渲染视频
    private static final boolean USE_TEXTURE_VIEW = false;

    //不显示视频流字幕
    private static final boolean ENABLE_SUBTITLES = false;

    private static final String ASSET_FILENAME = "bbb.m4v";

    private MyVLCVideoLayout mVideoLayout = null;
    private LibVLC mLibVLC = null;
    private MediaPlayer mMediaPlayer = null;
    private Media media = null;
    private Button playLocalBtn, continueBtn, pauseBtn;
    private SearchView searchView = null;
    private TextView getVedioDesText = null;
    private String address;


    public VideoFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final ArrayList<String> args = new ArrayList<>();
        //开启调试选项，以在控制台输出详细的控制信息
        args.add("-vvv");
        mLibVLC = new LibVLC(getActivity(), args);
        mMediaPlayer = new MediaPlayer(mLibVLC);
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_video, container, false);
    }

    public void FindViewById(View view) {
        mVideoLayout = view.findViewById(R.id.video_layout);
        playLocalBtn = view.findViewById(R.id.play_local_btn);
        continueBtn = view.findViewById(R.id.continue_btn);
        pauseBtn = view.findViewById(R.id.pause_btn);
        searchView = view.findViewById(R.id.search_view);
        getVedioDesText = view.findViewById(R.id.get_vedio_des_text);
    }

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        FindViewById(view);
//        mMediaPlayer.setPosition(MediaPlayer.Position.Top);
        System.out.println("===============" + mMediaPlayer.getPosition());
        searchView.setOnQueryTextListener(new mListener());
        playLocalBtn.setOnClickListener(new mClick());
        continueBtn.setOnClickListener(new mClick());
        pauseBtn.setOnClickListener(new mClick());
    }

    //设置搜索文本监听
    private class mListener implements SearchView.OnQueryTextListener {
        //当点击键盘中搜索按钮时触发该方法
        @Override
        public boolean onQueryTextSubmit(String query) {
            address = query;
            if (mMediaPlayer.isPlaying()) {
                mMediaPlayer.pause();
            }

            // 分离现有的mVideoLayout和MediaPlayer(确保每次搜索后，播放新视频时都会重新绑定所需的View)
            mMediaPlayer.detachViews();

            //绑定mVideoLayout，不监听布局变化null，是否开启字幕ENABLE_SUBTITLES,是否使用TextureView渲染视频
            mMediaPlayer.attachViews(mVideoLayout, null, ENABLE_SUBTITLES, USE_TEXTURE_VIEW);

            // 根据输入网址播放对应视频流
            media = new Media(mLibVLC, Uri.parse(address));
            mMediaPlayer.setMedia(media);
            media.release();
            mMediaPlayer.play();
            getVedioDesText.setText("获取视频流：" + query);
            return true;
        }

        //当搜索内容改变时触发该方法
        @Override
        public boolean onQueryTextChange(String newText) {
            return false;
        }
    }

    //添加播放按钮事件(默认播放本地视频)
    private class mClick implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            if (v == playLocalBtn) {
                if (mMediaPlayer.isPlaying()) {
                    mMediaPlayer.pause();
                }
                // 分离现有的Surface视图和MediaPlayer(确保每次播放新视频时都会正确重新绑定所需的View)
                mMediaPlayer.detachViews();

                //绑定布局mVideoLayout，不监听布局变化null，是否开启字幕ENABLE_SUBTITLES,是否使用渲染视频TextureView
                mMediaPlayer.attachViews(mVideoLayout, null, ENABLE_SUBTITLES, USE_TEXTURE_VIEW);
                //播放本地视频
                try {
                    media = new Media(mLibVLC, getActivity().getAssets().openFd(ASSET_FILENAME));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                mMediaPlayer.setMedia(media);
                media.release();
                mMediaPlayer.play();
                pauseBtn.setEnabled(true);
                continueBtn.setEnabled(false);
            } else if (v == continueBtn) {
                mMediaPlayer.play();
                continueBtn.setEnabled(false);
                pauseBtn.setEnabled(true);
            } else if (v == pauseBtn) {
                mMediaPlayer.pause();
                pauseBtn.setEnabled(false);
                continueBtn.setEnabled(true);

            }
        }

    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if(!isVisibleToUser){
            if(mMediaPlayer.isPlaying()){
                mMediaPlayer.stop();

            }
        }
    }
}