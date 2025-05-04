package com.github.ringoame196_s_mcPlugin.events

import com.github.ringoame196_s_mcPlugin.AdvancementManager
import com.github.ringoame196_s_mcPlugin.Data
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent

class GUIEvents : Listener {
    private val advancementManager = AdvancementManager()
    private val advancementGUITitle = advancementManager.guiTitle

    @EventHandler
    fun onInventoryClick(e: InventoryClickEvent) {

    }

    @EventHandler
    fun onInventoryClose(e: InventoryCloseEvent) {
        val gui = e.view
        val player = e.player

        if (gui.title != advancementGUITitle) return
        Data.usePlayerData.remove(player) // データ削除する
    }
}
