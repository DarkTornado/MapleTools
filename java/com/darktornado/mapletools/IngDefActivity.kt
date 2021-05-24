package com.darktornado.mapletools

import android.app.Activity
import android.app.AlertDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.view.Gravity
import android.widget.*
import kotlin.math.roundToInt

class IngDefActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        actionBar!!.setBackgroundDrawable(ColorDrawable(Color.parseColor("#F58801")))
        val layout = LinearLayout(this)
        layout.orientation = 1

        val txt1 = TextView(this)
        val txt2 = EditText(this)
        val txt3 = TextView(this)
        val txt4 = EditText(this)
        val txt5 = TextView(this)
        val txt6 = EditText(this)

        txt1.text = "스탯창 방무 : "
        txt1.textSize = 18f
        txt1.setTextColor(Color.BLACK);
        layout.addView(txt1)
        txt2.hint = "스탯창에 표시된 방어율 무시 입력"
        txt2.inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
        layout.addView(txt2)
        txt3.text = "\n나머지 방무(들) : "
        txt3.textSize = 18f
        txt3.setTextColor(Color.BLACK);
        layout.addView(txt3)
        txt4.hint = "나머지 방어율 무시(들) 입력 (엔터로 구분)"
//        txt4.inputType = InputType.TYPE_CLASS_NUMBER
        layout.addView(txt4)

        val calc = Button(this)
        calc.text = "실방무 계산"
        calc.setOnClickListener {
            val input1 = txt2.text.toString();
            val input2 = txt4.text.toString();
            if (input1.isBlank() || input2.isBlank()) {
                toast("입력되지 않은 값이 있어요.")
            } else {
                val result = calcIgnoreDEF(input1.toDouble(), input2).toString()
                txt6.text = Editable.Factory.getInstance().newEditable(result)
                toast("실방무 계산 결과 약 $result%인거에요.")
            }
        }
        layout.addView(calc)

        txt5.text = "\n계산 결과 : "
        txt5.textSize = 18f
        txt5.setTextColor(Color.BLACK);
        layout.addView(txt5)
        txt6.hint = "계산된 실방무..."
        txt6.inputType = InputType.TYPE_CLASS_NUMBER
        layout.addView(txt6)

        val info = Button(this)
        info.text = "기능 정보 & 도움말"
        info.setOnClickListener {
            showDialog("기능 정보 & 도움말", " '방어율 무시' 스탯을 계산해주는 기능이에요. 스탯창에 뜨는 방어율 무시 수치와, 스킬이나 코어 강화에 붙어있는 방어율 무시 수치(들)를 입력하면 실제로 적용되는 방어율 무시 수치를 계산해주는거에요.\n" +
                    " 스탯창에 뜨는 방어율 무시 수치는 올림된 값이고, 이 앱에서 계산한 결과가 100% 일치한다고 보장하지는 않는거에요\n\n" +
                    " 방무 적용 공식 : 현재방무 + (100 - 현재방무) × 추가되는 방무")
        }
        layout.addView(info)

        val maker = TextView(this)
        maker.text = "\n© 2021 Dark Tornado, All rights reserved.\n"
        maker.textSize = 13f
        maker.gravity = Gravity.CENTER
        maker.setTextColor(Color.BLACK)
        layout.addView(maker)

        val pad = dip2px(16)
        layout.setPadding(pad, pad, pad, pad)
        val scroll = ScrollView(this)
        scroll.addView(layout)
        setContentView(scroll)
    }

    fun calcIgnoreDEF(_current: Double, input: String): Double {
        var current: Double = _current
        val defs = input.split("\n")
        for (def in defs) {
            val diff = (100.0 - current) * (def.toDouble() / 100)
            current += diff
        }
        current *= 100
        return current.roundToInt().toDouble() / 100;
    }

    fun showDialog(title: String, msg: String) {
        val dialog = AlertDialog.Builder(this)
        dialog.setTitle(title)
        dialog.setMessage(msg)
        dialog.setNegativeButton("닫기", null)
        dialog.show()
    }

    fun toast(msg: String) = Toast.makeText(this, msg, Toast.LENGTH_LONG).show()

    fun dip2px(dips: Int) = Math.ceil((dips * this.resources.displayMetrics.density).toDouble()).toInt()

}