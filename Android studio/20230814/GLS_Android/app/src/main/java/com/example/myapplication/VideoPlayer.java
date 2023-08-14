package com.example.myapplication;

import android.net.Uri;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import org.videolan.libvlc.LibVLC;
import org.videolan.libvlc.Media;
import org.videolan.libvlc.MediaPlayer;

import java.util.ArrayList;

/**
 * 视频流播放类
 */
public class VideoPlayer {
    //不使用TextureView渲染视频
    private static final boolean USE_TEXTURE_VIEW = false;
    //不显示视频流字幕
    private static final boolean ENABLE_SUBTITLES = false;

    final ArrayList<String> args ;

    private MyVLCVideoLayout mVideoLayout;
    private LibVLC mLibVLC;
    private MediaPlayer mMediaPlayer;
    private Media media;
    private String address;

    public VideoPlayer(FragmentActivity activity){
        args = new ArrayList<>();
        args.add("-vvv");  //开启调试选项，以在控制台输出详细的控制信息
        mLibVLC = new LibVLC(activity, args);
        mMediaPlayer = new MediaPlayer(mLibVLC);

    }


    public void GetVideo(@NonNull View view, String ip) {
        Log.i("VideoPlayer","enter GetVideo");
        address = "rtsp://" + ip + ":8554/test";
        Log.i("VideoPlayer","address is:"+address);
        media = new Media(mLibVLC, Uri.parse(address));
        Log.i("VideoPlayer","after new media");
        mVideoLayout = view.findViewById(R.id.video_layout);
        Log.i("VideoPlayer","after findView");
        //绑定mVideoLayout，不监听布局变化null，是否开启字幕ENABLE_SUBTITLES,是否使用TextureView渲染视频
        mMediaPlayer.attachViews(mVideoLayout, null, ENABLE_SUBTITLES, USE_TEXTURE_VIEW);
        Log.i("VideoPlayer","after attach");
        mMediaPlayer.setMedia(media);
        Log.i("VideoPlayer","after setmedia");
        media.release();
        Log.i("VideoPlayer","after release");
        mMediaPlayer.play();
        Log.i("VideoPlayer","after play");
    }
}
