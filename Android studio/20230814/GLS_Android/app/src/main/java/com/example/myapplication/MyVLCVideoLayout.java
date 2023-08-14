package com.example.myapplication;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.videolan.libvlc.util.VLCVideoLayout;

public class MyVLCVideoLayout extends VLCVideoLayout {
    public MyVLCVideoLayout(@NonNull Context context) {
        super(context);
    }

    public MyVLCVideoLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public MyVLCVideoLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public MyVLCVideoLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        setBackgroundColor(getResources().getColor(android.R.color.white));
        final ViewGroup.LayoutParams lp = getLayoutParams();

        lp.height = 1606;
        lp.width = ViewGroup.LayoutParams.MATCH_PARENT;
        setLayoutParams(lp);
    }
}
