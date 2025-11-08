package com.justai.aimybox.assistant

import android.app.Application
import android.content.Context
import android.app.Application
import android.content.Context
import com.justai.aimybox.Aimybox
import com.justai.aimybox.components.AimyboxProvider
import com.justai.aimybox.core.Config
import com.justai.aimybox.speechkit.yandex.cloud.IAmTokenGenerator
import com.justai.aimybox.speechkit.yandex.cloud.Language
import com.justai.aimybox.speechkit.yandex.cloud.Speed
import com.justai.aimybox.speechkit.yandex.cloud.Voice
import com.justai.aimybox.speechkit.yandex.cloud.YandexSpeechToText
import com.justai.aimybox.speechkit.yandex.cloud.YandexTextToSpeech
import com.justai.aimybox.assistant.core.ActionDefinition
import com.justai.aimybox.assistant.core.ActionRegistry
import com.justai.aimybox.assistant.core.EnhancedDialogApi
import com.justai.aimybox.assistant.handlers.LocalActionHandler
import dagger.hilt.android.HiltAndroidApp
import java.util.UUID
import javax.inject.Inject

@HiltAndroidApp
class AimyboxApplication : Application(), AimyboxProvider {

    @Inject
    lateinit var actionRegistry: ActionRegistry

    companion object {
        private const val YANDEX_TOKEN = BuildConfig.token
        private const val YANDEX_FOLDER_ID = BuildConfig.folderId
    }

    override fun onCreate() {
        super.onCreate()
        actionRegistry.register(
            LocalActionHandler(
                this,
                ActionDefinition("toggle_wifi", "Toggles Wi-Fi")
            )
        )
    }

    override val aimybox by lazy { createAimybox(this) }

    private fun createAimybox(context: Context): Aimybox {

        val sttConfig = YandexSpeechToText.Config(
            literatureText = true,
            rawResults = true,
            enableProfanityFilter = true,
        )

        val tokenGenerator = IAmTokenGenerator(YANDEX_TOKEN)

        val stt = YandexSpeechToText(
            iAmTokenProvider = tokenGenerator,
            folderId = YANDEX_FOLDER_ID,
            language = Language.RU,
            config = sttConfig
        )

        val ttsConfig = YandexTextToSpeech.ConfigV3(
            voice = Voice.FILIPP,
            speed = Speed(1.0f)
        )

        val tts = YandexTextToSpeech.V3(
            context = context,
            iAmTokenProvider = tokenGenerator,
            folderId = YANDEX_FOLDER_ID,
            defaultLanguage = Language.RU,
            config = ttsConfig
        )

        val dialogApi = EnhancedDialogApi(actionRegistry)

        val aimyboxConfig = Config.create(stt, tts, dialogApi)
        return Aimybox(aimyboxConfig, this)
    }
}