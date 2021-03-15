package com.darktornado.mapletools;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.darktornado.listview.Item;
import com.darktornado.listview.ListAdapter;

import java.io.InputStream;
import java.util.ArrayList;

public class MutoActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#F58801")));
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(1);

        final ArrayList<Item> items = createFoodList();
        ListView list = new ListView(this);
        ListAdapter adapter = new ListAdapter();
        adapter.setItems(items);
        list.setAdapter(adapter);
        list.setOnItemClickListener((parent, view, pos, id) -> showRecipe(items.get(pos)));
        layout.addView(list);

        int pad = dip2px(16);
        layout.setPadding(pad, pad, pad, pad);
        setContentView(layout);
    }

    private void showRecipe(Item item) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle(item.title);
        dialog.setIcon(item.icon);

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(1);
        final ArrayList<Item> items = parseRecipe(item.subtitle);
        ListView list = new ListView(this);
        ListAdapter adapter = new ListAdapter();
        adapter.setIconSize(dip2px(25));
        adapter.setItems(items);
        list.setAdapter(adapter);
        layout.addView(list);

        int pad = dip2px(16);
        layout.setPadding(pad, pad, pad, pad);
        dialog.setView(layout);
        dialog.setNegativeButton("닫기", null);
        dialog.show();
    }

    private ArrayList<Item> parseRecipe(String input) {
        ArrayList<Item> list = new ArrayList<>();
        String[] data = input.split(", ");
        switch (data.length) {
            case 2:
                list.add(new Item(data[0], "5개", loadIconByName(data[0])));
                list.add(new Item(data[1], "10개", loadIconByName(data[1])));
                break;
            case 3:
                list.add(new Item(data[0], "5개", loadIconByName(data[0])));
                list.add(new Item(data[1], "5개", loadIconByName(data[1])));
                list.add(new Item(data[2], "10개", loadIconByName(data[2])));
                break;
            case 4:
                list.add(new Item(data[0], "5개", loadIconByName(data[0])));
                list.add(new Item(data[1], "5개", loadIconByName(data[1])));
                list.add(new Item(data[2], "10개", loadIconByName(data[2])));
                list.add(new Item(data[3], "1개", loadIconByName(data[3])));
                break;
        }
        return list;
    }

    private ArrayList<Item> createFoodList() {
        ArrayList<Item> list = new ArrayList<>();
        list.add(new Item("앗볶음", "느끼껍질, 폭신발바닥", loadFoodIcon(0, 0)));
        list.add(new Item("깔깔만두", "새콤껍질, 쫀득발바닥", loadFoodIcon(0, 1)));
        list.add(new Item("헉튀김", "달달발굽, 담백갈기", loadFoodIcon(0, 2)));
        list.add(new Item("낄낄볶음밥", "매콤발굽, 톡톡갈기", loadFoodIcon(0, 3)));
        list.add(new Item("허허말이", "담백갈기, 단단물갈퀴, 미끈깃털", loadFoodIcon(0, 4)));
        list.add(new Item("오잉피클", "톡톡갈기, 시큼물갈퀴, 끈적깃털", loadFoodIcon(0, 5)));
        list.add(new Item("이런면", "달달발굽, 담백갈기, 단단물갈퀴", loadFoodIcon(0, 6)));
        list.add(new Item("휴피자", "매콤발굽, 톡톡갈기, 시큼물갈퀴", loadFoodIcon(0, 7)));
        list.add(new Item("저런찜", "느끼껍질, 폭신발바닥, 바싹등껍질", loadFoodIcon(0, 8)));
        list.add(new Item("하빵", "새콤껍질, 쫀득발바닥, 말랑등껍질", loadFoodIcon(0, 9)));
        list.add(new Item("호호탕", "단단물갈퀴, 바싹등껍질, 텁텁발톱", loadFoodIcon(0, 10)));
        list.add(new Item("큭큭죽", "시큼물갈퀴, 말랑등껍질, 쫄깃발톱", loadFoodIcon(0, 11)));
        list.add(new Item("크헉구이", "느끼껍질, 폭신발바닥, 미끈깃털, 츄릅열매", loadFoodIcon(0, 12)));
        list.add(new Item("흑흑화채", "새콤껍질, 쫀득발바닥, 끈적깃털, 츄릅열매", loadFoodIcon(0, 13)));
        list.add(new Item("으악샐러드", "담백갈기, 바싹등껍질, 텁텁발톱, 츄릅열매", loadFoodIcon(0, 14)));
        list.add(new Item("엉엉순대", "톡톡갈기, 말랑등껍질, 쫄깃발톱, 츄릅열매", loadFoodIcon(0, 15)));
        return list;
    }

    private Drawable loadFoodIcon(int type, int id) {
        try {
            String[] types = {"foods", "ingredients"};
            AssetManager am = this.getAssets();
            InputStream is = am.open("images/" + types[type] + "/" + id + ".png");
            Bitmap bitmap = BitmapFactory.decodeStream(is);
            is.close();
            return new BitmapDrawable(bitmap);
        } catch (Exception e) {
            toast("앱 리버싱이 감지되었어요?\n" + e.toString());
        }
        return null;
    }

    private Drawable loadIconByName(String name) {
        String[] names = {"달달발굽", "매콤발굽", "느끼껍질", "새콤껍질", "담백갈기", "톡톡갈기", "폭신발바닥", "쫀득발바닥", "단단물갈퀴", "시큼물갈퀴", "바싹등껍질", "말랑등껍질", "미끈깃털", "끈적깃털", "텁텁발톱", "쫄깃발톱", "츄릅열매"};
        for (int n = 0; n < names.length; n++) {
            if (names[n].equals(name)) return loadFoodIcon(1, n);
        }
        return null;
    }


    public int dip2px(int dips) {
        return (int) Math.ceil(dips * this.getResources().getDisplayMetrics().density);
    }

    public void toast(final String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
}