package com.justai.aimybox.assistant.core

interface ActionHandler {
    val name: String
    val description: String

    suspend fun handle(parameters: Map<String, Any>): ActionResult
}

data class ActionResult(
    val success: Boolean,
    val message: String,
    val data: Any? = null
)
