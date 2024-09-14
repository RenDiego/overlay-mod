package dev.flamey.overlay.command;

import dev.flamey.overlay.OverlayMod;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;

public class OverlayCommand extends CommandBase {

    @Override
    public String getCommandName() {
        return "overlay";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/overlay";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        OverlayMod.guiState = true;
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 0;
    }
}
