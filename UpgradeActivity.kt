package com.darktornado.mapletools

import android.app.Activity
import android.app.AlertDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.InputType
import android.view.View
import android.widget.*

class UpgradeActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        actionBar!!.setBackgroundDrawable(ColorDrawable(Color.parseColor("#F58801")))

        val layout = LinearLayout(this)
        layout.orientation = 1

        val txt1 = TextView(this)
        txt1.text = "주스탯 증가량 : "
        txt1.setTextColor(Color.BLACK)
        txt1.textSize = 18f
        layout.addView(txt1)
        val txt2 = EditText(this)
        txt2.hint = "주스탯 증가량을 입력하세요..."
        txt2.inputType = InputType.TYPE_CLASS_NUMBER
        layout.addView(txt2)
        val txt3 = TextView(this)
        txt3.text = "\n부스탯 증가량 : "
        txt3.setTextColor(Color.BLACK)
        txt3.textSize = 18f
        layout.addView(txt3)
        val txt4 = EditText(this)
        txt4.hint = "부스탯 증가량을 입력하세요..."
        txt4.inputType = InputType.TYPE_CLASS_NUMBER
        layout.addView(txt4)
        val txt5 = TextView(this)
        txt5.text = "\n업그레이드 성공 횟수 : "
        txt5.setTextColor(Color.BLACK)
        txt5.textSize = 18f
        layout.addView(txt5)
        val txt6 = EditText(this)
        txt6.hint = "성공 횟수를 입력하세요..."
        txt6.inputType = InputType.TYPE_CLASS_NUMBER
        layout.addView(txt6)

        val calc = Button(this)
        calc.text = "주흔작 계산"
        calc.setOnClickListener { view: View? ->
            val input1 = txt2.text.toString()
            val input2 = txt4.text.toString()
            val input3 = txt6.text.toString()
            if (input1.isBlank() || input2.isBlank() || input3.isBlank()) {
                toast("입력되지 않은 값이 있어요.")
            } else {
//                selectItemType(input1.toInt(), input2.toInt(), input3.toInt())
            }
        }
        layout.addView(calc)

        val info = Button(this)
        info.text = "기능 정보"
        info.setOnClickListener { view: View? ->
            showDialog("기능 정보 & 도움말", "아이템에 사용된 주문서가 100%인지 70%인지 30%인지 15%인지 계산해주는 기능이에요.\n" +
                    "주문의 흔적이 아닌 다른 주문서를 사용한 아이템은 계산할 수 없어요.")
        }
        layout.addView(info)

        val pad: Int = dip2px(20)
        layout.setPadding(pad, pad, pad, pad)
        val scroll = ScrollView(this)
        scroll.addView(layout)
        setContentView(scroll)
    }

    private fun showDialog(title: String, msg: String) {
        try {
            val dialog = AlertDialog.Builder(this)
            dialog.setTitle(title)
            dialog.setMessage(msg)
            dialog.setNegativeButton("닫기", null)
            dialog.show()
        } catch (e: Exception) {
            toast(e.toString())
        }
    }

    fun toast(msg: String) = Toast.makeText(this, msg, Toast.LENGTH_LONG).show()

    fun dip2px(dips: Int) = Math.ceil((dips * this.resources.displayMetrics.density).toDouble()).toInt()
}