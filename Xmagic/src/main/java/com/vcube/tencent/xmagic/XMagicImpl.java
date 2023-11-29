package com.vcube.tencent.xmagic;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.view.View;

import com.tencent.xmagic.XmagicApi;
import com.tencent.xmagic.XmagicProperty;
import com.tencent.xmagic.auth.Auth;
import com.tencent.xmagic.telicense.TELicenseCheck;
import com.vcube.tencent.xmagic.module.XmagicResParser;

public class XMagicImpl implements SensorEventListener {

    XmagicApi mXmagicApi;
    public static boolean authCheck = false;
    public static boolean initAuth(Context context, String mXMagicLicenceUrl, String mXMagicKey) {
        Auth.AuthResult result = null;

        TELicenseCheck.getInstance().setTELicense(context, mXMagicLicenceUrl, mXMagicKey, new TELicenseCheck.TELicenseCheckListener() {
            @Override
            public void onLicenseCheckFinish(int errorCode, String msg) {
                //注意：このコールバックは呼び出しスレッド内にあるとは限りません
                if (errorCode == TELicenseCheck.ERROR_OK) {
                    //認証に成功しました
                    authCheck = true;
                }else{
                    //認証に失敗しました
                    authCheck = false;
                }
            }
        });
        return authCheck;
    }


    public XMagicImpl(Context context) {
        mXmagicApi = new XmagicApi(context, XmagicResParser.getResPath(), new XmagicApi.OnXmagicPropertyErrorListener() {
            @Override
            public void onXmagicPropertyError(String errorMsg, int code) {
//                if (context instanceof Activity) {
//                    ((Activity) context).runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            mXmagicPanelViewController.tvErrorMsg.setVisibility(View.VISIBLE);
//                            mXmagicPanelViewController.tvErrorMsg.setText(code + ":" + errorMsg);
//                        }
//                    });
//                }
            }
        });
    }

    public int process(int textureId, int width, int height) {
        if (mXmagicApi != null)
            return mXmagicApi.process(textureId, width, height);

        return textureId;
    }
    public void updateProperty(XmagicProperty<?> xmagicProperty) {
        if (mXmagicApi != null && xmagicProperty != null)
            mXmagicApi.updateProperty(xmagicProperty);
    }

    public void onResume() {
        if (mXmagicApi != null)
            mXmagicApi.onResume();
    }

    public void onDestroy() {
        if (mXmagicApi != null)
            mXmagicApi.onDestroy();
    }

    public void onPause() {
        if (mXmagicApi != null)
            mXmagicApi.onPause();
    }




    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}