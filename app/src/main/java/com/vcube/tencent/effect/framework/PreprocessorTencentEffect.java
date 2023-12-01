package com.vcube.tencent.effect.framework;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.SurfaceTexture;
import android.opengl.GLES20;
import android.util.Log;

import com.tencent.xmagic.GlUtil;
import com.vcube.tencent.xmagic.XMagicImpl;

import java.nio.ByteBuffer;

import io.agora.base.TextureBuffer;
import io.agora.base.TextureBufferHelper;
import io.agora.base.VideoFrame;
import io.agora.base.internal.video.YuvHelper;
import io.agora.rtc2.gl.EglBaseProvider;
import io.agora.rtc2.video.IVideoFrameObserver;

public class PreprocessorTencentEffect implements IVideoFrameObserver {
    private final static String TAG = PreprocessorTencentEffect.class.getSimpleName();
    private XMagicImpl mXMagic;

    private boolean renderSwitch = true;
    private TextureBufferHelper mTextureBufferHelper;


    private android.graphics.Matrix transformMatrix;

    private TextureConverter mTextureConverter = null;
    private TextureConverter mRevertConverter = null;


    public PreprocessorTencentEffect(XMagicImpl xMagic) {
        this.mXMagic = xMagic;
    }

    @Override
    public boolean onCaptureVideoFrame(int sourceType, VideoFrame videoFrame) {
        if (!renderSwitch) {
            return true;
        }
        int outputID = -1;
        Log.e(TAG, "" + sourceType + videoFrame.getBuffer().getClass().getName());
        if (mTextureBufferHelper == null) {
            mTextureBufferHelper = TextureBufferHelper.create("STRender", EglBaseProvider.instance().getRootEglBase().getEglBaseContext());
            if (mTextureBufferHelper != null) {
                mTextureBufferHelper.invoke(() -> {
                    if (mSurfaceViewListener != null) {
                        mSurfaceViewListener.onSurfaceCreated();
                    }
                    return null;
                });
            }
        }

        VideoFrame.Buffer buffer = videoFrame.getBuffer();
        int width = buffer.getWidth();
        int height = buffer.getHeight();
        int rotation = videoFrame.getRotation();

        if (mTextureBufferHelper != null) {
            if (videoFrame.getBuffer() instanceof VideoFrame.TextureBuffer) {
                TextureBuffer textureBuffer = (TextureBuffer) videoFrame.getBuffer();
                outputID = mTextureBufferHelper.invoke(() -> {
                            int rgbaId = mTextureBufferHelper.convertToRGBA(textureBuffer, 0);
                            return processVideo(rgbaId, width, height, true);
                        }
                );
                transformMatrix = textureBuffer.getTransformMatrix();
            }
        }


        if (outputID <= 0) {
            return false;
        }

        if (mTextureBufferHelper != null) {
            VideoFrame.TextureBuffer textureBuffer = mTextureBufferHelper.wrapTextureBuffer(
                    width, height, VideoFrame.TextureBuffer.Type.RGB, outputID, transformMatrix);
            videoFrame.replaceBuffer(textureBuffer, rotation, videoFrame.getTimestampNs());
        }
        return true;
    }


    private int processVideo(int rgbaId, int width, int height, boolean isFontCamera) {
        if (mTextureConverter == null) {
            mTextureConverter = new TextureConverter();
            mRevertConverter = new TextureConverter();
        }
        if (mXMagic != null) {
            int rotateId = mTextureConverter.convert(rgbaId, width, height, isFontCamera ? 270 : 90, false, false);
            int processId = mXMagic.process(rotateId, height, width);
            int resultId = mRevertConverter.convert(processId, height, width, isFontCamera ? 90 : 270, isFontCamera, false);
            GLES20.glFinish();
            return resultId;
        }
        return rgbaId;
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
        return PROCESS_MODE_READ_WRITE;
    }

    @Override
    public int getVideoFormatPreference() {
        return VIDEO_PIXEL_DEFAULT;
    }

    @Override
    public boolean getRotationApplied() {
        return false;
    }

    @Override
    public boolean getMirrorApplied() {
        return true;
    }

    @Override
    public int getObservedFramePosition() {
        return POSITION_POST_CAPTURER;
    }


    private SurfaceViewListener mSurfaceViewListener;

    public interface SurfaceViewListener {
        void onSurfaceCreated();

        void onSurfaceDestroyed();
    }

    public void setSurfaceListener(SurfaceViewListener surfaceViewListener) {
        this.mSurfaceViewListener = surfaceViewListener;
    }

    public void releaseRender() {
        renderSwitch = false;
        if (mTextureBufferHelper != null) {
            mTextureBufferHelper.invoke(() -> {
                if (mSurfaceViewListener != null) {
                    mSurfaceViewListener.onSurfaceDestroyed();
                }
                return null;
            });
            mTextureBufferHelper.dispose();
            mTextureBufferHelper = null;
        }
    }
}
