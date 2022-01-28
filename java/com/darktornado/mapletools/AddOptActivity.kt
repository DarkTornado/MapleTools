package com.darktornado.mapletools

import android.app.Activity
import android.app.AlertDialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Pair
import android.view.View
import android.widget.*
import android.widget.AdapterView.OnItemClickListener
import java.io.IOException
import java.util.*

class AddOptActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val layout = LinearLayout(this)
        layout.orientation = 1
        var _data: Pair<Array<String?>, Array<String?>> = createData("파프니르", "150")!!
        val names = ArrayList(Arrays.asList<String>(*_data.first))
        val data = ArrayList(Arrays.asList<String>(*_data.second))
        _data = createData("앱솔랩스", "160")!!
        names.addAll(Arrays.asList<String>(*_data.first))
        data.addAll(Arrays.asList<String>(*_data.second))
        _data = createData("아케인셰이드", "200")!!
        names.addAll(Arrays.asList<String>(*_data.first))
        data.addAll(Arrays.asList<String>(*_data.second))
        _data = createData("제네시스", "gen")!!
        names.addAll(Arrays.asList<String>(*_data.first))
        data.addAll(Arrays.asList<String>(*_data.second))
        val txt = EditText(this)
        txt.hint = "검색어를 입력하세요..."
        layout.addView(txt)
        val list = ListView(this)
        val adapter: ArrayAdapter<*> = ArrayAdapter<Any?>(this, android.R.layout.simple_list_item_1, names.toArray())
        list.adapter = adapter
        list.isFastScrollEnabled = true
        list.onItemClickListener = OnItemClickListener { adapterView: AdapterView<*>?, view: View, pos: Int, id: Long ->
            var index = 0
            val text = (view as TextView).text.toString()
            for (n in names.indices) {
                if (text == names[n]) {
                    index = n
                    break
                }
            }
            val title = names[index].split(" - ".toRegex()).toTypedArray()[0]
            showDialog(title, data[index])
        }
        txt.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                try {
                    adapter.filter.filter(s.toString())
                } catch (e: Exception) {
                    toast(e.toString())
                }
            }

            override fun afterTextChanged(s: Editable) {
                try {
                    if (txt.text.toString().length == 0) adapter.filter.filter(null)
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


    private fun createData(name: String, type: String): Pair<Array<String?>, Array<String?>>? {
        return try {
            val data0: List<String> = Tools.readAsset(this, "csv/add_opt_$type.csv").split("\n")
            val names = arrayOfNulls<String>(data0.size - 1)
            val data = arrayOfNulls<String>(data0.size - 1)
            for (n in 0 until data0.size - 1) {
                val datum = data0[n + 1].split(",".toRegex()).toTypedArray()
                names[n] = name + " " + datum[1] + " - " + datum[0]
                data[n] = """
                    종류 : ${datum[0]}
                    공격력 : ${datum[7]}
                    1추 : ${datum[2]}
                    2추 : ${datum[3]}
                    3추 : ${datum[4]}
                    4추 : ${datum[5]}
                    5추 : ${datum[6]}
                    """.trimIndent()
            }
            Pair(names, data)
        } catch (e: IOException) {
            Pair(arrayOfNulls(0), arrayOfNulls(0))
        }
    }


    private fun showDialog(title: String, msg: String) {
        val dialog = AlertDialog.Builder(this)
        dialog.setTitle(title)
        dialog.setMessage(msg)
        dialog.setNegativeButton("닫기", null)
        dialog.show()
    }

    fun dip2px(dips: Int): Int {
        return Math.ceil(dips * this.resources.displayMetrics.density.toDouble()).toInt()
    }

    fun toast(msg: String?) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }
}