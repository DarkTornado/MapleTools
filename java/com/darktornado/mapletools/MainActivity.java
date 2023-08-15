package com.darktornado.mapletools;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;
import org.jsoup.Jsoup;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class MainActivity extends Activity {

    private WebView web;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Uri uri;
        switch (item.getItemId()) {
            case 0:
                startActivity(new Intent(this, InfoActivity.class));
                break;
            case 1:
                uri = Uri.parse("https://github.com/DarkTornado/MapleTools");
                startActivity(new Intent(Intent.ACTION_VIEW, uri));
                break;
            case 2:
                uri = Uri.parse("https://blog.naver.com/dt3141592");
                startActivity(new Intent(Intent.ACTION_VIEW, uri));
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, 0, 0, "앱 정보");
        menu.add(0, 1, 0, "깃허브로 이동");
        menu.add(0, 2, 0, "개발자 블로그");
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#F58801")));
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(1);

        web = new WebView(this);
        web.addJavascriptInterface(new JSLinker(), "android");
        web.loadUrl("file:///android_asset/index.html");
        web.getSettings().setJavaScriptEnabled(true);
        web.getSettings().setAllowUniversalAccessFromFileURLs(true);
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
                startActivity(new Intent(this, MutoActivity.class));
                break;
            case 4:
                startActivity(new Intent(this, UpgradeActivity.class));
                break;
            case 5:
                startActivity(new Intent(this, AddOptActivity.class));
                break;
            case 6:
                new Thread(this::loadMesoPrice).start();
                break;
            case 7:
                startActivity(new Intent(this, InfoActivity.class));
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

    private void loadMesoPrice() {
        try {
            String[][] _unit = {
                    {"생수", "병"},
                    {"물", "L"},
                    {"딱밤", "대"},
                    {"무릎통증 진료", "회"},
                    {"어제 산에서 찾은 산삼", "뿌리"},
                    {"유통기한 하루 남은 라면", "봉지"},
                    {"어제 빠진 머리카락", "가닥"},
                    {"맛있는 민물장어", "마리"},
                    {"이리나가 깎은 감자", "개"},
                    {"로얄 가드 성공", "번"},
                    {"제로투 ", "시간 추기"},
                    {"1 :", "으로 싸운 썰 듣기"}
            };
            List<String[]> unit = Arrays.asList(_unit);
            Collections.shuffle(unit);
            String data0 = Jsoup.connect("https://commapi.gamemarket.kr/comm/graph")
                    .ignoreContentType(true)
                    .post().text();
            String[] names = {"스카니아", "베라", "루나", "제니스", "크로아", "유니온", "엘리시움", "이노시스", "레드", "오로라", "아케인", "노바"};
            JSONObject data = new JSONObject(data0);
            StringBuilder result = new StringBuilder("<meta name='viewport' content='user-scalable=no width=device-width' />" +
                    "<style>td{padding:5px;}table{border: 1px solid #000000;border-collapse: collapse;}</style>" +
                    "<table width=100% border=1>");
            JSONObject data1 = data.getJSONArray("reverse").getJSONObject(0);
            JSONObject data2 = data.getJSONArray("reverse2").getJSONObject(0);
            for (int n = 0; n < names.length; n++) {
                result.append("<tr align=center><td rowspan=2 bgcolor=#EEEEEE><b>" + names[n] + "</b></td><td>" + data1.getString("server" + (n + 1)) + " 메이플포인트</td></tr>")
                        .append("<tr align=center><td>" + unit.get(n)[0] + " " + data2.getString("server" + (n + 1)) + " " + unit.get(n)[1] + "</td></tr>");
            }
            result.append("</table>");
            Intent intent = new Intent(MainActivity.this, MesoActivity.class);
            intent.putExtra("data", result.toString());
            startActivity(intent);
        } catch (Exception e) {
            toast("메소 시세 불러오기 실패");
        }
    }

    @Override
    public void onBackPressed() {
        if (web.canGoBack()) web.goBack();
        else super.onBackPressed();
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