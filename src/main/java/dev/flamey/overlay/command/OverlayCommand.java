package dev.flamey.overlay.command;

import dev.flamey.overlay.OverlayMod;
import dev.flamey.overlay.hud.Overlay;
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
        if (args != null && args.length > 0) {
            if (args[0].equalsIgnoreCase("reload")) {
                Overlay.INSTANCE.reload();
            }
        } else
            OverlayMod.guiState = true;
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 0;
    }
}
