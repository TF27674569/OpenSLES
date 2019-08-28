package com.ubtech.myapplication.util;

import android.content.Context;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * create by TIAN FENG on 2019/8/27
 */
public class TextResourcesReader {

    public static String readTextFileFromResources(Context context, int resourceId) {
        StringBuilder body = new StringBuilder();
        InputStream is = null;
        InputStreamReader isr = null;
        BufferedReader br = null;
        try {
            is = context.getResources().openRawResource(resourceId);
            isr = new InputStreamReader(is);
            br = new BufferedReader(isr);
            String nextLine;
            while ((nextLine = br.readLine()) != null) {
                body.append(nextLine).append("\n");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeIO(br, isr, is);
        }
        return body.toString();
    }


    public static void closeIO(Closeable... closeables) {
        for (Closeable closeable : closeables) {
            if (closeable != null) {
                try {
                    closeable.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
