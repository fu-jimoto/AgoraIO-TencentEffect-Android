package com.vcube.tencent.effect;

import android.content.Context;

import io.agora.base.VideoFrame;
import io.agora.rtc2.video.IVideoFrameObserver;

public class PreprocessorTencentEffect implements IVideoFrameObserver {
    private Context mContext;

    public PreprocessorTencentEffect(Context context) {
        mContext = context;
        //mDefaultFilterPath = getDefaultFilterPath();
    }


    @Override
    public boolean onCaptureVideoFrame(int sourceType, VideoFrame videoFrame) {
        return false;
    }

    @Override
    public boolean onPreEncodeVideoFrame(int sourceType, VideoFrame videoFrame) {
        return false;
    }

    @Override
    public boolean onMediaPlayerVideoFrame(VideoFrame videoFrame, int mediaPlayerId) {
        return false;
    }

    @Override
    public boolean onRenderVideoFrame(String channelId, int uid, VideoFrame videoFrame) {
        return false;
    }

    @Override
    public int getVideoFrameProcessMode() {
        return 0;
    }

    @Override
    public int getVideoFormatPreference() {
        return 0;
    }

    @Override
    public boolean getRotationApplied() {
        return false;
    }

    @Override
    public boolean getMirrorApplied() {
        return false;
    }

    @Override
    public int getObservedFramePosition() {
        return 0;
    }
}
