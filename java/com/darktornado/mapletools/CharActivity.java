package com.darktornado.mapletools;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
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
import android.os.StrictMode;
import android.provider.MediaStore;
import android.text.format.DateFormat;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.darktornado.library.ImageSaver;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Arrays;
import java.util.Date;

public class CharActivity extends Activity {

    private CharInfo info;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (Build.VERSION.SDK_INT >= 23 && checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            toast("이미지를 저장하기 위해 해당 권한이 필요해요.");
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 5);
            selectBackground(item.getItemId());
        } else {
            selectBackground(item.getItemId());
        }
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

    private void selectBackground(final int itemId) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("배경 선택");
        String[] names = {"기본 배경", "자동 선택", "레벨 별 배경", "직업 별 배경"};
        dialog.setItems(names, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (Integer.parseInt(info.lv) < 200 && which == 2)
                    toast("캐릭터의 레벨이 200 이상일 때만 사용할 수 있어요 :(");
                else if (which < 2) prepareCard(itemId, which, -1);
                else selectBackground2(itemId, which);
            }
        });
        dialog.setNegativeButton("취소", null);
        dialog.show();
    }

    private void selectBackground2(final int itemId, final int which) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("지역/직업 선택");
        final String[][] names = {{"소멸의 여로 (200)", "리버스 시티 (205)", "츄츄 아일랜드 (210)", "얌얌 아일랜드 (215)", "꿈의 도시 레헬른 (220)",
                "신비의 숲 아르카나 (225)", "기억의 늪 모라스 (230)", "태초의 바다 에스페라 (235)", "셀라스, 별이 잠긴 곳 (240)", "문브릿지 (245)",
                "고통의 미궁 (250)", "리멘 (250)", "신의 도시 세르니움 (260)", "불타는 세르니움 (265)", "호텔 아르크스 (270)", "눈을 뜬 실험실 오디움 (275)"
        }, { //직업은 출시 순서
                "모험가", "시그너스 기사단", "영웅", "모험가 - 듀얼블레이드", "레지스탕스", "데몬", "카이저", "엔젤릭버스터",
                "제로", "키네시스", "카데나", "일리움", "아크", "모험가 - 패스파인더", "호영", "아델", "카인", "라라"
        }};
        final int[] lvs = {200, 205, 210, 215, 220, 225, 230, 235, 240, 245, 250, 255, 260, 265, 270, 275};
        int lv = Integer.parseInt(info.lv);
        for (int n = 0; n < names[0].length; n++) {
            if (lv < lvs[n]) names[0] = Arrays.copyOfRange(names[0], 0, n);
        }
        dialog.setItems(names[which - 2], new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int w) {
                if (which == 2) prepareCard(itemId, which, lvs[w]);
                else prepareCard(itemId, which, w);
            }
        });
        dialog.setNegativeButton("취소", null);
        dialog.show();
    }

    private void prepareCard(final int itemId, final int type, final int back) {
        new Thread(() -> {
            try {
                StrictMode.enableDefaults();
                Bitmap bitmap = createCard(type, back);
                String fileName = info.name + "_" + DateFormat.format("yyyyMMdd_HHmmss", new Date()).toString();
                String path = "Pictures/MapleTools";
                Uri uri = ImageSaver.INSTANCE.saveImage(this, bitmap, path, fileName);

                if (itemId == 1) {
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

    }

    private Bitmap createCard(int type, int back) {
        try {
            int s = 2;
            int lv = Integer.parseInt(info.lv);
            String path = "images/charcard/";
            if (type == 0) { //기본 배경
                path += "default";
            } else if (type == 1) { //자동 선택
                if (lv < 200) {
                    path += String.valueOf(jobBackground(info.job));
                } else {
                    if (lv < 205) path += "200";
                    else if (lv < 210) path += "205";
                    else if (lv < 215) path += "210";
                    else if (lv < 220) path += "215";
                    else if (lv < 225) path += "220";
                    else if (lv < 230) path += "225";
                    else if (lv < 235) path += "230";
                    else if (lv < 240) path += "235";
                    else if (lv < 245) path += "240";
                    else if (lv < 250) path += "245";
                    else if (lv < 255) path += "250";
                    else if (lv < 260) path += "255";
                    else if (lv < 265) path += "260";
                    else if (lv < 270) path += "265";
                    else if (lv < 275) path += "270";
                    else path += "275";
                }
            } else { //사용자가 직접 선택한 경우
                path += String.valueOf(back);
            }
            Bitmap background = BitmapFactory.decodeStream(getAssets().open(path + ".png"));
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

    private int jobBackground(String job) {
        if (job.startsWith("전사/")) return 0;
        if (job.startsWith("마법사/")) return 0;
        if (job.startsWith("궁수/패스파인더")) return 13;
        if (job.startsWith("궁수/")) return 0;
        if (job.startsWith("도적/듀얼블레이더")) return 3;
        if (job.startsWith("도적/")) return 0;
        if (job.startsWith("해적/")) return 0;

        if (job.startsWith("기사단/")) return 1;

        if (job.equals("아란")) return 2;
        if (job.equals("에반")) return 2;
        if (job.equals("메르세데스")) return 2;
        if (job.equals("팬텀")) return 2;
        if (job.equals("루미너스")) return 2;
        if (job.equals("은월")) return 2;

        if (job.contains("데몬")) return 5;
        if (job.startsWith("레지스탕스/")) return 4;


        if (job.equals("카이저")) return 6;
        if (job.equals("엔젤릭버스터")) return 7;
        if (job.equals("카데나")) return 10;
        if (job.equals("카인")) return 16;

        if (job.equals("아델")) return 15;
        if (job.equals("일리움")) return 11;
        if (job.equals("아크")) return 12;

        if (job.equals("호영")) return 14;
        if (job.equals("라라")) return 17;

        if (job.equals("제로")) return 8;
        if (job.equals("키네시스")) return 9;

        return 99;
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