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

        val txt5 = TextView(this)

        val hp = Switch(this)
        hp.text = "데몬 어벤져 작 계산"
        layout.addView(hp)

        val txt1 = TextView(this)
        txt1.text = "부위 : "
        layout.addView(txt1)
        val menus = arrayOf("무기 & 보조무기 (방패 제외)", "방어구 (방패 포함, 장갑 제외)", "장갑", "장신구")
        val type = intArrayOf(0)
        val radios = RadioGroup(this)
        for (n in menus.indices) {
            val radio = RadioButton(this)
            radio.text = menus[n]
            radio.id = n
            radios.addView(radio)
            if (n == 0) radio.isChecked = true
        }
        radios.setOnCheckedChangeListener { radioGroup: RadioGroup?, id: Int ->
            type[0] = id
            if (id == 2) {
                txt5.text = "\n공격력 증가량 : "
            } else {
                txt5.text = "\n주스탯 증가량 : "
            }
        }
        layout.addView(radios)

        val txt3 = TextView(this)
        txt3.text = "\n착용 레벨 제한 : "
        layout.addView(txt3)
        val txt4 = EditText(this)
        txt4.hint = "착용 레벨 제한 입력..."
        txt4.inputType = InputType.TYPE_CLASS_NUMBER
        layout.addView(txt4)

        txt5.text = "\n주스탯 증가량 : "
        layout.addView(txt5)
        val txt6 = EditText(this)
        txt6.hint = "착용 레벨 제한 입력..."
        txt6.inputType = InputType.TYPE_CLASS_NUMBER
        layout.addView(txt6)

        val txt7 = TextView(this)
        txt7.text = "\n업그레이드 성공 횟수 : "
        layout.addView(txt7)
        val txt8 = EditText(this)
        txt8.hint = "착용 레벨 제한 입력..."
        txt8.inputType = InputType.TYPE_CLASS_NUMBER
        layout.addView(txt8)

        val calc = Button(this)
        calc.text = "주흔작 계산"
        calc.setOnClickListener {
            val level = txt4.text.toString()
            val stat = txt6.text.toString()
            val up = txt8.text.toString()
            if (level == "" || stat == "" || up == "") {
                toast("입력되지 않은 값이 있어요")
            } else {
                val diff = stat.toDouble() / up.toDouble()
                if (Math.rint(diff) == diff) {
                    val lv = level.toInt()
                    val dif = diff.toInt()
                    val isDev = hp.isChecked
                    when (type[0]) {
                        0 -> weaponCalc(dif, lv)
                        1 -> armorCalc(dif, lv)
                        2 -> glovesCalc(dif, lv)
                        3 -> accessoryCalc(dif, lv)
                    }
                } else {
                    toast("주흔작 계산에 실패했어요.\n작이 섞여있거나 주문의 흔적이 아닌 다른 주문서도 사용한 것 같아요.")
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