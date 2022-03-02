package com.darktornado.mapletools

import android.app.Activity
import android.app.AlertDialog
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.util.Pair
import android.view.Gravity
import android.view.View
import android.webkit.WebView
import android.widget.*
import android.widget.AdapterView.OnItemClickListener
import com.darktornado.listview.Item
import com.darktornado.listview.ListAdapter
import java.io.IOException

class AddOptActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        actionBar!!.setBackgroundDrawable(ColorDrawable(Color.parseColor("#F58801")))
        val layout = LinearLayout(this)
        layout.orientation = 1
        var _data: Pair<ArrayList<Item>, ArrayList<String>> = createData("파프니르", "150")
        val items = _data.first;
        val data = _data.second;
        _data = createData("앱솔랩스", "160")
        items.addAll(_data.first)
        data.addAll(_data.second)
        _data = createData("아케인셰이드", "200")
        items.addAll(_data.first)
        data.addAll(_data.second)
        _data = createData("제네시스", "gen")
        items.addAll(_data.first)
        data.addAll(_data.second)
        items.add(Item("라피스 & 라즐리", "대검 & 태도", loadIcon()))
        data.add("")

        val txt = EditText(this)
        txt.hint = "검색어를 입력하세요..."
        layout.addView(txt)
        val list = ListView(this)
        val adapter = ListAdapter()
        adapter.setItems(items)
        list.adapter = adapter
        list.isFastScrollEnabled = true
        list.onItemClickListener = OnItemClickListener { adapterView: AdapterView<*>?, view: View, pos: Int, id: Long ->
            var index = 0
            var _layout = view as LinearLayout
            _layout = _layout.getChildAt(1) as LinearLayout
            val text = (_layout.getChildAt(0) as TextView).text.toString()
            for (n in items.indices) {
                if (text == items[n].title) {
                    index = n
                    break
                }
            }
            if (text == "라피스 & 라즐리") {
                openZeroTable()
            } else {
                showInfo(items[index], data[index])
            }
        }
        txt.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                try {
                    adapter.getFilter().filter(s.toString())
                } catch (e: Exception) {
                    toast(e.toString())
                }
            }

            override fun afterTextChanged(s: Editable) {
                try {
                    if (txt.text.toString().length == 0) adapter.getFilter().filter(null)
                } catch (e: Exception) {
                    toast(e.toString())
                }
            }
        })
        layout.addView(list)
        val pad = dip2px(16)
        layout.setPadding(pad, pad, pad, pad)
        setContentView(layout)
    }

    private fun createData(name: String, type: String): Pair<ArrayList<Item>, ArrayList<String>> {
        return try {
            val data0: List<String> = Tools.readAsset(this, "csv/add_opt_$type.csv").split("\n")
            val names = ArrayList<Item>(data0.size - 1)
            val data = ArrayList<String>(data0.size - 1)
            for (n in 0 until data0.size - 1) {
                val datum = data0[n + 1].split(",".toRegex()).toTypedArray()
                names.add(Item(name + " " + datum[1], datum[0], loadIcon(n, type)))
                data.add("<meta name='viewport' content='user-scalable=no width=device-width' />" +
                        "<style>td{padding:5px;}table{border: 1px solid #000000;border-collapse: collapse;}</style>" +
                        "<table width=100% border=1>" +
                        "<tr align=center><td width=20% bgcolor=#EEEEEE><b>종류</b></td><td colspan=2 width=40%>" + datum[0] + "</td><td width=20% bgcolor=#EEEEEE><b>공격력</b></td><td width=20%>" + datum[7] + "</td></tr>" +
                        "<tr align=center bgcolor=#EEEEEE><td><b>1추</b></td><td><b>2추</b></td><td><b>3추</b></td><td><b>4추</b></td><td><b>5추</b></td></tr>" +
                        "<tr align=center><td>" + datum[2] + "</td><td>" + datum[3] + "</td><td>" + datum[4] + "</td><td>" + datum[5] + "</td><td>" + datum[6] + "</td></tr>" +
                        "<table>")
            }
            Pair(names, data)
        } catch (e: IOException) {
            Log.i("_dev", e.toString())
            Pair(ArrayList<Item>(), ArrayList<String>())
        }
    }

    private fun loadIcon(index: Int, type: String): Drawable? {
        try {
            val types = arrayOf("One-handedSword", "One-handedAxe", "One-handedBluntWeapon", "Two-handedSword", "Two-handedAxe", "Two-handedBluntWeapon", "Spear", "Polearm", "Desperado", "ArmCannon", "Bladecaster", "Wand", "Staff", "ShiningRod", "Psy-limiter", "LucentGauntlet", "Bow", "CrowssBow", "AncientBow", "DualBowguns", "Whispershot", "Claw", "Dagger", "Cane", "Chain", "RitualFan", "WhipBlade", "Knuckle", "Gun", "HandCannon", "SoulShooter")
            val am = this.assets
            val `is` = am.open("images/weapons/" + types[index] + "_" + type + ".png")
            val bitmap = BitmapFactory.decodeStream(`is`)
            `is`.close()
            return BitmapDrawable(bitmap)
        } catch (e: IOException) {
            toast("앱 리버싱이 감지되었어요?\n$e")
        }
        return null
    }

    private fun loadIcon(): Drawable {
        try {
            val am = this.assets
            val `is` = am.open("images/LimitBreak.png")
            val bitmap = BitmapFactory.decodeStream(`is`)
            `is`.close()
            return BitmapDrawable(bitmap)
        } catch (e: IOException) {
            toast("앱 리버싱이 감지되었어요?\n$e")
        }
        return BitmapDrawable()
    }


    private fun showInfo(item: Item, data: String) {
        val dialog: AlertDialog.Builder = AlertDialog.Builder(this);
        dialog.setTitle(item.title)
        dialog.setIcon(item.icon)
        val layout = LinearLayout(this)
        layout.orientation = 1
        val web = WebView(this)
        if (Build.VERSION.SDK_INT > 23) {
            web.loadDataWithBaseURL(null, data, "text/html; charset=UTF-8", null, null)
        } else {
            web.loadData(data, "text/html; charset=UTF-8", null)
        }
        layout.addView(web)
        val pad = dip2px(16)
        layout.setPadding(pad, pad, pad, pad)
        dialog.setView(layout)
        dialog.setNegativeButton("닫기", null)
        dialog.show()
    }

    private fun openZeroTable() {
        val window = PopupWindow()
        val layout = LinearLayout(this)
        layout.orientation = 1
        val title = Toolbar(this)
        title.title = "제로 추가옵션 표"
        title.setTitleTextColor(Color.WHITE)
        title.setBackgroundColor(Color.parseColor("#F58801"))
        title.setElevation(dip2px(5).toFloat())
        layout.addView(title)
        val web = WebView(this)
        web.loadUrl("file:///android_asset/add_opt_zero.html")
        web.settings.javaScriptEnabled = true
        layout.addView(web)
        window.contentView = layout
        window.isFocusable = true
        window.width = -1
        window.height = -1
        window.animationStyle = android.R.style.Animation_InputMethod
        window.setBackgroundDrawable(ColorDrawable(Color.WHITE))
        window.showAtLocation(getWindow().decorView, Gravity.CENTER, 0, 0)
    }

    fun dip2px(dips: Int): Int {
        return Math.ceil(dips * this.resources.displayMetrics.density.toDouble()).toInt()
    }

    fun toast(msg: String?) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }
}