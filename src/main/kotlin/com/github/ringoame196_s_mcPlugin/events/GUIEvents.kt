package com.github.ringoame196_s_mcPlugin.events

import com.github.ringoame196_s_mcPlugin.AdvancementManager
import com.github.ringoame196_s_mcPlugin.Data
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import kotlin.collections.remove

class GUIEvents : Listener {
    private val advancementManager = AdvancementManager()
    private val advancementGUITitle = advancementManager.guiTitle

    @EventHandler
    fun onInventoryClick(e: InventoryClickEvent) {
        val gui = e.view
        val player = e.whoClicked as? Player ?: return
        if (gui == player.inventory) return // インベントリクリック時は対象外に
        if (gui.title != advancementGUITitle) return

        e.isCancelled = true
        val item = e.currentItem
        val sound = Sound.UI_BUTTON_CLICK
        player.playSound(player, sound, 1f, 1f)

        if (item != advancementManager.nextButtonItem()) return

        val usePlayerData = Data.usePlayerData[player] ?: return
        val targetPlayer = usePlayerData.targetPlayer
        val page = usePlayerData.page ++

        advancementManager.updateGUI(gui.topInventory, page, targetPlayer)
    }

    @EventHandler
    fun onInventoryClose(e: InventoryCloseEvent) {
        val gui = e.view
        val player = e.player

        if (gui.title != advancementGUITitle) return
        Data.usePlayerData.remove(player) // データ削除する
    }
}
