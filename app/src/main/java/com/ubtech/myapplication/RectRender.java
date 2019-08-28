package com.ubtech.myapplication;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * create by TIAN FENG on 2019/8/26
 */
public class RectRender implements GLSurfaceView.Renderer {

    // 顶点
    private static final float[] VERTEX = {   // in counterclockwise order:
            1, 1, 0,   // top right
            -1, 1, 0,  // top left
            -1, -1, 0, // bottom left
            1, -1, 0,  // bottom right
    };

    // 顶点绘制顺序 （也就是对应左边点 VERTEX[index]）
    private static final short[] VERTEX_INDEX = {0, 1, 2, 0, 2, 3};

    /**
     * 缓冲区
     */
    private final FloatBuffer mVertexBuffer;
    private final ShortBuffer mVertexIndexBuffer;


    public RectRender() {
        mVertexBuffer = ByteBuffer.allocateDirect(VERTEX.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer()
                .put(VERTEX);
        mVertexBuffer.position(0);

        mVertexIndexBuffer = ByteBuffer.allocateDirect(VERTEX_INDEX.length * 2)
                .order(ByteOrder.nativeOrder())
                .asShortBuffer()
                .put(VERTEX_INDEX);
        mVertexIndexBuffer.position(0);
    }


    private static final String VERTEX_SHADER =
            "attribute vec4 vPosition;\n"
                    + "void main() {\n"
                    + "  gl_Position = vPosition;\n"
                    + "}";
    private static final String FRAGMENT_SHADER =
            "precision mediump float;\n"
                    + "void main() {\n"
                    + "  gl_FragColor = vec4(0.5, 0, 0, 1);\n"
                    + "}";

    // 着色器程序句柄
    private int program;
    private int vPositionHandle;


    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);

        int program = GLES20.glCreateProgram();
        // 创建 着色器
        int vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, VERTEX_SHADER);
        int fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, FRAGMENT_SHADER);

        // 绑定着色器
        GLES20.glAttachShader(program,vertexShader);
        GLES20.glAttachShader(program,fragmentShader);

        // 链接gles 程序
        GLES20.glLinkProgram(program);
        // 使用gles
        GLES20.glUseProgram(program);

        // 获取 shader 变量vPosition
        vPositionHandle = GLES20.glGetAttribLocation(program, "vPosition");

        // 启用
        GLES20.glEnableVertexAttribArray(vPositionHandle);
        // 绑定
        GLES20.glVertexAttribPointer(vPositionHandle, 3, GLES20.GL_FLOAT, false,12, mVertexBuffer);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        GLES20.glViewport(0, 0, width, height);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);

//        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 3);
        GLES20.glDrawElements(GLES20.GL_TRIANGLES, VERTEX_INDEX.length,GLES20.GL_UNSIGNED_SHORT, mVertexIndexBuffer);
    }

    static int loadShader(int type, String shaderCode) {
        int shader = GLES20.glCreateShader(type);
        GLES20.glShaderSource(shader, shaderCode);
        GLES20.glCompileShader(shader);
        return shader;
    }
}
