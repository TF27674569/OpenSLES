package com.ubtech.myapplication;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;

import com.ubtech.myapplication.util.MatrixHelper;
import com.ubtech.myapplication.util.ShaderHelper;
import com.ubtech.myapplication.util.TextResourcesReader;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import static android.opengl.GLES20.*;


/**
 * create by TIAN FENG on 2019/8/27
 */
public class AirHockeyRenderer implements GLSurfaceView.Renderer {

    private static final int POSITION_COMPNENT_COUNT = 2;//2 x y 0 1  4 x y  z w
    private static final int COLOR_COMPNENT_COUNT = 3;// opengl 是本地系统直接运行在硬件上的  如果使用的是java的API那么就需要将java的内存拷贝到c里面（或者使用JNI开发）
    private static final int BYTES_PRE_FLOAT = 4;// 浮点数32位4字节
    private static final int STRIDE = (POSITION_COMPNENT_COUNT + COLOR_COMPNENT_COUNT) * BYTES_PRE_FLOAT;

    private static final String A_POSITION = "a_Position";
    private int aPositionLocation;


    private static final String A_COLOR = "a_Color";
    private int aColorLocation;

    // 正交投影矩阵
    private static final String U_MATRIX = "u_Matrix";
    private final float[] projectionMatrix = new float[16];
    private int uMatrixLocation;


    // 拷贝内存顶点数据对象 （java虚拟机堆拷贝到native堆）
    private final FloatBuffer vertexData;

    // 模型矩阵
    private final float[] modelMatrix = new float[16];

    // opengl 程序地址
    private int program;

    private final Context context;

    public AirHockeyRenderer(Context context) {
        this.context = context;
        // w分量处理三维效果
//        float[] tableVerticesWithTriangles = {
//                // x,y ,z ,w,r,g,b
//                0f, 0f, 0f, 1.5f, 1f, 1f, 1f,
//                -0.5f, -0.8f, 0f, 1f, 0.7f, 0.7f, 0.7f,
//                0.5f, -0.8f, 0f, 1f, 0.7f, 0.7f, 0.7f,
//                0.5f, 0.8f, 0f, 2f, 0.7f, 0.7f, 0.7f,
//                -0.5f, 0.8f, 0f, 2f, 0.7f, 0.7f, 0.7f,
//                -0.5f, -0.8f, 0f, 1f, 0.7f, 0.7f, 0.7f,
//                // 三角形的顶点为逆时针排序，这数据卷曲顺序，可以优化性能
//
//
//                // line
//                -0.5f, 0, 0f, 1.5f, 1f, 0f, 0f,
//                0.5f, 0, 0f, 1.5f, 1f, 0f, 0f,
//
//                // 木槌
//                0f, -0.4f, 0f, 1.25f, 0f, 0f, 1f,
//                0f, 0.4f, 0f, 1.75f, 1f, 0f, 0f
//        };


        float[] tableVerticesWithTriangles = {
                // x,y ,r,g,b
                0f, 0f, 1f,  1f, 1f,
                -0.5f, -0.8f,  0.7f, 0.7f, 0.7f,
                0.5f, -0.8f,   0.7f, 0.7f, 0.7f,
                0.5f, 0.8f,    0.7f, 0.7f, 0.7f,
                -0.5f, 0.8f,   0.7f, 0.7f, 0.7f,
                -0.5f, -0.8f,  0.7f, 0.7f, 0.7f,
                // 三角形的顶点为逆时针排序，这数据卷曲顺序，可以优化性能

                // line
                -0.5f, 0,  1f, 0f, 0f,
                0.5f, 0,   1f, 0f, 0f,
                // 木槌
                0f, -0.4f,  0f, 0f, 1f,
                0f, 0.4f,  1f, 0f, 0f
        };


        vertexData = ByteBuffer.allocateDirect(tableVerticesWithTriangles.length * BYTES_PRE_FLOAT)// 分配一块本地内存 注意销毁（JVM不会销毁此内存）
                .order(ByteOrder.nativeOrder())// 本地字节序
                .asFloatBuffer()// 底层转为floatbuffer
                .put(tableVerticesWithTriangles);//拷贝内存顶点数据对象 （java虚拟机堆拷贝到native堆） 进程结束时此代码会被释放
    }


    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        glClearColor(0.0f, 0f, 0f, 0f);

        // 读取着色器代码
        String vertexShaderSource = TextResourcesReader.readTextFileFromResources(context, R.raw.simple_vertex_shader);
        String fragmentShaderSource = TextResourcesReader.readTextFileFromResources(context, R.raw.simple_fragment_shader);

        // 顶点着色器 告诉opengl 绘制在哪里
        int vertexShader = ShaderHelper.compileVertexShader(vertexShaderSource);
        // 片段着色器 告诉opengl 绘制什么颜色 这两个着色器缺一不可
        int fragmentShader = ShaderHelper.compileFragmentShader(fragmentShaderSource);

        // 将着色器 链接opengl程序并放回程序地址
        program = ShaderHelper.linkProgram(vertexShader, fragmentShader);

        ShaderHelper.validateProgram(program);

        // 使用opengl程序
        glUseProgram(program);

        // 在片段着色器代码中定义的u_Color 字段，程序启用时查询位置 更新uniform时会用到这个位置  定义的是uniform glGetUniformLocation
        aColorLocation = glGetAttribLocation(program, A_COLOR);
        // 在顶点着色器代码中定义的a_Position 字段，程序启用时查询位置 更新attribute时会用到这个位置 定义的是attribute glGetAttribLocation
        aPositionLocation = glGetAttribLocation(program, A_POSITION);
        uMatrixLocation = glGetUniformLocation(program, U_MATRIX);

        // 将缓冲取的指针 置于头部 不能让其冲中间读取
        vertexData.position(0);

        /*
         * 告诉OpenGl冲缓冲区vertexData 找到 a_Position 对应的数据
         *
         * int indx  aPositionLocation 也就是a_Position 的位置
         * int size 需要几个分量 （这里是平面的点，只需要x，y）两个分量 但是着色器代码里面 我们设置的vec4 包含4个分量 为指定的分量，前三个为0 最后一个为1
         * int type 数据类型（这里顶点坐标用的float的点）
         * boolean normalized 暂时忽略
         * int stride
         * java.nio.Buffer ptr 告诉OpenGL取哪里读取数据
         */
        glVertexAttribPointer(aPositionLocation, POSITION_COMPNENT_COUNT, // 有几个字节
                GL_FLOAT, false, STRIDE, vertexData);

        // 使能 a_Position
        glEnableVertexAttribArray(aPositionLocation);

        vertexData.position(POSITION_COMPNENT_COUNT);
        glVertexAttribPointer(aColorLocation, COLOR_COMPNENT_COUNT, GL_FLOAT, false, STRIDE, vertexData);
        glEnableVertexAttribArray(aColorLocation);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        glViewport(0, 0, width, height);
        final float aspectTadio = width > height ? width / (float) height : height / (float) width;

        // 防止横竖屏变换的时候 图像变形
        // 通过矩阵处理正交投影，拓展坐标系空间
//        if (width > height) {
//            // 横屏下 扩展宽度的取值 让其不是在[-1 1] 而是[ -aspectTadio, aspectTadio]
//            Matrix.orthoM(projectionMatrix, 0, -aspectTadio, aspectTadio, -1f, 1f, -1f, 1f);
//        } else {
//            Matrix.orthoM(projectionMatrix, 0, -1f, 1f, -aspectTadio, aspectTadio, -1f, 1f);
//        }

        // 图像默认的z值为0
        // 视锥体是从45度视野 近处1 ，远处10 观看
        // 观看位置为 -1  -10
        MatrixHelper.perspectiveM(projectionMatrix, 45f, width / (float) height, 1f, 10f);

        // 将模型矩阵 初始化单位举证然后z轴移动-2个单位
        Matrix.setIdentityM(modelMatrix, 0);
        Matrix.translateM(modelMatrix, 0, 0f, 0f, -2f);

        final float[] temp = new float[16];
        //矩阵相乘的函数
        Matrix.multiplyMM(temp, 0, projectionMatrix, 0, modelMatrix, 0);
        // 将结果copy到 projectionMatrix
        System.arraycopy(temp, 0, projectionMatrix, 0, temp.length);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        glClear(GL_COLOR_BUFFER_BIT);

        // 处理正交投影矩阵
        glUniformMatrix4fv(uMatrixLocation, 1, false, projectionMatrix, 0);

        // GL_TRIANGLES 画三角形
        // 从 tableVerticesWithTriangles 开头读取顶点
        // 读取6 个点  那么此时 两个三角形就读取出来了
        glDrawArrays(GL_TRIANGLE_FAN, 0, 6);


        // 从第6个点 读两个
        glDrawArrays(GL_LINES, 6, 2);


        // 绘制木槌
        glDrawArrays(GL_POINTS, 8, 1);
        glDrawArrays(GL_POINTS, 9, 1);
    }
}
