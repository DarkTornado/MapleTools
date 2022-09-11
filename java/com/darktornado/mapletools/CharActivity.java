package com.darktornado.mapletools;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.text.format.DateFormat;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Date;

public class CharActivity extends Activity {

    private CharInfo info;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        new Thread(() -> {
            try {
                Bitmap bitmap = createCard();
                String fileName = info.name + "_" + DateFormat.format("yyyyMMdd_HHmmss", new Date()).toString();
                String path = "Pictures/MapleTools";
                Uri uri = saveCard(bitmap, path, fileName);

                if (item.getItemId() == 1) {
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_SEND);
                    intent.putExtra(Intent.EXTRA_STREAM, uri);
                    intent.setType("image/*");
                    startActivity(Intent.createChooser(intent, "캐릭터 정보 공유"));
                }
            } catch (Exception e) {
                toast(e.toString());
            }
        }).start();
        return super.onOptionsItemSelected(item);
    }

    private Uri saveCard(Bitmap bitmap, String path, String fileName) throws Exception {
        ContentResolver resolver = getContentResolver();

        if (Build.VERSION.SDK_INT >= 29) {
            Uri imageCollection = MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY);
            ContentValues values = new ContentValues();
            values.put(MediaStore.Images.Media.DISPLAY_NAME, fileName);
            values.put(MediaStore.Images.Media.MIME_TYPE, "image/png");
            values.put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/MapleTools");
            values.put(MediaStore.Images.Media.IS_PENDING, 1);

            Uri uri = resolver.insert(imageCollection, values);
            ParcelFileDescriptor pfd = resolver.openFileDescriptor(uri, "w", null);
            FileOutputStream fos = new FileOutputStream(pfd.getFileDescriptor());
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.close();

            values.clear();
            values.put(MediaStore.Images.Media.IS_PENDING, 0);
            resolver.update(uri, values, null, null);

            return uri;
        } else {
            fileName += ".png";

            path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + path;
            new File(path).mkdirs();

            File file = new File(path, fileName);
            FileOutputStream fos = new java.io.FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.close();

            return Uri.parse("file:///" + path);
        }

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

            return new CharInfo(name, img, rank, exp, pri, guild, serIcon, serName, lv, job);
        } catch (Exception e) {
            if (!params.equals("")) toast("해당 닉네임을 가진 캐릭터를 찾을 수 없거나, 오류가 발생햐였습니다.");
//            toast(e.toString());
        }
        return null;
    }

    private Bitmap createCard() {
        try {
            int s = 2;
            String path = "images/background.png";
            Bitmap background = BitmapFactory.decodeStream(getAssets().open(path));
            Bitmap cache = getImageFromWeb(info.img);
            Bitmap charImage = Bitmap.createScaledBitmap(cache, cache.getWidth() * s, cache.getHeight() * s, true);
            cache = getImageFromWeb(info.server);
            Bitmap server = Bitmap.createScaledBitmap(cache, cache.getWidth() * s, cache.getHeight() * s, true);

            Bitmap bitmap = Bitmap.createBitmap(background.getWidth(), background.getHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            canvas.drawBitmap(background, new Matrix(), new Paint());

//            Typeface font = Typeface.createFromAsset(getAssets(), "Maplestory.ttf");
            Paint paint = new Paint();
            paint.setAntiAlias(true);
//            paint.setTypeface(font);
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
