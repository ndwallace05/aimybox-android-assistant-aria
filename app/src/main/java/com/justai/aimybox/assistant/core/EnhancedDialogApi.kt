package com.justai.aimybox.assistant.core

import com.justai.aimybox.api.DialogApi
import com.justai.aimybox.model.DialogRequest
import com.justai.aimybox.model.DialogResponse

class EnhancedDialogApi(
    private val actionRegistry: ActionRegistry,
    private val contextManager: ContextManager = ContextManager(),
    private val uxController: UXController = UXController()
) : DialogApi {

    private val llmOrchestrator = LLMOrchestrator()

    override suspend fun send(request: DialogRequest): DialogResponse {
        val enrichedQuery = contextManager.enrichQuery(request.query)
        val availableTools = actionRegistry.getAllHandlers().map { it.name }
        val llmResponse = llmOrchestrator.plan(enrichedQuery, availableTools)

        return when {
            llmResponse.requiresConfirmation() -> {
                uxController.askForConfirmation(llmResponse.pendingAction)
            }
            llmResponse.hasToolCalls() -> {
                val results = llmResponse.toolCalls.map { toolCall ->
                    val handler = actionRegistry.getByName(toolCall.name)
                        ?: return DialogResponse("Error: I don't know how to do that.")
                    handler.handle(toolCall.parameters)
                }
                val summary = llmOrchestrator.summarizeResults(results)
                contextManager.updateFromResults(results)
                DialogResponse(summary)
            }
            else -> {
                DialogResponse(llmResponse.text)
            }
        }
    }
}

class ContextManager {
    fun enrichQuery(query: String): String = query
    fun updateFromResults(results: List<ActionResult>) {}
}

class UXController {
    fun askForConfirmation(action: String?): DialogResponse {
        return DialogResponse("Are you sure you want to execute $action?")
    }
}

class LLMOrchestrator {
    fun plan(query: String, tools: List<String>): LLMResponse {
        val toolCalls = tools.mapNotNull { tool ->
            if (query.contains(tool.replace("_", " "), ignoreCase = true)) {
                ToolCall(tool, emptyMap())
            } else {
                null
            }
        }
        return if (toolCalls.isNotEmpty()) {
            LLMResponse("", toolCalls = toolCalls)
        } else {
            LLMResponse(text = "I can't help with that.")
        }
    }

    fun summarizeResults(results: List<ActionResult>): String {
        return results.joinToString("\n") { it.message }
    }
}

data class LLMResponse(
    val text: String,
    val toolCalls: List<ToolCall> = emptyList(),
    val pendingAction: String? = null
) {
    fun requiresConfirmation(): Boolean = pendingAction != null
    fun hasToolCalls(): Boolean = toolCalls.isNotEmpty()
}

data class ToolCall(
    val name: String,
    val parameters: Map<String, Any>
)
