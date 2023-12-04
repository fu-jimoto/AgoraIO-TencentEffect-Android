package com.vcube.tencent.effect;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.view.SurfaceView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.tencent.xmagic.XmagicConstant;
import com.tencent.xmagic.XmagicProperty;
import com.tencent.xmagic.telicense.TELicenseCheck;
import com.vcube.tencent.effect.framework.PreprocessorTencentEffect;
import com.vcube.tencent.xmagic.XMagicImpl;
import com.vcube.tencent.xmagic.module.XmagicResParser;


import java.io.File;

import io.agora.rtc2.Constants;
import io.agora.rtc2.IRtcEngineEventHandler;
import io.agora.rtc2.RtcEngine;
import io.agora.rtc2.RtcEngineConfig;
import io.agora.rtc2.video.VideoCanvas;
import io.agora.rtc2.ChannelMediaOptions;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getName();

    private String effectLicenceUrl = "";
    private String effectKey = "";
    private final String appId = "";

    private String channelName = "sample";
    private String token = "";
    private int uid = 0;

    private boolean isJoined = false;
    private RtcEngine agoraEngine;
    private SurfaceView localSurfaceView;
    private SurfaceView remoteSurfaceView;

    private PreprocessorTencentEffect preProcessor;
    private XMagicImpl xMagic = null;
    private boolean isEffect = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (!checkSelfPermission()) {
            ActivityCompat.requestPermissions(this, REQUESTED_PERMISSIONS, PERMISSION_REQ_ID);
        }
        XmagicResParser.setResPath(new File(getFilesDir(), "xmagic").getAbsolutePath());
        copyXMagicRes();

    }


    private void copyXMagicRes() {
        if (isCopyRes()) {
            Log.i(TAG, "Has been copied");
            showMessage("Has been copied");
            auth();
        } else {
            new Thread(() -> {
                XmagicResParser.copyRes(MainActivity.this);
                Log.i(TAG, "copy res success");
                showMessage("copy res success");
                saveCopyData();
                auth();
            }).start();
        }
    }


    private void auth() {
        XMagicImpl.initAuth(this, effectLicenceUrl, effectKey, (errorCode, msg) -> {
            if (errorCode == TELicenseCheck.ERROR_OK) {
                showMessage("Effect Auth Success");
                runOnUiThread(() -> setupVideoSDKEngine());
            } else {
                showMessage("Effect Auth Error");
            }
        });
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (preProcessor != null) {
            preProcessor.releaseRender();
        }
        if (agoraEngine != null) {
            agoraEngine.stopPreview();
            agoraEngine.leaveChannel();
        }

        new Thread(() -> {
            RtcEngine.destroy();
            agoraEngine = null;
        }).start();
    }

    private void setupVideoSDKEngine() {
        try {
            RtcEngineConfig config = new RtcEngineConfig();
            config.mContext = getBaseContext();
            config.mAppId = appId;
            config.mEventHandler = mRtcEventHandler;
            agoraEngine = RtcEngine.create(config);
            agoraEngine.enableVideo();
            xMagic = new XMagicImpl(this);

            preProcessor = new PreprocessorTencentEffect(xMagic);

            preProcessor.setSurfaceListener(new PreprocessorTencentEffect.SurfaceViewListener() {
                @Override
                public void onSurfaceCreated() {

                }

                @Override
                public void onSurfaceDestroyed() {

                    if (xMagic != null) {
                        xMagic.onDestroy();
                    }
                }
            });

        } catch (Exception e) {
            showMessage(e.toString());
        }
    }

    private final IRtcEngineEventHandler mRtcEventHandler = new IRtcEngineEventHandler() {
        @Override
        public void onUserJoined(int uid, int elapsed) {
            showMessage("Remote user joined " + uid);
            runOnUiThread(() -> setupRemoteVideo(uid));
        }

        @Override
        public void onJoinChannelSuccess(String channel, int uid, int elapsed) {
            isJoined = true;
            showMessage("Joined Channel " + channel);
        }

        @Override
        public void onUserOffline(int uid, int reason) {
            showMessage("Remote user offline " + uid + " " + reason);
            runOnUiThread(() -> remoteSurfaceView.setVisibility(View.GONE));
        }
    };


    private void setupRemoteVideo(int uid) {
        FrameLayout container = findViewById(R.id.remote_video_view_container);
        remoteSurfaceView = new SurfaceView(getBaseContext());
        remoteSurfaceView.setZOrderMediaOverlay(true);
        container.addView(remoteSurfaceView);
        agoraEngine.setupRemoteVideo(new VideoCanvas(remoteSurfaceView, VideoCanvas.RENDER_MODE_FIT, uid));
        remoteSurfaceView.setVisibility(View.VISIBLE);
    }

    private void setupLocalVideo() {
        FrameLayout container = findViewById(R.id.local_video_view_container);
        localSurfaceView = new SurfaceView(getBaseContext());
        container.addView(localSurfaceView);
        agoraEngine.setupLocalVideo(new VideoCanvas(localSurfaceView, VideoCanvas.RENDER_MODE_HIDDEN, 0));
    }

    public void joinChannel(View view) {
        if (checkSelfPermission()) {
            ChannelMediaOptions options = new ChannelMediaOptions();
            options.channelProfile = Constants.CHANNEL_PROFILE_COMMUNICATION;
            options.clientRoleType = Constants.CLIENT_ROLE_BROADCASTER;
            setupLocalVideo();
            localSurfaceView.setVisibility(View.VISIBLE);
            agoraEngine.startPreview();
            agoraEngine.joinChannel(token, channelName, uid, options);
        } else {
            Toast.makeText(getApplicationContext(), "Permissions was not granted", Toast.LENGTH_SHORT).show();
        }
    }

    public void leaveChannel(View view) {
        if (!isJoined) {
            showMessage("Join a channel first");
        } else {
            agoraEngine.leaveChannel();
            showMessage("You left the channel");
            if (remoteSurfaceView != null) remoteSurfaceView.setVisibility(View.GONE);
            if (localSurfaceView != null) localSurfaceView.setVisibility(View.GONE);
            agoraEngine.registerVideoFrameObserver(null);
            isJoined = false;
        }
    }

    //https://www.tencentcloud.com/jp/document/product/1143/52514
    //category,ID,resPath,effkey,effValue
    public void updateEffect(View view) {

        Button btn = (Button) findViewById(R.id.effectButton);

        if(isEffect == true){
            btn.setText("EffectON");
            showMessage("Effect OFF");
            xMagic.updateProperty(null);
            agoraEngine.registerVideoFrameObserver(null);
            isEffect = false;
        }else{
            btn.setText("EffectOFF");
            showMessage("Update Effect");
            agoraEngine.registerVideoFrameObserver(preProcessor);

            //美肌
            XmagicProperty xmagicProperty = new XmagicProperty(
                    XmagicProperty.Category.BEAUTY,
                    null,
                    null,
                    XmagicConstant.BeautyConstant.BEAUTY_SMOOTH,
                    new XmagicProperty.XmagicPropertyValues(0, 100, 50, 0, 1));

            //目の大きさ
//            XmagicProperty xmagicProperty = new XmagicProperty(
//                    XmagicProperty.Category.BEAUTY,
//                    null,
//                    null,
//                    XmagicConstant.BeautyConstant.BEAUTY_ENLARGE_EYE,
//                    new XmagicProperty.XmagicPropertyValues(0, 100, 100, 0, 1));

            //口紅
//            XmagicProperty xmagicProperty = new XmagicProperty(
//                    XmagicProperty.Category.BEAUTY,
//                    XmagicConstant.BeautyConstant.BEAUTY_LIPS_LIPS_MASK,
//                    "/images/beauty/lips_fuguhong.png",
//                    XmagicConstant.BeautyConstant.BEAUTY_MOUTH_LIPSTICK,
//                    new XmagicProperty.XmagicPropertyValues(0, 100, 100, 0, 1));
            //Filter
//            XmagicProperty xmagicProperty = new XmagicProperty(
//                    XmagicProperty.Category.LUT,
//                    "baixi_lf.png",
//                    XmagicResParser.getResPath()+"light_material/lut/baixi_lf.png",
//                    null,
//                    new XmagicProperty.XmagicPropertyValues(0, 100, 60, 0, 1));
            //Animation
//            XmagicProperty xmagicProperty = new XmagicProperty(
//                    XmagicProperty.Category.MOTION,
//                    "video_keaituya",
//                    XmagicResParser.getResPath()+"MotionRes/2dMotionRes/video_keaituya",
//                    null,
//                    null);

            xMagic.updateProperty(xmagicProperty);
            isEffect = true;
        }
    }

    private static final int PERMISSION_REQ_ID = 22;
    private static final String[] REQUESTED_PERMISSIONS =
            {
                    Manifest.permission.RECORD_AUDIO,
                    Manifest.permission.CAMERA
            };

    private boolean checkSelfPermission() {
        if (ContextCompat.checkSelfPermission(this, REQUESTED_PERMISSIONS[0]) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, REQUESTED_PERMISSIONS[1]) != PackageManager.PERMISSION_GRANTED) {
            return false;
        }
        return true;
    }

    void showMessage(String message) {
        runOnUiThread(() ->
                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show());
    }


    private boolean isCopyRes() {
        String appVersionName = AppUtils.getAppVersionName(this);
        SharedPreferences sp = getSharedPreferences("demo_settings", Context.MODE_PRIVATE);
        String savedVersionName = sp.getString("resource_copied", "");
        return savedVersionName.equals(appVersionName);
    }

    private void saveCopyData() {
        String appVersionName = AppUtils.getAppVersionName(this);
        getSharedPreferences("demo_settings", Context.MODE_PRIVATE).edit()
                .putString("resource_copied", appVersionName).commit();
    }

}