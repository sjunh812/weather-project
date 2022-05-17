package org.sjhstudio.weathertestapp.util

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity

open class BaseActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
//        window.decorView.setPadding(0, statusBarHeight(this), 0, 0)
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            android.R.id.home -> {  // 툴바 뒤로가기
                finish()
                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }

}