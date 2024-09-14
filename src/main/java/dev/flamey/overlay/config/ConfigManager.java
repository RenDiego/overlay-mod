package dev.flamey.overlay.config;

import dev.flamey.overlay.OverlayMod;
import dev.flamey.overlay.hud.Overlay;
import dev.flamey.overlay.utils.Utils;

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
                    Overlay.x = Integer.parseInt(keyValue[1].trim());
                    break;
                case "y":
                    Overlay.y = Integer.parseInt(keyValue[1].trim());
                    break;
                case "toggled":
                    Overlay.toggled = Boolean.parseBoolean(keyValue[1].trim());
                    break;
                case "rainbow":
                    OverlayMod.INSTANCE.setRainbow(Boolean.parseBoolean(keyValue[1].trim()));
                    break;
                case "debug":
                    OverlayMod.INSTANCE.setDebug(Boolean.parseBoolean(keyValue[1].trim()));
                    break;
            }
        }
    }

    public void save() {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(file));
            boolean rainbow = OverlayMod.INSTANCE.isRainbow();
            boolean debug = OverlayMod.INSTANCE.isDebug();
            boolean toggled = Overlay.toggled;
            int key = OverlayMod.key;
            int x = Overlay.x;
            int y = Overlay.y;
            writer.write(String.format("keybind=%s x=%s y=%s toggled=%s rainbow=%s debug=%s", key, x, y, toggled, rainbow, debug));
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
