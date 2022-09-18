package com.darktornado.mapletools

import android.app.Activity
import android.app.AlertDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.InputType
import android.view.View
import android.widget.*
import android.widget.AdapterView.OnItemSelectedListener

class UpgradeActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        actionBar!!.setBackgroundDrawable(ColorDrawable(Color.parseColor("#F58801")))

        val layout = LinearLayout(this)
        layout.orientation = 1

        val txt1 = TextView(this)
        val txt3 = TextView(this)
        val txt5 = TextView(this)
        val txt4 = EditText(this)
        val txt6 = EditText(this)
        val txt7 = TextView(this)
        val txt8 = EditText(this)
        val txt9 = TextView(this)
        val txt10 = EditText(this)

        txt1.text = "부위 : "
        txt1.setTextColor(Color.BLACK)
        txt1.textSize = 18f
        layout.addView(txt1)
        val parts = arrayOf<String?>("무기 & 보조무기 (방패 제외)", "방어구 (방패 포함, 장갑 제외)", "장갑", "장신구")
        val spin = Spinner(this)
        spin.adapter = ArrayAdapter<Any?>(this, android.R.layout.simple_list_item_1, parts)
        spin.layoutParams = LinearLayout.LayoutParams(-1, -2)
        spin.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View, pos: Int, id: Long) {
                if (pos == 2) {
                    txt5.text = "\n공격력 증가량 : "
                    txt6.hint = "공격력 증가량을 입력하세요..."
                    txt7.text = "\n스타포스 : "
                    txt8.hint = "스타포스 수치를 입력하세요..."
                } else {
                    txt5.text = "\n주스탯 증가량 : "
                    txt6.hint = "주스탯 증가량을 입력하세요..."
                    txt7.text = "\n부스탯 증가량 : "
                    txt8.hint = "부스탯 증가량을 입력하세요..."
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
        layout.addView(spin)
        txt3.text = "\n착용 레벨 제한 : "
        txt3.setTextColor(Color.BLACK)
        txt3.textSize = 18f
        layout.addView(txt3)
        txt4.hint = "착용 레벨 제한을 입력하세요..."
        txt4.inputType = InputType.TYPE_CLASS_NUMBER
        layout.addView(txt4)

        txt5.text = "\n주스탯 증가량 : "
        txt5.setTextColor(Color.BLACK)
        txt5.textSize = 18f
        layout.addView(txt5)
        txt6.hint = "주스탯 증가량을 입력하세요..."
        txt6.inputType = InputType.TYPE_CLASS_NUMBER
        layout.addView(txt6)
        txt7.text = "\n부스탯 증가량 : "
        txt7.setTextColor(Color.BLACK)
        txt7.textSize = 18f
        layout.addView(txt7)
        txt8.hint = "부스탯 증가량을 입력하세요..."
        txt8.inputType = InputType.TYPE_CLASS_NUMBER
        layout.addView(txt8)

        txt9.text = "\n업그레이드 성공 횟수 : "
        txt9.setTextColor(Color.BLACK)
        txt9.textSize = 18f
        layout.addView(txt9)
        txt10.hint = "성공 횟수를 입력하세요..."
        txt10.inputType = InputType.TYPE_CLASS_NUMBER
        layout.addView(txt10)

        val calc = Button(this)
        calc.text = "주흔작 계산"
        calc.setOnClickListener { view: View? ->
            val type = spin.selectedItemPosition
            val _lv = txt4.text.toString()
            val main = txt6.text.toString()
            val sub = txt8.text.toString()
            val up = txt10.text.toString()
            if (_lv == "" || main == "" || sub == "" || up == "") {
                toast("입력되지 않은 값이 있어요.")
            } else {
                val lv = _lv.toInt()
                val diff: Double
                diff = if (type == 2) {
                    val attack = main.toDouble()
                    val star = sub.toDouble()
                    val attack2: Double = star2attack(star.toInt())
                    if (attack2 == -1.0) {
                        toast("22성까지만 계산할 수 있어요 :(")
                        return@setOnClickListener
                    }
                    (attack - attack2) / up.toDouble()
                } else {
                    (main.toDouble() - sub.toDouble()) / up.toDouble()
                }
                if (Math.rint(diff) != diff) {
                    toast("주흔작 계산에 실패했어요.\n아이템에 주문의 흔적이 아닌 다른 주문서도 사용한 것 같아요.")
                } else {
                    when (type) {
                        0 -> weaponCalc(diff.toInt(), lv)
                        1 -> armorCalc(diff.toInt(), lv)
                        2 -> glovesCalc(diff.toInt(), lv)
                        3 -> accessoryCalc(diff.toInt(), lv)
                    }
                }
            }
        }
        layout.addView(calc)
        val info = Button(this)
        info.text = "기능 정보"
        info.setOnClickListener { view: View? ->
            showDialog("기능 정보 & 도움말", " 아이템에 사용된 주문서가 100%인지 70%인지 30%인지 15%인지 계산해주는 기능이에요.\n" +
                    " 주문의 흔적이 아닌 다른 주문서를 사용한 아이템은 계산할 수 없어요.")
        }
        layout.addView(info)

        val pad: Int = dip2px(20)
        layout.setPadding(pad, pad, pad, pad)
        val scroll = ScrollView(this)
        scroll.addView(layout)
        setContentView(scroll)
    }

    private fun star2attack(star: Int): Double {
        if (star > 22) return (-1).toDouble()
        val data = intArrayOf(0,
                0, 0, 0, 0, 1, 1, 2, 2, 3, 3,
                4, 4, 5, 6, 7, 17, 28, 40, 53, 67,
                82, 99
        )
        return data[star].toDouble()
    }

    private fun weaponCalc(diff: Int, lv: Int) {
        if (lv <= 70) {
            if (diff == 0) toast("100% 주문서 또는 70% 주문서가 사용되었어요")
            else if (diff == 1) toast("30% 주문서가 사용되었어요")
            else if (diff == 2) toast("15% 주문서가 사용되었어요")
            else toast("주흔작 계산에 실패했어요.\n아이템에 주문의 흔적이 아닌 다른 주문서도 사용한 것 같아요.")
        } else if (lv <= 110) {
            if (diff == 0) toast("100% 주문서가 사용되었어요")
            else if (diff == 1) toast("70% 주문서가 사용되었어요")
            else if (diff == 2) toast("30% 주문서가 사용되었어요")
            else if (diff == 3) toast("15% 주문서가 사용되었어요")
            else toast("주흔작 계산에 실패했어요.\n아이템에 주문의 흔적이 아닌 다른 주문서도 사용한 것 같아요.")
        } else {
            if (diff == 1) toast("100% 주문서가 사용되었어요")
            else if (diff == 2) toast("70% 주문서가 사용되었어요")
            else if (diff == 3) toast("30% 주문서가 사용되었어요")
            else if (diff == 4) toast("15% 주문서가 사용되었어요")
            else toast("주흔작 계산에 실패했어요.\n아이템에 주문의 흔적이 아닌 다른 주문서도 사용한 것 같아요.")
        }
    }

    private fun armorCalc(diff: Int, lv: Int) {
        if (lv <= 70) {
            if (diff == 1) toast("100% 주문서가 사용되었어요")
            else if (diff == 2) toast("70% 주문서가 사용되었어요")
            else if (diff == 3) toast("30% 주문서가 사용되었어요")
            else toast("주흔작 계산에 실패했어요.\n아이템에 주문의 흔적이 아닌 다른 주문서도 사용한 것 같아요.")
        } else if (lv <= 110) {
            if (diff == 2) toast("100% 주문서가 사용되었어요")
            else if (diff == 3) toast("70% 주문서가 사용되었어요")
            else if (diff == 5) toast("30% 주문서가 사용되었어요")
            else toast("주흔작 계산에 실패했어요.\n아이템에 주문의 흔적이 아닌 다른 주문서도 사용한 것 같아요.")
        } else {
            if (diff == 3) toast("100% 주문서가 사용되었어요")
            else if (diff == 4) toast("70% 주문서가 사용되었어요")
            else if (diff == 7) toast("30% 주문서가 사용되었어요")
            else toast("주흔작 계산에 실패했어요.\n아이템에 주문의 흔적이 아닌 다른 주문서도 사용한 것 같아요.")
        }
    }

    private fun accessoryCalc(diff: Int, lv: Int) {
        if (lv <= 70) {
            if (diff == 1) toast("100% 주문서가 사용되었어요")
            else if (diff == 2) toast("70% 주문서가 사용되었어요")
            else if (diff == 3) toast("30% 주문서가 사용되었어요")
            else toast("주흔작 계산에 실패했어요.\n아이템에 주문의 흔적이 아닌 다른 주문서도 사용한 것 같아요.")
        } else if (lv <= 110) {
            if (diff == 1) toast("100% 주문서가 사용되었어요")
            else if (diff == 2) toast("70% 주문서가 사용되었어요")
            else if (diff == 4) toast("30% 주문서가 사용되었어요")
            else toast("주흔작 계산에 실패했어요.\n아이템에 주문의 흔적이 아닌 다른 주문서도 사용한 것 같아요.")
        } else {
            if (diff == 2) toast("100% 주문서가 사용되었어요")
            else if (diff == 3) toast("70% 주문서가 사용되었어요")
            else if (diff == 5) toast("30% 주문서가 사용되었어요")
            else toast("주흔작 계산에 실패했어요.\n아이템에 주문의 흔적이 아닌 다른 주문서도 사용한 것 같아요.")
        }
    }

    private fun glovesCalc(diff: Int, lv: Int) {
        if (lv <= 70) {
            if (diff == 0) toast("100% 주문서가 사용되었어요?")
            else if (diff == 1) toast("70% 주문서가 사용되었어요")
            else if (diff == 2) toast("30% 주문서가 사용되었어요")
            else toast("주흔작 계산에 실패했어요.\n아이템에 주문의 흔적이 아닌 다른 주문서도 사용한 것 같아요.")
        } else {
            if (diff == 1) toast("100% 주문서가 사용되었어요")
            else if (diff == 2) toast("70% 주문서가 사용되었어요")
            else if (diff == 3) toast("30% 주문서가 사용되었어요")
            else toast("주흔작 계산에 실패했어요.\n아이템에 주문의 흔적이 아닌 다른 주문서도 사용한 것 같아요.")
        }
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