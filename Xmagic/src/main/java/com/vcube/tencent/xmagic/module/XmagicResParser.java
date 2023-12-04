package com.vcube.tencent.xmagic.module;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

//import com.tencent.xmagic.demo.R;
import com.tencent.xmagic.XmagicConstant.BeautyConstant;
import com.tencent.xmagic.XmagicProperty;
import com.tencent.xmagic.XmagicProperty.XmagicPropertyValues;
//import com.tencent.xmagic.module.XmagicUIProperty.UICategory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class XmagicResParser {

    private static final String TAG = XmagicResParser.class.getSimpleName();
    private static String sResPath;


    private XmagicResParser() {/*nothing*/}

    public static void setResPath(String path) {
        if (!path.endsWith(File.separator)) {
            path = path + File.separator;
        }
        sResPath = path;
    }

    public static String getResPath() {
        ensureResPathAlreadySet();
        return sResPath;
    }

    public static void copyRes(Context context) {
        ensureResPathAlreadySet();

        new File(sResPath, "light_assets").delete();
        new File(sResPath, "light_material").delete();
        new File(sResPath, "MotionRes").delete();

        for (String path : new String[]{"Light3DPlugin", "LightCore", "LightHandPlugin", "LightSegmentPlugin"}) {
            copyAssets(context, path, sResPath + "light_assets");
        }

        for (String path : new String[]{"lut"}) {
            copyAssets(context, path, sResPath + "light_material" + File.separator + path);
        }

        for (String path : new String[]{"MotionRes"}) {
            copyAssets(context, path, sResPath + path);
        }
    }

    private static boolean copyAssets(Context context, String oldPath, String newPath) {
        try {
            String fileNames[] = context.getAssets().list(oldPath);
            if (fileNames.length > 0) {
                Log.e(TAG, "copyAssets path: " + Arrays.toString(fileNames));
                File file = new File(newPath);
                file.mkdirs();
                for (String fileName : fileNames) {
                    copyAssets(context, oldPath + "/" + fileName, newPath + "/" + fileName);
                }
            } else {
                InputStream is = context.getAssets().open(oldPath);
                FileOutputStream fos = new FileOutputStream(new File(newPath));
                byte[] buffer = new byte[1024 * 1024];
                int byteCount = 0;
                while ((byteCount = is.read(buffer)) != -1) {
                    fos.write(buffer, 0, byteCount);
                }
                fos.flush();
                is.close();
                fos.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    private static void ensureResPathAlreadySet() {
        if (TextUtils.isEmpty(sResPath)) {
            throw new IllegalStateException("resource path not set, call XmagicResParser.setResPath() first.");
        }
    }
}
