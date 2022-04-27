package org.sjhstudio.weathertestapp.util

import android.app.Activity
import android.content.IntentSender
import android.util.Log
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.install.model.UpdateAvailability
import org.sjhstudio.weathertestapp.util.Constants.DEBUG
import org.sjhstudio.weathertestapp.util.Constants.IN_APP_UPDATE_TYPE
import org.sjhstudio.weathertestapp.util.Constants.REQ_IN_APP_UPDATE

interface OnInAppUpdateCallback {
    fun onFailed()
}

object InAppUpdateHelper {

    private var callback: OnInAppUpdateCallback? = null

    fun setOnInAppUpdateCallback(callback: OnInAppUpdateCallback?) {
        this.callback = callback
    }

    fun inAppUpdate(activity: Activity, appUpdateManager: AppUpdateManager) {
        val appUpdateInfoTask = appUpdateManager.appUpdateInfo

        appUpdateInfoTask.apply {
            addOnSuccessListener { appUpdateInfo ->
                if(appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE) {
                    // Request the update.
                    Log.e(DEBUG, "inAppUpdate() : 업데이트 가능")
                    try {
                        appUpdateManager.startUpdateFlowForResult(
                            appUpdateInfo,
                            IN_APP_UPDATE_TYPE,
                            activity,
                            REQ_IN_APP_UPDATE
                        )
                    } catch(e: IntentSender.SendIntentException) {
                        e.printStackTrace()
                        Log.e(DEBUG, "inAppUpdate() : 에러")
                        callback?.onFailed()
                    }
                } else {
                    Log.e(DEBUG, "inAppUpdate() : 업데이트 없음")
                    callback?.onFailed()
                }
            }

            addOnFailureListener {
                Log.e(DEBUG, "inAppUpdate() : 에러(${it.message})")
                callback?.onFailed()
            }
        }
    }

    fun resumeInAppUpdate(activity: Activity, appUpdateManager: AppUpdateManager) {
        val appUpdateInfoTask = appUpdateManager.appUpdateInfo

        appUpdateInfoTask.addOnSuccessListener { appUpdateInfo ->
            if(appUpdateInfo.updateAvailability() == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS) {
                // Resume the update.
                Log.e(DEBUG, "resumeInAppUpdate() : 업데이트 재개")
                try {
                    appUpdateManager.startUpdateFlowForResult(
                        appUpdateInfo,
                        IN_APP_UPDATE_TYPE,
                        activity,
                        REQ_IN_APP_UPDATE
                    )
                } catch(e: IntentSender.SendIntentException) {
                    Log.e(DEBUG, "resumeInAppUpdate() : 에러")
                    e.printStackTrace()
                }
            } else {
                Log.e(DEBUG, "resumeInAppUpdate() : 진행중인 업데이트 없음")
            }
        }
    }
}