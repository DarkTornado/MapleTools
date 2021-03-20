package com.darktornado.mapletools;

import android.content.Context;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class Tools {

    public static final String VERSION = "1.1";

    public static String loadLicense(Context ctx, String fileName) {
        try {
            InputStreamReader isr = new InputStreamReader(ctx.getAssets().open("license/" + fileName + ".txt"));
            BufferedReader br = new BufferedReader(isr);
            StringBuilder str = new StringBuilder(br.readLine());
            String line = "";
            while ((line = br.readLine()) != null) {
                str.append("\n").append(line);
            }
            isr.close();
            br.close();
            return str.toString();
        } catch (Exception e) {
//            toast(e.toString());
            return "라이선스 정보 불러오기 실패";
        }
    }

}
