package com.justai.aimybox.assistant.core

interface ActionHandler {
    val definition: ActionDefinition

    suspend fun handle(parameters: Map<String, Any>): ActionResult
}

data class ActionResult(
    val success: Boolean,
    val message: String,
    val data: Any? = null
)
