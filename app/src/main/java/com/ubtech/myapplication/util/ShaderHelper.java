package com.ubtech.myapplication.util;

import static android.opengl.GLES20.*;

import android.util.Log;

/**
 * create by TIAN FENG on 2019/8/27
 */
public class ShaderHelper {

    private static final String TAG = "shaderHelper";

    public static int compileVertexShader(String shaderCode) {
        return compileShader(GL_VERTEX_SHADER, shaderCode);
    }

    public static int compileFragmentShader(String shaderCode) {
        return compileShader(GL_FRAGMENT_SHADER, shaderCode);
    }

    private static int compileShader(int type, String shaderCode) {
        // 创建对象  返回一个地址
        int shaderId = glCreateShader(type);
        if (shaderId == 0) {
            Log.e(TAG, "create shader error!");
            return shaderId;
        }
        // 关联 代码 shaderCode 就是下载raw下的glsl的代码
        glShaderSource(shaderId, shaderCode);
        // 上传代码
        glCompileShader(shaderId);

        // 检查编译是失败还是成功
        final int[] compileStatus = new int[1];
        glGetShaderiv(shaderId, GL_COMPILE_STATUS, compileStatus, 0);

        Log.e(TAG, "result of compile source " + shaderCode + " " + glGetShaderInfoLog(shaderId));

        if (compileStatus[0] == 0) {
            glDeleteShader(shaderId);
            return shaderId;
        }
        return shaderId;
    }

    /**
     * 将着色器与OpenGL程序
     *
     * @param vertexShaderId   顶点着色器
     * @param fragmentShaderId 片段着色器
     * @return OpenGL地址
     */
    public static int linkProgram(int vertexShaderId, int fragmentShaderId) {
        // 创建opengl程序 返回程序地址
        int programId = glCreateProgram();
        if (programId == 0) {
            Log.e(TAG, "gl create program error");
            return 0;
        }

        // 将着色器绑定在 opengl上
        glAttachShader(programId, vertexShaderId);
        glAttachShader(programId, fragmentShaderId);

        // 链接程序
        glLinkProgram(programId);

        // 检查链接状态
        final int[] linkStatus = new int[1];
        glGetProgramiv(programId, GL_LINK_STATUS, linkStatus, 0);

        Log.e(TAG, "result of link program " + glGetProgramInfoLog(programId));

        if (linkStatus[0] == 0) {
            glDeleteProgram(programId);
            return 0;
        }

        return programId;
    }


    /**
     * 判断OpenGL程序是否可用
     *
     * @param programId 程序地址
     * @return 是否可用
     */
    public static boolean validateProgram(int programId) {
        glValidateProgram(programId);
        final int[] valedateStatus = new int[1];
        glGetProgramiv(programId, GL_VALIDATE_STATUS, valedateStatus, 0);
        Log.e(TAG, "validate program  is " + valedateStatus[0] + " " + glGetProgramInfoLog(programId));
        return valedateStatus[0] != 0;
    }

}
