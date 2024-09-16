package com.github.WrongCoy.overlay.command;

import com.github.WrongCoy.overlay.OverlayMod;
import com.github.WrongCoy.overlay.hud.OverlayHUD;
import net.minecraft.command.CommandBase;
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
                OverlayHUD.INSTANCE.reload();
            }
        } else
            OverlayMod.guiState = true;
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 0;
    }
}
