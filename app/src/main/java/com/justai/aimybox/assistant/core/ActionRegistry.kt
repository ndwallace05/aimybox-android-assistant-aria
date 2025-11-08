package com.justai.aimybox.assistant.core

class ActionRegistry {
    private val handlers = mutableMapOf<String, ActionHandler>()

    fun register(handler: ActionHandler) {
        handlers[handler.name] = handler
    }

    fun getByName(name: String): ActionHandler? {
        return handlers[name]
    }

    fun getAllHandlers(): List<ActionHandler> {
        return handlers.values.toList()
    }
}
