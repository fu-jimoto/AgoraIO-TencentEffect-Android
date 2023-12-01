package com.vcube.tencent.effect.framework;

import static android.opengl.GLES20.GL_TRIANGLES;
import static android.opengl.GLES20.glActiveTexture;
import static android.opengl.GLES20.glBindTexture;
import static android.opengl.GLES20.glDrawArrays;

import android.opengl.GLES20;

import java.nio.ByteBuffer;

public class RotateRenderer extends BaseRenderer {

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
                    "    verticalFlipPosition = vec4(-position.y, -position.x, position.z, 1);\n" +// OpenGL 纹理渲染后被垂直翻转是常见现象, 这里翻转y轴坐标规避, 参考 https://blog.csdn.net/narutojzm1/article/details/51940817
                    "    gl_Position = verticalFlipPosition * uRotateMatrix * uScreenMatrix;\n" +
                    "}";


    private static final String TAG = "RotateRenderer";

    /**
     * 此方法会加载旋转方向的顶点着色器
     */
    @Override
    public void init() {
        super.init(VERT_SHADER, DEFAULT_FRAG_SHADER);
    }


    /**
     * 此方法会加载不进行旋转方向的顶点着色器
     */
    public void initRotate(int degree, boolean flipV, boolean flipH) {
        switch (degree % 360) {
            case 0:
                if (flipH) {
                    super.init(VERT_SHADER_ROTATE_0_H, DEFAULT_FRAG_SHADER);
                } else if (flipV) {
                    super.init(VERT_SHADER_ROTATE_0_V, DEFAULT_FRAG_SHADER);
                } else {
                    super.init(VERT_SHADER_ROTATE_0, DEFAULT_FRAG_SHADER);
                }
                break;
            case 90:
                if (flipH) {
                    super.init(VERT_SHADER_ROTATE_90_H, DEFAULT_FRAG_SHADER);
                } else if (flipV) {
                    super.init(VERT_SHADER_ROTATE_90_V, DEFAULT_FRAG_SHADER);
                } else {
                    super.init(VERT_SHADER_ROTATE_90, DEFAULT_FRAG_SHADER);
                }

                break;
            case 180:
                if (flipH) {
                    super.init(VERT_SHADER_ROTATE_180_H, DEFAULT_FRAG_SHADER);
                } else if (flipV) {
                    super.init(VERT_SHADER_ROTATE_180_V, DEFAULT_FRAG_SHADER);
                } else {
                    super.init(VERT_SHADER_ROTATE_180, DEFAULT_FRAG_SHADER);
                }
                break;
            case 270:
                if (flipH) {
                    super.init(VERT_SHADER_ROTATE_270_H, DEFAULT_FRAG_SHADER);
                } else if (flipV) {
                    super.init(VERT_SHADER_ROTATE_270_V, DEFAULT_FRAG_SHADER);
                } else {
                    super.init(VERT_SHADER_ROTATE_270, DEFAULT_FRAG_SHADER);
                }
                break;
        }
    }


    public void doRender(int srcTextureId, int desTextureId, int frameWidth, int frameHeight,
                         float[] srcTransformMatrix, float[] destTransformMatrix, ByteBuffer buffer) {
        super.beforeRender(desTextureId, frameWidth, frameHeight, srcTransformMatrix, destTransformMatrix);
        super.setVertex();

        glActiveTexture(GLES20.GL_TEXTURE0);
        glBindTexture(GLES20.GL_TEXTURE_2D, srcTextureId);
//        glUniform1i(uTexPosition, 0);

        glDrawArrays(GL_TRIANGLES, 0, 6);

        saveToBuffer(buffer);
    }

}
