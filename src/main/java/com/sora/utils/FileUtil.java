package com.sora.utils;

import jdk.internal.util.xml.impl.Input;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author Sora5175
 * @date 2019/6/17 16:41
 */
public class FileUtil {
    public static ByteArrayOutputStream streamToByte(InputStream is) throws IOException{

        ByteArrayOutputStream baos = new ByteArrayOutputStream();


        byte[] buffer = new byte[1024];

        int len=-1;

        while ((len = is.read(buffer)) != -1) {
            baos.write(buffer, 0, len);
        }
        baos.flush();
        return baos;
    }
    public static String streamToString(InputStream inputStream) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int i;
        while ((i = inputStream.read()) != -1) {
            baos.write(i);
        }
        String str = baos.toString();
        return str;
    }
    public static InputStream stringToStream(String str){
        InputStream inputStream = new ByteArrayInputStream(str.getBytes());
        return inputStream;
    }
}
