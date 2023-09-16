package com.darktornado.mapletools

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.widget.*

class InfoActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        actionBar!!.setBackgroundDrawable(ColorDrawable(Color.parseColor("#F58801")))
        val layout = LinearLayout(this)
        layout.orientation = 1

        val txt = TextView(this)
        txt.text = "앱 이름 : 메이플 도구\n" +
                "버전 : ${Tools.VERSION}\n" +
                "개발자 : Dark Tornado\n" +
                "라이선스 : GPL 3.0\n" +
                "\n" +
                " 메이플스토리와 관련된 잡기능들을 합쳐놓은 앱인거에요. 개발자의 깃허브에 소스 코드가 공개되어있어요.\n" +
                "\n" +
                Tools.loadLicense(this, "license") +
                "\n"
        txt.textSize = 18f
        layout.addView(txt);

        val lice = Button(this)
        lice.text = "오픈 소스 라이선스"
        lice.setOnClickListener {
            startActivity(Intent(this, LicenseActivity::class.java));
        }
        layout.addView(lice)

        val pad = dip2px(16)
        layout.setPadding(pad, pad, pad, pad)
        val scroll = ScrollView(this)
        scroll.addView(layout)
        setContentView(scroll)
    }

    fun dip2px(dips: Int) = Math.ceil((dips * this.resources.displayMetrics.density).toDouble()).toInt()
}