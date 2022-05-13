package org.sjhstudio.weathertestapp.util

import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity

open class BaseActivity: AppCompatActivity() {

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