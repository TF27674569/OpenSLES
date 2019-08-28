package com.ubtech.myapplication.util;

/**
 * create by TIAN FENG on 2019/8/28
 */
public class MatrixHelper {

    /**
     * 投影矩阵计算
     *
     * @param m             举证长度必须为16
     * @param yFovInDegrees 视野角度
     * @param aspect        宽高比
     * @param n             近处到平面的距离
     * @param f             远处到平面的距离
     */
    public static void perspectiveM(float[] m, float yFovInDegrees, float aspect, float n, float f) {

        if (n > f) throw new IllegalArgumentException("投影举证中，远处到平面的距离，必须大于近处到平面的距离！");

        // 投影矩阵
        //[ a/aspect      0              0                        0       ]
        //[ 0             a              0                        0       ]
        //[ 0             0       -((f+n)/(f - n))      -((2f*f*n)/(f-n)) ]
        //[ 0             0              -1                       0       ]

        // 计算焦距
        float angleInRedians = (float) (yFovInDegrees * Math.PI / 180);

        float a = (float) (1.0 / Math.tan(angleInRedians / 2.0));

        m[0] = a / aspect;
        m[1] = 0f;
        m[2] = 0f;
        m[3] = 0f;
        m[4] = 0f;
        m[5] = a;
        m[6] = 0f;
        m[7] = 0f;
        m[8] = 0f;
        m[9] = 0f;
        m[10] = -((f + n) / (f - n));
        m[11] = -1f;
        m[12] = 0f;
        m[13] = 0f;
        m[14] = -((2f * f * n) / (f - n));
        m[15] = 0f;
    }
}
