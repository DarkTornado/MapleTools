package com.darktornado.mapletools;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.InputType;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class BossActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#F58801")));
        final LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(1);

        TextView txt1 = new TextView(this);
        txt1.setText("보스 : ");
        txt1.setTextSize(18);
        txt1.setTextColor(Color.BLACK);
        layout.addView(txt1);
        final EditText txt2 = new EditText(this);
        txt2.setHint("보스의 방어율 입력...");
        txt2.setInputType(InputType.TYPE_CLASS_NUMBER);

        final String[] bossList = {"발록 (이지)",
                "자쿰 (이지)", "자쿰 (노멀)", "자쿰 (카오스)",
                "우르스 (노멀)",
                "매그너스 (이지)", "매그너스 (노멀)", "매그너스 (하드)",
                "힐라 (노멀)", "힐라 (하드)",
                "카웅 (노멀)",
                "파풀라투스 (이지)", "파풀라투스 (노멀)", "파풀라투스 (카오스)",
                "반반 (노멀)", "반반 (카오스)",
                "피에르 (노멀)", "피에르 (카오스)",
                "블러디퀸 (노멀)", "블러디퀸 (카오스)",
                "벨룸 (노멀)", "벨룸 (카오스)",
                "반 레온 (이지)", "반 레온 (노멀)", "반 레온 (하드)",
                "혼테일 (이지)", "혼테일 (노멀)", "혼테일 (카오스)",
                "아카이럼 (이지)", "아카이럼 (노멀)",
                "핑크빈 (노멀)", "핑크빈 (카오스)",
                "시그너스 (이지)", "시그너스 (노멀)",
                "스우 (노멀)", "스우 (하드)",
                "데미안 (노멀)", "데미안 (하드)",
                "루시드 (이지)", "루시드 (노멀)", "루시드 (하드)",
                "윌 (노멀)", "윌 (하드)",
                "더스크 (노멀)", "더스크 (카오스)",
                "진 힐라 (하드)",
                "듄켈 (노멀)", "듄켈 (하드)",
                "검은 마법사 (하드)",
                "세렌 (노멀)", "세렌 (하드)",
                "방어율 직접 입력"};
        final int[] def = {25, 30, 40, 100, 10, 50, 50, 120, 50, 100, 60, 50, 90, 250,
                50, 100, 50, 80, 50, 120, 55, 200, //카루타
                50, 80, 100, 40, 40, 50, 40, 90, 70, 100, 100, 100,
                300, 300, 300, 300, 300, 300, 300, 300, 300, //스데미 ~ 윌
                300, 300, 300, 300, 300,
                300, 380, 380, -1
        };
        int[] selectedId = {0};
        Spinner spin = new Spinner(this);
        spin.setAdapter(new ArrayAdapter(this, android.R.layout.simple_list_item_1, bossList));
        spin.setLayoutParams(new LinearLayout.LayoutParams(-1, -2));
        spin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                try {
                    selectedId[0] = pos;
                    if (def[pos] == -1) {
                        layout.addView(txt2, 2);
                    } else {
                        layout.removeView(txt2);
                    }
                } catch (Exception e) {
//                    toast(e.toString());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        layout.addView(spin);

        TextView txt3 = new TextView(this);
        txt3.setText("실방무 : ");
        txt3.setTextSize(18);
        txt3.setTextColor(Color.BLACK);
        layout.addView(txt3);
        final EditText txt4 = new EditText(this);
        txt4.setHint("내 실방무 입력...");
        txt4.setInputType(InputType.TYPE_CLASS_NUMBER);
        layout.addView(txt4);

        Button calc = new Button(this);
        calc.setText("계산");
        calc.setOnClickListener(v -> {
            String input = txt4.getText().toString();
            if (input.equals("")) {
                toast("캐릭터의 실제 방어율 무시를 입력해주세요.");
            } else {
                int ignore = Integer.parseInt(input);
                int defense = def[selectedId[0]];
                if(defense<0) defense = Integer.parseInt(txt2.getText().toString());
                calcDefense(bossList[selectedId[0]], defense, ignore);
            }
        });
        layout.addView(calc);
        Button inde = new Button(this);
        inde.setText("실방무 계산기");
        inde.setOnClickListener(v -> startActivity(new Intent(BossActivity.this, IngDefActivity.class)));
        layout.addView(inde);

        TextView maker = new TextView(this);
        maker.setText("\n© 2021-2023 Dark Tornado, All rights reserved.\n");
        maker.setTextSize(13f);
        maker.setTextColor(Color.BLACK);
        maker.setGravity(Gravity.CENTER);
        layout.addView(maker);

        int pad = dip2px(16);
        layout.setPadding(pad, pad, pad, pad);
        ScrollView scroll = new ScrollView(this);
        scroll.addView(layout);
        setContentView(scroll);
    }

    private void calcDefense(String boss, int protect, int ignore) {
        int defense = (int)((double)protect - (double)protect * (double)ignore / 100.0);
        int assault = 100 - defense;
        showDialog(boss.replace(")", " 모드)"),
                "보스 방어율 : " + protect + "%\n" +
                        "캐릭터 방어율 무시 : " + ignore + "%\n" +
                        "보스가 무시하는 데미지 : " + defense + "%\n" +
                        "들어가는 데미지 : " + assault + "%");
        if(assault<0) toast("들어가는 데미지가 1보다 낮으면 1씩 들어가는거에요.");
    }

    private void showDialog(String title, String msg){
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle(title);
        dialog.setMessage(msg);
        dialog.setNegativeButton("닫기", null);
        dialog.show();
    }

    public int dip2px(int dips) {
        return (int) Math.ceil(dips * this.getResources().getDisplayMetrics().density);
    }

    public void toast(final String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
}