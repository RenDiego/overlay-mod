package dev.flamey.overlay;

import dev.flamey.overlay.api.server.SupportedServer;
import dev.flamey.overlay.command.CommandClass;
import dev.flamey.overlay.command.gui.CommandGUI;
import dev.flamey.overlay.config.ConfigManager;
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

@Mod(modid = Main.NAME, version = Main.VERSION, name = Main.MODID)
public class Main {

    public Minecraft mc = Minecraft.getMinecraft();

    public final static String VERSION = "0.0.2";
    public final static String NAME = "overlay";
    public final static String MODID = "overlay";

    public static ConfigManager configManager = new ConfigManager();
    public static boolean toggledGUI = false;
    public static boolean toggled = false;
    public static SupportedServer server;

    @Mod.EventHandler
    public void init(FMLInitializationEvent e) {
        MinecraftForge.EVENT_BUS.register(this);
        ClientCommandHandler.instance.registerCommand(new CommandClass());
        configManager.load();
    }

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent e) {
        if (e.phase == TickEvent.Phase.END) {
            if (toggledGUI) {
                toggledGUI = false;
                mc.displayGuiScreen(new CommandGUI());
            }
        }
    }

    @SubscribeEvent
    public void onRender(RenderGameOverlayEvent e) {
        if (e.type.equals(RenderGameOverlayEvent.ElementType.TEXT)) {
            if (toggled && !(mc.gameSettings.showDebugInfo)) Overlay.INSTANCE.draw();
        }
    }

    @SubscribeEvent
    public void onServerJoin(FMLNetworkEvent.ClientConnectedToServerEvent e) {
        if (e.manager.getRemoteAddress().toString().toLowerCase().contains("pika")) {
            server = SupportedServer.PIKA;
        } else if (e.manager.getRemoteAddress().toString().toLowerCase().contains("jartex")) {
            server = SupportedServer.JARTEX;
        } else {
            server = SupportedServer.NONE;
        }
    }

    @SubscribeEvent
    public void serverDisconnect(FMLNetworkEvent.ClientDisconnectionFromServerEvent e) {
        server = SupportedServer.NONE;
        Overlay.INSTANCE.reset();
        Overlay.INSTANCE.setGaming(false);
    }

    @SubscribeEvent
    public void chat(ClientChatReceivedEvent e) {
        if (server != SupportedServer.NONE) Overlay.INSTANCE.chat(e);
    }

    @SubscribeEvent
    public void keyinput(InputEvent.KeyInputEvent e) {
        if (Keyboard.isCreated() && Keyboard.getEventKeyState()) {
            int key = Keyboard.getEventKey();
            if (key == CommandGUI.getKeybind()) {
                toggled = !toggled;
            }
        }
    }


}
