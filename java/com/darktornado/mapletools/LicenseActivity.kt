package com.darktornado.mapletools

import android.app.Activity
import android.app.AlertDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Html
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast

class LicenseActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        actionBar!!.setBackgroundDrawable(ColorDrawable(Color.parseColor("#F58801")))
        val layout = LinearLayout(this)
        layout.orientation = 1

        loadLicenseInfo(layout, "jsoup", "jsoup", "MIT License", "Jonathan Hedley", true)
        loadLicenseInfo(layout, "Material Design", "material", "Apache License 2.0", "Google", false)
        loadLicenseInfo(layout, "MapleStory", "maple", "", "Nexon", false)

        val pad = dip2px(16)
        layout.setPadding(pad, pad, pad, pad)
        val scroll = ScrollView(this)
        scroll.addView(layout)
        Tools.preventEdgeToEdge(scroll)
        setContentView(scroll)
    }


    private fun loadLicenseInfo(layout: LinearLayout, name: String, fileName: String, license: String, dev: String, tf: Boolean) {
        val pad = dip2px(10)
        val title = TextView(this)
        if (tf) title.text = Html.fromHtml("<b>$name<b>")
        else title.text = Html.fromHtml("<br><b>$name<b>")
        title.textSize = 24f
        title.setPadding(pad, 0, pad, dip2px(1))
        layout.addView(title)
        val subtitle = TextView(this)
        if (license.isBlank()) subtitle.text = "  by $dev"
        else subtitle.text = "  by $dev, $license"
        subtitle.textSize = 20f
        subtitle.setPadding(pad, 0, pad, pad)
        layout.addView(subtitle)
        val value = Tools.loadLicense(this, fileName)
        val txt = TextView(this)
        if (value.length > 1500) {
            txt.text = Html.fromHtml(value.substring(0, 1500).replace("\n", "<br>") + "...<font color='#757575'><b>[Show All]</b></font>")
            txt.setOnClickListener {
                showDialog(license, value)
            }
        } else {
            txt.text = value
        }
        txt.textSize = 17f
        txt.setPadding(pad, pad, pad, pad)
        txt.setBackgroundColor(Color.parseColor("#32F58801"))
        layout.addView(txt)
    }

    fun showDialog(title: String?, msg: String?) {
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