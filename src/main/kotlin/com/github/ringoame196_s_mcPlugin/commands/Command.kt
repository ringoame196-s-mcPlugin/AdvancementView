package com.github.ringoame196_s_mcPlugin.commands

import com.github.ringoame196_s_mcPlugin.data.Data
import com.github.ringoame196_s_mcPlugin.data.UsePlayer
import com.github.ringoame196_s_mcPlugin.managers.AdvancementManager
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.entity.Player

class Command : CommandExecutor, TabCompleter {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (sender !is Player) {
            val message = "${ChatColor.RED}このコマンドはプレイヤーのみ実行可能です"
            sender.sendMessage(message)
            return true
        }

        if (args.isEmpty()) return false

        val targetPlayerName = args[0]
        val targetPlayer = Bukkit.getPlayer(targetPlayerName)
        if (targetPlayer == null) {
            val message = "${ChatColor.RED}プレイヤーが見つかりませんでした"
            sender.sendMessage(message)
            return true
        }

        val advancementManager = AdvancementManager()
        val gui = advancementManager.makeAdvancementViewGUI(targetPlayer)
        sender.openInventory(gui)
        Data.usePlayerData[sender] = UsePlayer(0, targetPlayer) // usePlayerData設定

        return true
    }

    override fun onTabComplete(commandSender: CommandSender, command: Command, label: String, args: Array<out String>): MutableList<String>? {
        return when (args.size) {
            1 -> (Bukkit.getOnlinePlayers().map { it.name }).toMutableList()
            else -> mutableListOf()
        }
    }
}
