package com.vcube.tencent.effect.framework;

import static android.opengl.GLES20.GL_TRIANGLES;
import static android.opengl.GLES20.glActiveTexture;
import static android.opengl.GLES20.glBindTexture;
import static android.opengl.GLES20.glDrawArrays;
import static android.opengl.GLES20.glGetUniformLocation;
import static android.opengl.GLES20.glUniform1i;

import android.opengl.GLES11Ext;
import android.opengl.GLES20;

import java.nio.ByteBuffer;

// 用于OES纹理向RGBA纹理的转换
public class OESRenderer extends BaseRenderer {

    private static final String FRAG_SHADER = "#extension GL_OES_EGL_image_external : require\n" +
            "precision mediump float;\n" +
            "uniform samplerExternalOES uTextureSampler;\n" +
            "varying vec2 textureCoordinate;\n" +
            "void main()\n" +
            "{\n" +
            "  vec4 vCameraColor = texture2D(uTextureSampler, textureCoordinate);\n" +
            "  gl_FragColor = vCameraColor;\n" +
            "}\n";


    protected static final String VERT_SHADER =
            "attribute vec4 position;\n" +
                    "vec4 verticalFlipPosition;\n" +
                    "attribute vec4 inputTexCoord;\n" +
                    "uniform mat4 uInputMatrix;\n" +
                    "uniform mat4 uRotateMatrix;\n" +
                    "uniform mat4 uScreenMatrix;\n" +
                    "varying vec2 textureCoordinate;\n" +
                    "void main()\n" +
                    "{\n" +
                    "    textureCoordinate = (uInputMatrix * inputTexCoord).xy;\n" +
                    "    verticalFlipPosition = vec4(position.y, -position.x, position.z, 1);\n"+// OpenGL 纹理渲染后被垂直翻转是常见现象, 这里翻转y轴坐标规避, 参考 https://blog.csdn.net/narutojzm1/article/details/51940817
                    "    gl_Position = verticalFlipPosition * uRotateMatrix * uScreenMatrix;\n" +
                    "}";


    private static final String TAG = "OESRenderer";

    private int oesTextureLocation = -1;

    /**
     * 此方法会加载一个可以将画面正常绘制的顶点着色器
     */
    public void init() {
        super.init(DEFAULT_VERT_SHADER, FRAG_SHADER);
        oesTextureLocation = glGetUniformLocation(mShaderProgram, "uTextureSampler");
    }

    /**
     * 此方法会加载不进行旋转方向的顶点着色器
     */
    public void initNoRotate() {
        super.init(VERT_SHADER_ROTATE_0, FRAG_SHADER);
        oesTextureLocation = glGetUniformLocation(mShaderProgram, "uTextureSampler");
    }

    /**
     * 此方法会加载旋转方向的顶点着色器
     */
    public void initRotate(){
        super.init(OESRenderer.VERT_SHADER, FRAG_SHADER);
        oesTextureLocation = glGetUniformLocation(mShaderProgram, "uTextureSampler");
    }

    public void doRender(int srcTextureId, int desTextureId, int frameWidth, int frameHeight, float[] srcTransformMatrix, float[] destTransformMatrix, ByteBuffer buffer) {
        super.beforeRender(desTextureId, frameWidth, frameHeight, srcTransformMatrix, destTransformMatrix);
        super.setVertex();

        glActiveTexture(GLES20.GL_TEXTURE0);
        glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, srcTextureId);
        glUniform1i(oesTextureLocation, 0);

        glDrawArrays(GL_TRIANGLES, 0, 6);

        saveToBuffer(buffer);
    }

}
