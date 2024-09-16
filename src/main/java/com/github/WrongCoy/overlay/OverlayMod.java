package com.github.WrongCoy.overlay;

import com.github.WrongCoy.overlay.api.API;
import com.github.WrongCoy.overlay.api.Server;
import com.github.WrongCoy.overlay.command.OverlayCommand;
import com.github.WrongCoy.overlay.config.ConfigManager;
import com.github.WrongCoy.overlay.gui.OverlayGUI;
import com.github.WrongCoy.overlay.hud.OverlayHUD;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;
import org.lwjgl.input.Keyboard;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

@Mod(name = "Overlay", modid = OverlayMod.MODID, version = OverlayMod.VERSION)
public class OverlayMod {

    @Mod.Instance("@ID@")
    public static OverlayMod INSTANCE;
    public static final Minecraft mc = Minecraft.getMinecraft();
    public final static String VERSION = "@VER@";
    public final static String MODID = "@ID@";
    public static boolean isOutdated, guiState = false;
    public static int key = Keyboard.KEY_NONE;
    private Server server = Server.NONE;
    private ConfigManager configManager;
    private boolean rainbow, debug;
    private int opacity = 100;

    @Mod.EventHandler
    public void onInit(FMLInitializationEvent event) {
        INSTANCE = this;
        MinecraftForge.EVENT_BUS.register(this);
        ClientCommandHandler.instance.registerCommand(new OverlayCommand());
        try {
            configManager = new ConfigManager();
            configManager.load();
            checkVersion();
        } catch (IOException e) {
            System.out.println("Failed to load overlay config");
            e.printStackTrace();
        }
    }

    @SubscribeEvent
    public void onRender(RenderGameOverlayEvent e) {
        if (!e.type.equals(RenderGameOverlayEvent.ElementType.TEXT) || mc.currentScreen instanceof OverlayGUI || mc.gameSettings.showDebugInfo || !OverlayHUD.toggled) return;
        OverlayHUD.INSTANCE.draw();
    }

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent e) {
        if (e.phase == TickEvent.Phase.END) {
            if (guiState) {
                Minecraft.getMinecraft().displayGuiScreen(new OverlayGUI());
                guiState = false;
            }
        }
    }

    @SubscribeEvent
    public void onServerJoin(FMLNetworkEvent.ClientConnectedToServerEvent e) {
        if (e.manager.getRemoteAddress().toString().toLowerCase().contains("pika")) {
            this.server = Server.PIKA;
        } else if (e.manager.getRemoteAddress().toString().toLowerCase().contains("jartex")) {
            this.server = Server.JARTEX;
        } else {
            this.server = Server.NONE;
        }
    }

    @SubscribeEvent
    public void onDisconnect(FMLNetworkEvent.ClientDisconnectionFromServerEvent e) {
        OverlayHUD.INSTANCE.profiles.clear();
        API.fetchedProfiles.clear();
        this.server = Server.NONE;
    }

    @SubscribeEvent
    public void onKeyPressed(InputEvent.KeyInputEvent e) {
        try {
            if (Keyboard.isCreated()) {
                if (Keyboard.getEventKeyState()) {
                    int keyCode = Keyboard.getEventKey();
                    if (keyCode == key) {
                        OverlayHUD.toggled = !OverlayHUD.toggled;
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @SubscribeEvent
    public void onChat(ClientChatReceivedEvent e) {
        if (OverlayMod.INSTANCE.getServer() != Server.NONE) {
            OverlayHUD.INSTANCE.onChat(e.message);
        }
    }

    public void checkVersion() throws IOException {
        URL url = new URL("https://raw.githubusercontent.com/WrongCoy/overlay-mod/master/latest");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty(
                "User-Agent",
                "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:25.0) Gecko/20100101 Firefox/25.0"
        );
        conn.connect();

        double latest = Double.parseDouble(new BufferedReader(new InputStreamReader(conn.getInputStream())).readLine());
        double currentVersion = Double.parseDouble(VERSION);
        isOutdated = latest > currentVersion;
    }

    public Server getServer() {
        return server;
    }

    public boolean isRainbow() {
        return rainbow;
    }

    public boolean isDebug() {
        return debug;
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    public void setRainbow(boolean rainbow) {
        this.rainbow = rainbow;
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public int getOpacity() {
        return opacity;
    }

    public void setOpacity(int opacity) {
        this.opacity = opacity;
    }
}
