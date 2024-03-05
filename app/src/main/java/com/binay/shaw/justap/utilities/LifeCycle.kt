package com.binay.shaw.justap.utilities

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner

/**
 * @description print activity life cycle
 *
 * @author shengj (shengj@rd.netease.com)
 * @date 9/15/20 18:54
 */
class LifeCyclePrinter(private val TAG: String) : DefaultLifecycleObserver {
    override fun onCreate(owner: LifecycleOwner) {
        Logger.debugLog(TAG, "onCreate")
    }

    override fun onStart(owner: LifecycleOwner) {
        Logger.debugLog(TAG, "onStart")
    }

    override fun onResume(owner: LifecycleOwner) {
        Logger.debugLog(TAG, "onResume")
    }

    override fun onPause(owner: LifecycleOwner) {
        Logger.debugLog(TAG, "onPause")
    }

    override fun onStop(owner: LifecycleOwner) {
        Logger.debugLog(TAG, "onStop")
    }

    override fun onDestroy(owner: LifecycleOwner) {
        Logger.debugLog(TAG, "onDestroy")
    }
}