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
        layout.addView(txt2);

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
            String input_b = txt2.getText().toString();
            String input_c = txt4.getText().toString();
            if (input_b.equals("") || input_c.equals("")) {
                toast("입력하지 않은 값이 있어요.");
            } else {
                int defense = Integer.parseInt(input_b);
                int ignore = Integer.parseInt(input_c);
                calcDefense(defense, ignore);
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

    private void calcDefense(int protect, int ignore) {
        int defense = (int)((double)protect - (double)protect * (double)ignore / 100.0);
        int assault = 100 - defense;
        showDialog("계산 결과", "보스 방어율 : " + protect + "%\n" +
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