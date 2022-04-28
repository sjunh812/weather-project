package org.sjhstudio.weathertestapp.util

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import org.sjhstudio.weathertestapp.R
import java.text.SimpleDateFormat
import java.util.*

object Utils {

    @SuppressLint("SimpleDateFormat")
    val dateFormat = SimpleDateFormat("yyyy년 MM월 dd일 a h시 mm분")
    @SuppressLint("SimpleDateFormat")
    val baseDateFormat = SimpleDateFormat("yyyyMMdd")
    @SuppressLint("SimpleDateFormat")
    val fcstTimeFormat = SimpleDateFormat("HH00")
    @SuppressLint("SimpleDateFormat")
    val beforeTimeFormat = SimpleDateFormat("HHmm")
    @SuppressLint("SimpleDateFormat")
    val afterTimeFormat = SimpleDateFormat("a h시")

    private val cal = Calendar.getInstance()

    fun getDataFormatString(
        value: String,
        inputFormat: SimpleDateFormat,
        outputFormat: SimpleDateFormat
    ): String? {
        val date = inputFormat.parse(value)
        return date?.let { outputFormat.format(it) }
    }

    fun getYesterday(): Date {
        cal.time = Date()
        cal.add(Calendar.DATE, -1)

        return cal.time
    }

    /**
     * 위치권한 체크
     * @param view : Snackbar에 이용
     */
    fun checkLocPermission(context: Context, view: View): Boolean {
        return if(
            ActivityCompat.checkSelfPermission(context, ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
            ActivityCompat.checkSelfPermission(context, ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            true
        } else {
            Snackbar.make(view, context.getString(R.string.permit_location_plz), 1000).show()
            false
        }
    }

    /**
     * 상단 상태바 색상설정
     */
    fun setStatusBarColor(activity: Activity, color: Int) {
        activity.window?.statusBarColor = ContextCompat.getColor(activity, color)
    }

    fun getYesOrNOAlertDialog(
        context: Context,
        positiveListener: DialogInterface.OnClickListener,
        negativeListener: DialogInterface.OnClickListener
    ): AlertDialog {
        return AlertDialog.Builder(context, android.R.style.Theme_DeviceDefault_Light_Dialog_Alert)
            .apply {
                setTitle(context.getString(R.string.network_error))
                setMessage(context.getString(R.string.plz_try_again_in_a_few_minutes))
                setPositiveButton(
                    context.getString(R.string.re_try),
                    positiveListener
                )
                setNegativeButton(
                    context.getString(R.string.cancel),
                    negativeListener
                )
            }.create()

    }

}