package com.github.ringoame196_s_mcPlugin

import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.advancement.Advancement
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack

class AdvancementManager {
    private val advancementIteratorList = Bukkit.advancementIterator().asSequence()
        .filter { !it.key.key.startsWith("recipes/") } // レシピを除外
        .toList()

    val guiTitle = "${ChatColor.DARK_BLUE}実績一覧"
    private val guiSize = 54

    fun makeAdvancementViewGUI(targetPlayer: Player): Inventory {
        val gui = Bukkit.createInventory(null, guiSize, guiTitle)
        updateGUI(gui, 0, targetPlayer)
        return gui
    }

    fun updateGUI(gui: Inventory, page: Int, targetPlayer: Player) {
        val advancementSize = guiSize - 2
        var advancementNumber = page * advancementSize
        for (i in 0..advancementSize) {
            advancementNumber ++
            val item = makeViewItem(advancementNumber, targetPlayer) ?: continue
            gui.setItem(i, item)
        }
        gui.setItem((guiSize - 1), nextButtonItem())
    }

    fun nextButtonItem(): ItemStack {
        val item = ItemStack(Material.STICK)
        val itemMeta = item.itemMeta ?: return item
        itemMeta.setDisplayName("${ChatColor.GOLD}次へ")
        item.itemMeta = itemMeta
        return item
    }

    private fun makeViewItem(advancementNumber: Int, targetPlayer: Player): ItemStack? {
        if (advancementIteratorList.size < advancementNumber) return null
        val advancementIterator = advancementIteratorList[advancementNumber]
        val display = advancementIterator.display ?: return null
        val id = advancementIterator.key.toString().replace("/", ".").replace("minecraft:", "")

        val title = Data.lang["$id.title"] ?: id
        val description = Data.lang["$id.description"]
        val icon = display.icon

        val itemMeta = icon.itemMeta ?: return null
        itemMeta.setDisplayName("${display.type.color}$title")
        itemMeta.lore = mutableListOf("${ChatColor.AQUA}$description")
        itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS)

        icon.itemMeta = itemMeta

        // 実績解除済みの場合 光らせる
        if (hasAdvancement(targetPlayer, advancementIterator)) {
            icon.addUnsafeEnchantment(Enchantment.DURABILITY, 1)
        }

        return icon
    }

    // プレイヤーが実績解除しているかチェック
    private fun hasAdvancement(player: Player, advancement: Advancement): Boolean {
        val progress = player.getAdvancementProgress(advancement)
        return progress.isDone
    }
}
