package com.github.ringoame196_s_mcPlugin

import com.github.ringoame196_s_mcPlugin.commands.Command
import com.github.ringoame196_s_mcPlugin.data.Data
import com.github.ringoame196_s_mcPlugin.events.GUIEvents
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.bukkit.plugin.java.JavaPlugin
import java.io.File

class Main : JavaPlugin() {
    private val plugin = this
    override fun onEnable() {
        super.onEnable()

        saveResource("lang.json", false) // lang.json生成
        loadLang()

        server.pluginManager.registerEvents(GUIEvents(), plugin)
        val command = getCommand("adview")
        command!!.setExecutor(Command())
    }

    // lang.jsonを読み込む
    private fun loadLang() {
        val jsonString = File(plugin.dataFolder, "lang.json").readText()
        val gson = Gson()
        val type = object : TypeToken<Map<String, Any>>() {}.type
        val map: Map<String, String> = gson.fromJson(jsonString, type)
        Data.lang.putAll(map)
    }
}
