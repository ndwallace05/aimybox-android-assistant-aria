package com.justai.aimybox.assistant.core

import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ActionRegistry @Inject constructor() {
    private val handlers = ConcurrentHashMap<String, ActionHandler>()

    fun register(handler: ActionHandler) {
        handlers[handler.definition.name] = handler
    }

    fun getByName(name: String): ActionHandler? {
        return handlers[name]
    }

    fun getAllDefinitions(): List<ActionDefinition> {
        return handlers.values.map { it.definition }
    }
}
