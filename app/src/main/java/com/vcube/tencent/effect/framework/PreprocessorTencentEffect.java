package com.vcube.tencent.effect.framework;

import android.content.Context;
import android.graphics.Matrix;
import android.graphics.SurfaceTexture;
import android.opengl.GLES20;
import android.util.Log;

import com.vcube.tencent.xmagic.XMagicImpl;
import static com.tencent.xmagic.GlUtil.createTexture;
import java.util.concurrent.Callable;

import io.agora.base.TextureBufferHelper;
import io.agora.base.VideoFrame;
import io.agora.rtc2.video.IVideoFrameObserver;

public class PreprocessorTencentEffect implements IVideoFrameObserver {
    private final static String TAG = PreprocessorTencentEffect.class.getSimpleName();
    private Context mContext;
    private TextureBufferHelper textureBufferHelper;
    private  XMagicImpl mXMagic;
    private int rgbaID = -1;
    private int outputID = -1;
    private final float[] mOesMatrix = new float[16];
    private OESRenderer mYUV2RGBAConverter;
    private RotateRenderer mRotateRender;
    private SurfaceTexture mSurfaceTexture;

    public PreprocessorTencentEffect(Context context) {
        mContext = context;
    }

    @Override
    public boolean onCaptureVideoFrame(int sourceType, VideoFrame videoFrame) {
        VideoFrame.Buffer buffer = videoFrame.getBuffer();
        if(!(buffer instanceof VideoFrame.TextureBuffer)){
            return false;
        }
        VideoFrame.TextureBuffer textureBuffer = (VideoFrame.TextureBuffer) buffer;

        int srcID = textureBuffer.getTextureId();
        int width = buffer.getWidth();
        int height = buffer.getHeight();
        if (mYUV2RGBAConverter == null) {
            mYUV2RGBAConverter = new OESRenderer();
            mYUV2RGBAConverter.init();
            mYUV2RGBAConverter.setRotateAndFlip(180,0,1);
            mSurfaceTexture = new SurfaceTexture(srcID);
            rgbaID = createTexture(height,width, GLES20.GL_RGBA);
        }

        if (mRotateRender == null) {
            mRotateRender = new RotateRenderer();
            mRotateRender.init();
            outputID = createTexture(width,height, GLES20.GL_RGBA);
        }

        mSurfaceTexture.updateTexImage();
        mSurfaceTexture.getTransformMatrix(mOesMatrix);
        mYUV2RGBAConverter.doRender(srcID, rgbaID, height, width, mOesMatrix, null, null);

        Integer texId = textureBufferHelper.invoke((Callable<Integer>) () -> mXMagic.process(rgbaID, height,width));
        mRotateRender.doRender(texId,outputID,width,height,null,null,null);

        VideoFrame.TextureBuffer retBuffer = textureBufferHelper.wrapTextureBuffer(textureBuffer.getWidth(), textureBuffer.getHeight(), VideoFrame.TextureBuffer.Type.RGB,
                texId, new Matrix());
        videoFrame.replaceBuffer(retBuffer, videoFrame.getRotation(), videoFrame.getTimestampNs());

        return true;
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
