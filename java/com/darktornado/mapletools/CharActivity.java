package com.darktornado.mapletools;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

public class CharActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#F58801")));
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(1);
        WebView web = new WebView(this);
        LinearLayout.LayoutParams margin = new LinearLayout.LayoutParams(-1, -1);
        int mar = dip2px(20);
        margin.setMargins(mar, mar, mar, mar);
        web.setLayoutParams(margin);
        layout.addView(web);
        loadMapleInfo(getIntent().getStringExtra("name"), web);

        layout.setBackgroundColor(Color.WHITE);
        setContentView(layout);
    }

    private void loadMapleInfo(final String name, final WebView web) {
        new Thread(() -> {
            try {
                CharInfo info = loadCharInfo(name, "");
                if (info == null) info = loadCharInfo(name, "&w=254");

                final StringBuilder result = new StringBuilder("<meta name='viewport' content='user-scalable=no width=device-width' />")
                        .append("<style>td{padding:5px;}table{border: 1px solid #000000;border-collapse: collapse;}</style>")
                        .append("<table width=100% border=1><tr align=center><td style='padding:4px;'><b>닉네임</b></td><td>" + name + "</td></tr>")
                        .append("<tr align=center><td style='padding:4px;'><b>직업</b></td><td>" + info.job + "</td></tr>")
                        .append("<tr align=center><td style='padding:4px;'><b>레벨</b></td><td>" + info.lv + "</td></tr>")
                        .append("<tr align=center><td style='padding:4px;'><b>경험치</b></td><td>" + info.exp + "</td></tr>")
                        .append("<tr align=center><td style='padding:4px;'><b>순위</b></td><td>" + info.rank + "위</td></tr>")
                        .append("<tr align=center><td style='padding:4px;'><b>인기도</b></td><td>" + info.pri + "</td></tr>")
                        .append("<tr align=center><td style='padding:4px;'><b>서버</b></td><td><img src='" + info.server + "'>&nbsp;" + info.world + "</td></tr>");
                if (!info.guild.equals("")) {
                    result.append("<tr align=center><td style='padding:4px;'><b>길드</b></td><td>" + info.guild + "</td></tr>");
                }
                result.append("<tr align=center><td style='padding:4px;'><b>캐릭터</b></td><td><img src='" + info.img + "'></td></tr>")
                        .append("</table>");

                runOnUiThread(() -> {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        web.loadDataWithBaseURL(null, result.toString(), "text/html; charset=UTF-8", null, null);
                    } else {
                        web.loadData(result.toString(), "text/html; charset=UTF-8", null);
                    }
                });
            } catch (Exception e) {
                toast("해당 닉네임을 가진 캐릭터를 찾을 수 없거나, 오류가 발생했어요.");
//                toast(e.toString());
            }
        }).start();
    }

    private CharInfo loadCharInfo(String name, String params) {
        try {
            Elements data0 = Jsoup.connect("https://maplestory.nexon.com/Ranking/World/Total?c=" + name + params).get().select("tr.search_com_chk");
            String url = "https://maplestory.nexon.com" + data0.select("a").attr("href");//.replace(name + "?p=", name + "?Ranking=");
            Document data = Jsoup.connect(url).get();

            String img = data0.select("span.char_img").select("img").get(0).attr("src");
            String rank = data0.select("td").get(0).select("p").get(0).text();
            String exp = data0.select("td").get(3).text();
            String pri = data0.select("td").get(4).text();
            String guild = data0.select("td").get(5).text();
            String serIcon = data0.select("img").get(5).attr("src");

            Elements tmp = data.select("div.char_info").select("dd");
            String lv = tmp.get(0).text().replace("LV.", "");
            String job = tmp.get(1).text();
            String serName = tmp.get(2).text();

            return new CharInfo(img, rank, exp, pri, guild, serIcon, serName, lv, job);
        } catch (Exception e) {
            if (!params.equals("")) toast("해당 닉네임을 가진 캐릭터를 찾을 수 없거나, 오류가 발생햐였습니다.");
//            toast(e.toString());
        }
        return null;
    }


    public int dip2px(int dips) {
        return (int) Math.ceil(dips * this.getResources().getDisplayMetrics().density);
    }

    public void toast(final String msg) {
        runOnUiThread(() -> Toast.makeText(CharActivity.this, msg, Toast.LENGTH_SHORT).show());
    }


    private static class CharInfo {
        String img, rank, exp, pri, guild, server, world, lv, job;

        public CharInfo(String img, String rank, String exp, String pri, String guild, String server, String world, String lv, String job) {
            this.img = img;
            this.rank = rank;
            this.exp = exp;
            this.guild = guild;
            this.pri = pri;
            this.server = server;
            this.world = world;
            this.lv = lv;
            this.job = job;
        }
    }
}
