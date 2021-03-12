package com.darktornado.mapletools;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#F58801")));
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(1);

        WebView web = new WebView(this);
        web.addJavascriptInterface(new JSLinker(), "android");
        web.loadUrl("file:///android_asset/index.html");
        web.getSettings().setJavaScriptEnabled(true);
        layout.addView(web);

        layout.setBackgroundColor(Color.WHITE);
        setContentView(layout);
    }

    public void onButtonClick(int type) {
        switch (type) {
            case 0:
                inputCharName();
                break;
            case 1:
                startActivity(new Intent(this, IngDefActivity.class));
                break;
            case 2:
                startActivity(new Intent(this, BossActivity.class));
                break;
            case 3:
                break;
        }
    }

    private void inputCharName() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(1);
        TextView txt1 = new TextView(this);
        txt1.setText("캐릭터 이름 : ");
        txt1.setTextColor(Color.BLACK);
        txt1.setTextSize(18);
        layout.addView(txt1);
        final EditText txt2 = new EditText(this);
        txt2.setHint("캐릭터 이름을 입력하세요...");
        txt2.setSingleLine(true);
        layout.addView(txt2);
        int pad = dip2px(16);
        layout.setPadding(pad, pad, pad, pad);
        ScrollView scroll = new ScrollView(this);
        scroll.addView(layout);
        dialog.setView(scroll);
        dialog.setTitle("캐릭터 정보 조회");
        dialog.setNegativeButton("취소", null);
        dialog.setPositiveButton("확인", (dialog1, whichButton) -> {
            String name = txt2.getText().toString();
            if (name.equals("")) {
                toast("캐릭터의 이름이 입력되지 않았어요.");
            } else {
                Intent intent = new Intent(MainActivity.this, CharActivity.class);
                intent.putExtra("name", name);
                startActivity(intent);
                toast("캐릭터 정보를 불러오고 있어요...");
            }
        });
        dialog.show();
    }


    public int dip2px(int dips) {
        return (int) Math.ceil(dips * this.getResources().getDisplayMetrics().density);
    }

    public void toast(final String msg) {
        runOnUiThread(() -> Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show());
    }

    private class JSLinker {
        @JavascriptInterface
        public void callNative(final int type) {
            new Handler().post(() -> onButtonClick(type));
        }

    }
    
}
