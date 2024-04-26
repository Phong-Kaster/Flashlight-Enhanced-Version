package com.example.flashlightenhancedversion.data.repository

import com.example.flashlightenhancedversion.data.datastore.SettingDatastore
import com.example.flashlightenhancedversion.data.enums.Language
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SettingRepository
@Inject
constructor(
    private val settingDatastore: SettingDatastore,
)
{
    // LANGUAGE
    fun getLanguage(): Language {
        return settingDatastore.language
    }

    fun setLanguage(language: Language) {
        settingDatastore.language = language
    }

    fun getLanguageFlow() = settingDatastore.languageFlow
}