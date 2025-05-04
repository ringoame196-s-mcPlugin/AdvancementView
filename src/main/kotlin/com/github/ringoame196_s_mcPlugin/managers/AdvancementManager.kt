package com.github.ringoame196_s_mcPlugin.managers

import com.github.ringoame196_s_mcPlugin.data.Data
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
    val advancementList = Bukkit.advancementIterator().asSequence()
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
        val advancementSize = guiSize - 1
        var advancementNumber = page * advancementSize
        gui.clear()
        for (i in 0 until advancementSize) {
            advancementNumber ++
            val item = makeViewItem(advancementNumber, targetPlayer) ?: continue
            gui.setItem(i, item)
        }
        val lastSlot = guiSize - 1
        gui.setItem(lastSlot, nextButtonItem()) // 次へボタン設置
    }

    fun changeAdvancement(targetPlayer: Player, advancement: Advancement) {
        val progress = targetPlayer.getAdvancementProgress(advancement)
        if (hasAdvancement(targetPlayer, advancement)) {
            // 実績取り消し
            for (criteria in progress.awardedCriteria) {
                progress.revokeCriteria(criteria)
            }
        } else {
            // 実績解除
            for (criteria in progress.remainingCriteria) {
                progress.awardCriteria(criteria)
            }
        }
    }

    fun nextButtonItem(): ItemStack {
        val item = ItemStack(Material.STICK)
        val itemMeta = item.itemMeta ?: return item
        itemMeta.setDisplayName("${ChatColor.GOLD}次へ")
        item.itemMeta = itemMeta
        return item
    }

    private fun makeViewItem(advancementNumber: Int, targetPlayer: Player): ItemStack? {
        if (advancementList.size < advancementNumber) return null
        val advancement = advancementList[advancementNumber]
        val display = advancement.display ?: return null
        val id = advancement.key.toString().replace("/", ".").replace("minecraft:", "")

        val title = Data.lang["$id.title"] ?: id
        val description = Data.lang["$id.description"]
        val icon = display.icon

        val itemMeta = icon.itemMeta ?: return null

        // 実績解除済みの場合 表示名に[解除済み]と追記
        val displayName = if (hasAdvancement(targetPlayer, advancement)) {
            "${display.type.color}$title${ChatColor.YELLOW}[解除済み]"
        } else {
            "${display.type.color}$title"
        }
        itemMeta.setDisplayName(displayName)

        itemMeta.lore = mutableListOf(
            "${ChatColor.AQUA}$description",
            "${ChatColor.YELLOW}シフトクリックで実績状態を切り替える(OP)",
            "$advancementNumber"
        )
        itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS)

        icon.itemMeta = itemMeta

        // 実績解除済みの場合 光らせる
        if (hasAdvancement(targetPlayer, advancement)) {
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