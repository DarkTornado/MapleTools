package com.darktornado.mapletools;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.darktornado.library.ImageSaver;

import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Arrays;
import java.util.Date;

public class CharActivity extends Activity {

    private CharInfo info;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (Build.VERSION.SDK_INT >= 23) {
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 5);
        }
        selectBackground(item.getItemId());
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, 0, 0, "프로필 저장");
        menu.add(0, 1, 0, "프로필 저장 및 공유");
        return true;
    }

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
                info = loadCharInfo(name, "");
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
            Elements data0 = Jsoup.connect("https://maplestory.nexon.com/N23Ranking/World/Total?c=" + name + params)
//                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/71.0.3578.98 Safari/537.36")
                    .get().select("tr.search_com_chk");
            String url = "https://maplestory.nexon.com" + data0.select("a").attr("href");//.replace(name + "?p=", name + "?Ranking=");

            Elements td = data0.select("td");
            String rank = td.get(0).select("p").get(0).text();
            String job = td.get(1).select("dd").text();
            String level = td.get(2).text().replace("Lv.", "");
            String exp = td.get(3).text();
            String pri = td.get(4).text();
            String guild = td.get(5).text();

            Document data = Jsoup.parse(Tools.getWebText(url));

            Elements dd = data.select("div.char_info").select("dd");
            String image = data.select("div.char_img").select("img").attr("src")
                    .replace("/Character/180/", "/Character/");
            String server = dd.get(2).text();
            String icon = dd.get(2).select("img").attr("src");

            return new CharInfo(name, image, rank, exp, pri, guild, icon, server, level, job);
        } catch (Exception e) {
            if (!params.equals("")) toast("해당 닉네임을 가진 캐릭터를 찾을 수 없거나, 오류가 발생햐였습니다.");
//            toast(e.toString());
        }
        return null;
    }


    private void selectBackground(final int itemId) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("배경 선택");
        String[] names = {"기본 배경", "레벨 별 배경 자동 선택"};
        dialog.setItems(names, (dialog1, which) -> {
            if (which == 0) prepareCard(itemId, -1);
            else new Thread(() -> selectLevel(itemId)).start();
        });
        dialog.setNegativeButton("취소", null);
        dialog.show();
    }

    private void selectLevel(final int itemId) {
        String url = "https://raw.githubusercontent.com/DarkTornado/MapleTools/main/max_level_map.txt";
        String data = getTextFromWeb(url);
        if (data == null) {
            toast("인터넷 연결을 확인해주세요");
        } else {
            int lv = Integer.parseInt(info.lv);
            if (lv < 200) {
                lv = -1;
            } else {
                lv /= 5;
                lv *= 5;
            }
            int maxlv = Integer.parseInt(data);
            prepareCard(itemId, Math.min(lv, maxlv));
        }
    }

    private void prepareCard(final int itemId, final int back) {
        new Thread(() -> {
            try {
                toast("이미지 생성중...");
                Bitmap bitmap = createCard(back);
                runOnUiThread(() -> showCard(bitmap, itemId));
            } catch (Exception e) {
                toast(e.toString());
            }
        }).start();
    }

    private Bitmap createCard(int back) {
        try {
            int s = 2;
            Bitmap background;
            if (back == -1) background = BitmapFactory.decodeStream(getAssets().open("images/charcard.png"));
            else background = getImageFromWeb("https://darktornado.net/mapletools/images/" + back + ".jpg");

            Bitmap cache = getImageFromWeb(info.img.replace("/Character/", "/Character/180/"));
            Bitmap charImage = Bitmap.createScaledBitmap(cache, cache.getWidth() * s, cache.getHeight() * s, true);
            cache = getImageFromWeb(info.server);
            Bitmap server = Bitmap.createScaledBitmap(cache, cache.getWidth() * s, cache.getHeight() * s, true);

            Bitmap bitmap = Bitmap.createBitmap(background.getWidth(), background.getHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            canvas.drawBitmap(background, new Matrix(), new Paint());

            Typeface font = Typeface.createFromAsset(getAssets(), "fonts/Maplestory.ttf");
            Paint paint = new Paint();
            paint.setAntiAlias(true);
            paint.setTypeface(font);
            paint.setColor(Color.argb(90, 255, 255, 255));

            canvas.drawRect(0, 0, 360 * s, 180 * s, paint);

            canvas.drawBitmap(charImage, 200 * s, 0 * s, new Paint());
            canvas.drawBitmap(server, 8 * s, 9 * s, new Paint());

            paint.setColor(Color.BLACK);
            paint.setTextSize(20 * s);
            canvas.drawText(info.name, 20 * s, 160 * s, paint);

            paint.setTextSize(12 * s);
            canvas.drawText("Lv." + info.lv + " " + info.job, 20 * s, 135 * s, paint);
            canvas.drawText(info.world + (info.guild.equals("") ? "" : " / " + info.guild), 30 * s, 19 * s, paint);

            return bitmap;
        } catch (IOException e) {
            toast(e.toString());
        }
        return null;
    }

    private void showCard(final Bitmap bitmap, final int itemId) {
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(1);
        ImageView image = new ImageView(this);
        image.setImageBitmap(bitmap);
        image.setLayoutParams(new LinearLayout.LayoutParams(-1, -2));
        layout.addView(image);
        TextView txt = new TextView(this);
        txt.setText("저장/공유");
        txt.setGravity(Gravity.RIGHT);
        int pad = dip2px(8);
        txt.setPadding(pad, pad, pad, pad);
        layout.addView(txt);
        pad = dip2px(16);
        layout.setPadding(pad, pad, pad, pad);
        ScrollView scroll = new ScrollView(this);
        scroll.addView(layout);
        final PopupWindow window = new PopupWindow(scroll, -2, -2, true);
        txt.setOnClickListener(view -> {
            try {
                String fileName = info.name + "_" + DateFormat.format("yyyyMMdd_HHmmss", new Date()).toString();
                String path = "Pictures/MapleTools";
                Uri uri = ImageSaver.INSTANCE.saveImage(this, bitmap, path, fileName);
                if (itemId == 1) {
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_SEND);
                    intent.putExtra(Intent.EXTRA_STREAM, uri);
                    intent.setType("image/*");
                    startActivity(Intent.createChooser(intent, "캐릭터 정보 공유"));
                    bitmap.recycle();
                } else {
                    toast("저장되었어요.");
                }
                window.dismiss();
            } catch (Exception e) {
                toast(e.toString());
            }
        });
        window.setAnimationStyle(android.R.style.Animation_InputMethod);
        window.setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        window.setElevation(dip2px(3));
        window.showAtLocation(getWindow().getDecorView(), Gravity.CENTER, 0, 0);
    }

    public String getTextFromWeb(String link) {
        try {
            URL url = new URL(link);
            URLConnection con = url.openConnection();
            if (con != null) {
                con.setConnectTimeout(5000);
                con.setUseCaches(false);
                con.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/85.0.4183.121 Safari/537.36");
                InputStreamReader isr = new InputStreamReader(con.getInputStream());
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

    public Bitmap getImageFromWeb(String link) {
        try {
            URL url = new URL(link);
            URLConnection con = url.openConnection();
            if (con != null) {
                con.setConnectTimeout(5000);
                con.setUseCaches(false);
                BufferedInputStream bis = new BufferedInputStream(con.getInputStream());
                Bitmap bitmap = BitmapFactory.decodeStream(bis);
                bis.close();
                return bitmap;
            }
        } catch (Exception e) {
            toast(e.toString());
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
        String name, img, rank, exp, pri, guild, server, world, lv, job;

        public CharInfo(String name, String img, String rank, String exp, String pri, String guild, String server, String world, String lv, String job) {
            this.name = name;
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