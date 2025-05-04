package com.github.ringoame196_s_mcPlugin.events

import com.github.ringoame196_s_mcPlugin.AdvancementManager
import com.github.ringoame196_s_mcPlugin.Data
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.inventory.InventoryView
import org.bukkit.inventory.ItemStack
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
        val isShiftClick = e.isShiftClick
        val sound = Sound.UI_BUTTON_CLICK
        player.playSound(player, sound, 1f, 1f)

        when (item) {
            advancementManager.nextButtonItem() -> nextPage(player, gui)
            else -> changeAdvancement(gui, item ?: return, player, isShiftClick)
        }
    }

    private fun nextPage(player: Player, gui: InventoryView) {
        val usePlayerData = Data.usePlayerData[player] ?: return
        val targetPlayer = usePlayerData.targetPlayer
        val page = ++ usePlayerData.page

        advancementManager.updateGUI(gui.topInventory, page, targetPlayer)
    }

    private fun changeAdvancement(gui: InventoryView, item: ItemStack, player: Player, isShiftClick: Boolean) {
        if (!isShiftClick) return
        if (!player.isOp) return

        val sound = Sound.BLOCK_BELL_USE
        val listId = item.itemMeta?.lore?.get(2)?.toIntOrNull() ?: return
        val advancement = advancementManager.advancementIteratorList[listId] ?: return

        val usePlayerData = Data.usePlayerData[player] ?: return
        val targetPlayer = usePlayerData.targetPlayer
        val page = usePlayerData.page
        advancementManager.changeAdvancement(targetPlayer, advancement)
        advancementManager.updateGUI(gui.topInventory, page, targetPlayer)
        player.playSound(player, sound, 1f, 1f)
    }

    @EventHandler
    fun onInventoryClose(e: InventoryCloseEvent) {
        val gui = e.view
        val player = e.player

        if (gui.title != advancementGUITitle) return
        Data.usePlayerData.remove(player) // データ削除する
    }
}
