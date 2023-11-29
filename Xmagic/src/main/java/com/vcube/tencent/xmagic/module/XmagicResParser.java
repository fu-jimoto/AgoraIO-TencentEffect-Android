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
    //private static List<XmagicPropertyData> sAllProperties;

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

    /*
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
    }*/
/*
    public static void parseRes() {
        File file = new File(sResPath);
        String[] list = file.list();
        if (!file.exists() || list == null || list.length == 0) {
            throw new IllegalStateException("resource dir not found or empty, call XmagicResParser.copyRes first.");
        }

        final List<XmagicPropertyData> allProperties = new ArrayList<>();
        //美颜列表
        pareBeauty(allProperties);
        //滤镜列表
        parseLutProperty(allProperties);
        //动效列表
        parseMotion(allProperties);
        sAllProperties = allProperties;
    }*/

    /*private static class LutTemporaryProperty {
        public String respath;
        public String name;
        public int icons;
        public float displayDefaultValue;

        public LutTemporaryProperty(String name, String respath, int icons, float displayDefaultValue) {
            this.respath = respath;
            this.name = name;
            this.icons = icons;
            this.displayDefaultValue = displayDefaultValue;
        }
    }*/

    /*private static void parseLutProperty(List<XmagicPropertyData> allProperties) {
        Map<String, LutTemporaryProperty> lutIdNames = new LinkedHashMap<>();
        lutIdNames.put("n_baixi.png", new LutTemporaryProperty("白皙", "n_baixi.png", R.mipmap.filter_baizhi, 60));
        lutIdNames.put("n_ziran.png", new LutTemporaryProperty("自然", "n_ziran.png", R.mipmap.filter_ziran, 60));
        lutIdNames.put("moren_lf.png", new LutTemporaryProperty("初恋", "moren_lf.png", R.mipmap.filter_chulian, 60));
        lutIdNames.put("xindong_lf.png", new LutTemporaryProperty("心动", "xindong_lf.png", R.mipmap.filter_xindong, 60));
        lutIdNames.put("dongjing_lf.png", new LutTemporaryProperty("东京", "dongjing_lf.png", R.mipmap.filter_dongjing, 60));
        //收集全部滤镜
        String lutDir = sResPath + "light_material/lut/";
        String[] lutFilelist = new File(lutDir).list();
        if(lutFilelist == null)
            return;
        final List<XmagicPropertyData> allLutProperties = new ArrayList<>();
        for (String lutId : lutFilelist) {
            LutTemporaryProperty lut = lutIdNames.get(lutId);
            if (lut != null) {
                String resPath = lutDir + lutId;
                allLutProperties.add(new XmagicPropertyData(new XmagicUIProperty<>(UICategory.LUT, lut.name, lutId, resPath, lut.icons, null, new XmagicPropertyValues(0, 100, lut.displayDefaultValue, 0, 1))));
            }
        }
        //添加 "无" 滤镜
        allProperties.add(new XmagicPropertyData(new XmagicUIProperty<>(UICategory.LUT, "无", XmagicProperty.ID_NONE, "", R.mipmap.naught, null, null)));
        //按顺序添加滤镜
        for (String expectLutId : lutIdNames.keySet()) {
            for (XmagicPropertyData lutProperty : allLutProperties) {
                if (TextUtils.equals(expectLutId, lutProperty.uiProperty.property.id)) {
                    allProperties.add(lutProperty);
                    break;
                }
            }
        }
    }*/

    /*
    private static void pareBeauty(List<XmagicPropertyData> allProperties) {
        String effDirs = "/images/beauty/";
        Map<String, String> effs = new HashMap<>();
        for (String effPath : new File(sResPath + "light_assets" + effDirs).list()) {
            effs.put(new File(effPath).getName(), effPath);
        }
        allProperties.add(new XmagicPropertyData(new XmagicUIProperty<>(UICategory.BEAUTY, "美白", R.mipmap.beauty_whiten, BeautyConstant.BEAUTY_WHITEN, new XmagicPropertyValues(0, 100, 30, 0, 1))));
        allProperties.add(new XmagicPropertyData(new XmagicUIProperty<>(UICategory.BEAUTY, "磨皮", R.mipmap.beauty_smooth, BeautyConstant.BEAUTY_SMOOTH, new XmagicPropertyValues(0, 100, 50, 0, 1))));
        allProperties.add(new XmagicPropertyData(new XmagicUIProperty<>(UICategory.BEAUTY, "红润", R.mipmap.beauty_ruddy, BeautyConstant.BEAUTY_ROSY, new XmagicPropertyValues(0, 100, 20, 0, 2))));
        allProperties.add(new XmagicPropertyData(new XmagicUIProperty<>(UICategory.BEAUTY, "对比度", R.mipmap.image_contrast, BeautyConstant.BEAUTY_CONTRAST, new XmagicPropertyValues(-100, 100, 0, -1, 1))));
        allProperties.add(new XmagicPropertyData(new XmagicUIProperty<>(UICategory.BEAUTY, "饱和度", R.mipmap.image_saturation, BeautyConstant.BEAUTY_SATURATION, new XmagicPropertyValues(-100, 100, 0, -1, 1))));
        allProperties.add(new XmagicPropertyData(new XmagicUIProperty<>(UICategory.BEAUTY, "清晰度", R.mipmap.image_sharpen, BeautyConstant.BEAUTY_CLEAR, new XmagicPropertyValues(0, 100, 0, 0, 2))));
        allProperties.add(new XmagicPropertyData(new XmagicUIProperty<>(UICategory.BEAUTY, "大眼", R.mipmap.beauty_enlarge_eye, BeautyConstant.BEAUTY_ENLARGE_EYE, new XmagicPropertyValues(0, 100, 20, 0, 1))));
        List<XmagicUIProperty<?>> itemThinFacePropertys = new ArrayList<>();
        itemThinFacePropertys.add(new XmagicUIProperty<>(UICategory.BEAUTY, "自然", "nature", null, R.mipmap.beauty_thin_face1, BeautyConstant.BEAUTY_FACE_NATURE, new XmagicPropertyValues(0, 100, 30, 0, 1), "瘦脸"));
        itemThinFacePropertys.add(new XmagicUIProperty<>(UICategory.BEAUTY, "女神", "femaleGod", null, R.mipmap.beauty_thin_face2, BeautyConstant.BEAUTY_FACE_GODNESS, new XmagicPropertyValues(0, 100, 0, 0, 1), "瘦脸"));
        itemThinFacePropertys.add(new XmagicUIProperty<>(UICategory.BEAUTY, "英俊", "maleGod", null, R.mipmap.beauty_thin_face3, BeautyConstant.BEAUTY_FACE_MALE_GOD, new XmagicPropertyValues(0, 100, 0, 0, 1), "瘦脸"));
        XmagicPropertyData thinFace = new XmagicPropertyData(new XmagicUIProperty("瘦脸", R.mipmap.beauty_thin_face, UICategory.BEAUTY), itemThinFacePropertys);
        allProperties.add(thinFace);
        allProperties.add(new XmagicPropertyData(new XmagicUIProperty<>(UICategory.BEAUTY, "V脸", R.mipmap.beauty_v_face, BeautyConstant.BEAUTY_FACE_V, new XmagicPropertyValues(0, 100, 30, 0, 1))));
        allProperties.add(new XmagicPropertyData(new XmagicUIProperty<>(UICategory.BEAUTY, "窄脸", R.mipmap.beauty_narrow_face, BeautyConstant.BEAUTY_FACE_THIN, new XmagicPropertyValues(0, 100, 0, 0, 1))));
        allProperties.add(new XmagicPropertyData(new XmagicUIProperty<>(UICategory.BEAUTY, "短脸", R.mipmap.beauty_short_face, BeautyConstant.BEAUTY_FACE_SHORT, new XmagicPropertyValues(0, 100, 0, 0, 1))));
        allProperties.add(new XmagicPropertyData(new XmagicUIProperty<>(UICategory.BEAUTY, "脸型", R.mipmap.beauty_basic_face, BeautyConstant.BEAUTY_FACE_BASIC, new XmagicPropertyValues(0, 100, 0, 0, 1))));
        Map<String, String> lipsResPathNames = new LinkedHashMap<>();
        lipsResPathNames.put("lips_fuguhong.png", "复古红");
        lipsResPathNames.put("lips_mitaose.png", "蜜桃色");
        lipsResPathNames.put("lips_shanhuju.png", "珊瑚橘");
        lipsResPathNames.put("lips_wenroufen.png", "温柔粉");
        lipsResPathNames.put("lips_huolicheng.png", "活力橙");
        List<XmagicUIProperty<?>> itemLipsPropertys = new ArrayList<>();
        String lipId = "beauty.lips.lipsMask";
        for (String ids : lipsResPathNames.keySet()) {
            itemLipsPropertys.add(new XmagicUIProperty<>(UICategory.BEAUTY, lipsResPathNames.get(ids), lipId, effDirs + effs.get(ids), R.mipmap.beauty_lips, BeautyConstant.BEAUTY_MOUTH_LIPSTICK, new XmagicPropertyValues(0, 100, 50, 0, 1), "口红"));
        }
        XmagicPropertyData itemLips = new XmagicPropertyData(new XmagicUIProperty<>("口红", R.mipmap.beauty_lips, UICategory.BEAUTY), itemLipsPropertys);
        allProperties.add(itemLips);
        Map<String, String> redcheeksResPathNames = new LinkedHashMap<>();
        redcheeksResPathNames.put("saihong_jianyue.png", "简约");
        redcheeksResPathNames.put("saihong_shengxia.png", "盛夏");
        redcheeksResPathNames.put("saihong_haixiu.png", "害羞");
        redcheeksResPathNames.put("saihong_chengshu.png", "成熟");
        redcheeksResPathNames.put("saihong_queban.png", "雀斑");

        List<XmagicUIProperty<?>> itemRedcheekPropertys = new ArrayList<>();
        String redcheekId = "beauty.makeupMultiply.multiplyMask";
        for (String ids : redcheeksResPathNames.keySet()) {
            itemRedcheekPropertys.add(new XmagicUIProperty<>(UICategory.BEAUTY, redcheeksResPathNames.get(ids), redcheekId, effDirs + effs.get(ids), R.mipmap.beauty_redcheeks, BeautyConstant.BEAUTY_FACE_RED_CHEEK, new XmagicPropertyValues(0, 100, 50, 0, 1), "腮红"));
        }
        XmagicPropertyData itemRedcheeks = new XmagicPropertyData(new XmagicUIProperty<>("腮红", R.mipmap.beauty_redcheeks, UICategory.BEAUTY), itemRedcheekPropertys);
        allProperties.add(itemRedcheeks);

        Map<String, String> litisResPathNames = new LinkedHashMap<>();
        litisResPathNames.put("liti_ziran.png", "自然");
        litisResPathNames.put("liti_junlang.png", "俊朗");
        litisResPathNames.put("liti_guangmang.png", "光芒");
        litisResPathNames.put("liti_qingxin.png", "清新");

        List<XmagicUIProperty<?>> itemLitiPropertys = new ArrayList<>();
        String litiId = "beauty.softLight.softLightMask";
        for (String ids : litisResPathNames.keySet()) {
            itemLitiPropertys.add(new XmagicUIProperty<>(UICategory.BEAUTY, litisResPathNames.get(ids), litiId, effDirs + effs.get(ids), R.mipmap.beauty_liti, BeautyConstant.BEAUTY_FACE_SOFTLIGHT, new XmagicPropertyValues(0, 100, 50, 0, 1), "立体"));
        }
        XmagicPropertyData itemLitis = new XmagicPropertyData(new XmagicUIProperty("立体", R.mipmap.beauty_liti, UICategory.BEAUTY), itemLitiPropertys);
        allProperties.add(itemLitis);

        allProperties.add(new XmagicPropertyData(new XmagicUIProperty<>(UICategory.BEAUTY, "瘦颧骨", R.mipmap.beauty_thin_cheek, BeautyConstant.BEAUTY_FACE_THIN_CHEEKBONE, new XmagicPropertyValues(-100, 100, 0, -1, 1))));
        allProperties.add(new XmagicPropertyData(new XmagicUIProperty<>(UICategory.BEAUTY, "下巴", R.mipmap.beauty_chin, BeautyConstant.BEAUTY_FACE_THIN_CHIN, new XmagicPropertyValues(-100, 100, 0, -1, 1))));
        allProperties.add(new XmagicPropertyData(new XmagicUIProperty<>(UICategory.BEAUTY, "额头", R.mipmap.beauty_forehead, BeautyConstant.BEAUTY_FACE_FOREHEAD, new XmagicPropertyValues(-100, 100, 0, -1, 1))));
        allProperties.add(new XmagicPropertyData(new XmagicUIProperty<>(UICategory.BEAUTY, "亮眼", R.mipmap.beauty_eye_lighten,BeautyConstant.BEAUTY_EYE_LIGHTEN, new XmagicPropertyValues(0, 100, 30, 0, 1))));
        allProperties.add(new XmagicPropertyData(new XmagicUIProperty<>(UICategory.BEAUTY, "眼距", R.mipmap.beauty_eye_distance, BeautyConstant.BEAUTY_EYE_DISTANCE, new XmagicPropertyValues(-100, 100, 0, -1, 1))));
        allProperties.add(new XmagicPropertyData(new XmagicUIProperty<>(UICategory.BEAUTY, "眼角", R.mipmap.beauty_eye_angle, BeautyConstant.BEAUTY_EYE_ANGLE, new XmagicPropertyValues(-100, 100, 0, -1, 1))));
        allProperties.add(new XmagicPropertyData(new XmagicUIProperty<>(UICategory.BEAUTY, "瘦鼻", R.mipmap.beauty_thin_nose, BeautyConstant.BEAUTY_NOSE_THIN, new XmagicPropertyValues(0, 100, 0, 0, 1))));
        allProperties.add(new XmagicPropertyData(new XmagicUIProperty<>(UICategory.BEAUTY, "鼻翼", R.mipmap.beauty_nose_wing, BeautyConstant.BEAUTY_NOSE_WING, new XmagicPropertyValues(-100, 100, 0, -1, 1))));
        allProperties.add(new XmagicPropertyData(new XmagicUIProperty<>(UICategory.BEAUTY, "鼻子位置", R.mipmap.beauty_nose_position, BeautyConstant.BEAUTY_NOSE_HEIGHT, new XmagicPropertyValues(-100, 100, 0, -1, 1))));
        allProperties.add(new XmagicPropertyData(new XmagicUIProperty<>(UICategory.BEAUTY, "白牙", R.mipmap.beauty_tooth_beauty, BeautyConstant.BEAUTY_TOOTH_WHITEN, new XmagicPropertyValues(0, 100, 0, 0, 1))));
        allProperties.add(new XmagicPropertyData(new XmagicUIProperty<>(UICategory.BEAUTY, "祛皱", R.mipmap.beauty_remove_pounch, BeautyConstant.BEAUTY_FACE_REMOVE_WRINKLE, new XmagicPropertyValues(0, 100, 0, 0, 1))));
        allProperties.add(new XmagicPropertyData(new XmagicUIProperty<>(UICategory.BEAUTY, "祛法令纹", R.mipmap.beauty_wrinkle_smooth, BeautyConstant.BEAUTY_FACE_REMOVE_LAW_LINE, new XmagicPropertyValues(0, 100, 0, 0, 1))));
        allProperties.add(new XmagicPropertyData(new XmagicUIProperty<>(UICategory.BEAUTY, "祛眼袋", R.mipmap.beauty_remove_eye_pouch, BeautyConstant.BEAUTY_FACE_REMOVE_EYE_BAGS, new XmagicPropertyValues(0, 100, 0, 0, 1))));
        allProperties.add(new XmagicPropertyData(new XmagicUIProperty<>(UICategory.BEAUTY, "嘴型", R.mipmap.beauty_mouth_size, BeautyConstant.BEAUTY_MOUTH_SIZE, new XmagicPropertyValues(-100, 100, 0, -1, 1))));
        allProperties.add(new XmagicPropertyData(new XmagicUIProperty<>(UICategory.BEAUTY, "嘴唇厚度", R.mipmap.beauty_mouth_height, BeautyConstant.BEAUTY_MOUTH_HEIGHT, new XmagicPropertyValues(-100, 100, 0, -1, 1))));
    }*/

    /*
    private static void parseMotion(List<XmagicPropertyData> allProperties) {
        allProperties.add(new XmagicPropertyData(new XmagicUIProperty(UICategory.MOTION, "无", XmagicProperty.ID_NONE, sResPath + "light_assets/template.json", R.mipmap.naught, null, null)));
        allProperties.add(new XmagicPropertyData(new XmagicUIProperty(UICategory.MAKEUP, "无", XmagicProperty.ID_NONE, sResPath + "light_assets/template.json", R.mipmap.naught, null, null)));
        allProperties.add(new XmagicPropertyData(new XmagicUIProperty(UICategory.SEGMENTATION, "无", XmagicProperty.ID_NONE, sResPath + "light_assets/template.json", R.mipmap.naught, null, null)));
        final String motionResStr =
                "video_lianliancaomei:恋恋草莓," +
                        "video_zuijiuweixun:醉酒微醺," +
                        "video_tianxinmengniiu:甜心萌牛," +
                        "video_litihuaduo:立体花朵," +
                        "video_aiyimanman:爱意满满," +
                        "video_keaituya:可爱涂鸦," +
                        "video_naipingmianmo:奶瓶面膜," +
                        "video_qiqiupaidui:气球派对," +
                        "video_mengmengxiong:萌萌熊," +
                        "video_aixinyanhua:爱心烟花," +
                        "video_xingganxiaochou:性感小丑," +
                        "video_xiaohonghua:小红花," +
                        "video_qipaoshui:气泡水," +
                        "video_kangnaixin:氛围康乃馨," +
                        "video_xiangsuyuzhou:像素宇宙," +
                        "video_shangtoule:上头了," +
                        "video_xiaohongxing:小红星," +
                        "video_xiaohuangmao:童趣鸭鸭帽," +
                        "video_chudao:C位出道," +
                        "video_fenweiqiehuan:氛围切换," +
                        "video_bingjingaixin:冰晶爱心," +
                        "video_biaobai:表白," +
                        "video_kawayixiaoxiong:卡哇伊小熊," +
                        "video_yaogunyue:摇摇乐," +
                        "video_rixishaonv:日系少女," +
                        "video_tutujiang:兔兔酱," +
                        "video_xuancainihong:炫彩霓虹," +
                        "video_kaixueqianhou:上学前后," +
                        "video_nightgown:毛毡发箍," +
                        "video_xuanmeizhuang:元气贴纸," +
                        "video_quebanzhuang:日系雀斑妆," +
                        "video_lengliebingmo:冷冽冰瞳," +
                        "video_dongriliange:冬日恋歌," +
                        "video_fugu_dv:复古DV," +
                        "video_baozilian:包子脸," +
                        "video_boom:动感墨镜," +
                        "video_boys:闪动爱心," +
                        "video_cherries:小樱桃," +
                        "video_guifeiface:贵妃脸," +
                        "video_heimaomi:可爱喵," +
                        "video_liuhaifadai:刘海发带," +
                        "video_otwogirl:波点女孩," +
                        "video_shuangmahua:双麻花," +
                        "video_egaoshuangwanzi:恶搞双丸子," +
                        "video_yazi:抖抖鸭," +
                        "video_tantanfagu:弹弹发箍," +
                        "video_ningmengyayamao:柠檬鸭鸭帽," +
                        "video_hudiejie:魔法蝴蝶结," +
                        "video_maoxinvhai:猫系女孩," +
                        "video_jinli:锦鲤," +
                        "video_zhixingmeigui:知性玫瑰," +
                        "video_tiankulamei:甜酷辣妹," +
                        "video_feitianzhuzhu:飞天猪猪," +
                        "video_tonghuagushi:童话故事," +
                        "video_shoushiwu:手指泡泡," +
                        "video_sakuragirl:樱花女孩," +
                        "video_bubblegum:童年泡泡糖," +
                        "video_3DFace_springflower:花仙子";

        final String makeupResStr =
                "video_fenfenxia:桃花妆," +
                        "video_guajiezhuang:元气妆," +
                        "video_shuimitao:蜜桃妆," +
                        "video_xiaohuazhuang:校花妆," +
                        "video_shaishangzhuang:晒伤妆," +
                        "video_zhiganzhuang:质感妆," +
                        "video_nvtuanzhuang:女团妆," +
                        "video_hongjiuzhuang:红酒妆," +
                        "video_xuejiezhuang:学姐妆";
        final String segmentationResStr=
                "video_empty_segmentation:分割," +
                        "video_segmentation_blur_45:背景模糊-弱," +
                        "video_segmentation_blur_75:背景模糊-强,";

        LinkedHashMap<String, String> motionPropertyNameConverter = getMaterialDataByStr(motionResStr);
        LinkedHashMap<String, String> makeupNameConverter = getMaterialDataByStr(makeupResStr);
        LinkedHashMap<String, String> segmentationNameConverter = getMaterialDataByStr(segmentationResStr);

        String motionPath = sResPath + "/MotionRes" + File.separator;
        if (!new File(motionPath).exists()) {
            return;
        }
        File[] fileList = new File(motionPath).listFiles();
        if (fileList == null || fileList.length <= 0) {
            return;
        }

        String motion2dResPath = sResPath + "/MotionRes/2dMotionRes/";
        String motion3dResPath = sResPath + "/MotionRes/3dMotionRes/";
        String handMotionResPath = sResPath + "/MotionRes/handMotionRes/";
        String makeupResPath = sResPath + "/MotionRes/makeupRes/";
        String segmentMotionResPath = sResPath + "/MotionRes/segmentMotionRes/";
        String ganMotionResPath = sResPath + "/MotionRes/ganMotionRes/";
        File[] motion2dFiles = getFilesByPath(motion2dResPath);
        File[] motion3dFiles = getFilesByPath(motion3dResPath);
        File[] handMotionFiles = getFilesByPath(handMotionResPath);
        File[] makeupFiles = getFilesByPath(makeupResPath);
        File[] segmentMotionFiles = getFilesByPath(segmentMotionResPath);
        File[] ganMotionFiles = getFilesByPath(ganMotionResPath);
        //构造2D素材数据
        if (motion2dFiles != null) {
            XmagicPropertyData item2dMotions = new XmagicPropertyData(new XmagicUIProperty<>("2D动效", R.mipmap.motion_2d, UICategory.MOTION));
            allProperties.add(item2dMotions);
            List<XmagicUIProperty<?>> itemPropertys = new ArrayList<>();
            item2dMotions.itemPropertys = new ArrayList<>();
            for (File file : motion2dFiles) {
                if (file.isDirectory()) {
                    String id = file.getName();
                    String materialPath = motion2dResPath + id;
                    if (motionPropertyNameConverter.containsKey(id)){
                        XmagicUIProperty xmagicUIProperty = new XmagicUIProperty<>(UICategory.MOTION, motionPropertyNameConverter.get(id), id, materialPath, 0, null, null,"2D动效");
                        xmagicUIProperty.thumbImagePath = getTemplateImgPath("2dMotionRes/"+id);
                        itemPropertys.add(xmagicUIProperty);
                    }
                }
            }
            compareData(itemPropertys, motionPropertyNameConverter,item2dMotions.itemPropertys);
        }
        //构造3D素材数据
        if (motion3dFiles != null) {
            XmagicPropertyData item3dMotions = new XmagicPropertyData(new XmagicUIProperty<>("3D动效", R.mipmap.motion_3d, UICategory.MOTION));
            allProperties.add(item3dMotions);
            List<XmagicUIProperty<?>> itemPropertys = new ArrayList<>();
            item3dMotions.itemPropertys = new ArrayList<>();
            for (File file : motion3dFiles) {
                if (file.isDirectory()) {
                    String id = file.getName();
                    String materialPath = motion3dResPath + id;
                    if (motionPropertyNameConverter.containsKey(id)){
                        XmagicUIProperty xmagicUIProperty = new XmagicUIProperty<>(UICategory.MOTION, motionPropertyNameConverter.get(id), id, materialPath, 0, null, null,"3D动效");
                        xmagicUIProperty.thumbImagePath = getTemplateImgPath("3dMotionRes/"+id);
                        itemPropertys.add(xmagicUIProperty);
                    }
                }
            }
            compareData(itemPropertys, motionPropertyNameConverter,item3dMotions.itemPropertys);
        }
        //构造手势素材数据
        if (handMotionFiles != null) {
            XmagicPropertyData itemHandMotions = new XmagicPropertyData(new XmagicUIProperty<>("手势动效", R.mipmap.motion_hand, UICategory.MOTION));
            allProperties.add(itemHandMotions);
            List<XmagicUIProperty<?>> itemPropertys = new ArrayList<>();
            itemHandMotions.itemPropertys = new ArrayList<>();
            for (File file : handMotionFiles) {
                if (file.isDirectory()) {
                    String id = file.getName();
                    String materialPath = handMotionResPath + id;
                    if (motionPropertyNameConverter.containsKey(id)){
                        XmagicUIProperty xmagicUIProperty = new XmagicUIProperty<>(UICategory.MOTION, motionPropertyNameConverter.get(id), id, materialPath, 0, null, null,"手势动效");
                        xmagicUIProperty.thumbImagePath = getTemplateImgPath("handMotionRes/"+id);
                        itemPropertys.add(xmagicUIProperty);
                    }
                }
            }
            compareData(itemPropertys, motionPropertyNameConverter,itemHandMotions.itemPropertys);
        }
        //趣味素材数据
        if (ganMotionFiles != null) {
            XmagicPropertyData itemGanMotions = new XmagicPropertyData(new XmagicUIProperty<>("趣味", R.mipmap.motion_gan, UICategory.MOTION));
            allProperties.add(itemGanMotions);
            List<XmagicUIProperty<?>> itemPropertys = new ArrayList<>();
            itemGanMotions.itemPropertys = new ArrayList<>();
            for (File file : ganMotionFiles) {
                if (file.isDirectory()) {
                    String id = file.getName();
                    String materialPath = ganMotionResPath + id;
                    if (motionPropertyNameConverter.containsKey(id)){
                        XmagicUIProperty xmagicUIProperty = new XmagicUIProperty<>(UICategory.MOTION, motionPropertyNameConverter.get(id), id, materialPath, 0, null, null,"趣味");
                        xmagicUIProperty.thumbImagePath = getTemplateImgPath("ganMotionRes/"+id);
                        itemPropertys.add(xmagicUIProperty);
                    }
                }
            }
            compareData(itemPropertys, motionPropertyNameConverter,itemGanMotions.itemPropertys);
        }
        //构造整妆素材数据
        if (makeupFiles != null) {
            List<XmagicPropertyData> itemPropertys = new ArrayList<>();
            for (File file : makeupFiles) {
                if (file.isDirectory()) {
                    String id = file.getName();
                    String materialPath = makeupResPath + id;
                    if (makeupNameConverter.containsKey(id)) {
                        XmagicUIProperty xmagicUIProperty = new XmagicUIProperty<>(UICategory.MAKEUP, makeupNameConverter.get(id), id, materialPath, 0, "makeup.strength", new XmagicPropertyValues(0, 100, 60, 0, 1));
                        xmagicUIProperty.thumbImagePath = getTemplateImgPath("makeupRes/"+id);
                        itemPropertys.add(new XmagicPropertyData(xmagicUIProperty));
                    }
                }
            }
            compareRootData(itemPropertys,makeupNameConverter,allProperties);
        }
        //构造分割素材数据
        if (segmentMotionFiles != null) {
            List<XmagicPropertyData> segList = new ArrayList<>();
            for (File file : segmentMotionFiles) {
                if (file.isDirectory()) {
                    String id = file.getName();
                    String materialPath = segmentMotionResPath + id;
                    if(segmentationNameConverter.containsKey(id)){
                        if(id.equals("video_empty_segmentation")) {
                            segList.add(new XmagicPropertyData(new XmagicUIProperty<>(UICategory.SEGMENTATION, "自定义背景", id, materialPath, R.mipmap.segmentation_formulate, "", null)));
                        }else{
                            XmagicUIProperty xmagicUIProperty = new XmagicUIProperty<>(UICategory.SEGMENTATION, segmentationNameConverter.get(id), id, materialPath,0, null, null);
                            xmagicUIProperty.thumbImagePath = getTemplateImgPath("segmentMotionRes/"+id);

                            segList.add(new XmagicPropertyData(xmagicUIProperty));
                        }
                    }
                }
            }
            compareSegData(segList,allProperties);
        }
    }*/

    /*private static void compareSegData(List<XmagicPropertyData> property, List<XmagicPropertyData> allProperties){
        List<String> segNameList = new ArrayList<>();
        segNameList.add("自定义背景");
        segNameList.add("背景模糊-弱");
        segNameList.add("背景模糊-强");


        for (String segName:segNameList) {
            for (XmagicPropertyData data:property) {
                if(segName.equals(data.uiProperty.displayName)){
                    allProperties.add(data);
                }
            }
        }
    }*/

    /*
    private static void compareRootData(List<XmagicPropertyData> property, LinkedHashMap<String, String> idMap,List<XmagicPropertyData> allProperties){
        Iterator iter = idMap.keySet().iterator();
        while (iter.hasNext()) {
            String key = (String) iter.next();
            for (XmagicPropertyData data:property) {
                if(data.uiProperty.property.id.equals(key)){
                    allProperties.add(data);
                    break;
                }
            }
        }
    }*/
    /*
    private static void compareData(List<XmagicUIProperty<?>> property, LinkedHashMap<String, String> idMap,List<XmagicUIProperty<?>> allProperties){
        Iterator iter = idMap.keySet().iterator();
        while (iter.hasNext()) {
            String key = (String) iter.next();
            for (XmagicUIProperty data:property) {
                if(data.property.id.equals(key)){
                    allProperties.add(data);
                    break;
                }
            }
        }
    }*/

    /*
    private static File[] getFilesByPath(String path){
        File[] fileList = new File(path).listFiles();
        if(fileList != null && fileList.length>0) {
            List fileNames = Arrays.asList(fileList);
            Collections.sort(fileNames, new Comparator<File>() {
                @Override
                public int compare(File o1, File o2) {
                    if (o1.isDirectory() && o2.isFile()) {
                        return -1;
                    }
                    if (o1.isFile() && o2.isDirectory()) {
                        return 1;
                    }
                    return o1.getName().compareTo(o2.getName());
                }
            });
            return fileList;
        }
        return null;
    }*/

    /*
    private static LinkedHashMap<String, String> getMaterialDataByStr(String str) {
        LinkedHashMap<String, String> propertyNameConverter = new LinkedHashMap<>();
        String[] pairs = str.split(",");
        for (int i = 0; i < pairs.length; i++) {
            String pair = pairs[i];
            String[] keyValue = pair.split(":");
            propertyNameConverter.put(keyValue[0], keyValue[1]);
        }
        return propertyNameConverter;
    }*/



    /*
    public static List<XmagicPropertyData> getProperties() {
        if (sAllProperties.isEmpty()) {
            throw new IllegalStateException("No properties found, call XmagicResParser.parseRes() first.");
        }
        return sAllProperties;
    }*/

    /*
    private static String getTemplateImgPath(String motionName) {
        return sResPath + "MotionRes" + File.separator + motionName + File.separator +"template.png";
    }*/


    /*
    private static boolean exists(String path) {
        if (TextUtils.isEmpty(path)) {
            return false;
        }

        // assets中的文件，默认一定存在；非assets中的文件，需要正常判断是否存在
        return path.indexOf("assets") >= 0 || new File(path).exists();

    }*/

    /*
    private static String genSeperateFileDir(String fileDir) {
        if (fileDir != null && (!fileDir.endsWith(File.separator))) {
            fileDir = fileDir + File.separator;
        }

        return fileDir;
    }*/

    /*
    private static boolean copyAssets(Context context, String oldPath, String newPath) {
        try {
            String fileNames[] = context.getAssets().list(oldPath);// 获取assets目录下的所有文件及目录名
            if (fileNames.length > 0) {// 如果是目录
                Log.e(TAG, "copyAssets path: " + Arrays.toString(fileNames));
                File file = new File(newPath);
                file.mkdirs();// 如果文件夹不存在，则递归
                for (String fileName : fileNames) {
                    copyAssets(context, oldPath + "/" + fileName, newPath + "/" + fileName);
                }
            } else {// 如果是文件
                InputStream is = context.getAssets().open(oldPath);
                FileOutputStream fos = new FileOutputStream(new File(newPath));
                byte[] buffer = new byte[1024 * 1024];
                int byteCount = 0;
                while ((byteCount = is.read(buffer)) != -1) {// 循环从输入流读取
                    // buffer字节
                    fos.write(buffer, 0, byteCount);// 将读取的输入流写入到输出流
                }
                fos.flush();// 刷新缓冲区
                is.close();
                fos.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }*/

    private static void ensureResPathAlreadySet() {
        if (TextUtils.isEmpty(sResPath)) {
            throw new IllegalStateException("resource path not set, call XmagicResParser.setResPath() first.");
        }
    }
}
