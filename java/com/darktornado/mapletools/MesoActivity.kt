package com.darktornado.mapletools

import android.app.Activity
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.webkit.WebView
import android.widget.LinearLayout

class MesoActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        actionBar!!.setBackgroundDrawable(ColorDrawable(Color.parseColor("#F58801")))

        val data = intent.getStringExtra("data")
        val layout = LinearLayout(this)
        layout.orientation = 1

        val web = WebView(this)
        if (Build.VERSION.SDK_INT > 23) {
            web.loadDataWithBaseURL(null, data!!, "text/html; charset=UTF-8", null, null)
        } else {
            web.loadData(data!!, "text/html; charset=UTF-8", null)
        }
        val mar = dip2px(16)
        val margin = LinearLayout.LayoutParams(-2, -1)
        margin.setMargins(mar, mar, mar, mar)
        web.layoutParams = margin
        layout.addView(web)
        layout.setBackgroundColor(Color.WHITE)
        setContentView(layout)
    }

    fun dip2px(dips: Int) = Math.ceil((dips * this.resources.displayMetrics.density).toDouble()).toInt()
}