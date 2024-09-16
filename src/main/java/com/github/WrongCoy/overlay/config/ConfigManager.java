package com.github.WrongCoy.overlay.config;

import com.github.WrongCoy.overlay.OverlayMod;
import com.github.WrongCoy.overlay.hud.OverlayHUD;
import com.github.WrongCoy.overlay.utils.Utils;

import java.io.*;

public class ConfigManager {

    private final File file;

    public ConfigManager() {
        file = new File(Utils.mc.mcDataDir.getAbsolutePath() + File.separator + "config" + File.separator + "overlay.bruh");
        if (!file.exists()) {
            try {
                if (file.createNewFile()) {
                    System.out.println("Created the overlay config file");
                    save();
                }
            } catch (IOException e) {
                System.out.println("Failed to create an overlay config file");
                e.printStackTrace();
            }
        }
    }

    public void load() throws IOException, NullPointerException {
        BufferedReader reader = new BufferedReader(new FileReader(file));
        String line = reader.readLine();
        String[] parts = line.split(" ");
        for (String part : parts) {
            String[] keyValue = part.split("=");
            switch (keyValue[0]) {
                case "keybind":
                    OverlayMod.key = Integer.parseInt(keyValue[1]);
                    break;
                case "x":
                    OverlayHUD.x = Integer.parseInt(keyValue[1].trim());
                    break;
                case "y":
                    OverlayHUD.y = Integer.parseInt(keyValue[1].trim());
                    break;
                case "toggled":
                    OverlayHUD.toggled = Boolean.parseBoolean(keyValue[1].trim());
                    break;
                case "rainbow":
                    OverlayMod.INSTANCE.setRainbow(Boolean.parseBoolean(keyValue[1].trim()));
                    break;
                case "debug":
                    OverlayMod.INSTANCE.setDebug(Boolean.parseBoolean(keyValue[1].trim()));
                    break;
                case "opacity":
                    OverlayMod.INSTANCE.setOpacity(Integer.parseInt(keyValue[1].trim()));
                    break;
            }
        }
    }

    public void save() {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(file));
            boolean rainbow = OverlayMod.INSTANCE.isRainbow();
            boolean debug = OverlayMod.INSTANCE.isDebug();
            boolean toggled = OverlayHUD.toggled;
            int opacity = OverlayMod.INSTANCE.getOpacity();
            int key = OverlayMod.key;
            int x = OverlayHUD.x;
            int y = OverlayHUD.y;
            writer.write(
                    String.format(
                            "keybind=%s x=%s y=%s toggled=%s rainbow=%s debug=%s opacity=%s", key, x, y, toggled, rainbow, debug, opacity
                    )
            );
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
