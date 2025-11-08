package com.justai.aimybox.assistant.handlers

import android.content.Context
import android.net.wifi.WifiManager
import com.justai.aimybox.assistant.core.ActionHandler
import com.justai.aimybox.assistant.core.ActionResult

class LocalActionHandler(
    private val context: Context,
    override val name: String,
    override val description: String = ""
) : ActionHandler {

    override suspend fun handle(parameters: Map<String, Any>): ActionResult {
        return when (name) {
            "toggle_wifi" -> toggleWifi(parameters)
            else -> ActionResult(false, "Unknown local action: $name")
        }
    }

    private fun toggleWifi(parameters: Map<String, Any>): ActionResult {
        val wifiManager = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        val enabled = parameters["enabled"] as? Boolean ?: !wifiManager.isWifiEnabled
        wifiManager.isWifiEnabled = enabled
        val status = if (enabled) "enabled" else "disabled"
        return ActionResult(true, "Wi-Fi has been $status")
    }
}
