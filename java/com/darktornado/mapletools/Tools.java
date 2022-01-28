package com.darktornado.mapletools;

import android.content.Context;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class Tools {

    public static final String VERSION = "2.0";

    public static String loadLicense(Context ctx, String fileName) {
        String license = readAsset(ctx, "license/" + fileName + ".txt");
        if (license == null) return "라이선스 정보 불러오기 실패";
        return license;
    }

    public static String readAsset(Context ctx, String path) {
        try {
            InputStreamReader isr = new InputStreamReader(ctx.getAssets().open(path));
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
            return null;
        }
    }

    public static String getMesoData() {

        return null;
    }

}
