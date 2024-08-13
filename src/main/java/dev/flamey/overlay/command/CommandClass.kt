package dev.flamey.overlay.command

import dev.flamey.overlay.Main
import dev.flamey.overlay.Overlay
import net.minecraft.client.Minecraft
import net.minecraft.command.CommandBase
import net.minecraft.command.ICommandSender
import net.minecraft.util.ChatComponentText

class CommandClass : CommandBase() {

    override fun getCommandName(): String {
        return "overlay"
    }

    override fun getCommandUsage(p0: ICommandSender?): String {
        return "/overlay"
    }

    override fun processCommand(p0: ICommandSender?, p1: Array<out String>?) {
        if (p1?.isNotEmpty() == true && p1?.get(0)?.equals("reload") == true) {
            Minecraft.getMinecraft().thePlayer.addChatMessage(ChatComponentText("[Overlay] Reloading..."))
            Overlay.reload()
            return
        }
        Main.toggledGUI = !Main.toggledGUI
    }

    override fun getRequiredPermissionLevel(): Int {
        return 0
    }

}