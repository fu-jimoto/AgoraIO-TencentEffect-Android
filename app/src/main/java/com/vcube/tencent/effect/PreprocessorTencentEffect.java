package com.vcube.tencent.effect;

import android.content.Context;

import com.vcube.tencent.xmagic.XMagicImpl;

import java.util.concurrent.Callable;

import io.agora.base.TextureBufferHelper;
import io.agora.base.VideoFrame;
import io.agora.rtc2.video.IVideoFrameObserver;

public class PreprocessorTencentEffect implements IVideoFrameObserver {
    private XMagicImpl mXMagic;
    private Context mContext;
    private TextureBufferHelper textureBufferHelper;
    private int rgbaID = -1;

    public PreprocessorTencentEffect(Context context) {
        mContext = context;
        //mDefaultFilterPath = getDefaultFilterPath();
    }

    @Override
    public boolean onCaptureVideoFrame(int sourceType, VideoFrame videoFrame) {
        VideoFrame.Buffer buffer = videoFrame.getBuffer();
        if(!(buffer instanceof VideoFrame.TextureBuffer)){
            return false;
        }


        int width = buffer.getWidth();
        int height = buffer.getHeight();
        int texId = mXMagic.process(rgbaID, height,width);


//        VideoFrame.TextureBuffer textureBuffer = (VideoFrame.TextureBuffer) buffer;
//        if(textureBufferHelper == null){
//            textureBufferHelper = TextureBufferHelper.create("Render", textureBuffer.getEglBaseContext());
//            textureBuffer.getTextureId();
//            textureBufferHelper.invoke((Callable<Void>) () -> {
////                mEffectHelper = new EffectHelper(mContext);
////                mEffectHelper.initEffectSDK();
////                mEnabled = true;
////                setFilter(getDefaultFilterPath());
//                return null;
//            });
//        }


        return true;

//        int w = buffer.getWidth();
//        int h = buffer.getHeight();
//        int cropX = (w - 320)/2, cropY = (h - 240)/2, cropWidth = 320, cropHeight = 240, scaleWidth = 320, scaleHeight = 240;
//        buffer = buffer.cropAndScale(cropX, cropY, cropWidth, cropHeight, scaleWidth, scaleHeight);
//        videoFrame.replaceBuffer(buffer, 270, videoFrame.getTimestampNs());
//        return true;



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
        return 1;
    }

    @Override
    public int getVideoFormatPreference() {
        return 1;
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
