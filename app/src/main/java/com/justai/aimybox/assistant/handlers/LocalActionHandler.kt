package com.justai.aimybox.assistant.handlers

import android.content.Context
import android.net.wifi.WifiManager
import com.justai.aimybox.assistant.core.ActionDefinition
import com.justai.aimybox.assistant.core.ActionHandler
import com.justai.aimybox.assistant.core.ActionResult

class LocalActionHandler(
    private val context: Context,
    override val definition: ActionDefinition
) : ActionHandler {

    override suspend fun handle(parameters: Map<String, Any>): ActionResult {
        return when (definition.name) {
            "toggle_wifi" -> toggleWifi(parameters)
            else -> ActionResult(false, "Unknown local action: ${definition.name}")
        }
    }

    private fun toggleWifi(parameters: Map<String, Any>): ActionResult {
        val wifiManager = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as? WifiManager
            ?: return ActionResult(false, "Wi-Fi service is not available on this device.")

        if (context.checkSelfPermission(android.Manifest.permission.CHANGE_WIFI_STATE) != android.content.pm.PackageManager.PERMISSION_GRANTED) {
            return ActionResult(false, "Permission to change Wi-Fi state is not granted.")
        }

        return try {
            val enabled = parameters["enabled"] as? Boolean ?: !wifiManager.isWifiEnabled
            wifiManager.isWifiEnabled = enabled
            val status = if (enabled) "enabled" else "disabled"
            ActionResult(true, "Wi-Fi has been $status")
        } catch (e: SecurityException) {
            ActionResult(false, "Failed to change Wi-Fi state due to a security exception.")
        }
    }
}
