package dev.flamey.overlay.command

import dev.flamey.overlay.Main
import dev.flamey.overlay.Overlay
import net.minecraft.command.CommandBase
import net.minecraft.command.ICommandSender

class CommandClass : CommandBase() {

    override fun getCommandName(): String {
        return "overlay"
    }

    override fun getCommandUsage(p0: ICommandSender?): String {
        return "/overlay"
    }

    override fun processCommand(p0: ICommandSender?, p1: Array<out String>?) {
        Main.toggledGUI = !Main.toggledGUI
        if (p1?.isNotEmpty() == true && p1[0] == "reset") {
            Overlay.reset()
        }
    }

    override fun getRequiredPermissionLevel(): Int {
        return 0
    }

}