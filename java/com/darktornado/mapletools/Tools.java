package com.darktornado.mapletools;

import android.content.Context;
import android.graphics.Insets;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowInsets;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

public class Tools {

    public static final String VERSION = "3.0";

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

    public static String getWebText(String link) {
        try {
            URL url = new URL(link);
            URLConnection con = url.openConnection();
            if (con != null) {
                con.setConnectTimeout(5000);
                con.setUseCaches(false);
                InputStreamReader isr = new InputStreamReader(con.getInputStream(), "UTF-8");
                BufferedReader br = new BufferedReader(isr);
                String str = br.readLine();
                String line = "";
                while ((line = br.readLine()) != null) {
                    str += "\n" + line;
                }
                br.close();
                isr.close();
                return str;
            }
        } catch (Exception e) {
            //toast(e.toString());
        }
        return null;
    }

    public static void preventEdgeToEdge(View view) {
        if (Build.VERSION.SDK_INT >= 35) view.setOnApplyWindowInsetsListener(new View.OnApplyWindowInsetsListener() {
            @Override
            public WindowInsets onApplyWindowInsets(View view, WindowInsets windowInsets) {
//                if (Build.VERSION.SDK_INT < 35) return null;
                Insets insets = windowInsets.getInsets(WindowInsets.Type.systemBars());

                ViewGroup.MarginLayoutParams mlp = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
                mlp.topMargin = insets.top;
                mlp.bottomMargin = insets.bottom;
                mlp.leftMargin = insets.left;
                mlp.rightMargin = insets.right;

                return WindowInsets.CONSUMED;
            }
        });
    }

}
