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

    private val FAILED = "주흔작 계산에 실패했어요.\n작이 섞여있거나 주문의 흔적이 아닌 다른 주문서도 사용한 것 같아요."

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        actionBar!!.setBackgroundDrawable(ColorDrawable(Color.parseColor("#F58801")))

        val layout = LinearLayout(this)
        layout.orientation = 1

        val txt0 = TextView(this)
        txt0.text = "직업 : "
        layout.addView(txt0)
        val jobs = arrayOf<String?>("일반", "제논 (올스탯 주문서)", "데몬 어벤져 (체력 주문서)")
        val spin = Spinner(this)
        spin.adapter = ArrayAdapter<Any?>(this, android.R.layout.simple_list_item_1, jobs)
        spin.setSelection(0)
        spin.layoutParams = LinearLayout.LayoutParams(-1, -2)
        layout.addView(spin)

        val txt1 = TextView(this)
        txt1.text = "부위 : "
        layout.addView(txt1)
        val menus = arrayOf("무기 & 보조무기 (방패 제외)", "방어구 (방패 포함, 장갑 제외)", "장갑", "장신구", "기계심장")
        val radios = RadioGroup(this)
        for (n in menus.indices) {
            val radio = RadioButton(this)
            radio.text = menus[n]
            radio.id = n
            radios.addView(radio)
            if (n == 0) radio.isChecked = true
        }
        layout.addView(radios)

        val txt3 = TextView(this)
        txt3.text = "\n착용 레벨 제한 : "
        layout.addView(txt3)
        val txt4 = EditText(this)
        txt4.hint = "착용 레벨 제한 입력..."
        txt4.inputType = InputType.TYPE_CLASS_NUMBER
        layout.addView(txt4)

        val txt5 = TextView(this)
        txt5.text = "\n공격력 증가량 : "
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

        spin.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View, pos: Int, id: Long) {
                changeText(txt5, id.toInt(), radios.checkedRadioButtonId)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
        radios.setOnCheckedChangeListener { radioGroup: RadioGroup?, id: Int ->
            changeText(
                txt5,
                spin.selectedItemId.toInt(), id
            )
        }

        val calc = Button(this)
        calc.text = "주흔작 계산"
        calc.setOnClickListener {
            val job = spin.selectedItemId.toInt()
            val part = radios.checkedRadioButtonId
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
                    when (part) {
                        0 -> weaponCalc(dif, lv)
                        1 -> armorCalc(dif, lv, job)
                        2 -> glovesCalc(dif, lv)
                        3 -> accessoryCalc(dif, lv, job)
                        4 -> heartCalc(dif, lv)
                    }
                } else {
                    toast(FAILED)
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
        Tools.preventEdgeToEdge(scroll)
        setContentView(scroll)
    }

    private fun changeText(txt: TextView, job: Int, part: Int) {
        when (part) {
            0, 2, 4 -> //무기, 장갑, 기계심장
                txt.text = "\n공격력/마력 증가량 : "
            1 ->  // 방어구
                if (job == 1 || job == 2) { //제논 & 데벤
                    txt.text = "\n최대 HP 증가량 : "
                } else { //나머지
                    txt.text = "\n주스탯 증가량 : "
                }
            3 ->  //장신구
                if (job == 1) { //제논 (올스탯 주문서 확인)
                    txt.text = "\n올스탯 증가량 : "
                } else if (job == 2) { //데벤
                    txt.text = "\n최대 HP 증가량 : "
                } else { //나머지
                    txt.text = "\n주스탯 증가량 : "
                }
        }
    }

    private fun weaponCalc(diff: Int, lv: Int) {
        if (lv <= 70) {
            if (diff == 1) toast("100% 주문서가 사용되었어요")
            else if (diff == 2) toast("70% 주문서가 사용되었어요")
            else if (diff == 3) toast("30% 주문서가 사용되었어요")
            else if (diff == 5) toast("15% 주문서가 사용되었어요")
            else toast(FAILED)
        } else if (lv <= 110) {
            if (diff == 2) toast("100% 주문서가 사용되었어요")
            else if (diff == 3) toast("70% 주문서가 사용되었어요")
            else if (diff == 5) toast("30% 주문서가 사용되었어요")
            else if (diff == 7) toast("15% 주문서가 사용되었어요")
            else toast(FAILED)
        } else {
            if (diff == 3) toast("100% 주문서가 사용되었어요")
            else if (diff == 5) toast("70% 주문서가 사용되었어요")
            else if (diff == 7) toast("30% 주문서가 사용되었어요")
            else if (diff == 9) toast("15% 주문서가 사용되었어요")
            else toast(FAILED)
        }
    }

    private fun armorCalc(diff: Int, lv: Int, job: Int) {
        //주스탯으로 확인
        if (job == 0) {
            if (lv <= 70) {
                if (diff == 1) toast("100% 주문서가 사용되었어요")
                else if (diff == 2) toast("70% 주문서가 사용되었어요")
                else if (diff == 3) toast("30% 주문서가 사용되었어요")
                else if (diff == 4) toast("15% 주문서가 사용되었어요")
                else toast(FAILED)
            } else if (lv <= 115) {
                if (diff == 2) toast("100% 주문서가 사용되었어요")
                else if (diff == 3) toast("70% 주문서가 사용되었어요")
                else if (diff == 5) toast("30% 주문서가 사용되었어요")
                else if (diff == 7) toast("15% 주문서가 사용되었어요")
                else toast(FAILED)
            } else {
                if (diff == 3) toast("100% 주문서가 사용되었어요")
                else if (diff == 4) toast("70% 주문서가 사용되었어요")
                else if (diff == 7) toast("30% 주문서가 사용되었어요")
                else if (diff == 10) toast("15% 주문서가 사용되었어요")
                else toast(FAILED)
            }
        }

        //체력 증가로 확인 - 제논
        else if (job == 1) {
            if (lv <= 70) {
                if (diff == 30) toast("30% 주문서가 사용되었어요")
                else if (diff == 45) toast("15% 주문서가 사용되었어요")
                else toast(FAILED)
            } else if (lv <= 115) {
                if (diff == 70) toast("30% 주문서가 사용되었어요")
                else if (diff == 110) toast("15% 주문서가 사용되었어요")
                else toast(FAILED)
            } else {
                if (diff == 120) toast("30% 주문서가 사용되었어요")
                else if (diff == 170) toast("15% 주문서가 사용되었어요")
                else toast(FAILED)
            }
        }

        //체력 증가로 확인 - 데벤
        else {
            if (lv <= 70) {
                if (diff == 55) toast("100% 주문서가 사용되었어요")
                else if (diff == 115) toast("70% 주문서가 사용되었어요")
                else if (diff == 180) toast("30% 주문서가 사용되었어요")
                else if (diff == 245) toast("15% 주문서가 사용되었어요")
                else toast(FAILED)
            } else if (lv <= 115) {
                if (diff == 120) toast("100% 주문서가 사용되었어요")
                else if (diff == 190) toast("70% 주문서가 사용되었어요")
                else if (diff == 320) toast("30% 주문서가 사용되었어요")
                else if (diff == 460) toast("15% 주문서가 사용되었어요")
                else toast(FAILED)
            } else {
                if (diff == 180) toast("100% 주문서가 사용되었어요")
                else if (diff == 270) toast("70% 주문서가 사용되었어요")
                else if (diff == 470) toast("30% 주문서가 사용되었어요")
                else if (diff == 670) toast("15% 주문서가 사용되었어요")
                else toast(FAILED)
            }
        }
    }

    private fun glovesCalc(diff: Int, lv: Int) {
        if (lv <= 70) {
            if (diff == 0) toast("100% 주문서가 사용된 것 같아요")
            else if (diff == 1) toast("70% 주문서가 사용되었어요")
            else if (diff == 2) toast("30% 주문서가 사용되었어요")
            else if (diff == 3) toast("15% 주문서가 사용되었어요")
            else toast(FAILED)
        } else {
            if (diff == 1) toast("100% 주문서가 사용되었어요")
            else if (diff == 2) toast("70% 주문서가 사용되었어요")
            else if (diff == 3) toast("30% 주문서가 사용되었어요")
            else if (diff == 4) toast("15% 주문서가 사용되었어요")
            else toast(FAILED)
        }
    }

    private fun accessoryCalc(diff: Int, lv: Int, job: Int) {
        //주스탯으로 확인
        if (job == 0) {
            if (lv <= 70) {
                if (diff == 1) toast("100% 주문서가 사용되었어요");
                else if (diff == 2) toast("70% 주문서가 사용되었어요");
                else if (diff == 3) toast("30% 주문서가 사용되었어요");
                else toast(FAILED);
            } else if (lv <= 115) {
                if (diff == 1) toast("100% 주문서가 사용되었어요");
                else if (diff == 2) toast("70% 주문서가 사용되었어요");
                else if (diff == 4) toast("30% 주문서가 사용되었어요");
                else toast(FAILED);
            } else {
                if (diff == 2) toast("100% 주문서가 사용되었어요");
                else if (diff == 3) toast("70% 주문서가 사용되었어요");
                else if (diff == 5) toast("30% 주문서가 사용되었어요");
                else toast(FAILED);
            }
        }

        //올스탯 증가로 확인 - 제논
        else if (job == 1) {
            if (lv <= 70) {
                if (diff == 1) toast("30% 주문서가 사용되었어요");
                else toast(FAILED);
            } else if (lv <= 115) {
                if (diff == 2) toast("30% 주문서가 사용되었어요");
                else toast(FAILED);
            } else {
                if (diff == 3) toast("30% 주문서가 사용되었어요");
                else toast(FAILED);
            }
        }

        //체력 증가로 확인 - 데벤
        else {
            if (lv <= 70) {
                if (diff == 50) toast("100% 주문서가 사용되었어요");
                else if (diff == 100) toast("70% 주문서가 사용되었어요");
                else if (diff == 150) toast("30% 주문서가 사용되었어요");
                else toast(FAILED);
            } else if (lv <= 115) {
                if (diff == 50) toast("100% 주문서가 사용되었어요");
                else if (diff == 100) toast("70% 주문서가 사용되었어요");
                else if (diff == 200) toast("30% 주문서가 사용되었어요");
                else toast(FAILED);
            } else {
                if (diff == 100) toast("100% 주문서가 사용되었어요");
                else if (diff == 150) toast("70% 주문서가 사용되었어요");
                else if (diff == 250) toast("30% 주문서가 사용되었어요");
                else toast(FAILED);
            }
        }
    }

    private fun heartCalc(diff: Int, lv: Int) {
        if (diff == 9) {
            toast("스페셜 하트 공격력/마력 주문서가 사용된 것 같아요");
        } else if (lv <= 30) {
            if (diff == 1) toast("100% 주문서가 사용되었어요");
            else if (diff == 2) toast("70% 주문서가 사용되었어요");
            else if (diff == 3) toast("30% 주문서가 사용되었어요");
            else toast(FAILED);
        } else if (lv <= 100) {
            if (diff == 2) toast("100% 주문서가 사용되었어요");
            else if (diff == 3) toast("70% 주문서가 사용되었어요");
            else if (diff == 5) toast("30% 주문서가 사용되었어요");
            else toast(FAILED);
        } else {
            if (diff == 3) toast("100% 주문서가 사용되었어요");
            else if (diff == 4) toast("70% 주문서가 사용되었어요");
            else if (diff == 7) toast("30% 주문서가 사용되었어요");
            else toast(FAILED);
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