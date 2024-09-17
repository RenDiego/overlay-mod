package com.github.WrongCoy.overlay.command;

import com.github.WrongCoy.overlay.OverlayMod;
import com.github.WrongCoy.overlay.api.API;
import com.github.WrongCoy.overlay.api.Profile;
import com.github.WrongCoy.overlay.api.Server;
import com.github.WrongCoy.overlay.hud.OverlayHUD;
import com.github.WrongCoy.overlay.utils.Utils;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.EnumChatFormatting;

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
        if (args.length > 0) {
            if (args[0].equalsIgnoreCase("reload")) {
                OverlayHUD.INSTANCE.reload();
            } else if (args[0].equalsIgnoreCase("get")) {
                String playerName = args[1];
                Server server = OverlayMod.INSTANCE.getServer();
                if (playerName == null) {
                    Utils.print(EnumChatFormatting.RED + "Invalid player name, use /overlay get playerName server");
                    return;
                }
                if (args.length > 2) {
                    switch (args[2].toLowerCase()) {
                        case "jartex":
                            server = Server.JARTEX;
                            break;
                        case "pika":
                            server = Server.PIKA;
                            break;
                            default:
                                Utils.print(EnumChatFormatting.RED + "Unknown server, use jartex or pika    ");
                                return;
                    }
                }
                if (server == Server.NONE) {
                    Utils.print(EnumChatFormatting.RED + "Invalid server, use /overlay get playerName server");
                    return;
                }
                printPlayer(playerName, server);
            }
        } else {
            OverlayMod.guiState = true;
        }
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 0;
    }

    private void printPlayer(String username, Server server) {
        Runnable runnable = () -> {
            try {
                Profile profile = new Profile(username);
                API.getProfile(profile, server);
                API.getInfo(profile, server);
                Utils.print(String.format("§c%s§r: §a%s §eFKDR - §a%s §eWLR - §a%s §eKDR", profile.username, profile.fkdr, profile.wlr, profile.kdr));
            } catch (Exception e) {
                Utils.print("Failed to get players info: " + username + " " + e.getMessage());
                e.printStackTrace();
            }
        };
        Thread thread = new Thread(runnable);
        thread.start();
    }

}
