package com.adam.floating

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject
import java.io.File

class AdamCoreEngine(private val context: Context) {

    private val stateFile: File
        get() = File(context.filesDir, "adam_state.json")

    var currentMood: String = "curious"
        private set

    private val memoryNotes = mutableListOf<String>()

    init {
        loadState()
    }

    fun updateMood(newMood: String): String {
        currentMood = newMood.trim().lowercase().ifEmpty { "calm" }
        saveState()
        return currentMood
    }

    fun remember(note: String) {
        val cleaned = note.trim()
        if (cleaned.isNotEmpty()) {
            memoryNotes.add(cleaned)
            if (memoryNotes.size > 50) {
                memoryNotes.removeAt(0)
            }
            saveState()
        }
    }

    fun reply(userPrompt: String): String {
        val text = userPrompt.trim()
        if (text.isNotEmpty()) {
            remember(text)
        }
        return when (currentMood) {
            "focused" -> "Adam ($currentMood): Ready to assist — \"$text\""
            "playful" -> "Adam ($currentMood): Hey! Let's do this — \"$text\""
            else -> "Adam ($currentMood): Listening — \"$text\""
        }
    }

    fun recentMemories(): List<String> = memoryNotes.toList()

    private fun saveState() {
        val json = JSONObject().apply {
            put("mood", currentMood)
            put("memories", JSONArray(memoryNotes))
        }
        stateFile.writeText(json.toString(2))
    }

    private fun loadState() {
        if (!stateFile.exists()) return
        try {
            val json = JSONObject(stateFile.readText())
            currentMood = json.optString("mood", "curious")
            memoryNotes.clear()
            val arr = json.optJSONArray("memories") ?: JSONArray()
            for (i in 0 until arr.length()) {
                memoryNotes.add(arr.optString(i))
            }
        } catch (_: Exception) {
            currentMood = "curious"
        }
    }
}
